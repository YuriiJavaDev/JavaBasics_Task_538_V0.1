package main.com.yurii.pavlenko.ui.panels.tools;

import javax.swing.*;
import java.awt.*;

/**
 * Interactive utility dashboard executing foreign exchange currency conversions.
 */
public class CurrencyConverterPanel extends JPanel {
    public CurrencyConverterPanel() {
        setBorder(BorderFactory.createTitledBorder("Currency Converter"));
        setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("Currency Exchange Placeholder", SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }
}