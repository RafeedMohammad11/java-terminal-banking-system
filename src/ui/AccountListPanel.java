package ui;

import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountListPanel extends JPanel {

    private final DefaultTableModel tableModel;

    public AccountListPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Accounts", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"Account No", "Type", "Holder Name",
                "Email", "Phone", "Balance (BDT)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;  // read-only table
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn    = new JButton("Back to Dashboard");
        btnRow.add(refreshBtn);
        btnRow.add(backBtn);
        add(btnRow, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAccounts(frame));
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));

        loadAccounts(frame);
    }

    public void loadAccounts(MainFrame frame) {
        tableModel.setRowCount(0);  // clear existing rows
        List<Account> accounts = frame.getBankService().getAllAccounts();
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc.getAccountType(),
                    acc.getHolderName(),
                    acc.getEmail(),
                    acc.getPhone(),
                    String.format("%.2f", acc.getBalance())
            });
        }
    }
}