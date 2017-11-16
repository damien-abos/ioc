package com.example.beans;

import com.example.annotation.Bean;

@Bean("beanA")
public class BeanA implements IBeanA {
    private String value;

    public BeanA() {
        this.value = "a";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}