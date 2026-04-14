package com.tenco.main;

import com.tenco.dao.ParkingZoneDAO;
import com.tenco.model.MonthlyPass;
import com.tenco.model.ParkingRecord;
import com.tenco.model.ParkingZone;
import com.tenco.service.FeeCalculator;
import com.tenco.service.MonthlyPassService;
import com.tenco.service.ParkingService;
import com.tenco.ui.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {

    private static final ParkingService parkingService       = new ParkingService();
    private static final MonthlyPassService monthlyPassService = new MonthlyPassService();
    private static final FeeCalculator feeCalculator         = new FeeCalculator();
    private static final ParkingZoneDAO parkingZoneDAO       = new ParkingZoneDAO();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter D_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            bindEntry(frame);
            bindExit(frame);
            bindStatus(frame);
            bindHistory(frame);
            bindMonthlyPass(frame);
            bindZoneManage(frame);

            // 시작 시 현황 자동 로드
            loadStatus(frame.getStatusPanel());

            frame.setVisible(true);
        });
    }

    // ── 입차 ──────────────────────────────────────────────

    private static void bindEntry(MainFrame frame) {
        EntryPanel panel = frame.getEntryPanel();

        // 구역 새로고침
        panel.getRefreshZoneBtn().addActionListener(e -> loadAvailableZones(panel));

        // 입차 등록
        panel.getEntryBtn().addActionListener(e -> {
            String carNumber = panel.getCarNumber();
            String zoneCode  = panel.getSelectedZone();

            if (carNumber.isEmpty()) {
                panel.setResult("차량 번호를 입력해주세요.", false);
                return;
            }
            if (zoneCode.isEmpty() || zoneCode.equals("빈 구역 없음")) {
                panel.setResult("주차 구역을 선택해주세요.", false);
                return;
            }

            try {
                parkingService.parking(carNumber, zoneCode);
                panel.setResult("입차 등록 완료: " + carNumber + " → " + zoneCode, true);
                panel.clearForm();
                loadAvailableZones(panel);
            } catch (SQLException ex) {
                panel.setResult("오류: " + ex.getMessage(), false);
            }
        });

        // 초기 구역 로드
        loadAvailableZones(panel);
    }

    private static void loadAvailableZones(EntryPanel panel) {
        try {
            List<ParkingZone> zones = parkingService.getParkingZoneList();
            panel.setZoneList(zones);
        } catch (SQLException ex) {
            panel.setResult("구역 목록 조회 실패: " + ex.getMessage(), false);
        }
    }

    // ── 출차 ──────────────────────────────────────────────

    private static void bindExit(MainFrame frame) {
        ExitPanel panel = frame.getExitPanel();

        // 조회
        panel.getSearchBtn().addActionListener(e -> {
            String carNumber = panel.getCarNumber();
            if (carNumber.isEmpty()) {
                panel.clearInfo();
                panel.setResult("차량 번호를 입력해주세요.", false);
                return;
            }

            try {
                ParkingRecord record = parkingService.getRecordByCarNum(carNumber);
                if (record == null || record.getExitTime() != null) {
                    panel.clearInfo();
                    panel.setResult("현재 입차 중인 차량이 없습니다.", false);
                    return;
                }

                LocalDateTime now = LocalDateTime.now();
                long minutes = ChronoUnit.MINUTES.between(record.getEntryTime(), now);
                BigDecimal fee = feeCalculator.calculateFee(carNumber);

                panel.setInfoZone(record.getZoneCode() != null ? record.getZoneCode() : String.valueOf(record.getZoneId()));
                panel.setInfoEntryTime(record.getEntryTime().format(DT_FMT));
                panel.setInfoDuration(minutes + " 분");
                panel.setInfoFee(String.format("%,.0f 원", fee.doubleValue()));

                boolean hasPass = monthlyPassService.hasValidPass(carNumber);
                panel.setInfoPass(hasPass ? "유효 (요금 면제)" : "없음");

                panel.enableExitBtn(true);
                panel.setResult(" ", true);

            } catch (SQLException ex) {
                panel.clearInfo();
                panel.setResult("조회 실패: " + ex.getMessage(), false);
            }
        });

        // 출차 처리
        panel.getExitBtn().addActionListener(e -> {
            String carNumber = panel.getCarNumber();
            try {
                parkingService.exiting(carNumber);
                panel.setResult("출차 처리 완료: " + carNumber, true);
                panel.clearInfo();
            } catch (SQLException ex) {
                panel.setResult("출차 실패: " + ex.getMessage(), false);
            }
        });
    }

    // ── 주차 현황 ──────────────────────────────────────────

    private static void bindStatus(MainFrame frame) {
        StatusPanel panel = frame.getStatusPanel();
        panel.getRefreshBtn().addActionListener(e -> loadStatus(panel));
    }

    private static void loadStatus(StatusPanel panel) {
        try {
            List<ParkingRecord> records = parkingService.getCurrentParkingRecords();

            Object[][] rows = new Object[records.size()][3];
            int occupied = 0;
            int empty = 0;

            for (int i = 0; i < records.size(); i++) {
                ParkingRecord r = records.get(i);
                rows[i][0] = r.getZoneCode();
                rows[i][1] = r.getCarNumber() != null ? r.getCarNumber() : "";
                rows[i][2] = r.getEntryTime() != null ? r.getEntryTime().format(DT_FMT) : "";

                if (r.getCarNumber() != null) occupied++;
                else empty++;
            }

            panel.setRows(rows);
            panel.setOccupiedCount(occupied);
            panel.setEmptyCount(empty);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "현황 조회 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── 이력 조회 ──────────────────────────────────────────

    private static void bindHistory(MainFrame frame) {
        HistoryPanel panel = frame.getHistoryPanel();

        // 전체 조회
        panel.getAllBtn().addActionListener(e -> {
            panel.resetFilter();
            loadHistory(panel, "", "", "");
        });

        // 검색
        panel.getSearchBtn().addActionListener(e ->
            loadHistory(panel, panel.getCarNumber(), panel.getFromDate(), panel.getToDate())
        );

        // 초기화
        panel.getResetBtn().addActionListener(e -> {
            panel.resetFilter();
            loadHistory(panel, "", "", "");
        });
    }

    private static void loadHistory(HistoryPanel panel, String carNumber, String fromDate, String toDate) {
        try {
            List<ParkingRecord> records = parkingService.getParkingRecords(carNumber, fromDate, toDate);

            Object[][] rows = new Object[records.size()][5];
            for (int i = 0; i < records.size(); i++) {
                ParkingRecord r = records.get(i);
                rows[i][0] = r.getCarNumber();
                rows[i][1] = r.getZoneCode();
                rows[i][2] = r.getEntryTime() != null ? r.getEntryTime().format(DT_FMT) : "";
                rows[i][3] = r.getExitTime()  != null ? r.getExitTime().format(DT_FMT)  : "주차 중";
                rows[i][4] = r.getFee() != null ? String.format("%,.0f", r.getFee().doubleValue()) : "";
            }
            panel.setRows(rows);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "이력 조회 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── 월정기권 ───────────────────────────────────────────

    private static void bindMonthlyPass(MainFrame frame) {
        MonthlyPassPanel panel = frame.getMonthlyPassPanel();

        // 등록
        panel.getRegisterBtn().addActionListener(e -> {
            String car   = panel.getRegCar();
            String owner = panel.getRegOwner();
            String phone = panel.getRegPhone();
            String start = panel.getRegStart();
            String end   = panel.getRegEnd();
            String fee   = panel.getRegFee();

            if (car.isEmpty() || owner.isEmpty() || start.isEmpty() || end.isEmpty()) {
                panel.setRegResult("차량번호, 소유자, 시작일, 종료일은 필수입니다.", false);
                return;
            }

            try {
                MonthlyPass pass = MonthlyPass.builder()
                        .carNumber(car)
                        .ownerName(owner)
                        .phone(phone)
                        .startDate(Date.valueOf(start))
                        .endDate(Date.valueOf(end))
                        .fee(fee.isEmpty() ? BigDecimal.ZERO : new BigDecimal(fee))
                        .build();

                monthlyPassService.monthInsert(pass);
                panel.setRegResult("정기권 등록 완료: " + car, true);
                panel.clearRegForm();
                loadMonthlyPass(panel);

            } catch (Exception ex) {
                panel.setRegResult("등록 실패: " + ex.getMessage(), false);
            }
        });

        // 삭제
        panel.getDeleteBtn().addActionListener(e -> {
            String carNumber = panel.getSelectedCarNumber();
            if (carNumber == null) {
                JOptionPane.showMessageDialog(panel, "삭제할 정기권을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                monthlyPassService.monthDelete(carNumber);
                loadMonthlyPass(panel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "삭제 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 새로고침
        panel.getRefreshBtn().addActionListener(e -> loadMonthlyPass(panel));

        loadMonthlyPass(panel);
    }

    private static void loadMonthlyPass(MonthlyPassPanel panel) {
        try {
            List<MonthlyPass> list = monthlyPassService.monthSubList();
            LocalDate today = LocalDate.now();

            Object[][] rows = list.stream()
                    .filter(p -> p.isAvailable())
                    .map(p -> {
                        LocalDate endDate = p.getEndDate().toLocalDate();
                        long daysLeft = ChronoUnit.DAYS.between(today, endDate);
                        String status;
                        if (daysLeft < 0)          status = "만료";
                        else if (daysLeft <= 7)    status = "만료 임박";
                        else                       status = "유효";

                        return new Object[]{
                                p.getPassId(),
                                p.getCarNumber(),
                                p.getOwnerName(),
                                p.getPhone() != null ? p.getPhone() : "",
                                p.getStartDate(),
                                p.getEndDate(),
                                p.getFee() != null ? String.format("%,.0f", p.getFee().doubleValue()) : "0",
                                status
                        };
                    })
                    .toArray(Object[][]::new);

            panel.setRows(rows);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "정기권 조회 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── 구역 관리 ──────────────────────────────────────────

    private static void bindZoneManage(MainFrame frame) {
        ZoneManagePanel panel = frame.getZoneManagePanel();

        // 추가
        panel.getAddBtn().addActionListener(e -> {
            String code = panel.getAddCode();
            if (code.isEmpty()) {
                panel.setAddResult("구역 코드를 입력해주세요.", false);
                return;
            }
            try {
                parkingZoneDAO.insertParkingZone(code);
                panel.setAddResult("구역 추가 완료: " + code, true);
                panel.clearAddForm();
                loadZones(panel);
            } catch (Exception ex) {
                panel.setAddResult("추가 실패: " + ex.getMessage(), false);
            }
        });

        // 삭제
        panel.getDeleteBtn().addActionListener(e -> {
            int zoneId = panel.getSelectedZoneId();
            if (zoneId < 0) {
                JOptionPane.showMessageDialog(panel, "삭제할 구역을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                parkingZoneDAO.deleteParkingZone(zoneId);
                loadZones(panel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "삭제 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 상태 변경 (토글)
        panel.getToggleBtn().addActionListener(e -> {
            int zoneId = panel.getSelectedZoneId();
            if (zoneId < 0) {
                JOptionPane.showMessageDialog(panel, "상태를 변경할 구역을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                boolean current = parkingZoneDAO.checkZoneById(zoneId);
                parkingZoneDAO.updateParkingZone(zoneId, !current);
                loadZones(panel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "상태 변경 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 새로고침
        panel.getRefreshBtn().addActionListener(e -> loadZones(panel));

        loadZones(panel);
    }

    private static void loadZones(ZoneManagePanel panel) {
        try {
            List<ParkingZone> zones = parkingZoneDAO.getAllParkingZone();
            Object[][] rows = zones.stream()
                    .map(z -> new Object[]{
                            z.getZoneId(),
                            z.getZoneCode(),
                            z.isAvailable() ? "사용 가능" : "점검 중"
                    })
                    .toArray(Object[][]::new);
            panel.setRows(rows);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "구역 조회 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
