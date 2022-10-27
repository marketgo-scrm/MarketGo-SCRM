package com.easy.marketgo.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : ssk
 * @version : v1.0
 * @date : 2022-05-31 14:40:17
 * @description : 服务间请求返回日志耗时参数打印
 */
@Log4j2
@Aspect
@Configuration
public class WebLogAop {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void log() {

    }


    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch(String.valueOf(System.currentTimeMillis()));
        stopWatch.start("解析请求Url");
        ImmutablePair<String, String> pair = getUrlAndMethod(joinPoint);

        String className = joinPoint.getSignature().getDeclaringTypeName();
        log.info("执行的类名：({} 方法{})", className.substring(className.lastIndexOf(".") + 1) + ".java", joinPoint.getSignature().getName());

        // 去除Request 和Response对象
        Object[] args = joinPoint.getArgs();
        Stream<?> stream = args == null ? Stream.empty() : Arrays.stream(args);
        List<Object> logArgs = stream.filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                                     .collect(Collectors.toList());
        log.info("请求url:{}, {} ,参数:{}", pair.left, pair.right, JSON.toJSONString(logArgs, SerializerFeature.IgnoreNonFieldGetter));
        stopWatch.stop();
        stopWatch.start("执行业务逻辑");
        Object result = joinPoint.proceed(joinPoint.getArgs());
        stopWatch.stop();
        String retStr = JSON.toJSONString(result, SerializerFeature.IgnoreNonFieldGetter);
        log.info("\n------请求url-------: {}, -------返回结果-------： \n{}, " +
                "\n-------耗时-------: \n{}", pair.left, retStr, stopWatch.prettyPrint());
        return result;
    }


    private ImmutablePair<String, String> getUrlAndMethod(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();

        for (Annotation declaredAnnotation : declaredAnnotations) {
            InvocationHandler handler = Proxy.getInvocationHandler(declaredAnnotation);
            Class fieldValue = (Class) getFieldValue(handler);

            if (PostMapping.class.getName().equals(fieldValue.getName())) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                return ImmutablePair.of(Arrays.toString(postMapping.value()), "POST");
            } else if (GetMapping.class.getName().equals(fieldValue.getName())) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                return ImmutablePair.of(Arrays.toString(getMapping.value()), "GET");
            } else if (RequestMapping.class.getName().equals(fieldValue.getName())) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String requestMethod = Arrays.stream(requestMapping.method()).findFirst().map(Enum::name).orElse(StringUtils.EMPTY);
                return ImmutablePair.of(Arrays.toString(requestMapping.value()), requestMethod);
            }
        }
        return ImmutablePair.of(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @SneakyThrows
    private Object getFieldValue(InvocationHandler handler) {

        Class<? extends InvocationHandler> handlerClass = handler.getClass();
        Field field = handlerClass.getDeclaredField("type");
        field.setAccessible(true);
        return field.get(handler);
    }
}
