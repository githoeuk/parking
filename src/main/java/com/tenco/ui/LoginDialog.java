package com.tenco.ui;

import com.tenco.service.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

import static com.tenco.ui.UIFont.*;

public class LoginDialog extends JDialog {

    private final AdminService adminService;
    private boolean loginSuccess = false;

    private JTextField userIdField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginDialog(AdminService adminService) {
        this.adminService = adminService;
        setTitle("주차관리시스템 - 로그인");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // 상단 헤더
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 40, 55));
        header.setPreferredSize(new Dimension(400, 100));
        header.setLayout(new GridBagLayout());

        JLabel logoLabel = new JLabel("P 주차관리시스템");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(bold(20));
        header.add(logoLabel);

        root.add(header, BorderLayout.NORTH);

        // 중앙 폼
        JPanel form = new JPanel();
        form.setBackground(new Color(245, 247, 250));
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(30, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // 로그인 타이틀
        JLabel title = new JLabel("관리자 로그인");
        title.setFont(bold(16));
        title.setForeground(new Color(40, 55, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        form.add(title, gbc);

        // 아이디 레이블
        JLabel idLabel = new JLabel("아이디");
        idLabel.setFont(plain(13));
        idLabel.setForeground(new Color(80, 95, 120));
        gbc.gridy = 1; gbc.insets = new Insets(16, 0, 4, 0);
        form.add(idLabel, gbc);

        // 아이디 필드
        userIdField = new JTextField();
        userIdField.setFont(plain(14));
        userIdField.setPreferredSize(new Dimension(300, 40));
        userIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        form.add(userIdField, gbc);

        // 비밀번호 레이블
        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setFont(plain(13));
        pwLabel.setForeground(new Color(80, 95, 120));
        gbc.gridy = 3; gbc.insets = new Insets(8, 0, 4, 0);
        form.add(pwLabel, gbc);

        // 비밀번호 필드
        passwordField = new JPasswordField();
        passwordField.setFont(plain(14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 8, 0);
        form.add(passwordField, gbc);

        // 오류 메시지
        messageLabel = new JLabel(" ");
        messageLabel.setFont(plain(12));
        messageLabel.setForeground(new Color(200, 60, 60));
        gbc.gridy = 5; gbc.insets = new Insets(4, 0, 4, 0);
        form.add(messageLabel, gbc);

        // 로그인 버튼
        JButton loginBtn = new JButton("로그인");
        loginBtn.setFont(bold(14));
        loginBtn.setPreferredSize(new Dimension(300, 44));
        loginBtn.setBackground(new Color(30, 40, 55));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(new Color(50, 65, 90));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(new Color(30, 40, 55));
            }
        });
        gbc.gridy = 6; gbc.insets = new Insets(12, 0, 0, 0);
        form.add(loginBtn, gbc);

        root.add(form, BorderLayout.CENTER);

        // 엔터키로 로그인
        passwordField.addActionListener(e -> doLogin());
        userIdField.addActionListener(e -> passwordField.requestFocus());
        loginBtn.addActionListener(e -> doLogin());

        // 창 닫으면 앱 종료
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void doLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        try {
            adminService.login(userId, password);
            loginSuccess = true;
            dispose();
        } catch (SQLException ex) {
            messageLabel.setText(ex.getMessage());
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}