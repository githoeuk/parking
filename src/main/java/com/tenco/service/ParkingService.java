package com.tenco.service;

import com.tenco.dao.ParkingRecordDAO;
import com.tenco.dao.ParkingZoneDAO;
import com.tenco.model.ParkingRecord;
import com.tenco.model.ParkingZone;

import java.sql.SQLException;
import java.util.List;

public class ParkingService {

    private final ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAO();
    private final ParkingZoneDAO parkingZoneDAO = new ParkingZoneDAO();

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

        parkingRecordDAO.parking(carNumber, zoneId);
    }

    // 출차
    public void exiting(String carNumber) throws SQLException {
        if(carNumber.trim().isEmpty()){
            throw new SQLException("주차 구역은 필수 항목입니다.");
        }
        parkingRecordDAO.exiting(carNumber);
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
