package ui;

import db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Welcome to Banking System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Button grid
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        JButton createBtn      = new JButton("Open New Account");
        JButton listBtn        = new JButton("View All Accounts");
        JButton transactionBtn = new JButton("Deposit / Withdraw");
        JButton transferBtn    = new JButton("Transfer Funds");
        JButton updateBtn      = new JButton("Update Account");
        JButton exitBtn        = new JButton("Exit");

        // Style buttons
        Font btnFont = new Font("Arial", Font.PLAIN, 14);
        for (JButton btn : new JButton[]{
                createBtn, listBtn, transactionBtn,
                transferBtn, updateBtn, exitBtn}) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Navigation actions
        createBtn.addActionListener(e -> frame.showPanel("CREATE"));
        listBtn.addActionListener(e -> frame.showPanel("LIST"));
        transactionBtn.addActionListener(e -> frame.showPanel("TRANSACTION"));
        transferBtn.addActionListener(e -> frame.showPanel("TRANSACTION"));
        updateBtn.addActionListener(e -> frame.showPanel("UPDATE"));
        exitBtn.addActionListener(e -> {
            DatabaseConnection.close();
            System.exit(0);
        });
    }
}