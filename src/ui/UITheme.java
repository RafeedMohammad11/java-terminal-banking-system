package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {

    // Color palette
    public static final Color PRIMARY      = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color DANGER       = new Color(220, 38, 38);
    public static final Color SUCCESS      = new Color(22, 163, 74);
    public static final Color BG           = new Color(248, 250, 252);
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(15, 23, 42);
    public static final Color TEXT_MUTED   = new Color(100, 116, 139);
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    public static final Color ROW_ALT      = new Color(241, 245, 249);
    public static final Color HEADER_BG    = new Color(30, 41, 59);

    // Fonts
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);

    // Reusable styled button
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(CARD_BG);
        btn.setForeground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    // Reusable styled text field
    public static JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    // Card panel with shadow-like border
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        return panel;
    }

    // Section title label
    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    // Style a JTable
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(CARD_BG);

        // Header
        table.getTableHeader().setFont(FONT_HEADING);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Alternating row colors
        table.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel,
                            boolean focus, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                        setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                        if (!sel) {
                            setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                            setForeground(TEXT_DARK);
                        }
                        return this;
                    }
                });
    }
}