package lee.fund.util.test.spring;

import com.google.common.base.Strings;
import lee.fund.util.config.ConfProperties;
import lee.fund.util.config.ConfigUtils;
import lee.fund.util.ioc.ServiceLocator;
import lee.fund.util.jetcd.JetcdClient;
import lee.fund.util.lang.UncheckedException;
import lee.fund.util.log.ConsoleLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 基于 Spring 的 JUnit 辅助工具。
 */
public class SpringJUnit {
    private static final Logger log = LoggerFactory.getLogger(JetcdClient.class);

    /**
     * @param configClass
     */
    public static void boot(Class configClass) {
        boot(configClass, configClass);
    }

    /**
     * 启动 Spring Boot Application。
     *
     * @param configClassInLocalTest 单元测试项目中的 @SpringBootApplication 类，用于获取单元测试项目的配置；
     *                               注意不要是使用任何测试类本身，会导致 Spring Boot 陷入死循环。
     * @param classInServiceImpl     远程服务实现项目中的 @SpringBootApplication 类，用于获取远程服务实现项目的配置，实现本地调用服务。
     */
    public static void boot(Class configClassInLocalTest, Class classInServiceImpl) {
        Objects.requireNonNull(configClassInLocalTest, "arg configClassInLocalTest");

        //
        boolean remoting = isRemoting(classInServiceImpl == null ? configClassInLocalTest : classInServiceImpl);
        Class configClass = remoting ? configClassInLocalTest : classInServiceImpl;
        ConsoleLogger.info("testing " + (remoting ? "remoting" : "local"));
        //
        if (!remoting && classInServiceImpl == null) {
            throw new IllegalArgumentException("intend to invoke local service impl but classInServiceImpl is null");
        }

        String configPath = getConfigDir(remoting ? configClassInLocalTest : classInServiceImpl);
        ConsoleLogger.info("configDir " + configPath);
        ConfigUtils.setConfigDir(configPath);
        //
        Properties props = new Properties();
        props.put("spring.main.banner-mode", "off");
        props.put("spring.main.web_environment", false);
        props.put("spring.config.location", configPath);
        props.put("spring.config.name", "app");
        props.put("spring.profiles.active", ConfProperties.INSTANCE.getActiveProfile());
        String logConfigPath = ConfigUtils.searchConf("log4j2.xml");
        if (logConfigPath == null) {
            logConfigPath = ConfigUtils.searchGlobalConf("log4j2.xml");
        }
        props.put("logging.config", logConfigPath);
//        System.setProperty("spring.config.location", MessageFormat.format("{0}spring.boot.properties,{0}", configPath));
//        System.setProperty("spring.config.name", "app");
        SpringApplication app = new SpringApplication(configClass);
        String xmlFilePath = ConfigUtils.searchConf("spring.xml");
        if (xmlFilePath != null) {
            log.info("found spring config: " + xmlFilePath);
            Set<String> sourceSet = new HashSet<>();
            sourceSet.add("file:" + xmlFilePath);
            app.setSources(sourceSet);
        } else {
            log.info("not found spring root xml " + xmlFilePath);
        }
        app.setDefaultProperties(props);
        app.addInitializers(ctx -> {
            ServiceLocator.INSTANCE.setCtx(ctx);
        });
        app.run();
    }

    public static boolean isRemoting(Class clazz) {
        final String longFlag = "mtime.testing.remoting";
        final String shortFlag = "r";
        String r = System.getenv(longFlag);
        if (Strings.isNullOrEmpty(r)) {
            r = System.getenv(shortFlag);
        }
        if (Strings.isNullOrEmpty(r)) {
            // ..../target/classes/
            String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
            // 基于 maven 测试项目目录约定
            path += "../../src/test/resources/etc/app.properties";
            File file = new File(path);
            if (file.exists()) {
                try (FileInputStream globalPropsInputStream = new FileInputStream(file)) {
                    System.getProperties().load(globalPropsInputStream);
                } catch (Exception ex) {
                    throw new UncheckedException(ex);
                }
            }
            r = System.getProperty(longFlag);
        }

        return !Strings.isNullOrEmpty(r) && Boolean.parseBoolean(r);
    }

    private static String getConfigDir(Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath() + "etc/";
    }

}

