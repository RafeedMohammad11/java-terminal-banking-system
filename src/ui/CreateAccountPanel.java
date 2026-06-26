package ui;

import exception.DuplicateAccountException;
import model.Account;
import model.CurrentAccount;
import model.LoanAccount;
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
    private final JTextField        nidField      = UITheme.styledField();
    private final JTextField        addressField  = UITheme.styledField();
    private final JTextField        balanceField  = UITheme.styledField();
    private final JTextField        extraField    = UITheme.styledField();
    private final JComboBox<String> typeBox       =
            new JComboBox<>(new String[]{"Savings Account", "Current Account", "Loan Account"});
    private final JComboBox<String> branchBox     = new JComboBox<>();
    private final JLabel balanceLabel = new JLabel("Initial Balance (BDT):");
    private final JLabel extraLabel   = new JLabel("Interest Rate (%):");


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
        branchBox.setFont(UITheme.FONT_BODY);
        branchBox.setPreferredSize(new Dimension(0, 36));
        balanceLabel.setFont(UITheme.FONT_BODY);
        balanceLabel.setForeground(UITheme.TEXT_DARK);
        extraLabel.setFont(UITheme.FONT_BODY);
        extraLabel.setForeground(UITheme.TEXT_DARK);

        // ── Card ──────────────────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(0, 2, 12, 8));
        card.setPreferredSize(new Dimension(700, 650));

        card.add(styledLabel("Account Number:")); card.add(accNumDisplay);
        card.add(new JLabel(""));                 card.add(new JLabel(" "));

        card.add(styledLabel("Holder Name:"));    card.add(nameField);
        card.add(new JLabel(""));                 card.add(nameError);

        card.add(styledLabel("Email:"));          card.add(emailField);
        card.add(new JLabel(""));                 card.add(emailError);

        card.add(styledLabel("Phone:"));          card.add(phoneField);
        card.add(new JLabel(""));                 card.add(phoneError);

        card.add(styledLabel("NID:"));            card.add(nidField);
        card.add(new JLabel(""));                 card.add(new JLabel(" "));

        card.add(styledLabel("Address:"));        card.add(addressField);
        card.add(new JLabel(""));                 card.add(new JLabel(" "));

        card.add(balanceLabel);                    card.add(balanceField);
        card.add(new JLabel(""));                  card.add(balanceError);

        card.add(styledLabel("Account Type:"));   card.add(typeBox);
        card.add(styledLabel("Branch:"));         card.add(branchBox);
        card.add(extraLabel);                     card.add(extraField);

        typeBox.addActionListener(e -> updateTypeSpecificLabels());

        center.add(card);
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

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
        attachLiveValidator(extraField,   balanceError, "extra");

        // ── Actions ───────────────────────────────────────────
        refreshAccountNumber();
        updateTypeSpecificLabels();

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
            case "balance" -> {
                String amtErr = InputValidator.validateAmount(value);
                if (amtErr != null) yield amtErr;
                if (typeBox.getSelectedIndex() == 0) {
                    try {
                        if (Double.parseDouble(value) < 500)
                            yield "Savings accounts require a minimum of BDT 500";
                    } catch (NumberFormatException ignored) {}
                }
                if (typeBox.getSelectedIndex() == 2) {
                    try {
                        double due = Double.parseDouble(value);
                        String limitText = extraField.getText().trim();
                        if (!limitText.isBlank()) {
                            double limit = Double.parseDouble(limitText);
                            if (due > limit)
                                yield "Amount due cannot exceed loan limit";
                        }
                    } catch (NumberFormatException ignored) {}
                }
                yield null;
            }
            case "extra" -> {
                String amtErr = InputValidator.validateAmount(value);
                if (amtErr != null) yield amtErr;
                if (typeBox.getSelectedIndex() == 2) {
                    try {
                        double limit = Double.parseDouble(value);
                        String dueText = balanceField.getText().trim();
                        if (!dueText.isBlank()) {
                            double due = Double.parseDouble(dueText);
                            if (due > limit)
                                yield "Loan limit must be at least the amount due";
                        }
                    } catch (NumberFormatException ignored) {}
                }
                yield null;
            }
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
        } else if (typeBox.getSelectedIndex() == 0) {
            try {
                if (Double.parseDouble(balanceField.getText().trim()) < 500) {
                    UITheme.setFieldError(balanceField, balanceError,
                            "Savings accounts require a minimum of BDT 500");
                    valid = false;
                }
            } catch (NumberFormatException ignored) {}
        } else if (typeBox.getSelectedIndex() == 2) {
            try {
                double due = Double.parseDouble(balanceField.getText().trim());
                double limit = Double.parseDouble(extraField.getText().trim());
                if (due > limit) {
                    UITheme.setFieldError(balanceField, balanceError,
                            "Amount due cannot exceed loan limit");
                    valid = false;
                }
            } catch (NumberFormatException ignored) {}
        }

        String extraErr = InputValidator.validateAmount(extraField.getText().trim());
        if (extraErr != null) {
            UITheme.setFieldError(extraField, balanceError, extraErr);
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
            String nid     = nidField.getText().trim();
            String address = addressField.getText().trim();
            String branch  = (String) branchBox.getSelectedItem();
            double balance = Double.parseDouble(balanceField.getText().trim());
            double extra   = Double.parseDouble(extraField.getText().trim());

            Account account;

            if (typeBox.getSelectedIndex() == 0) {

                account = new SavingsAccount(
                        accNum,
                        name,
                        balance,
                        extra,
                        email,
                        phone,
                        nid,
                        address,
                        branch
                );

            } else if (typeBox.getSelectedIndex() == 1) {

                account = new CurrentAccount(
                        accNum,
                        name,
                        email,
                        phone,
                        balance,
                        extra,
                        nid,
                        address,
                        branch
                );

            } else {
                account = new LoanAccount(
                        accNum,
                        name,
                        email,
                        phone,
                        balance,
                        extra,
                        nid,
                        address,
                        branch
                );
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

    private void updateTypeSpecificLabels() {
        switch (typeBox.getSelectedIndex()) {
            case 0 -> {
                balanceLabel.setText("Initial Balance (BDT):");
                extraLabel.setText("Interest Rate (%):");
            }
            case 1 -> {
                balanceLabel.setText("Initial Balance (BDT):");
                extraLabel.setText("Overdraft Limit (BDT):");
            }
            case 2 -> {
                balanceLabel.setText("Amount Due (BDT):");
                extraLabel.setText("Loan Limit (BDT):");
            }
        }

        String bal = balanceField.getText().trim();
        if (!bal.isBlank()) {
            validate(balanceField, balanceError, "balance");
        }
        String extra = extraField.getText().trim();
        if (!extra.isBlank()) {
            validate(extraField, balanceError, "extra");
        }
    }

    public void refreshAccountNumber() {
        accNumDisplay.setText(AccountNumberGenerator.generate());
    }

    public void refreshBranches(MainFrame frame) {
        branchBox.removeAllItems();
        java.util.List<String> branches = frame.getBankService().getAllBranches();
        for (String b : branches) {
            branchBox.addItem(b);
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        nidField.setText("");
        addressField.setText("");
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