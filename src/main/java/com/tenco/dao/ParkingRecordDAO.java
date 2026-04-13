package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.ParkingRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ParkingRecordDAO {

    ParkingRecord parkingRecord = new ParkingRecord();

    // 입차 시 INSERT
    public void parking(String carNumber, int zoneId) throws SQLException {
        String sql = """
                INSERT INTO parking_record(car_number, zone_id, entry_time)
                VALUES (?, ?, current_time())
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);
            pstmt.setInt(2, zoneId);
            pstmt.executeUpdate();
        }
    }



    // carNumber로 요금 조회
    public void selectByCarNum(String carNumber) throws SQLException {
        String sql = """
                SELECT car_number, fee FROM parking_record WHERE car_number = ? ORDER BY exit_time LIMIT 1;
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(carNumber + " 의 주차 요금 : " + parkingRecord.getFee());
                }
            }
        }
    }

    // 출차 시 is_available 업데이트 --> is_available 은 parkingZone 에서?

    // 구역 조회(차번호 검색 시 어디 구역에 주차되어있는지)

    // 주차 가능 잔여 자리 조회 (capacity 컬럼 없음)

    // 테스트 코드
    public static void main(String[] args) throws SQLException {

        ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAO();
        // parkingRecordDAO.parking("18수 1234", 1);

        parkingRecordDAO.selectByCarNum("12바 1234");

    }

}
