package ui;

import db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setBackground(UITheme.HEADER_BG);

        JLabel bankIcon = new JLabel("🏦");
        bankIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel bankName = new JLabel("MY BANK");
        bankName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bankName.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Banking System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(new Color(148, 163, 184));

        brandRow.add(bankIcon);
        brandRow.add(bankName);

        JPanel brandStack = new JPanel();
        brandStack.setLayout(new BoxLayout(brandStack, BoxLayout.Y_AXIS));
        brandStack.setBackground(UITheme.HEADER_BG);
        brandStack.add(bankName);
        brandStack.add(tagline);

        header.add(bankIcon,   BorderLayout.WEST);
        header.add(brandStack, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ── Welcome strip ─────────────────────────────────────
        JPanel welcomeStrip = new JPanel(new BorderLayout());
        welcomeStrip.setBackground(UITheme.PRIMARY);
        welcomeStrip.setBorder(BorderFactory.createEmptyBorder(18, 32, 18, 32));

        JLabel welcomeTitle = new JLabel("Welcome back.");
        welcomeTitle.setFont(UITheme.FONT_DISPLAY);
        welcomeTitle.setForeground(Color.WHITE);

        JLabel welcomeSub = new JLabel(
                "Manage accounts, process transactions, and track activity.");
        welcomeSub.setFont(UITheme.FONT_BODY);
        welcomeSub.setForeground(new Color(191, 219, 254));

        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setBackground(UITheme.PRIMARY);
        welcomeText.add(welcomeTitle);
        welcomeText.add(Box.createVerticalStrut(4));
        welcomeText.add(welcomeSub);
        welcomeStrip.add(welcomeText, BorderLayout.CENTER);
        add(welcomeStrip, BorderLayout.AFTER_LAST_LINE);

        // ── Nav card grid ─────────────────────────────────────
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(UITheme.BG);
        gridWrapper.setBorder(BorderFactory.createEmptyBorder(28, 32, 12, 32));

        JLabel sectionLabel = new JLabel("QUICK ACTIONS");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLabel.setForeground(UITheme.TEXT_MUTED);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        gridWrapper.add(sectionLabel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 3, 14, 14));
        grid.setBackground(UITheme.BG);

        JButton openBtn    = UITheme.navCard("➕", "Open Account",
                "Create new account", UITheme.ACCENT);
        JButton listBtn    = UITheme.navCard("👥", "All Accounts",
                "View & manage", UITheme.PRIMARY);
        JButton txBtn      = UITheme.navCard("💸", "Transactions",
                "Deposit, withdraw, transfer", UITheme.WARNING);
        JButton historyBtn = UITheme.navCard("📋", "History",
                "Past transactions", new Color(139, 92, 246));
        JButton updateBtn  = UITheme.navCard("✏️", "Update Info",
                "Edit account details", new Color(20, 184, 166));
        JButton deleteBtn  = UITheme.navCard("🗑️", "Close Account",
                "Remove an account", UITheme.DANGER);
        JButton shiftBtn   = UITheme.navCard("🔀", "Shift Branch",
                "Transfer account to branch", new Color(59, 130, 246));
        JButton branchBtn  = UITheme.navCard("🏢", "Branches",
                "Manage branches", new Color(245, 158, 11));
        JButton exitBtn    = UITheme.navCard("🚪", "Exit",
                "Close application", new Color(100, 116, 139));

        grid.add(openBtn);
        grid.add(listBtn);
        grid.add(txBtn);
        grid.add(historyBtn);
        grid.add(updateBtn);
        grid.add(deleteBtn);
        grid.add(shiftBtn);
        grid.add(branchBtn);
        grid.add(exitBtn);

        gridWrapper.add(grid, BorderLayout.CENTER);
        add(gridWrapper, BorderLayout.CENTER);

        // ── Footer status bar ─────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.SIDEBAR_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        JLabel statusLabel = new JLabel("● Connected  —  banking.db");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(new Color(74, 222, 128));

        JLabel versionLabel = new JLabel("MY BANK v1.0");
        versionLabel.setFont(UITheme.FONT_SMALL);
        versionLabel.setForeground(new Color(100, 116, 139));

        footer.add(statusLabel,  BorderLayout.WEST);
        footer.add(versionLabel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        // ── Actions ───────────────────────────────────────────
        openBtn.addActionListener(e    -> frame.showPanel("CREATE"));
        listBtn.addActionListener(e    -> frame.showPanel("LIST"));
        txBtn.addActionListener(e      -> frame.showPanel("TRANSACTION"));
        historyBtn.addActionListener(e -> frame.showPanel("HISTORY"));
        updateBtn.addActionListener(e  -> frame.showPanel("UPDATE"));
        deleteBtn.addActionListener(e  -> frame.showPanel("DELETE"));
        shiftBtn.addActionListener(e   -> frame.showPanel("SHIFT_BRANCH"));
        branchBtn.addActionListener(e -> new ManageBranchesDialog(frame, frame.getBankService()).setVisible(true));
        exitBtn.addActionListener(e -> {
            DatabaseConnection.close();
            System.exit(0);
        });
    }
}