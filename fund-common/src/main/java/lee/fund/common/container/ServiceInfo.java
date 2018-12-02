package lee.fund.common.container;

import lee.fund.common.annotation.RpcMethod;
import lee.fund.common.annotation.RpcParameter;
import lee.fund.common.app.NamingConvertEnum;
import lee.fund.common.util.MethodUtils;
import lee.fund.util.lang.StrKit;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 17:44
 * Desc:
 */
@Setter
@Getter
public class ServiceInfo {
    private String name;// 名称
    private String description;// 描述
    private Map<String, MethodInfo> methodMap;// 方法列表

    public ServiceInfo(Class<?> clazz, String name, String description, NamingConvertEnum convert) {
        this.name = name;
        this.description = description;
        this.methodMap = new HashMap<>();
        Arrays.stream(clazz.getMethods()).filter(m -> m.getDeclaringClass() != Object.class).forEach(m->{
            Optional<RpcMethod> mdOptional = Optional.ofNullable(m.getAnnotation(RpcMethod.class));
            MethodInfo mi = new MethodInfo();
            mi.name = MethodUtils.getMethodName(m, convert, mdOptional);
            mi.description = mdOptional.map(o->o.description()).orElse(Strings.EMPTY);
            mi.returnPiInfo = getReturn(m);
            mi.parameters = getParameters(m);
            this.methodMap.put(mi.name, mi);
        });
    }

    private static List<ParameterInfo> getParameters(Method method) {
        List<ParameterInfo> list = Arrays.stream(method.getParameters()).map(p -> {
            ParameterInfo pi = getParameter(p.getType(), p.getAnnotation(RpcParameter.class));
            if (StrKit.isBlank(pi.name)) {
                pi.name = p.getName();
            }
            return pi;
        }).collect(Collectors.toList());
        return list;
    }

    private static ParameterInfo getReturn(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == void.class) {
            return null;
        }
        return getParameter(returnType, method.getAnnotation(RpcParameter.class));
    }

    private static ParameterInfo getParameter(Class<?> paramType, RpcParameter rpcParameter) {
        Optional<RpcParameter> rpmOptional = Optional.ofNullable(rpcParameter);
        ParameterInfo pi = new ParameterInfo();
        pi.type = paramType;
        pi.name = rpmOptional.map(o -> o.name()).orElse(null);
        pi.description = rpmOptional.map(o -> o.description()).orElse(null);
        return pi;
    }

    @Getter
    @Setter
    public static class MethodInfo {
        private String name;// 名称
        private String description;// 描述
        private List<ParameterInfo> parameters;// 参数列表
        private ParameterInfo returnPiInfo;// 返回值
    }

    @Getter
    @Setter
    public static class ParameterInfo {
        private String name;// 名称
        private Class<?> type;// 类型
        private String description;// 描述
    }
}
