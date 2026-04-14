package com.tenco.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private EntryPanel entryPanel;
    private ExitPanel exitPanel;
    private StatusPanel statusPanel;
    private HistoryPanel historyPanel;
    private MonthlyPassPanel monthlyPassPanel;
    private ZoneManagePanel zoneManagePanel;

    public MainFrame() {
        setTitle("주차관리시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildNavBar(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nav.setBackground(new Color(30, 40, 55));
        nav.setPreferredSize(new Dimension(900, 48));

        JLabel logo = new JLabel("  P 주차관리시스템  ");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 14));
        nav.add(logo);

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 48));
        sep.setForeground(new Color(60, 80, 100));
        nav.add(sep);

        String[] menus = {"입차", "출차", "주차현황", "이력조회", "정기권", "구역관리"};
        String[] cards  = {"ENTRY", "EXIT", "STATUS", "HISTORY", "PASS", "ZONE"};

        for (int i = 0; i < menus.length; i++) {
            nav.add(buildNavButton(menus[i], cards[i]));
        }
        return nav;
    }

    private JButton buildNavButton(String label, String card) {
        JButton btn = new JButton(label);
        btn.setPreferredSize(new Dimension(88, 48));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(30, 40, 55));
        btn.setForeground(new Color(180, 200, 220));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(50, 65, 85));
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 40, 55));
                btn.setForeground(new Color(180, 200, 220));
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, card));
        return btn;
    }

    private JPanel buildContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        entryPanel       = new EntryPanel();
        exitPanel        = new ExitPanel();
        statusPanel      = new StatusPanel();
        historyPanel     = new HistoryPanel();
        monthlyPassPanel = new MonthlyPassPanel();
        zoneManagePanel  = new ZoneManagePanel();

        contentPanel.add(entryPanel,       "ENTRY");
        contentPanel.add(exitPanel,        "EXIT");
        contentPanel.add(statusPanel,      "STATUS");
        contentPanel.add(historyPanel,     "HISTORY");
        contentPanel.add(monthlyPassPanel, "PASS");
        contentPanel.add(zoneManagePanel,  "ZONE");

        cardLayout.show(contentPanel, "STATUS");
        return contentPanel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        bar.setBackground(new Color(230, 234, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 205, 215)));
        JLabel lbl = new JLabel("준비");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(100, 110, 130));
        bar.add(lbl);
        return bar;
    }

    public void showCard(String card) {
        cardLayout.show(contentPanel, card);
    }

    public EntryPanel       getEntryPanel()       { return entryPanel; }
    public ExitPanel        getExitPanel()         { return exitPanel; }
    public StatusPanel      getStatusPanel()       { return statusPanel; }
    public HistoryPanel     getHistoryPanel()      { return historyPanel; }
    public MonthlyPassPanel getMonthlyPassPanel()  { return monthlyPassPanel; }
    public ZoneManagePanel  getZoneManagePanel()   { return zoneManagePanel; }
}
