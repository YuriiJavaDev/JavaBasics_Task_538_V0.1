package com.yurii.pavlenko.service.tools.calculator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MathOperationEvaluatorTest {

    private MathOperationEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new MathOperationEvaluator();
    }

    @ParameterizedTest(name = "Binary operation: {0} {2} {1} = {3}")
    @CsvSource({
            "5.0,   3.0,   '+',   8.0",
            "10.0,  4.0,   '-',   6.0",
            "3.0,   3.0,   '*',   9.0",
            "7.0,   2.0,   '/',   3.5",
            "50.0,  3.0,   'mod', 2.0",
            "2.0,   3.0,   'x^y', 8.0"
    })
    void shouldCorrectlyCalculateBinaryOperations(double first, double second, String operator, double expected) {
        double actual = evaluator.calculateBinary(first, second, operator);
        assertEquals(expected, actual, 0.0001);
    }

    @ParameterizedTest(name = "Unary operation: {1}({0}) = {2}")
    @CsvSource({
            "45.0,  '%',    0.45",
            "16.0,  'sqrt', 4.0",
            "8.0,   'cbrt', 2.0",
            "4.0,   'x²',   16.0",
            "3.0,   'x³',   27.0",
            "3.0,   '10^x', 1000.0",
            "5.0,   '1/x',  0.2",
            "-9.9,  'abs',  9.9",
            "5.0,   'n!',   120.0",
            "0.0,   'n!',   1.0",
            "0.0,   'exp',  1.0"
    })
    void shouldCorrectlyCalculateUnaryOperations(double operand, String operation, double expected) {
        double actual = evaluator.calculateUnary(operand, operation, false);
        assertEquals(expected, actual, 0.0001);
    }

    @ParameterizedTest(name = "Trigonometry in degrees: {1}({0}) = {2}")
    @CsvSource({
            "30.0,  'sin',  0.5",
            "60.0,  'cos',  0.5",
            "45.0,  'tan',  1.0",
            "0.5,   'asin', 30.0",
            "0.5,   'acos', 60.0",
            "1.0,   'atan', 45.0"
    })
    void shouldCorrectlyCalculateTrigonometryInDegrees(double angle, String operation, double expected) {
        double actual = evaluator.calculateUnary(angle, operation, false);
        assertEquals(expected, actual, 0.0001);
    }

    @ParameterizedTest(name = "Trigonometry in radians: {1}({0}) = {2}")
    @CsvSource({
            "0.0,                 'sin',  0.0",
            "0.0,                 'cos',  1.0",
            "0.7853981633974483,  'tan',  1.0"
    })
    void shouldCorrectlyCalculateTrigonometryInRadians(double angle, String operation, double expected) {
        double actual = evaluator.calculateUnary(angle, operation, true);
        assertEquals(expected, actual, 0.0001);
    }

    @Test
    void shouldThrowExceptionWhenDividingByZeroInBinary() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateBinary(10, 0, "/")
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForUndefinedTan() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(90, "tan", false)
        );
        assertEquals("Undefined (Infinity)", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidAsinInput() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(1.01, "asin", false)
        );
        assertEquals("Invalid asin input range [-1, 1]", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidAcosInput() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(-1.01, "acos", false)
        );
        assertEquals("Invalid acos input range [-1, 1]", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidLnInput() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(0, "ln", false)
        );
        assertEquals("Invalid log input", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidLogInput() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(-5, "log", false)
        );
        assertEquals("Invalid log input", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidSqrtInput() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(-1, "sqrt", false)
        );
        assertEquals("Invalid square root input", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInverseDivisionByZero() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(0, "1/x", false)
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeFactorial() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                evaluator.calculateUnary(-5, "n!", false)
        );
        assertEquals("Invalid factorial input", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForUnknownBinaryOperator() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                evaluator.calculateBinary(2, 2, "unknown_op")
        );
        assertEquals("Unknown binary operator: unknown_op", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForUnknownUnaryOperator() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                evaluator.calculateUnary(5, "unknown_unary", false)
        );
        assertEquals("Unknown unary operation: unknown_unary", exception.getMessage());
    }
}