package com.example.beans;

import com.example.annotation.Bean;
import com.example.annotation.ReferenceBean;

@Bean("classB")
public class ClassB {
    private String value;
    @ReferenceBean("classA")
    private ClassA classA;

    public ClassB() {
        this.value = "b";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ClassA getClassA() {
        return classA;
    }

    public void setClassA(ClassA classA) {
        this.classA = classA;
    }

    @Override
    public String toString() {
        return value + classA.toString();
    }
}