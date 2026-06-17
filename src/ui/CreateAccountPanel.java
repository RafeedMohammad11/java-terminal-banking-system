package ui;

import exception.DuplicateAccountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;

import javax.swing.*;
import java.awt.*;

public class CreateAccountPanel extends JPanel {

    private final JTextField  accNumField   = new JTextField();
    private final JTextField  nameField     = new JTextField();
    private final JTextField  emailField    = new JTextField();
    private final JTextField  phoneField    = new JTextField();
    private final JTextField  balanceField  = new JTextField();
    private final JTextField  extraField    = new JTextField(); // interest or overdraft
    private final JComboBox<String> typeBox = new JComboBox<>(
            new String[]{"Savings Account", "Current Account"});
    private final JLabel extraLabel         = new JLabel("Interest Rate (%):");

    public CreateAccountPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Open New Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        form.add(new JLabel("Account Number:"));  form.add(accNumField);
        form.add(new JLabel("Holder Name:"));     form.add(nameField);
        form.add(new JLabel("Email:"));           form.add(emailField);
        form.add(new JLabel("Phone:"));           form.add(phoneField);
        form.add(new JLabel("Initial Balance:")); form.add(balanceField);
        form.add(new JLabel("Account Type:"));    form.add(typeBox);
        form.add(extraLabel);                     form.add(extraField);

        // Swap label when type changes
        typeBox.addActionListener(e -> {
            if (typeBox.getSelectedIndex() == 0) {
                extraLabel.setText("Interest Rate (%):");
            } else {
                extraLabel.setText("Overdraft Limit (BDT):");
            }
        });

        add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton createBtn = new JButton("Create Account");
        JButton clearBtn  = new JButton("Clear");
        JButton backBtn   = new JButton("Back to Dashboard");
        btnRow.add(createBtn);
        btnRow.add(clearBtn);
        btnRow.add(backBtn);
        add(btnRow, BorderLayout.SOUTH);

        // Actions
        createBtn.addActionListener(e -> handleCreate(frame));
        clearBtn.addActionListener(e -> clearFields());
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
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
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}