package com.tenco.ui;

import com.tenco.model.Admin;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import static com.tenco.ui.UIFont.*;

@Getter
public class AdminPanel extends JPanel {

    // 관리자 추가 폼
    private JTextField addUserIdField;
    private JPasswordField addPasswordField;
    private JTextField addNameField;
    private JButton addBtn;
    private JLabel addResultLabel;

    // 관리자 목록
    private JTable adminTable;
    private DefaultTableModel tableModel;
    private JButton deleteBtn;
    private JButton refreshBtn;

    public AdminPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildAddForm(), BorderLayout.NORTH);
        add(buildListPanel(), BorderLayout.CENTER);
    }

    private JPanel buildAddForm() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                "관리자 추가",
                TitledBorder.LEFT, TitledBorder.TOP,
                bold(13), new Color(40, 55, 80)
            ),
            new EmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 아이디
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(label("아이디"), gbc);
        addUserIdField = textField();
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(addUserIdField, gbc);

        // 비밀번호
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(label("비밀번호"), gbc);
        addPasswordField = new JPasswordField();
        addPasswordField.setFont(plain(13));
        addPasswordField.setPreferredSize(new Dimension(160, 32));
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(addPasswordField, gbc);

        // 이름
        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(label("이름"), gbc);
        addNameField = textField();
        gbc.gridx = 5; gbc.weightx = 1.0;
        panel.add(addNameField, gbc);

        // 추가 버튼
        addBtn = new JButton("추가");
        addBtn.setFont(bold(13));
        addBtn.setBackground(new Color(30, 40, 55));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setPreferredSize(new Dimension(80, 32));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 6; gbc.weightx = 0;
        panel.add(addBtn, gbc);

        // 결과 메시지
        addResultLabel = new JLabel(" ");
        addResultLabel.setFont(plain(12));
        GridBagConstraints resultGbc = new GridBagConstraints();
        resultGbc.gridx = 0; resultGbc.gridy = 1;
        resultGbc.gridwidth = 7;
        resultGbc.insets = new Insets(2, 8, 4, 8);
        resultGbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addResultLabel, resultGbc);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                "관리자 목록",
                TitledBorder.LEFT, TitledBorder.TOP,
                bold(13), new Color(40, 55, 80)
            ),
            new EmptyBorder(8, 8, 8, 8)
        ));

        // 테이블
        String[] cols = {"ID", "아이디", "이름", "등록일"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        adminTable = new JTable(tableModel);
        adminTable.setFont(plain(13));
        adminTable.setRowHeight(28);
        adminTable.getTableHeader().setFont(bold(12));
        adminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminTable.setGridColor(new Color(220, 225, 235));

        // 컬럼 너비
        adminTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        adminTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        adminTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        adminTable.getColumnModel().getColumn(3).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(adminTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));
        panel.add(scroll, BorderLayout.CENTER);

        // 버튼 바
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setBackground(new Color(245, 247, 250));

        refreshBtn = new JButton("새로고침");
        styleBtn(refreshBtn, false);
        btnBar.add(refreshBtn);

        deleteBtn = new JButton("비활성화");
        styleBtn(deleteBtn, true);
        btnBar.add(deleteBtn);

        panel.add(btnBar, BorderLayout.SOUTH);
        return panel;
    }

    // ── 외부 호출용 메서드 ────────────────────────────────────

    public void setRows(List<Admin> admins) {
        tableModel.setRowCount(0);
        for (Admin a : admins) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getUserId(),
                a.getName(),
                a.getCreatedAt() != null ? a.getCreatedAt().toString().replace("T", " ") : ""
            });
        }
    }

    public String getSelectedUserId() {
        int row = adminTable.getSelectedRow();
        if (row < 0) return null;
        return (String) tableModel.getValueAt(row, 1);
    }

    public String getAddUserId()   { return addUserIdField.getText().trim(); }
    public String getAddPassword() { return new String(addPasswordField.getPassword()); }
    public String getAddName()     { return addNameField.getText().trim(); }

    public void clearAddForm() {
        addUserIdField.setText("");
        addPasswordField.setText("");
        addNameField.setText("");
    }

    public void setAddResult(String msg, boolean success) {
        addResultLabel.setText(msg);
        addResultLabel.setForeground(success ? new Color(30, 130, 70) : new Color(200, 60, 60));
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(plain(13));
        lbl.setForeground(new Color(60, 75, 100));
        return lbl;
    }

    private JTextField textField() {
        JTextField f = new JTextField();
        f.setFont(plain(13));
        f.setPreferredSize(new Dimension(160, 32));
        return f;
    }

    private void styleBtn(JButton btn, boolean danger) {
        btn.setFont(plain(13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 32));
        if (danger) {
            btn.setBackground(new Color(180, 50, 50));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(90, 110, 140));
            btn.setForeground(Color.WHITE);
        }
    }
}