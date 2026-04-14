package com.tenco.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 월정기권 관리 화면
 * - 정기권 목록 테이블 (차량번호 / 소유자 / 시작일 / 종료일 / 요금 / 상태)
 * - 만료 임박 7일 이내 강조 (노랑)
 * - 등록 버튼: DB INSERT (is_available = true)
 * - 삭제 버튼: soft delete (is_available = false), 목록에서 is_available=true인 행만 표시
 */
@Getter
public class MonthlyPassPanel extends JPanel {

    private static final String[] COLUMNS = {"ID", "차량 번호", "소유자", "연락처", "시작일", "종료일", "요금(원)", "상태"};

    private DefaultTableModel tableModel;
    private JTable table;

    // 등록 폼
    private JTextField regCarField;
    private JTextField regOwnerField;
    private JTextField regPhoneField;
    private JTextField regStartField;
    private JTextField regEndField;
    private JTextField regFeeField;
    private JButton    registerBtn;
    private JLabel     regResultLabel;

    private JButton deleteBtn;
    private JButton refreshBtn;

    public MonthlyPassPanel() {
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
        JLabel title = new JLabel("월정기권 관리");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 40, 55));
        p.add(title);
        return p;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(buildRegFormCard(), BorderLayout.NORTH);
        content.add(buildTablePanel(),  BorderLayout.CENTER);
        return content;
    }

    private JPanel buildRegFormCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel sectionLbl = new JLabel("정기권 등록");
        sectionLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        sectionLbl.setForeground(new Color(70, 80, 100));
        card.add(sectionLbl);
        card.add(Box.createVerticalStrut(10));

        regCarField   = buildSmallField(160, "차량 번호 예) 12가 3456");
        regOwnerField = buildSmallField(120, "소유자 이름");
        regPhoneField = buildSmallField(140, "예) 010-1234-5678");
        regStartField = buildSmallField(120, "yyyy-MM-dd");
        regEndField   = buildSmallField(120, "yyyy-MM-dd");
        regFeeField   = buildSmallField(100, "요금 (원)");

        // 1행: 차량번호 / 소유자 / 연락처
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row1.setOpaque(false);
        row1.add(buildInlineLabel("차량번호")); row1.add(regCarField);
        row1.add(buildInlineLabel("소유자"));   row1.add(regOwnerField);
        row1.add(buildInlineLabel("연락처"));   row1.add(regPhoneField);

        // 2행: 시작일 / 종료일 / 요금 / 등록버튼
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row2.setOpaque(false);
        row2.add(buildInlineLabel("시작일")); row2.add(regStartField);
        row2.add(buildInlineLabel("종료일")); row2.add(regEndField);
        row2.add(buildInlineLabel("요금"));   row2.add(regFeeField);

        registerBtn = new JButton("등록");
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        registerBtn.setBackground(new Color(37, 99, 235));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setPreferredSize(new Dimension(72, 32));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row2.add(registerBtn);

        card.add(row1);
        card.add(row2);
        card.add(Box.createVerticalStrut(4));

        regResultLabel = new JLabel(" ");
        regResultLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(regResultLabel);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        // 툴바
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        deleteBtn  = buildBtn("선택 삭제", new Color(220, 38, 38), Color.WHITE);
        deleteBtn.setToolTipText("soft delete: is_available = false 처리");
        refreshBtn = buildBtn("새로고침",  new Color(240, 243, 248), new Color(60, 70, 90));
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);

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
        // 컬럼 너비: ID / 차량번호 / 소유자 / 연락처 / 시작일 / 종료일 / 요금 / 상태
        int[] colWidths = {45, 130, 100, 140, 110, 110, 100, 90};
        for (int i = 0; i < colWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // 상태 열 렌더러
        table.getColumnModel().getColumn(7).setCellRenderer(new PassStatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ── 헬퍼 ──────────────────────────────────────────────

    private JTextField buildSmallField(int width, String tip) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setToolTipText(tip);
        f.setPreferredSize(new Dimension(width, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(2, 6, 2, 6)));
        return f;
    }

    private JLabel buildInlineLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(70, 80, 100));
        return lbl;
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

    public String getRegCar()   { return regCarField.getText().trim(); }
    public String getRegOwner() { return regOwnerField.getText().trim(); }
    public String getRegPhone() { return regPhoneField.getText().trim(); }
    public String getRegStart() { return regStartField.getText().trim(); }
    public String getRegEnd()   { return regEndField.getText().trim(); }
    public String getRegFee()   { return regFeeField.getText().trim(); }

    /** 선택된 행의 passId (0번 컬럼) 반환. 선택 없으면 -1 */
    public int getSelectedPassId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        Object val = tableModel.getValueAt(row, 0);
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return -1; }
    }

    /** 선택된 행의 차량번호 (1번 컬럼) 반환. 선택 없으면 null */
    public String getSelectedCarNumber() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object val = tableModel.getValueAt(row, 1);
        return val != null ? val.toString() : null;
    }

    /**
     * 테이블 데이터 설정 (is_available = true 인 행만 전달)
     * row: [passId, carNumber, ownerName, phone, startDate, endDate, fee, 상태문자열]
     * 상태 값: "유효" | "만료 임박" | "만료"
     */
    public void setRows(Object[][] rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) tableModel.addRow(row);
    }

    public void clearRegForm() {
        regCarField.setText("");
        regOwnerField.setText("");
        regPhoneField.setText("");
        regStartField.setText("");
        regEndField.setText("");
        regFeeField.setText("");
        regResultLabel.setText(" ");
    }

    public void setRegResult(String msg, boolean success) {
        regResultLabel.setText(msg);
        regResultLabel.setForeground(success ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }


    // ── 상태 컬럼 렌더러 ──────────────────────────────────

    private static class PassStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);

            if (!isSelected) {
                String v = value == null ? "" : value.toString();
                switch (v) {
                    case "유효":
                        setBackground(new Color(220, 252, 231));
                        setForeground(new Color(22, 101, 52));
                        break;
                    case "만료 임박":
                        setBackground(new Color(254, 249, 195));
                        setForeground(new Color(133, 77, 14));
                        break;
                    case "만료":
                        setBackground(new Color(254, 226, 226));
                        setForeground(new Color(153, 27, 27));
                        break;
                    default:
                        setBackground(Color.WHITE);
                        setForeground(new Color(30, 40, 55));
                }
            }
            return this;
        }
    }
}
