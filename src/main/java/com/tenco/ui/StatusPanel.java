package com.tenco.ui;

import com.tenco.model.ParkingZone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 주차 현황 화면
 * - 전체 구역 목록 테이블 (구역코드 / 상태 / 차량번호 / 입차시각)
 * - 사용 가능: 초록 / 사용 중: 빨강 / 점검 중: 노랑
 * - 빈 자리 수 표시
 */
public class StatusPanel extends JPanel {

    private static final String[] COLUMNS = {"구역 코드", "상태", "차량 번호", "입차 시각"};

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel emptyCountLabel;
    private JLabel occupiedCountLabel;
    private JButton refreshBtn;

    public StatusPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("주차 현황");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 40, 55));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        occupiedCountLabel = new JLabel("주차 중: —");
        occupiedCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        occupiedCountLabel.setForeground(new Color(220, 38, 38));

        emptyCountLabel = new JLabel("빈 자리: —");
        emptyCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        emptyCountLabel.setForeground(new Color(22, 163, 74));

        refreshBtn = new JButton("새로고침");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(37, 99, 235));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setPreferredSize(new Dimension(90, 32));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        right.add(occupiedCountLabel);
        right.add(emptyCountLabel);
        right.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 243, 248));
        table.getTableHeader().setForeground(new Color(70, 80, 100));
        table.setGridColor(new Color(230, 234, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setShowVerticalLines(false);

        // 상태 열 색상 렌더러
        table.getColumnModel().getColumn(1).setCellRenderer(new StatusCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── 서비스 연결용 public API ───────────────────────────

    /**
     * 테이블 데이터 갱신
     * row 구성: [구역코드, 상태, 차량번호, 입차시각]
     */
    public void setRows(Object[][] rows) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    public void setEmptyCount(int count) {
        emptyCountLabel.setText("빈 자리: " + count + " 개");
    }

    public void setOccupiedCount(int count) {
        occupiedCountLabel.setText("주차 중: " + count + " 대");
    }

    public JButton getRefreshBtn() { return refreshBtn; }

    // ── 상태 컬럼 색상 렌더러 ──────────────────────────────

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);

            if (!isSelected) {
                String v = value == null ? "" : value.toString();
                switch (v) {
                    case "사용 가능":
                        setBackground(new Color(220, 252, 231));
                        setForeground(new Color(22, 101, 52));
                        break;
                    case "사용 중":
                        setBackground(new Color(254, 226, 226));
                        setForeground(new Color(153, 27, 27));
                        break;
                    case "점검 중":
                        setBackground(new Color(254, 249, 195));
                        setForeground(new Color(133, 77, 14));
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
