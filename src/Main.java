import db.DatabaseConnection;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Initialize database once at startup
        try {
            DatabaseConnection.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Database initialization failed:\n" + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Always launch Swing on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}