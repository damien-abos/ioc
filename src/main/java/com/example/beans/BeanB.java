package com.example.beans;

import com.example.annotation.Bean;
import com.example.annotation.ReferenceBean;

@Bean("beanB")
public class BeanB implements IBeanB {
    private String value;
    @ReferenceBean("beanA")
    private IBeanA beanA;

    public BeanB() {
        this.value = "b";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IBeanA getBeanA() {
        return beanA;
    }

    public void setBeanA(IBeanA beanA) {
        this.beanA = beanA;
    }

    @Override
    public String toString() {
        return value + (beanA != null ? beanA.toString() : "<null>");
    }
}