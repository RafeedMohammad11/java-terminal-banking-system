package ui;

import db.DatabaseConnection;
import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionHistoryPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterBox;
    private final JLabel            totalLabel = new JLabel("Total records: 0");

    public TransactionHistoryPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // ── Header ────────────────────────────────────────────
        JPanel header = UITheme.headerBar("Transaction History");
        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setFont(UITheme.FONT_SMALL);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(UITheme.HEADER_BG);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(refreshBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Filter bar ────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(UITheme.BG);
        filterBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel filterLabel = new JLabel("Filter by type:");
        filterLabel.setFont(UITheme.FONT_BODY);
        filterLabel.setForeground(UITheme.TEXT_BODY);

        filterBox = new JComboBox<>(
                new String[]{"All", "DEPOSIT", "WITHDRAW", "TRANSFER",
                        "ACCOUNT_CREATED", "UPDATED"});
        filterBox.setFont(UITheme.FONT_BODY);
        filterBox.setPreferredSize(new Dimension(180, 32));

        totalLabel.setFont(UITheme.FONT_SMALL);
        totalLabel.setForeground(UITheme.TEXT_MUTED);

        filterBar.add(filterLabel);
        filterBar.add(filterBox);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(totalLabel);
        add(filterBar, BorderLayout.AFTER_LAST_LINE);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {"#", "Account Number", "Holder Name",
                "Type", "Amount (BDT)", "Date & Time"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Color-code transaction type column
        table.getColumnModel().getColumn(3).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel,
                            boolean focus, int row, int col) {
                        super.getTableCellRendererComponent(
                                t, val, sel, focus, row, col);
                        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                        String type = val != null ? val.toString() : "";
                        if (!sel) {
                            switch (type) {
                                case "DEPOSIT"         -> setForeground(UITheme.SUCCESS);
                                case "WITHDRAW"        -> setForeground(UITheme.DANGER);
                                case "TRANSFER"        -> setForeground(UITheme.WARNING);
                                case "ACCOUNT_CREATED" -> setForeground(UITheme.PRIMARY);
                                default                -> setForeground(UITheme.TEXT_MUTED);
                            }
                            setBackground(row % 2 == 0 ? UITheme.CARD_BG : UITheme.ROW_ALT);
                        }
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                        return this;
                    }
                });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(170);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.BG);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        tableWrapper.add(scroll);
        add(tableWrapper, BorderLayout.CENTER);

        // ── Bottom bar ────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(UITheme.BG);
        JButton backBtn = UITheme.primaryButton("Dashboard");
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
        refreshBtn.addActionListener(e -> loadHistory(frame));
        filterBox.addActionListener(e -> loadHistory(frame));

        loadHistory(frame);
    }

    public void loadHistory(MainFrame frame) {
        tableModel.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();

        try {
            Connection conn = DatabaseConnection.getInstance();

            // Build query with optional filter
            String sql;
            if ("All".equals(filter)) {
                sql = "SELECT t.id, t.account_number, a.holder_name, " +
                        "t.type, t.amount, t.timestamp " +
                        "FROM transactions t " +
                        "LEFT JOIN accounts a ON t.account_number = a.account_number " +
                        "ORDER BY t.id DESC";
            } else {
                sql = "SELECT t.id, t.account_number, a.holder_name, " +
                        "t.type, t.amount, t.timestamp " +
                        "FROM transactions t " +
                        "LEFT JOIN accounts a ON t.account_number = a.account_number " +
                        "WHERE t.type = '" + filter + "' " +
                        "ORDER BY t.id DESC";
            }

            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs   = stmt.executeQuery(sql);

            int count = 0;
            while (rs.next()) {
                count++;
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("account_number"),
                        rs.getString("holder_name") != null
                                ? rs.getString("holder_name") : "—",
                        rs.getString("type"),
                        String.format("%.2f", rs.getDouble("amount")),
                        rs.getString("timestamp")
                });
            }

            totalLabel.setText("Total records: " + count);
            if (count == 0) {
                totalLabel.setText("No transactions found.");
            }

        } catch (SQLException e) {
            totalLabel.setText("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}