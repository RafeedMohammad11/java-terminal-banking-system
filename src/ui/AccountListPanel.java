package ui;

import model.Account;
import model.CurrentAccount;
import model.LoanAccount;
import model.SavingsAccount;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class AccountListPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable            table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Keep the full account list so we can look up details on click
    private List<Account> allAccounts;

    public AccountListPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // ── Top bar ───────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.HEADER_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        JLabel title = new JLabel("All Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        // Search field in header
        JTextField searchField = new JTextField();
        searchField.setFont(UITheme.FONT_BODY);
        searchField.setPreferredSize(new Dimension(240, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        searchField.setToolTipText("Search by name, number, email, phone...");

        JLabel searchIcon = new JLabel("🔍  Search:");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchIcon.setForeground(Color.WHITE);
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(UITheme.HEADER_BG);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        topBar.add(searchPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {
            "Account No", "Type", "Holder Name",
            "Email", "Phone", "Balance (BDT)", "Rate / Limit"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Make "Rate / Limit" column right-aligned
        table.getColumnModel().getColumn(6)
                .setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                    { setHorizontalAlignment(SwingConstants.RIGHT); }
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                        if (!sel) {
                            setBackground(row % 2 == 0 ? UITheme.CARD_BG : UITheme.ROW_ALT);
                            // Colour-code: interest rate = teal, overdraft = orange
                            String v = val == null ? "" : val.toString();
                            setForeground(v.endsWith("%") ? UITheme.ACCENT : UITheme.WARNING);
                        }
                        return this;
                    }
                });

        // Click row → detail dialog
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    if (allAccounts != null && modelRow < allAccounts.size()) {
                        showDetailDialog(allAccounts.get(modelRow), frame);
                    }
                    table.clearSelection();
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.BG);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        tableWrapper.add(scroll);
        add(tableWrapper, BorderLayout.CENTER);

        // ── Bottom bar ────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(UITheme.BG);
        JButton refreshBtn = UITheme.ghostButton("Refresh");
        JButton backBtn    = UITheme.primaryButton("Dashboard");
        bottom.add(refreshBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // ── Live search ───────────────────────────────────────
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { applyFilter(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { applyFilter(searchField.getText()); }
        });

        refreshBtn.addActionListener(e -> loadAccounts(frame));
        backBtn.addActionListener(e -> frame.showPanel("DASHBOARD"));

        loadAccounts(frame);
    }

    // ── Load data ─────────────────────────────────────────────

    public void loadAccounts(MainFrame frame) {
        tableModel.setRowCount(0);
        allAccounts = frame.getBankService().getAllAccounts();
        for (Account acc : allAccounts) {
            String rateOrLimit = formatRateLimit(acc);
            tableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc.getAccountType(),
                    acc.getHolderName(),
                    acc.getEmail(),
                    acc.getPhone(),
                    String.format("%.2f", acc.getBalance()),
                    rateOrLimit
            });
        }
    }

    // ── Search filter ─────────────────────────────────────────

    private void applyFilter(String text) {
        if (text == null || text.isBlank()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // ── Detail dialog ─────────────────────────────────────────

    private void showDetailDialog(Account acc, MainFrame frame) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Account Details — " + acc.getAccountNumber(),
                true
        );
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel hTitle = new JLabel("Account Details");
        hTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        hTitle.setForeground(Color.WHITE);
        JLabel accTag = new JLabel(acc.getAccountNumber());
        accTag.setFont(UITheme.FONT_BODY);
        accTag.setForeground(UITheme.PRIMARY_LIGHT);
        header.add(hTitle, BorderLayout.WEST);
        header.add(accTag, BorderLayout.EAST);
        dialog.add(header, BorderLayout.NORTH);

        // Details grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 6));
        grid.setBackground(UITheme.CARD_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        addDetailRow(grid, "Account Number",  acc.getAccountNumber());
        addDetailRow(grid, "Account Type",    acc.getAccountType());
        addDetailRow(grid, "Holder Name",     acc.getHolderName());
        addDetailRow(grid, "Email",           acc.getEmail());
        addDetailRow(grid, "Phone",           acc.getPhone());
        addDetailRow(grid, "NID",             blankIfEmpty(acc.getNid()));
        addDetailRow(grid, "Address",         blankIfEmpty(acc.getAddress()));
        addDetailRow(grid, "Balance (BDT)",   String.format("%.2f", acc.getBalance()));

        if (acc instanceof SavingsAccount sa) {
            addDetailRow(grid, "Interest Rate", String.format("%.2f%%", sa.getInterestRate()));
        } else if (acc instanceof CurrentAccount ca) {
            addDetailRow(grid, "Overdraft Limit (BDT)", String.format("%.2f", ca.getOverDraftLimit()));
        } else if (acc instanceof LoanAccount la) {
            addDetailRow(grid, "Amount Due (BDT)", String.format("%.2f", la.getAmountDue()));
            addDetailRow(grid, "Loan Limit (BDT)", String.format("%.2f", la.getLoanLimit()));
            addDetailRow(grid, "Remaining Credit (BDT)", String.format("%.2f", la.getRemainingCredit()));
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        dialog.add(sp, BorderLayout.CENTER);

        // Action buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnBar.setBackground(UITheme.BG);
        JButton editBtn = UITheme.accentButton("Edit Account");
        JButton deleteBtn = UITheme.dangerButton("Close Account");
        JButton closeBtn = UITheme.ghostButton("Close");
        editBtn.setPreferredSize(new Dimension(130, 36));
        deleteBtn.setPreferredSize(new Dimension(140, 36));
        closeBtn.setPreferredSize(new Dimension(100, 36));
        editBtn.addActionListener(e -> {
            dialog.dispose();
            frame.showUpdatePanel(acc);
        });
        deleteBtn.addActionListener(e -> {
            dialog.dispose();
            frame.showDeletePanel(acc);
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(editBtn);
        btnBar.add(deleteBtn);
        btnBar.add(closeBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────

    private String formatRateLimit(Account acc) {
        if (acc instanceof SavingsAccount sa) {
            return String.format("%.2f%%", sa.getInterestRate());
        } else if (acc instanceof CurrentAccount ca) {
            return String.format("%.2f BDT", ca.getOverDraftLimit());
        } else if (acc instanceof LoanAccount la) {
            return String.format("%.2f BDT", la.getLoanLimit());
        }
        return "—";
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value == null || value.isBlank() ? "—" : value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(UITheme.TEXT_DARK);

        panel.add(lbl);
        panel.add(val);
    }

    private String blankIfEmpty(String s) {
        return (s == null || s.isBlank()) ? "" : s;
    }
}