package ui;

import exception.DuplicateAccountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import util.AccountNumberGenerator;
import util.InputValidator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CreateAccountPanel extends JPanel {

    private final JLabel            accNumDisplay = new JLabel();
    private final JTextField        nameField     = UITheme.styledField();
    private final JTextField        emailField    = UITheme.styledField();
    private final JTextField        phoneField    = UITheme.styledField();
    private final JTextField        balanceField  = UITheme.styledField();
    private final JTextField        extraField    = UITheme.styledField();
    private final JComboBox<String> typeBox       =
            new JComboBox<>(new String[]{"Savings Account", "Current Account"});
    private final JLabel extraLabel    = new JLabel("Interest Rate (%):");

    // Error labels
    private final JLabel nameError    = errorLabel();
    private final JLabel emailError   = errorLabel();
    private final JLabel phoneError   = errorLabel();
    private final JLabel balanceError = errorLabel();

    private JLabel errorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        label.setForeground(UITheme.FIELD_ERROR);
        label.setVisible(false);
        return label;
    }

    public CreateAccountPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // ── Top bar ───────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.HEADER_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel topTitle = new JLabel("Open New Account");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topTitle.setForeground(Color.WHITE);
        topBar.add(topTitle, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // ── Field styling ─────────────────────────────────────
        accNumDisplay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        accNumDisplay.setForeground(UITheme.PRIMARY);

        typeBox.setFont(UITheme.FONT_BODY);
        typeBox.setPreferredSize(new Dimension(0, 36));
        extraLabel.setFont(UITheme.FONT_BODY);
        extraLabel.setForeground(UITheme.TEXT_DARK);

        // ── Card ──────────────────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(12, 2, 12, 4));
        card.setPreferredSize(new Dimension(560, 460));

        card.add(styledLabel("Account Number:")); card.add(accNumDisplay);
        card.add(new JLabel(""));                 card.add(new JLabel(" "));

        card.add(styledLabel("Holder Name:"));    card.add(nameField);
        card.add(new JLabel(""));                 card.add(nameError);

        card.add(styledLabel("Email:"));          card.add(emailField);
        card.add(new JLabel(""));                 card.add(emailError);

        card.add(styledLabel("Phone:"));          card.add(phoneField);
        card.add(new JLabel(""));                 card.add(phoneError);

        card.add(styledLabel("Initial Balance:")); card.add(balanceField);
        card.add(new JLabel(""));                  card.add(balanceError);

        card.add(styledLabel("Account Type:"));   card.add(typeBox);
        card.add(extraLabel);                     card.add(extraField);

        typeBox.addActionListener(e -> extraLabel.setText(
                typeBox.getSelectedIndex() == 0
                        ? "Interest Rate (%):"
                        : "Overdraft Limit (BDT):"
        ));

        center.add(card);
        add(center, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(UITheme.BG);
        JButton clearBtn  = UITheme.ghostButton("Clear");
        JButton backBtn   = UITheme.ghostButton("Dashboard");
        JButton createBtn = UITheme.primaryButton("Create Account");
        bottom.add(clearBtn);
        bottom.add(backBtn);
        bottom.add(createBtn);
        add(bottom, BorderLayout.SOUTH);

        // ── Live validation listeners ─────────────────────────
        attachLiveValidator(nameField,    nameError,    "name");
        attachLiveValidator(emailField,   emailError,   "email");
        attachLiveValidator(phoneField,   phoneError,   "phone");
        attachLiveValidator(balanceField, balanceError, "balance");

        // ── Actions ───────────────────────────────────────────
        refreshAccountNumber();

        createBtn.addActionListener(e -> handleCreate(frame));
        clearBtn.addActionListener(e -> {
            clearFields();
            refreshAccountNumber();
        });
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
    }

    // ── Live validation ───────────────────────────────────────

    private void attachLiveValidator(JTextField field, JLabel errorLbl, String type) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validate(field, errorLbl, type); }
            public void removeUpdate(DocumentEvent e)  { validate(field, errorLbl, type); }
            public void changedUpdate(DocumentEvent e) { validate(field, errorLbl, type); }
        });
    }

    private void validate(JTextField field, JLabel errorLbl, String type) {
        String value = field.getText().trim();

        if (value.isBlank()) {
            UITheme.resetField(field, errorLbl);
            return;
        }

        String error = switch (type) {
            case "name"    -> InputValidator.validateAccountName(value);
            case "email"   -> InputValidator.validateEmail(value);
            case "phone"   -> InputValidator.validatePhone(value);
            case "balance" -> InputValidator.validateAmount(value);
            default        -> null;
        };

        if (error != null) {
            UITheme.setFieldError(field, errorLbl, error);
        } else {
            UITheme.setFieldSuccess(field, errorLbl);
        }
    }

    // ── Full submit validation ────────────────────────────────

    private boolean validateAll() {
        boolean valid = true;

        String nameErr = InputValidator.validateAccountName(nameField.getText().trim());
        if (nameErr != null) {
            UITheme.setFieldError(nameField, nameError, nameErr);
            valid = false;
        }

        String emailErr = InputValidator.validateEmail(emailField.getText().trim());
        if (emailErr != null) {
            UITheme.setFieldError(emailField, emailError, emailErr);
            valid = false;
        }

        String phoneErr = InputValidator.validatePhone(phoneField.getText().trim());
        if (phoneErr != null) {
            UITheme.setFieldError(phoneField, phoneError, phoneErr);
            valid = false;
        }

        String balanceErr = InputValidator.validateAmount(balanceField.getText().trim());
        if (balanceErr != null) {
            UITheme.setFieldError(balanceField, balanceError, balanceErr);
            valid = false;
        }

        return valid;
    }

    // ── Handle create ─────────────────────────────────────────

    private void handleCreate(MainFrame frame) {
        if (!validateAll()) {
            showError("Please fix the highlighted fields before submitting.");
            return;
        }

        try {
            String accNum  = accNumDisplay.getText();
            String name    = nameField.getText().trim();
            String email   = emailField.getText().trim();
            String phone   = phoneField.getText().trim();
            double balance = Double.parseDouble(balanceField.getText().trim());
            double extra   = Double.parseDouble(extraField.getText().trim());

            Account account;
            if (typeBox.getSelectedIndex() == 0) {
                account = new SavingsAccount(accNum, name, balance, extra, email, phone);
            } else {
                account = new CurrentAccount(accNum, name, email, phone, balance, extra);
            }

            frame.getBankService().createAccount(account);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nAccount Number: " + accNum,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();
            refreshAccountNumber();
            frame.showPanel("LIST");

        } catch (DuplicateAccountException ex) {
            refreshAccountNumber();
            showError("Number conflict — new number assigned. Please try again.");
        } catch (NumberFormatException ex) {
            showError("Balance and rate/limit must be valid numbers.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────

    public void refreshAccountNumber() {
        accNumDisplay.setText(AccountNumberGenerator.generate());
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        balanceField.setText("");
        extraField.setText("");

        // Reset all borders and error labels
        UITheme.resetField(nameField,    nameError);
        UITheme.resetField(emailField,   emailError);
        UITheme.resetField(phoneField,   phoneError);
        UITheme.resetField(balanceField, balanceError);
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}