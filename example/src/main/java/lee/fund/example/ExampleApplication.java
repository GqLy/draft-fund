package lee.fund.example;

import lee.fund.common.config.Configuration;
import lee.fund.remoting.application.RpcApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 14:33
 * Desc:
 */
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        Configuration configuration=new Configuration();
        RpcApplication app = new RpcApplication(ExampleApplication.class, configuration, args);
        app.run();
    }
}