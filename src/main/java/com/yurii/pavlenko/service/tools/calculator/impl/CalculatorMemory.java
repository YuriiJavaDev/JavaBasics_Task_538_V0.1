package com.yurii.pavlenko.service.tools.calculator.impl;

public class CalculatorMemory {

    private double memory = 0.0;

    public void addToMemory(double value) {
        this.memory += value;
    }

    public void clearMemory() {
        this.memory = 0.0;
    }

    public double getMemoryValue() {
        return this.memory;
    }
}