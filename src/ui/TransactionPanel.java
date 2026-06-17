package ui;

import exception.AccountNotFoundException;
import exception.InSufficientFundsException;
import exception.InvalidAmountException;
//import exception.OverdraftLimitExceededException;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransactionPanel extends JPanel {

    private final JComboBox<String> accNumBox  = new JComboBox<>();
    private final JComboBox<String> accNumBox2 = new JComboBox<>(); // for transfer
    private final JTextField        amountField = UITheme.styledField();
    private final JComboBox<String> opBox       = new JComboBox<>(
            new String[]{"Deposit", "Withdraw", "Transfer"});
    private final JLabel toLabel   = new JLabel("To Account:");
    private final JLabel toSubLabel = new JLabel(); // shows holder name for "to" account

    // Sub-label showing holder name of selected account
    private final JLabel accInfoLabel = new JLabel(" ");

    public TransactionPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.HEADER_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel topTitle = new JLabel("Transactions");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topTitle.setForeground(Color.WHITE);

        // Refresh button in top bar
        JButton refreshAccBtn = new JButton("⟳ Refresh Accounts");
        refreshAccBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshAccBtn.setForeground(Color.WHITE);
        refreshAccBtn.setBackground(UITheme.HEADER_BG);
        refreshAccBtn.setBorderPainted(false);
        refreshAccBtn.setFocusPainted(false);
        refreshAccBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        topBar.add(topTitle, BorderLayout.WEST);
        topBar.add(refreshAccBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Style dropdowns
        styleComboBox(accNumBox);
        styleComboBox(accNumBox2);
        styleComboBox(opBox);

        // Info label under account selection
        accInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        accInfoLabel.setForeground(UITheme.TEXT_MUTED);

        toSubLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        toSubLabel.setForeground(UITheme.TEXT_MUTED);

        // Card
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setLayout(new GridLayout(9, 2, 12, 10));
        card.setPreferredSize(new Dimension(560, 400));

        toLabel.setFont(UITheme.FONT_BODY);
        toLabel.setForeground(UITheme.TEXT_DARK);

        card.add(styledLabel("Operation:"));       card.add(opBox);
        card.add(styledLabel("Account Number:"));  card.add(accNumBox);
        card.add(new JLabel(""));                  card.add(accInfoLabel);
        card.add(toLabel);                         card.add(accNumBox2);
        card.add(new JLabel(""));                  card.add(toSubLabel);
        card.add(styledLabel("Amount (BDT):"));    card.add(amountField);

        // Hide "To Account" rows initially
        toLabel.setVisible(false);
        accNumBox2.setVisible(false);
        toSubLabel.setVisible(false);

        // Show/hide transfer row + update info labels
        opBox.addActionListener(e -> {
            boolean isTransfer = opBox.getSelectedIndex() == 2;
            toLabel.setVisible(isTransfer);
            accNumBox2.setVisible(isTransfer);
            toSubLabel.setVisible(isTransfer);
            card.revalidate();
            card.repaint();
        });

        // Show holder name when account is selected
        accNumBox.addActionListener(e -> updateInfoLabel(frame, accNumBox, accInfoLabel));
        accNumBox2.addActionListener(e -> updateInfoLabel(frame, accNumBox2, toSubLabel));

        center.add(card);
        add(center, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(UITheme.BG);
        JButton backBtn   = UITheme.ghostButton("Dashboard");
        JButton submitBtn = UITheme.primaryButton("Submit");
        bottom.add(backBtn);
        bottom.add(submitBtn);
        add(bottom, BorderLayout.SOUTH);

        // Load accounts on first open
        loadAccountNumbers(frame);

        submitBtn.addActionListener(e -> handleTransaction(frame));
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
        refreshAccBtn.addActionListener(e -> loadAccountNumbers(frame));
    }

    // Populate both dropdowns with account numbers from DB
    private void loadAccountNumbers(MainFrame frame) {
        List<Account> accounts = frame.getBankService().getAllAccounts();

        accNumBox.removeAllItems();
        accNumBox2.removeAllItems();

        if (accounts.isEmpty()) {
            accNumBox.addItem("-- No accounts found --");
            accNumBox2.addItem("-- No accounts found --");
            accInfoLabel.setText("No accounts in the system yet.");
            return;
        }

        for (Account acc : accounts) {
            // Show number + name together for easy identification
            String item = acc.getAccountNumber() + "  —  " + acc.getHolderName();
            accNumBox.addItem(item);
            accNumBox2.addItem(item);
        }

        accInfoLabel.setText(" ");
        toSubLabel.setText(" ");
    }

    // Show balance info below the selected account
    private void updateInfoLabel(MainFrame frame,
                                 JComboBox<String> box,
                                 JLabel infoLabel) {
        String selected = (String) box.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            infoLabel.setText(" ");
            return;
        }

        // Extract account number from "ACC10001  —  Rahim" format
        String accNum = selected.split("\\s+—\\s+")[0].trim();

        try {
            Account acc = frame.getBankService().findAccount(accNum);
            infoLabel.setText(
                    acc.getAccountType() + "  |  Balance: BDT " +
                            String.format("%.2f", acc.getBalance())
            );
        } catch (AccountNotFoundException e) {
            infoLabel.setText("Account not found.");
        }
    }

    private void handleTransaction(MainFrame frame) {
        try {
            String selected = (String) accNumBox.getSelectedItem();
            if (selected == null || selected.startsWith("--")) {
                showError("Please select a valid account.");
                return;
            }

            // Parse account number from dropdown item
            String accNum = selected.split("\\s+—\\s+")[0].trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            int op        = opBox.getSelectedIndex();

            switch (op) {
                case 0 -> frame.getBankService().deposit(accNum, amount);
                case 1 -> frame.getBankService().withdraw(accNum, amount);
                case 2 -> {
                    String selected2 = (String) accNumBox2.getSelectedItem();
                    if (selected2 == null || selected2.startsWith("--")) {
                        showError("Please select a destination account.");
                        return;
                    }
                    String toAccNum = selected2.split("\\s+—\\s+")[0].trim();
                    if (accNum.equals(toAccNum)) {
                        showError("Cannot transfer to the same account.");
                        return;
                    }
                    frame.getBankService().transfer(accNum, toAccNum, amount);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Transaction successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            amountField.setText("");
            loadAccountNumbers(frame); // refresh balances after transaction

        } catch (AccountNotFoundException ex) {
            showError("Account not found: " + ex.getAccountNumber());
        } catch (InSufficientFundsException ex) {
            showError("Insufficient funds.\nAvailable: BDT "
                    + String.format("%.2f", ex.getAvailableBalance()));
        } catch (InvalidAmountException ex) {
            showError("Invalid amount: " + ex.getAmount());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(UITheme.FONT_BODY);
        box.setPreferredSize(new Dimension(0, 36));
        box.setBackground(Color.WHITE);
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