package com.yurii.pavlenko.controller.tools.calculator;

public class ResultFormatter {

    public String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value);
        }
        if (Math.abs(value) > 1e14 || (Math.abs(value) < 1e-12 && value != 0)) {
            return String.valueOf(value);
        }
        double scaled = Math.round(value * 1e12) / 1e12;
        if (scaled % 1 == 0) {
            return String.valueOf((long) scaled);
        }
        return String.valueOf(scaled);
    }
}