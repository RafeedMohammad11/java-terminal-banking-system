package ui;

import db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        topBar.setBackground(UITheme.HEADER_BG);
        JLabel appName = new JLabel("🏦  MY BANK");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        topBar.add(appName);
        add(topBar, BorderLayout.NORTH);

        // Center card
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);

        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 20));
        card.setPreferredSize(new Dimension(520, 360));

        // Welcome text
        JPanel welcomeRow = new JPanel(new BorderLayout());
        welcomeRow.setBackground(UITheme.CARD_BG);
        JLabel title = UITheme.titleLabel("Welcome Back");
        JLabel sub   = UITheme.mutedLabel("What would you like to do today?");
        welcomeRow.add(title, BorderLayout.NORTH);
        welcomeRow.add(sub,   BorderLayout.SOUTH);
        card.add(welcomeRow, BorderLayout.NORTH);

        // Button grid
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setBackground(UITheme.CARD_BG);

        JButton openBtn   = UITheme.primaryButton("Open Account");
        JButton listBtn   = UITheme.primaryButton("View Accounts");
        JButton depositBtn   = UITheme.primaryButton("Deposit");
        JButton withdrawBtn  = UITheme.ghostButton("Withdraw");
        JButton transferBtn  = UITheme.ghostButton("Transfer");
        JButton updateBtn    = UITheme.ghostButton("Update Info");

        grid.add(openBtn);
        grid.add(listBtn);
        grid.add(depositBtn);
        grid.add(withdrawBtn);
        grid.add(transferBtn);
        grid.add(updateBtn);
        card.add(grid, BorderLayout.CENTER);

        // Exit row
        JPanel exitRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitRow.setBackground(UITheme.CARD_BG);
        JButton exitBtn = UITheme.dangerButton("Exit");
        exitRow.add(exitBtn);
        card.add(exitRow, BorderLayout.SOUTH);

        center.add(card);
        add(center, BorderLayout.CENTER);

        // Actions
        openBtn.addActionListener(e -> frame.showPanel("CREATE"));
        listBtn.addActionListener(e -> frame.showPanel("LIST"));
        depositBtn.addActionListener(e -> frame.showPanel("TRANSACTION"));
        withdrawBtn.addActionListener(e -> frame.showPanel("TRANSACTION"));
        transferBtn.addActionListener(e -> frame.showPanel("TRANSACTION"));
        updateBtn.addActionListener(e -> frame.showPanel("UPDATE"));
        exitBtn.addActionListener(e -> {
            DatabaseConnection.close();
            System.exit(0);
        });
    }
}