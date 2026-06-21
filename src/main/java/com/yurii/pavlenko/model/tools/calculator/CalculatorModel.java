package com.yurii.pavlenko.model.tools.calculator;

/**
 * Model component that stores the state and data transitions of the calculator application.
 */
public class CalculatorModel {

    private String currentInput;
    private double firstOperand;
    private String activeOperator;
    private double lastResult;
    private boolean isRadians;
    private boolean isAwaitingNewInput;

    public CalculatorModel() {
        this.isRadians = false;
        reset();
    }

    /**
     * Restores the model to its default zero state.
     */
    public void reset() {
        this.currentInput = "0";
        this.firstOperand = 0;
        this.activeOperator = "";
        this.lastResult = 0;
        this.isAwaitingNewInput = true;
    }

    // Getters and Setters following Clean Code conventions

    public String getCurrentInput() {
        return currentInput;
    }

    public void setCurrentInput(String currentInput) {
        this.currentInput = currentInput;
    }

    public double getFirstOperand() {
        return firstOperand;
    }

    public void setFirstOperand(double firstOperand) {
        this.firstOperand = firstOperand;
    }

    public String getActiveOperator() {
        return activeOperator;
    }

    public void setActiveOperator(String activeOperator) {
        this.activeOperator = activeOperator;
    }

    public double getLastResult() {
        return lastResult;
    }

    public void setLastResult(double lastResult) {
        this.lastResult = lastResult;
    }

    public boolean isRadians() {
        return isRadians;
    }

    public void setRadians(boolean radians) {
        isRadians = radians;
    }

    public boolean isAwaitingNewInput() {
        return isAwaitingNewInput;
    }

    public void setAwaitingNewInput(boolean awaitingNewInput) {
        this.isAwaitingNewInput = awaitingNewInput;
    }
}