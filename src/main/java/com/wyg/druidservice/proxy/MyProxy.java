package com.wyg.druidservice.proxy;

import com.wyg.druidservice.dao.base.BaseDaoInterface;
import com.wyg.druidservice.util.BaseDao;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
@Component
public class MyProxy implements InvocationHandler, ApplicationContextAware {
    private static ApplicationContext ctx;
    private Class<?> interfaceClass;
    @Override
    public void setApplicationContext(ApplicationContext contex)
            throws BeansException
    {
        System.out.println("--------------------contex---------"+contex);
        ctx = contex;
    }


    //注意此处变成了static
    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public Object bind(Class<?> cls) {
        this.interfaceClass = cls;
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (proxy instanceof BaseDaoInterface){
            BaseDaoInterface daoInterface = (BaseDaoInterface) proxy;
            Class targetClass = getJdkDynamicProxyTargetClass(proxy);
            BaseDao proxyBean = ctx.getBean(BaseDao.class);
            return proxyBean.handleSearch(targetClass,method.getName(),args[0]);
        }else{
            return method.invoke(proxy,args);
        }

    }

    private Method getRealMethod(Method[] methods){
        for (Method methodInfo : methods) {
            if (methodInfo.getName().endsWith("getTargetSource")) {
                return methodInfo;
            }
        }
        return null;
    }

    private static Class getJdkDynamicProxyTargetClass(Object proxy) {
        try{
            Class superClass = proxy.getClass().getSuperclass();
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            MyProxy myProxy = (MyProxy) h.get(proxy);
            Class clazz = myProxy.interfaceClass;
            return clazz;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


}