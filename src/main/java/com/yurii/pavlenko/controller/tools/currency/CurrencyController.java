package com.yurii.pavlenko.controller.tools.currency;

import com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import com.yurii.pavlenko.service.tools.currency.CurrencyService;
import com.yurii.pavlenko.ui.panels.tools.CurrencyConverterPanel;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CurrencyController implements ActionListener {

    private final CurrencyConverterPanel view;
    private final CurrencyService service;

    public CurrencyController(CurrencyConverterPanel view, CurrencyService service) {
        this.view = view;
        this.service = service;
        this.view.registerController(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String from = view.getBaseCurrencyInput();
        String to = view.getTargetCurrencyInput();
        String amountStr = view.getAmountInput();

        if (amountStr.isEmpty()) {
            view.displayError("Please enter amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                view.displayError("Amount must be positive");
                return;
            }
        } catch (NumberFormatException ex) {
            view.displayError("Invalid number format");
            return;
        }

        view.setButtonsEnabled(false);

        SwingWorker<CurrencyModelDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected CurrencyModelDTO doInBackground() throws Exception {
                return service.convert(from, to, amount);
            }

            @Override
            protected void done() {
                try {
                    CurrencyModelDTO result = get();
                    view.updateConversionDisplay(result);
                } catch (Exception ex) {
                    view.displayError("Network connection error");
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }
}