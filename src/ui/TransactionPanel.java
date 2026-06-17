package ui;

import exception.AccountNotFoundException;
import exception.InSufficientFundsException;
import exception.InvalidAmountException;
//import exception.OverdraftLimitExceededException;

import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends JPanel {

    private final JTextField accNumField  = new JTextField();
    private final JTextField accNumField2 = new JTextField(); // for transfer
    private final JTextField amountField  = new JTextField();
    private final JComboBox<String> opBox = new JComboBox<>(
            new String[]{"Deposit", "Withdraw", "Transfer"});
    private final JLabel toLabel          = new JLabel("To Account:");

    public TransactionPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Transactions", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        form.add(new JLabel("Operation:"));      form.add(opBox);
        form.add(new JLabel("Account Number:")); form.add(accNumField);
        form.add(toLabel);                       form.add(accNumField2);
        form.add(new JLabel("Amount (BDT):"));   form.add(amountField);

        // Show/hide "To Account" based on operation
        toLabel.setVisible(false);
        accNumField2.setVisible(false);
        opBox.addActionListener(e -> {
            boolean isTransfer = opBox.getSelectedIndex() == 2;
            toLabel.setVisible(isTransfer);
            accNumField2.setVisible(isTransfer);
        });

        add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton submitBtn = new JButton("Submit");
        JButton backBtn   = new JButton("Back to Dashboard");
        btnRow.add(submitBtn);
        btnRow.add(backBtn);
        add(btnRow, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleTransaction(frame));
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
    }

    private void handleTransaction(MainFrame frame) {
        try {
            String accNum = accNumField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            int op = opBox.getSelectedIndex();

            switch (op) {
                case 0 -> frame.getBankService().deposit(accNum, amount);
                case 1 -> frame.getBankService().withdraw(accNum, amount);
                case 2 -> {
                    String toAccNum = accNumField2.getText().trim();
                    frame.getBankService().transfer(accNum, toAccNum, amount);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Transaction successful!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            accNumField.setText("");
            accNumField2.setText("");
            amountField.setText("");

        } catch (AccountNotFoundException ex) {
            showError("Account not found: " + ex.getAccountNumber());
        } catch (InSufficientFundsException ex) {
            showError("Insufficient funds.\nAvailable: BDT "
                    + ex.getAvailableBalance());
        } catch (InvalidAmountException ex) {
            showError("Invalid amount: " + ex.getAmount());
//        } catch (OverdraftLimitExceededException ex) {
//            showError("Overdraft limit exceeded.\nLimit: BDT "
//                    + ex.getOverdraftLimit());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}