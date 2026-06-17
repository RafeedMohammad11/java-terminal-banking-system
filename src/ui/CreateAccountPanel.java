package ui;

import exception.DuplicateAccountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import javax.swing.*;
import java.awt.*;

public class CreateAccountPanel extends JPanel {

    private final JTextField accNumField  = UITheme.styledField();
    private final JTextField nameField    = UITheme.styledField();
    private final JTextField emailField   = UITheme.styledField();
    private final JTextField phoneField   = UITheme.styledField();
    private final JTextField balanceField = UITheme.styledField();
    private final JTextField extraField   = UITheme.styledField();
    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{"Savings Account", "Current Account"});
    private final JLabel extraLabel = new JLabel("Interest Rate (%):");

    public CreateAccountPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.HEADER_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel topTitle = new JLabel("Open New Account");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topTitle.setForeground(Color.WHITE);
        topBar.add(topTitle, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // Card
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(8, 2, 12, 14));
        card.setPreferredSize(new Dimension(540, 380));

        typeBox.setFont(UITheme.FONT_BODY);
        typeBox.setPreferredSize(new Dimension(0, 36));
        extraLabel.setFont(UITheme.FONT_BODY);
        extraLabel.setForeground(UITheme.TEXT_DARK);

        card.add(styledLabel("Account Number:")); card.add(accNumField);
        card.add(styledLabel("Holder Name:"));    card.add(nameField);
        card.add(styledLabel("Email:"));          card.add(emailField);
        card.add(styledLabel("Phone:"));          card.add(phoneField);
        card.add(styledLabel("Initial Balance:")); card.add(balanceField);
        card.add(styledLabel("Account Type:"));   card.add(typeBox);
        card.add(extraLabel);                      card.add(extraField);

        typeBox.addActionListener(e -> extraLabel.setText(
                typeBox.getSelectedIndex() == 0
                        ? "Interest Rate (%):"
                        : "Overdraft Limit (BDT):"
        ));

        center.add(card);
        add(center, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(UITheme.BG);
        JButton clearBtn  = UITheme.ghostButton("Clear");
        JButton backBtn   = UITheme.ghostButton("Dashboard");
        JButton createBtn = UITheme.primaryButton("Create Account");
        bottom.add(clearBtn);
        bottom.add(backBtn);
        bottom.add(createBtn);
        add(bottom, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> handleCreate(frame));
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    private void handleCreate(MainFrame frame) {
        try {
            String accNum  = accNumField.getText().trim();
            String name    = nameField.getText().trim();
            String email   = emailField.getText().trim();
            String phone   = phoneField.getText().trim();
            double balance = Double.parseDouble(balanceField.getText().trim());
            double extra   = Double.parseDouble(extraField.getText().trim());

            if (accNum.isBlank() || name.isBlank()) {
                showError("Account number and name are required.");
                return;
            }

            Account account;
            if (typeBox.getSelectedIndex() == 0) {
                account = new SavingsAccount(accNum, name, balance, extra, email, phone);
            } else {
                account = new CurrentAccount(accNum, name, email, phone, balance, extra);
            }

            frame.getBankService().createAccount(account);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            frame.showPanel("LIST");

        } catch (DuplicateAccountException ex) {
            showError("Account already exists: " + ex.getAccountNumber());
        } catch (NumberFormatException ex) {
            showError("Balance and rate/limit must be valid numbers.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        accNumField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        balanceField.setText("");
        extraField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}