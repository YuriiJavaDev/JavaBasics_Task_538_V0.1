package com.yurii.pavlenko.model.tools.calculator;

/**
 * Model component that stores the state and data transitions of the calculator application.
 */
public class CalculatorModel {

    private String currentInput;
    private double lastResult;
    private boolean isRadians;
    private boolean isAwaitingNewInput;
    private boolean isCalculatedOrMemory;

    public CalculatorModel() {
        this.isRadians = false;
        reset();
    }

    /**
     * Restores the model to its default zero state.
     */
    public void reset() {
        this.currentInput = "0";
        this.lastResult = 0;
        this.isAwaitingNewInput = true;
        this.isCalculatedOrMemory = false;
    }

    // Getters and Setters following Clean Code conventions

    public String getCurrentInput() {
        return currentInput;
    }

    public void setCurrentInput(String currentInput) {
        this.currentInput = currentInput;
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

    public boolean isCalculatedOrMemory() {
        return isCalculatedOrMemory;
    }

    public void setCalculatedOrMemory(boolean calculatedOrMemory) {
        this.isCalculatedOrMemory = calculatedOrMemory;
    }
}