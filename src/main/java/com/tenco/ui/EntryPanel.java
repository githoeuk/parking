package com.tenco.ui;

import com.tenco.model.ParkingZone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * 입차 화면
 * - 차량 번호 입력
 * - 빈 구역 콤보박스 선택
 * - 입차 등록 버튼
 */
public class EntryPanel extends JPanel {

    private JTextField carNumberField;
    private JComboBox<String> zoneCombo;
    private JButton refreshZoneBtn;
    private JButton entryBtn;
    private JLabel resultLabel;

    public EntryPanel() {
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
        JLabel title = new JLabel("입차 등록");
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

        card.add(buildFieldRow("차량 번호", carNumberField = buildTextField("예) 12가 3456")));
        card.add(Box.createVerticalStrut(16));
        card.add(buildZoneRow());
        card.add(Box.createVerticalStrut(28));
        card.add(buildButtonRow());
        card.add(Box.createVerticalStrut(16));
        card.add(buildResultRow());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel buildFieldRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(80, 36));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 80, 100));
        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildZoneRow() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel("주차 구역");
        lbl.setPreferredSize(new Dimension(80, 36));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 80, 100));

        zoneCombo = new JComboBox<>();
        zoneCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        refreshZoneBtn = buildSmallButton("새로고침");

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setOpaque(false);
        right.add(zoneCombo,      BorderLayout.CENTER);
        right.add(refreshZoneBtn, BorderLayout.EAST);

        row.add(lbl,   BorderLayout.WEST);
        row.add(right, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        entryBtn = buildPrimaryButton("입차 등록");
        row.add(entryBtn);
        return row;
    }

    private JPanel buildResultRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        row.add(resultLabel);
        return row;
    }

    // ── 공용 위젯 헬퍼 ────────────────────────────────────

    private JTextField buildTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setToolTipText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(4, 8, 4, 8)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return f;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(new Color(37, 99, 235));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(new Color(240, 243, 248));
        btn.setForeground(new Color(60, 70, 90));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(80, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── 서비스 연결용 public API ───────────────────────────

    public String getCarNumber() {
        return carNumberField.getText().trim();
    }

    public String getSelectedZone() {
        Object sel = zoneCombo.getSelectedItem();
        return sel != null ? sel.toString() : "";
    }

    public void setZoneList(List<ParkingZone> zones) {
        zoneCombo.removeAllItems();
        for (ParkingZone z : zones) {
            zoneCombo.addItem(z.getZoneCode() + " (id:" + z.getZoneId() + ")");
        }
    }

    public void setResult(String msg, boolean success) {
        resultLabel.setText(msg);
        resultLabel.setForeground(success ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }

    public void clearForm() {
        carNumberField.setText("");
        resultLabel.setText(" ");
    }

    public JButton getEntryBtn()      { return entryBtn; }
    public JButton getRefreshZoneBtn(){ return refreshZoneBtn; }
    public JComboBox<String> getZoneCombo() { return zoneCombo; }
}
