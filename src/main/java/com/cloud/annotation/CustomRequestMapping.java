package com.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 2:49 PM
 * @Description: CustomRequestMapping自定义请求映射注解
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomRequestMapping {
    String value() default "";
}
