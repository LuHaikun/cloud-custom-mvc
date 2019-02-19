package com.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 3:01 PM
 * @Description: CustomRequestParam 自定义请求参数注解
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomRequestParam {
    String value() default "";
}
