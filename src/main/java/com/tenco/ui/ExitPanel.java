package com.tenco.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 출차 화면
 * - 차량 번호 입력 → 조회
 * - 입차 정보 + 예상 요금 표시
 * - 출차 처리 버튼
 */
@Getter
public class ExitPanel extends JPanel {

    private JTextField carNumberField;

    private JButton searchBtn;

    private JLabel infoZoneLabel;
    private JLabel infoEntryTimeLabel;
    private JLabel infoDurationLabel;
    private JLabel infoFeeLabel;
    private JLabel infoPassLabel;

    private JButton exitBtn;
    private JLabel resultLabel;

    public ExitPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildTitleBar(), BorderLayout.NORTH);
        add(buildForm(),     BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel title = new JLabel("출차 처리");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 40, 55));
        p.add(title);
        return p;
    }

    private JPanel buildForm() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(28, 32, 28, 32)));

        // 검색 행
        card.add(buildSearchRow());
        card.add(Box.createVerticalStrut(24));

        // 구분선
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(230, 234, 240));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        // 입차 정보 섹션
        JLabel sectionLbl = new JLabel("입차 정보");
        sectionLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        sectionLbl.setForeground(new Color(100, 110, 130));
        card.add(sectionLbl);
        card.add(Box.createVerticalStrut(12));

        infoZoneLabel      = buildInfoLabel("—");
        infoEntryTimeLabel = buildInfoLabel("—");
        infoDurationLabel  = buildInfoLabel("—");
        infoFeeLabel       = buildInfoLabel("—");
        infoPassLabel      = buildInfoLabel("—");

        card.add(buildInfoRow("구역",       infoZoneLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(buildInfoRow("입차 시각",  infoEntryTimeLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(buildInfoRow("주차 시간",  infoDurationLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(buildInfoRow("요금",       infoFeeLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(buildInfoRow("정기권",     infoPassLabel));
        card.add(Box.createVerticalStrut(24));

        // 출차 버튼
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        exitBtn = buildPrimaryButton("출차 처리", new Color(220, 38, 38));
        exitBtn.setEnabled(false);
        btnRow.add(exitBtn);
        card.add(btnRow);
        card.add(Box.createVerticalStrut(12));

        // 결과 메시지
        JPanel resultRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        resultRow.setOpaque(false);
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultRow.add(resultLabel);
        card.add(resultRow);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel buildSearchRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel("차량 번호");
        lbl.setPreferredSize(new Dimension(80, 36));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 80, 100));

        carNumberField = new JTextField() {
            private static final String PLACEHOLDER = "예) 12가 3456";
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(180, 185, 200));
                    g.setFont(getFont().deriveFont(java.awt.Font.ITALIC));
                    java.awt.Insets ins = getInsets();
                    g.drawString(PLACEHOLDER, ins.left + 2, getHeight() - ins.bottom - 4);
                }
            }
        };
        carNumberField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        carNumberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(4, 8, 4, 8)));

        searchBtn = new JButton("조회");
        searchBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchBtn.setBackground(new Color(37, 99, 235));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(72, 36));
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        row.add(lbl,           BorderLayout.WEST);
        row.add(carNumberField, BorderLayout.CENTER);
        row.add(searchBtn,     BorderLayout.EAST);
        return row;
    }

    private JPanel buildInfoRow(String key, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel keyLbl = new JLabel(key);
        keyLbl.setPreferredSize(new Dimension(80, 24));
        keyLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        keyLbl.setForeground(new Color(100, 110, 130));

        row.add(keyLbl,     BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        return row;
    }

    private JLabel buildInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(30, 40, 55));
        return lbl;
    }

    private JButton buildPrimaryButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── 서비스 연결용 public API ───────────────────────────

    public String getCarNumber() {
        return carNumberField.getText().trim();
    }

    public void setInfoZone(String v)      { infoZoneLabel.setText(v); }
    public void setInfoEntryTime(String v) { infoEntryTimeLabel.setText(v); }
    public void setInfoDuration(String v)  { infoDurationLabel.setText(v); }
    public void setInfoFee(String v)       { infoFeeLabel.setText(v); }
    public void setInfoPass(String v)      { infoPassLabel.setText(v); }

    public void clearInfo() {
        infoZoneLabel.setText("—");
        infoEntryTimeLabel.setText("—");
        infoDurationLabel.setText("—");
        infoFeeLabel.setText("—");
        infoPassLabel.setText("—");
        exitBtn.setEnabled(false);
        resultLabel.setText(" ");
    }

    public void enableExitBtn(boolean enable) {
        exitBtn.setEnabled(enable);
    }

    public void setResult(String msg, boolean success) {
        resultLabel.setText(msg);
        resultLabel.setForeground(success ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }

}
