package com.cloud.controller;

import com.cloud.annotation.CustomAutowired;
import com.cloud.annotation.CustomController;
import com.cloud.annotation.CustomRequestMapping;
import com.cloud.annotation.CustomRequestParam;
import com.cloud.service.CloudService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 3:15 PM
 * @Description:
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
@CustomController
@CustomRequestMapping("/cloud")
public class CloudController {

    @CustomAutowired("CloudServiceImpl")
    private CloudService cloudService;

    @CustomRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
           @CustomRequestParam("name")String name, @CustomRequestParam("age")String age){
        try {
            PrintWriter writer = response.getWriter();
            String result = cloudService.query(name,age);
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
