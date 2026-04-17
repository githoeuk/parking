package com.tenco.service;

import com.tenco.dao.ParkingRecordDAO;
import com.tenco.dao.ParkingZoneDAO;
import com.tenco.model.ParkingRecord;
import com.tenco.model.ParkingZone;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingService {

    private final ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAO();
    private final ParkingZoneDAO parkingZoneDAO = new ParkingZoneDAO();
    private final FeeCalculator feeCalculator = new FeeCalculator();

    // 입차 등록
    public void parking(String carNumber, String zoneCode) throws SQLException {

        if(carNumber.trim().isEmpty()){
            throw new SQLException("차번호는 필수 항목입니다.");
        }

        if(zoneCode.trim().isEmpty()){
            throw new SQLException("주차 구역은 필수 항목입니다.");
        }

        int zoneId = parkingZoneDAO.getZoneIdByZoneCode(zoneCode);
        if(zoneId == -1){
            throw new SQLException("입차 등록 실패");
        }

        if(!parkingZoneDAO.checkZoneById(zoneId)){
            throw new SQLException("이미 주차되어 있습니다.");
        }

        if (parkingRecordDAO.selectByCarNum(carNumber) != null){
            throw new SQLException("이미 주차된 차량입니다.");
        }

        parkingRecordDAO.parking(carNumber.replaceAll(" ", ""), zoneId);
    }

    // 출차
    public void exiting(String carNumber) throws SQLException {
        if(carNumber.trim().isEmpty()){
            throw new SQLException("차량 번호는 필수 항목입니다.");
        }

        BigDecimal fee = feeCalculator.calculateFee(carNumber);
        parkingRecordDAO.exiting(carNumber, fee);
        System.out.println("성공");
    }

    // 주차 현황
    public List<ParkingRecord> getCurrentParkingRecords() throws SQLException {
        return parkingRecordDAO.getCurrentParkingRecords();
    }

    // 이력 조회
    public List<ParkingRecord> getParkingRecords() throws SQLException {
        return parkingRecordDAO.getParkingRecords();
    }

    // 입차 중인 차량 조회 (출차 화면용)
    public ParkingRecord getRecordByCarNum(String carNumber) throws SQLException {
        return parkingRecordDAO.selectByCarNum(carNumber.replaceAll(" ", ""));
    }

    // 이력 조회 (필터 포함)
    public List<ParkingRecord> getParkingRecords(String carNumber, String fromDate, String toDate) throws SQLException {
        List<ParkingRecord> all = parkingRecordDAO.getParkingRecords();
        return all.stream()
                .filter(r -> carNumber.isEmpty() || r.getCarNumber().contains(carNumber))
                .filter(r -> {
                    if (fromDate.isEmpty()) return true;
                    try {
                        return !r.getEntryTime().toLocalDate().isBefore(LocalDate.parse(fromDate));
                    } catch (Exception e) { return true; }
                })
                .filter(r -> {
                    if (toDate.isEmpty()) return true;
                    try {
                        return !r.getEntryTime().toLocalDate().isAfter(LocalDate.parse(toDate));
                    } catch (Exception e) { return true; }
                })
                .collect(Collectors.toList());
    }

    // 전체 구역 출력
    public List<ParkingZone> getParkingZoneList() throws SQLException {
        return parkingZoneDAO.getParkingZoneList();
    }

    // 구역 추가
    public void insertParkingZone(String zoneCode) throws SQLException {
        parkingZoneDAO.insertParkingZone(zoneCode);
        System.out.println("성공");
    }
}
