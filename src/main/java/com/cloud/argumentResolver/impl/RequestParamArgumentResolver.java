package com.cloud.argumentResolver.impl;

import com.cloud.annotation.CustomRequestParam;
import com.cloud.annotation.CustomService;
import com.cloud.argumentResolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 4:44 PM
 * @Description:
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@CustomService("requestParamArgumentResolver")
public class RequestParamArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int paramIndex, Method method) {
        // type = class java.lang.String
        // @CustomRequestParam("name")String name
        //获取当前方法的参数
        Annotation[][] an = method.getParameterAnnotations();
        Annotation[] paramAns = an[paramIndex];

        for (Annotation paramAn : paramAns) {
            //判断传进的paramAn.getClass()是不是 CustomRequestParam 类型
            if (CustomRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object argumentResolver(HttpServletRequest request,
                                   HttpServletResponse response, Class<?> type, int paramIndex,
                                   Method method) {

        //获取当前方法的参数
        Annotation[][] an = method.getParameterAnnotations();
        Annotation[] paramAns = an[paramIndex];

        for (Annotation paramAn : paramAns) {
            //判断传进的paramAn.getClass()是不是 CustomRequestParam 类型
            if (CustomRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                CustomRequestParam customRequestParam = (CustomRequestParam) paramAn;
                String value = customRequestParam.value();

                return request.getParameter(value);
            }
        }
        return null;
    }
}
