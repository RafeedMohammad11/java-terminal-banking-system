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
        setBackground(UITheme.BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.HEADER_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel title = new JLabel("All Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Account No", "Type", "Holder Name",
                "Email", "Phone", "Balance (BDT)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.BG);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        tableWrapper.add(scroll);
        add(tableWrapper, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(UITheme.BG);
        JButton refreshBtn = UITheme.ghostButton("Refresh");
        JButton backBtn    = UITheme.primaryButton("Dashboard");
        bottom.add(refreshBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadAccounts(frame));
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));

        loadAccounts(frame);
    }

    public void loadAccounts(MainFrame frame) {
        tableModel.setRowCount(0);
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