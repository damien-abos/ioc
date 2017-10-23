package com.example;

import com.example.annotation.Bean;
import com.example.annotation.ReferenceBean;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
                    Object instance = clazz.newInstance();
                    this.context.put(bean.value(), instance);
                } catch (InstantiationException | IllegalAccessException e) {
                    // ignore exception
                }
            }
        }
        // now resolve reference bean
        for (Object o : this.context.values()) {
            Class<?> clazz = o.getClass();
            for (Field field: clazz.getDeclaredFields()) {
                ReferenceBean referenceBean = field.getAnnotation(ReferenceBean.class);
                if (referenceBean != null) {
                    field.setAccessible(true);
                    try {
                        field.set(o, this.context.get(referenceBean.value()));
                    } catch (IllegalAccessException e) {
                        // ignore exception
                    }
                }
            }
        }
    }
}