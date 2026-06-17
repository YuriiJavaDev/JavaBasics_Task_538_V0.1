package main.com.yurii.pavlenko.ui.panels.tools;

import main.com.yurii.pavlenko.controller.tools.currency.CurrencyController;
import main.com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import main.com.yurii.pavlenko.utils.CurrencyApiConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Visual panel component for currency calculations.
 * Places the exchange vector icon on the left and the result text on the right
 * on a single horizontal line inside the display card.
 */
public class CurrencyConverterPanel extends JPanel {

    private JComboBox<String> comboFrom;
    private JComboBox<String> comboTo;
    private JTextField txtAmount;
    private JButton btnConvert;

    private JPanel displayCard;
    private JLabel lblGraphicIcon;
    private JLabel lblResultText;

    public CurrencyConverterPanel() {
        setBorder(BorderFactory.createTitledBorder("Currency Converter"));
        setLayout(new BorderLayout(5, 5));

        initInputFields();
        initDisplayCard();
    }

    private void initInputFields() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        Font barFont = new Font("Segoe UI", Font.PLAIN, 12);

        txtAmount = new JTextField("100", 6);
        txtAmount.setFont(barFont);

        comboFrom = new JComboBox<>(CurrencyApiConfig.SUPPORTED_CURRENCIES);
        comboFrom.setSelectedItem("USD");
        comboFrom.setFont(barFont);

        JLabel lblArrow = new JLabel("➔");
        lblArrow.setForeground(Color.GRAY);

        comboTo = new JComboBox<>(CurrencyApiConfig.SUPPORTED_CURRENCIES);
        comboTo.setSelectedItem("ILS");
        comboTo.setFont(barFont);

        btnConvert = new JButton("Convert");
        btnConvert.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnConvert.setFocusPainted(false);

        controlPanel.add(new JLabel("Amt:"));
        controlPanel.add(txtAmount);
        controlPanel.add(comboFrom);
        controlPanel.add(lblArrow);
        controlPanel.add(comboTo);
        controlPanel.add(btnConvert);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void initDisplayCard() {
        displayCard = new JPanel(new GridBagLayout());
        displayCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 15, 12, 15),
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1, true)
        ));
        displayCard.setBackground(new Color(250, 250, 253));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER; // Идеальное центрирование по вертикали
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;

        // 1. Левая невидимая распорка (заставляет всё сдвигаться к центру по горизонтали)
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        displayCard.add(Box.createHorizontalGlue(), gbc);

        // 2. Векторное цветное кольцо обмена
        lblGraphicIcon = new JLabel(createExchangeVectorIcon());
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 15); // Зазор 15px справа от кольца до текста
        displayCard.add(lblGraphicIcon, gbc);

        // 3. Текстовое поле результата
        lblResultText = new JLabel("Enter amount and press Convert");
        lblResultText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblResultText.setForeground(new Color(50, 50, 60));
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        displayCard.add(lblResultText, gbc);

        // 4. Правая невидимая распорка (уравновешивает левую сторону)
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        displayCard.add(Box.createHorizontalGlue(), gbc);

        // Добавляем displayCard напрямую в центр БЕЗ врапперов, чтобы рамка была во всю ширину
        add(displayCard, BorderLayout.CENTER);
    }

    public void updateConversionDisplay(CurrencyModelDTO data) {
        String resultStr = String.format("%.2f", data.getCalculatedResult());
        String amountStr = String.format("%.2f", data.getAmount());

        lblResultText.setText(amountStr + " " + data.getBaseCurrency() + " = " + resultStr + " " + data.getTargetCurrency());
        displayCard.setBackground(new Color(245, 248, 253));
    }

    public void displayError(String message) {
        lblResultText.setText(message);
        displayCard.setBackground(new Color(255, 243, 243));
    }

    /**
     * Рисует кастомную цветную иконку обмена валют с утолщенными линиями (BasicStroke 5).
     */
    private ImageIcon createExchangeVectorIcon() {
        BufferedImage img = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Зеленая верхняя стрелка
        g2.setColor(new Color(40, 165, 100));
        g2.drawArc(10, 12, 26, 24, 45, 180);
        g2.fillPolygon(new int[]{32, 39, 33}, new int[]{6, 14, 20}, 3);

        // Синяя нижняя стрелка
        g2.setColor(new Color(50, 120, 220));
        g2.drawArc(12, 12, 26, 24, 225, 180);
        g2.fillPolygon(new int[]{15, 9, 16}, new int[]{42, 34, 28}, 3);

        g2.dispose();
        return new ImageIcon(img);
    }

    public void registerController(CurrencyController controller) {
        btnConvert.addActionListener(controller);
    }

    public String getBaseCurrencyInput() { return (String) comboFrom.getSelectedItem(); }
    public String getTargetCurrencyInput() { return (String) comboTo.getSelectedItem(); }
    public String getAmountInput() { return txtAmount.getText().trim(); }
    public void setButtonsEnabled(boolean enabled) { btnConvert.setEnabled(enabled); }
}