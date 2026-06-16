package main.com.yurii.pavlenko.controller.tools.currency;

import main.com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import main.com.yurii.pavlenko.service.tools.currency.CurrencyService;
import main.com.yurii.pavlenko.ui.panels.tools.CurrencyConverterPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller orchestrating interaction between the UI panel and the currency conversion service.
 */
public class CurrencyController implements ActionListener {

    private final CurrencyConverterPanel view;
    private final CurrencyService service;

    public CurrencyController(CurrencyConverterPanel view, CurrencyService service) {
        this.view = view;
        this.service = service;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String from = view.getBaseCurrencyInput();
        String to = view.getTargetCurrencyInput();
        String amountStr = view.getAmountInput();

        // Проверка на пустой ввод
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

        // Используем SwingWorker для плавной работы UI без фризов
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