package com.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 2:43 PM
 * @Description: CustomController自定义controller注解
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@Target(ElementType.TYPE)//作用类上
@Retention(RetentionPolicy.RUNTIME)//运行时
@Documented
public @interface CustomController {
    String value() default "";
}
