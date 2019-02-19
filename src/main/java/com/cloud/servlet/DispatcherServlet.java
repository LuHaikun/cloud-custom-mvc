package com.cloud.servlet;

import com.cloud.annotation.*;
import com.cloud.controller.CloudController;
import com.cloud.handleAdapter.HandlerAdapterService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @Author: luhk
 * @Email lhk2014@163.com
 * @Date: 2018/12/13 2:36 PM
 * @Description:
 * @Created with cloud-custom-mvc
 * @Version: 1.0
 */
public class DispatcherServlet extends HttpServlet{

    List<String> classNames = new ArrayList<String>();
    Map<String,Object> beans = new HashMap<String,Object>();
    Map<String,Object> urlMap = new HashMap<String,Object>();

    public DispatcherServlet() {
        System.out.println("DispatchServlet()........");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("init()............");
        // 1.扫描需要的实例化的类
        scanBasePackage("com.cloud");
        System.out.println("当前文件下所有的class类.......");
        for(String name: classNames) {
            System.out.println(name);
        }

        // 2.实例化
        doInstance();
        System.out.println("当前实例化的对象信息.........");
        for(Map.Entry<String, Object> map: beans.entrySet()) {
            System.out.println("key:" + map.getKey() + "; value:" + map.getValue());
        }
        //3.将IOC容器中的service对象设置给controller层定义的field上
        doAutowired();

        //4.建立path与method的映射关系
        handleMapping();
        System.out.println("Controller层的path和方法映射.........");
        for(Map.Entry<String, Object> map: urlMap.entrySet()) {
            System.out.println("key:" + map.getKey() + "; value:" + map.getValue());
        }
    }

    private void scanBasePackage(String basePackagee) {
        //url =>  basePackagee文件所在系统的路径
        URL url = this.getClass().getClassLoader().getResource("/" + basePackagee.replaceAll("\\.", "/"));
        String fileurl = url.getFile();
        File file = new File(fileurl);
        String[] fileList = file.list();
        for (String path : fileList) {
            File thisFile = new File(fileurl + path);
            // 如果当前是目录，则递归
            if (thisFile.isDirectory()) {
                scanBasePackage(basePackagee + "." + path);
                // 如果是文件，则直接添加到classNames
            } else {
                classNames.add(basePackagee + "." + path);
            }
        }
    }

    // 通过存储的classnames的类字符串来反射实例化对象，并存储与beans的Map中
    // com.cloud.annotation.CustomController.class => com.cloud.annotation.CustomController
    private void doInstance() {
        if(classNames.isEmpty()) {
            System.out.println("doScanner Fail....");
            return;
        }
        // 开始实例化对象,通过反射来实现
        for(String className : classNames){
            String cn = className.replace(".class","");
            try {
                Class<?> clazz = Class.forName(cn);
                // 判断当前类是否有注解CustomController类，获取设置的CustomeRequestMapping的值
                if(clazz.isAnnotationPresent(CustomController.class)){
                   Object instance = clazz.newInstance();
                   CustomRequestMapping customRequestMapping = clazz.getAnnotation(CustomRequestMapping.class);
                   String key = customRequestMapping.value();
                   beans.put(key,instance);
                   // 判断当前的类是否有注解CustomService（考虑Service层），获取值
                } else if (clazz.isAnnotationPresent(CustomService.class)){
                    CustomService cs = clazz.getAnnotation(CustomService.class);
                    Object instance = clazz.newInstance();
                    String key = cs.value();
                    beans.put(key,instance);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void doAutowired() {
        if(beans.isEmpty()) {
            System.out.println("no class is instance......");
            return;
        }
        for (Map.Entry<String,Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            // 如果当前是CustomController类，则获取类中定义的field来设置其对象
            if (clazz.isAnnotationPresent(CustomController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    // 如果当前的成员变量使用注解CustomAutowired进行处理
                    if (field.isAnnotationPresent(CustomAutowired.class)) {
                        CustomAutowired customAutowired = field.getAnnotation(CustomAutowired.class);
                        String key = customAutowired.value();
                        field.setAccessible(true);
                        try {
                            field.set(instance,beans.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void handleMapping() {
        if(beans.isEmpty()) {
            System.out.println("no class is instance......");
            return;
        }
        for (Map.Entry<String,Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(CustomController.class)) {
                // 获取类上的路径
                CustomRequestMapping classCustomRequestMapping = clazz.getAnnotation(CustomRequestMapping.class);
                String classPath = classCustomRequestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    CustomRequestMapping methodCustomRequestMapping = method.getAnnotation(CustomRequestMapping.class);
                    if(methodCustomRequestMapping !=null) {
                        String key = methodCustomRequestMapping.value();
                        urlMap.put(classPath+key,method);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String methodKey = uri.replace(context,"");
        Method method = (Method) urlMap.get(methodKey);
        CloudController instance = (CloudController) beans.get("/"+methodKey.split("/")[1]);
        // 处理参数使用反射调用实现的代码为：method.invoke(instance, args); 其中涉及的参数变量为args，目前的实现中请求的参数为HttpServletRequest request, HttpServletResponse response,
        // @CustomRequestParam("name")String name, @CustomRequestParam("age")String age
        //可以通过Annotation[][] annotations = method.getParameterAnnotations();获取所有的参数的注解 与Class<?>[] paramTypes = method.getParameterTypes();获取所有参数的类型进行遍历将args赋值，然后再通过上面的反射进行调用
        HandlerAdapterService handlerAdapter = (HandlerAdapterService) beans.get("customHandlerAdapter");
        Object[] args = handlerAdapter.handle(req, resp, method, beans);
//        Object[] args = getRequestParam(req, resp, method, beans);
        try {
            method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object[] getRequestParam(HttpServletRequest req, HttpServletResponse resp,
                                 Method method) {
        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        //保存参数值
        Object [] paramValues= new Object[parameterTypes.length];

        //方法的参数列表
        for (int i = 0; i<parameterTypes.length; i++){
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();

            if (requestParam.equals("HttpServletRequest")){
                //参数类型已明确，这边强转类型
                paramValues[i]=req;
                continue;
            }else if (requestParam.equals("HttpServletResponse")){
                paramValues[i]=resp;
                continue;
            }

            //获取当前方法的参数
            Annotation[][] an = method.getParameterAnnotations();
            Annotation[] paramAns = an[i];

            for (Annotation paramAn : paramAns) {
                //判断传进的paramAn.getClass()是不是 CustomRequestParam 类型
                if (CustomRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                    CustomRequestParam customRequestParam = (CustomRequestParam) paramAn;
                    String value = customRequestParam.value();
                    paramValues[i] = req.getParameter(value);
                }
            }
        }
        return paramValues;
    }
}
