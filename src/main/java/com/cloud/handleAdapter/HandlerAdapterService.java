package com.cloud.handleAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Created with IntelliJ IDEA.
 * @Description: cloud-custom-mvc
 * @Author: luhk
 * @Date: 2018-12-13
 * @Time: 4:38 PM
 * @Version: 1.0
 */
public interface HandlerAdapterService {
    public Object[] handle(HttpServletRequest req, HttpServletResponse resp,
                           Method method, Map<String, Object> beans);
}
