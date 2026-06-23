package com.yurii.pavlenko.controller.tools.currency;

import com.yurii.pavlenko.service.tools.currency.CurrencyService;
import com.yurii.pavlenko.ui.panels.tools.CurrencyConverterPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    private CurrencyConverterPanel mockView;
    private CurrencyService mockService;
    private CurrencyController controller;

    @BeforeEach
    void setUp() {
        mockView = mock(CurrencyConverterPanel.class);
        mockService = mock(CurrencyService.class);
        controller = new CurrencyController(mockView, mockService);
    }

    @Test
    void actionPerformedShouldShowErrorIfAmountEmpty() {
        when(mockView.getAmountInput()).thenReturn("");

        controller.actionPerformed(new ActionEvent(new JButton(), 0, "Convert"));

        verify(mockView).displayError("Please enter amount");
    }

    @Test
    void actionPerformedShouldShowErrorIfAmountNegative() {
        when(mockView.getAmountInput()).thenReturn("-10");

        controller.actionPerformed(new ActionEvent(new JButton(), 0, "Convert"));

        verify(mockView).displayError("Amount must be positive");
    }
}