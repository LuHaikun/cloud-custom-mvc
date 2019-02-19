package com.cloud.service.impl;

import com.cloud.annotation.CustomService;
import com.cloud.service.CloudService;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 3:20 PM
 * @Description: service类
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@CustomService("CloudServiceImpl")
public class CloudServiceImpl implements CloudService {
    @Override
    public String query(String name, String age) {
        return "name:" + name + ";age:" + age;
    }
}
