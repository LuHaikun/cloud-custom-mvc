package com.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 3:03 PM
 * @Description: CustomAutowired 自定义全局变量注入注解
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomQualifier {
    String value() default "";
}
