package ui;

import db.DatabaseConnection;
import service.BankService;
import service.BankServiceImpl;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel   = new JPanel(cardLayout);
    private final BankService bankService = new BankServiceImpl();;

    public MainFrame() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("MY BANK — Banking System");
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contentPanel.add(new DashboardPanel(this),     "DASHBOARD");
        contentPanel.add(new CreateAccountPanel(this), "CREATE");
        contentPanel.add(new AccountListPanel(this),   "LIST");
        contentPanel.add(new TransactionPanel(this),   "TRANSACTION");
//        contentPanel.add(new UpdateAccountPanel(this), "UPDATE");

        add(contentPanel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DatabaseConnection.close();
            }
        });

        showPanel("DASHBOARD");
    }
    // Called by every panel to navigate
    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    // Panels get the service through MainFrame
    public BankService getBankService() {
        return bankService;
    }
}