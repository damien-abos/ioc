package com.example.beans;

import com.example.annotation.Bean;

@Bean("classA")
public class ClassA {
    private String value;

    public ClassA() {
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