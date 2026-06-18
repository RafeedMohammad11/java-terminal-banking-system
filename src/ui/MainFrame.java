package ui;

import db.DatabaseConnection;
import db.AccountDAO;
import service.BankService;
import service.BankServiceImpl;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final BankService bankService = new BankServiceImpl(new AccountDAO());

    // Panels that need refreshing on navigation
    private final CreateAccountPanel createPanel = new CreateAccountPanel(this);
    private final AccountListPanel   listPanel   = new AccountListPanel(this);
    private final TransactionPanel transactionPanel = new TransactionPanel(this);
    private final TransactionHistoryPanel historyPanel = new TransactionHistoryPanel(this);
    private final UpdateAccountPanel updatePanel = new UpdateAccountPanel(this);
    private final DeleteAccountPanel deletePanel = new DeleteAccountPanel(this);

    public MainFrame() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        setTitle("MY BANK — Banking System");
        setSize(950, 750);
        setMinimumSize(new Dimension(800, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // All panels initialized before being added
        contentPanel.add(new DashboardPanel(this), "DASHBOARD");
        contentPanel.add(createPanel, "CREATE");
        contentPanel.add(listPanel, "LIST");
        contentPanel.add(transactionPanel, "TRANSACTION");
        contentPanel.add(updatePanel, "UPDATE");
        contentPanel.add(deletePanel, "DELETE");
        contentPanel.add(historyPanel, "HISTORY");

        add(contentPanel, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DatabaseConnection.close();
            }
        });

        showPanel("DASHBOARD");
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);

        // Refresh each panel when navigated to
        switch (name) {
            case "CREATE"      -> createPanel.refreshAccountNumber();
            case "LIST"        -> listPanel.loadAccounts(this);
            case "TRANSACTION" -> transactionPanel.loadAccountNumbers(this);
            case "HISTORY"     -> historyPanel.loadHistory(this);
            case "UPDATE"      -> updatePanel.prepare(this, null);
            case "DELETE"      -> deletePanel.prepare(this, null);
        }
    }

    public void showUpdatePanel(model.Account account) {
        updatePanel.prepare(this, account);
        cardLayout.show(contentPanel, "UPDATE");
    }

    public void showDeletePanel(model.Account account) {
        deletePanel.prepare(this, account);
        cardLayout.show(contentPanel, "DELETE");
    }

    public BankService getBankService() {
        return bankService;
    }
}