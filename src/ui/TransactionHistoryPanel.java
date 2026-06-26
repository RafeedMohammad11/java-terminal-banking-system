package ui;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionHistoryPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterBox;
    private final JLabel            totalLabel = new JLabel("Total records: 0");

    public TransactionHistoryPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        JPanel header = UITheme.headerBar("Transaction History");
        JButton refreshBtn = UITheme.headerActionButton("⟳ Refresh");
        JButton headerBackBtn = UITheme.headerActionButton("← Dashboard");
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerActions.setOpaque(false);
        headerActions.add(headerBackBtn);
        headerActions.add(refreshBtn);
        header.add(headerActions, BorderLayout.EAST);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(UITheme.CARD_BG);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));

        JLabel filterLabel = new JLabel("Filter by type:");
        filterLabel.setFont(UITheme.FONT_BODY);
        filterLabel.setForeground(UITheme.TEXT_BODY);

        filterBox = new JComboBox<>(
                new String[]{"All", "DEPOSIT", "WITHDRAW", "TRANSFER_OUT", "TRANSFER_IN",
                        "ACCOUNT_CREATED", "UPDATED", "SHIFT_BRANCH"});
        UITheme.styleComboBox(filterBox);
        filterBox.setPreferredSize(new Dimension(200, 36));

        totalLabel.setFont(UITheme.FONT_SMALL);
        totalLabel.setForeground(UITheme.TEXT_MUTED);

        filterBar.add(filterLabel);
        filterBar.add(filterBox);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(totalLabel);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(header);
        north.add(filterBar);
        add(north, BorderLayout.NORTH);

        String[] cols = {"#", "Account Number", "Holder Name",
                "Type", "Amount (BDT)", "Date & Time"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

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
                            setForeground(switch (type) {
                                case "DEPOSIT"         -> UITheme.SUCCESS;
                                case "WITHDRAW"        -> UITheme.DANGER;
                                case "TRANSFER_OUT",
                                     "TRANSFER_IN"     -> UITheme.WARNING;
                                case "ACCOUNT_CREATED" -> UITheme.PRIMARY;
                                case "UPDATED"         -> UITheme.ACCENT;
                                case "SHIFT_BRANCH"    -> new Color(59, 130, 246);
                                default                -> UITheme.TEXT_MUTED;
                            });
                            setBackground(row % 2 == 0 ? UITheme.CARD_BG : UITheme.ROW_ALT);
                        }
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                        return this;
                    }
                });

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

        JButton backBtn = UITheme.ghostButton("← Dashboard");
        add(UITheme.bottomActionBar(backBtn), BorderLayout.SOUTH);

        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
        headerBackBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));
        refreshBtn.addActionListener(e -> loadHistory(frame));
        filterBox.addActionListener(e -> loadHistory(frame));

        loadHistory(frame);
    }

    public void loadHistory(MainFrame frame) {
        tableModel.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();

        try {
            Connection conn = DatabaseConnection.getInstance();

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

            totalLabel.setText(count == 0 ? "No transactions found." : "Total records: " + count);

        } catch (SQLException e) {
            totalLabel.setText("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
