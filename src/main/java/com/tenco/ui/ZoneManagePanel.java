package com.tenco.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 구역 관리 화면 (관리자)
 * - 구역 목록 테이블 (ID / 구역코드 / 사용여부)
 * - 구역 추가 / 삭제 / 상태 변경 (is_available 토글)
 */

@Getter
public class ZoneManagePanel extends JPanel {

    private static final String[] COLUMNS = {"ID", "구역 코드", "사용 여부"};

    private DefaultTableModel tableModel;
    private JTable table;

    // 추가 폼
    private JTextField addCodeField;
    private JButton    addBtn;
    private JLabel     addResultLabel;

    private JButton deleteBtn;
    private JButton toggleBtn;
    private JButton refreshBtn;

    public ZoneManagePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildTitleBar(), BorderLayout.NORTH);
        add(buildContent(),  BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel title = new JLabel("구역 관리");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 40, 55));
        p.add(title);
        return p;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(buildAddCard(),    BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildAddCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(14, 20, 14, 20)));

        JLabel sectionLbl = new JLabel("구역 추가");
        sectionLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        sectionLbl.setForeground(new Color(70, 80, 100));
        card.add(sectionLbl);
        card.add(Box.createVerticalStrut(10));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel lbl = new JLabel("구역 코드");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 80, 100));

        addCodeField = new JTextField();
        addCodeField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        addCodeField.setToolTipText("예) A-01");
        addCodeField.setPreferredSize(new Dimension(160, 32));
        addCodeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(2, 8, 2, 8)));

        addBtn = new JButton("추가");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        addBtn.setBackground(new Color(37, 99, 235));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setPreferredSize(new Dimension(68, 32));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        row.add(lbl);
        row.add(addCodeField);
        row.add(addBtn);
        card.add(row);
        card.add(Box.createVerticalStrut(4));

        addResultLabel = new JLabel(" ");
        addResultLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(addResultLabel);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        deleteBtn  = buildBtn("선택 삭제",  new Color(220, 38, 38),   Color.WHITE);
        toggleBtn  = buildBtn("상태 변경",  new Color(245, 158, 11),  Color.WHITE);
        refreshBtn = buildBtn("새로고침",   new Color(240, 243, 248), new Color(60, 70, 90));

        JLabel hint = new JLabel("  * 상태 변경: 사용 가능 ↔ 점검 중");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(130, 140, 160));

        toolbar.add(deleteBtn);
        toolbar.add(toggleBtn);
        toolbar.add(refreshBtn);
        toolbar.add(hint);

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
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // 사용 여부 열 색상
        table.getColumnModel().getColumn(2).setCellRenderer(new AvailableRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    private JButton buildBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── 서비스 연결용 public API ───────────────────────────

    public String getAddCode() { return addCodeField.getText().trim(); }

    /** 선택 행의 zoneId (0번 컬럼). 없으면 -1 */
    public int getSelectedZoneId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        try { return Integer.parseInt(tableModel.getValueAt(row, 0).toString()); }
        catch (Exception e) { return -1; }
    }

    /**
     * 테이블 데이터 설정
     * row: [zoneId, zoneCode, "사용 가능" | "점검 중"]
     */
    public void setRows(Object[][] rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) tableModel.addRow(row);
    }

    public void clearAddForm() {
        addCodeField.setText("");
        addResultLabel.setText(" ");
    }

    public void setAddResult(String msg, boolean success) {
        addResultLabel.setText(msg);
        addResultLabel.setForeground(success ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }

    // ── 사용 여부 컬럼 렌더러 ─────────────────────────────

    private static class AvailableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);

            if (!isSelected) {
                String v = value == null ? "" : value.toString();
                if ("사용 가능".equals(v)) {
                    setBackground(new Color(220, 252, 231));
                    setForeground(new Color(22, 101, 52));
                } else {
                    setBackground(new Color(254, 249, 195));
                    setForeground(new Color(133, 77, 14));
                }
            }
            return this;
        }
    }
}
