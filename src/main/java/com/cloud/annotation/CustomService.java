package com.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 2:53 PM
 * @Description: CustomService自定义service注解
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@Target(ElementType.TYPE)//作用类上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomService {
    String value() default "";
}
