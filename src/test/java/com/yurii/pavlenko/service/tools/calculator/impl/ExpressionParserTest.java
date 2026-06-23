package com.yurii.pavlenko.service.tools.calculator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

    private ExpressionParser parser;

    @BeforeEach
    void setUp() {
        MathOperationEvaluator evaluator = new MathOperationEvaluator();
        parser = new ExpressionParser(evaluator);
    }

    @ParameterizedTest(name = "Expression: {0} expected to be {1}")
    @CsvSource({
            "'2 + 2', 4.0",
            "'10 - 3', 7.0",
            "'4 * 5', 20.0",
            "'12 / 4', 3.0",
            "' 2   +  3 * 4 ', 14.0",
            "'-5 + 3', -2.0",
            "'+5 - -3', 8.0",
            "'(2 + 3) * 4', 20.0",
            "'2 + 3 * 4', 14.0",
            "'10 / (2 + 3)', 2.0",
            "'50 mod 3', 2.0",
            "'10 mod 5', 0.0",
            "'22 mod 4', 2.0",
            "'2 ^ 3', 8.0",
            "'2 + 3 ^ 2', 11.0",
            "'(2 + 3) ^ 2', 25.0",
            "'sqrt(9)', 3.0",
            "'abs(-4.5)', 4.5"
    })
    void shouldCorrectlyParseAndCalculateValidExpressions(String expression, double expectedResult) {
        double actualResult = parser.parse(expression, true);
        assertEquals(expectedResult, actualResult, 0.0001, "Failed calculation for expression: " + expression);
    }

    @Test
    void shouldThrowExceptionWhenDividingByZero() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                parser.parse("10 / 0", false)
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenModByZero() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                parser.parse("50 mod 0", false)
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionOnUnexpectedCharacter() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                parser.parse("5 + 3 # 2", false)
        );
        assertTrue(exception.getMessage().contains("Unexpected character"));
    }

    @Test
    void shouldThrowExceptionWhenMissingParenthesisAfterFunction() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                parser.parse("sin 30", false)
        );
        assertEquals("Missing parenthesis after function: sin", exception.getMessage());
    }
}