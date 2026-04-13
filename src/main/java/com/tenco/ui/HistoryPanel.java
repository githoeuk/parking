package com.tenco.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 이력 조회 화면
 * - 차량 번호 검색 + 날짜 범위 필터
 * - 이력 테이블 (차량번호 / 구역 / 입차 / 출차 / 요금)
 */
public class HistoryPanel extends JPanel {

    private static final String[] COLUMNS = {"차량 번호", "구역", "입차 시각", "출차 시각", "요금(원)"};

    private JTextField carNumberField;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JButton searchBtn;
    private JButton resetBtn;

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel countLabel;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildTitleBar(),  BorderLayout.NORTH);
        add(buildContent(),   BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel title = new JLabel("입출차 이력");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 40, 55));
        p.add(title);
        return p;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        content.add(buildFilterCard(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildFilterCard() {
        JPanel card = new JPanel();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(8, 16, 8, 16)));

        card.add(buildLabel("차량 번호"));
        carNumberField = buildTextField(160, "예) 12가 3456");
        card.add(carNumberField);

        card.add(buildLabel("기간"));
        fromDateField = buildTextField(110, "yyyy-MM-dd");
        card.add(fromDateField);
        card.add(buildLabel("~"));
        toDateField = buildTextField(110, "yyyy-MM-dd");
        card.add(toDateField);

        searchBtn = buildBtn("검색", new Color(37, 99, 235), Color.WHITE);
        resetBtn  = buildBtn("초기화", new Color(240, 243, 248), new Color(60, 70, 90));
        card.add(searchBtn);
        card.add(resetBtn);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        // 하단 카운트
        countLabel = new JLabel("총 0 건");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(new Color(100, 110, 130));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 243, 248));
        table.getTableHeader().setForeground(new Color(70, 80, 100));
        table.setGridColor(new Color(230, 234, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setShowVerticalLines(false);

        // 요금 열 오른쪽 정렬
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightAlign);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll,      BorderLayout.CENTER);
        panel.add(countLabel,  BorderLayout.SOUTH);
        return panel;
    }

    // ── 헬퍼 ──────────────────────────────────────────────

    private JLabel buildLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 80, 100));
        return lbl;
    }

    private JTextField buildTextField(int width, String tip) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setToolTipText(tip);
        f.setPreferredSize(new Dimension(width, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(2, 6, 2, 6)));
        return f;
    }

    private JButton buildBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(72, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── 서비스 연결용 public API ───────────────────────────

    public String getCarNumber() { return carNumberField.getText().trim(); }
    public String getFromDate()  { return fromDateField.getText().trim(); }
    public String getToDate()    { return toDateField.getText().trim(); }

    public void setRows(Object[][] rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) tableModel.addRow(row);
        countLabel.setText("총 " + rows.length + " 건");
    }

    public void resetFilter() {
        carNumberField.setText("");
        fromDateField.setText("");
        toDateField.setText("");
    }

    public JButton getSearchBtn() { return searchBtn; }
    public JButton getResetBtn()  { return resetBtn; }
}
