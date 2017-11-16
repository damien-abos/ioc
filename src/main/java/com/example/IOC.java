package com.example;

import com.example.annotation.Bean;
import com.example.annotation.ReferenceBean;

import java.io.File;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IOC {

    private Map<String, Object> context = new HashMap<>();

    public static void main(String[] args) {
        IOC ioc = new IOC();
        ioc.init();
        // list
        for (Object o : ioc.context.values()) {
            System.out.println(o);
        }
    }

    // Recursively find classes in classpath
    public List<Class<?>> findClasses(String resourceName, List<Class<?>> classes) {
        // reuse classes or init the first time
        List<Class<?>> foundClasses = classes;
        if (classes == null) {
            foundClasses = new LinkedList<>();
        }
        // retrieve the resource
        String filename = Thread.currentThread().getContextClassLoader().getResource(resourceName).getFile();
        File file = new File(filename);
        if (file.isDirectory()) {
            // when directory then loop over children
            for (String child : file.list()) {
                String childResourceName = resourceName;
                if (!resourceName.isEmpty()) {
                    // build a valid path adding slashes
                    childResourceName += "/";
                }
                childResourceName += child;
                // the recursive invocation
                findClasses(childResourceName, foundClasses);
            }
        } else if (file.isFile() && filename.endsWith(".class")) {
            // when .class file then add the corresponding class the list
            String className = resourceName.substring(0, resourceName.length() - 6).replaceAll("/", ".");
            try {
                Class<?> clazz = Class.forName(className);
                foundClasses.add(clazz);
            } catch (ClassNotFoundException e) {
                // ignore exception
            }
        }
        return foundClasses;
    }

    public void init() {
        // look for classes in classpath
        List<Class<?>> foundClasses = findClasses("", null);
        // loop over result
        // to create beans
        for (Class<?> clazz : foundClasses) {
            Bean bean = clazz.getAnnotation(Bean.class);
            if (bean != null) {
                try {
                    String name = bean.value().isEmpty() ? clazz.getSimpleName() : bean.value();
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Object proxyInstance = Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new BeanInvocationHandler(instance, name));
                    this.context.put(name, proxyInstance);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    // ignore exception
                }
            }
        }
        // now resolve reference bean
        for (Object o : this.context.values()) {
            Object target = ((BeanInvocationHandler) Proxy.getInvocationHandler(o)).target;
            Class<?> clazz = target.getClass();
            for (Field field: clazz.getDeclaredFields()) {
                ReferenceBean referenceBean = field.getAnnotation(ReferenceBean.class);
                if (referenceBean != null) {
                    field.setAccessible(true);
                    try {
                        field.set(target, this.context.get(referenceBean.value()));
                    } catch (IllegalAccessException e) {
                        // ignore exception
                    }
                }
            }
        }
    }

    private static class BeanInvocationHandler implements InvocationHandler {

        private final Object target;
        private final String name;

        public BeanInvocationHandler(Object target, String name) {
            this.target = target;
            this.name = name;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                System.out.println("before:"+name+":"+method);
                return method.invoke(target, args);
            } finally {
                System.out.println("after:"+name+":"+method);
            }
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BeanInvocationHandler) {
                   return target.equals(((BeanInvocationHandler)obj).target);
            }
            return target.equals(obj);
        }
    }
}