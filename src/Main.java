import db.DatabaseConnection;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Init DB before UI launches
        try {
            DatabaseConnection.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed:\n" + e.getMessage(),
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