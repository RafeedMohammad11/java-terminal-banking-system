package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITheme {

    // ── Color Palette ─────────────────────────────────────────
    public static final Color PRIMARY       = new Color(37,  99,  235);
    public static final Color PRIMARY_DARK  = new Color(29,  78,  216);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color ACCENT        = new Color(16,  185, 129); // emerald
    public static final Color ACCENT_DARK   = new Color(5,   150, 105);
    public static final Color DANGER        = new Color(220, 38,  38);
    public static final Color DANGER_LIGHT  = new Color(254, 226, 226);
    public static final Color WARNING       = new Color(245, 158, 11);
    public static final Color SUCCESS       = new Color(22,  163, 74);
    public static final Color SUCCESS_LIGHT = new Color(220, 252, 231);

    public static final Color BG            = new Color(241, 245, 249);
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color HEADER_BG     = new Color(15,  23,  42);
    public static final Color SIDEBAR_BG    = new Color(30,  41,  59);

    public static final Color TEXT_DARK     = new Color(15,  23,  42);
    public static final Color TEXT_BODY     = new Color(51,  65,  85);
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);
    public static final Color ROW_ALT       = new Color(248, 250, 252);

    // Validation colors
    public static final Color FIELD_ERROR   = new Color(220, 38,  38);
    public static final Color FIELD_SUCCESS = new Color(22,  163, 74);
    public static final Color FIELD_DEFAULT = BORDER_COLOR;

    // ── Typography ────────────────────────────────────────────
    public static final Font FONT_DISPLAY = new Font("Segoe UI", Font.BOLD,   26);
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,   18);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN,  13);

    // ── Button Factories ──────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_DARK); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    public static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_DARK); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(ACCENT); }
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
        btn.setOpaque(true);
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
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    // Large nav card button for dashboard
    public static JButton navCard(String emoji, String label,
                                  String sublabel, Color accent) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(0, 4));
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel mainLabel = new JLabel(label);
        mainLabel.setFont(FONT_HEADING);
        mainLabel.setForeground(TEXT_DARK);

        JLabel subLabel = new JLabel(sublabel);
        subLabel.setFont(FONT_SMALL);
        subLabel.setForeground(TEXT_MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_BG);
        textPanel.add(mainLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLabel);

        // Color accent bar on left
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(4, 0));

        btn.add(accentBar,  BorderLayout.WEST);
        btn.add(iconLabel,  BorderLayout.NORTH);
        btn.add(textPanel,  BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(248, 250, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 1),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)
                ));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)
                ));
            }
        });

        return btn;
    }

    // ── Field Factories ───────────────────────────────────────
    public static JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_DARK);
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    // ── Panel Factories ───────────────────────────────────────
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        return panel;
    }

    public static JPanel headerBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel label = new JLabel(title);
        label.setFont(FONT_TITLE);
        label.setForeground(Color.WHITE);
        bar.add(label, BorderLayout.WEST);
        return bar;
    }

    // ── Table Styling ─────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_BODY);
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(CARD_BG);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(FONT_HEADING);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel,
                            boolean focus, int row, int col) {
                        super.getTableCellRendererComponent(
                                t, val, sel, focus, row, col);
                        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                        if (!sel) {
                            setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                            setForeground(TEXT_BODY);
                        }
                        return this;
                    }
                });
    }

    // ── Validation Helpers ────────────────────────────────────
    public static void setFieldError(JTextField field, JLabel errorLabel, String msg) {
        if (field != null)
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_ERROR, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        if (errorLabel != null) {
            errorLabel.setText("⚠ " + msg);
            errorLabel.setForeground(FIELD_ERROR);
            errorLabel.setVisible(true);
        }
    }

    public static void setFieldSuccess(JTextField field, JLabel errorLabel) {
        if (field != null)
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_SUCCESS, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        if (errorLabel != null) {
            errorLabel.setText("✓ Looks good");
            errorLabel.setForeground(FIELD_SUCCESS);
            errorLabel.setVisible(true);
        }
    }

    public static void resetField(JTextField field, JLabel errorLabel) {
        if (field != null)
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_DEFAULT, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        if (errorLabel != null) {
            errorLabel.setText(" ");
            errorLabel.setVisible(false);
        }
    }
}