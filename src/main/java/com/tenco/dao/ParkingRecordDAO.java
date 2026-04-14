package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.ParkingRecord;

import java.math.BigDecimal;
import java.sql.*;


public class ParkingRecordDAO {


    // 입차 시 INSERT
    public void parking(String carNumber, int zoneId) throws SQLException {
        String sql = """
                INSERT INTO parking_record(car_number, zone_id, entry_time)
                VALUES (?, ?, NOW())
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);
            pstmt.setInt(2, zoneId);
            pstmt.executeUpdate();
        }
    }



    // carNumber로 정보 조회(요금 조회 및 입출차 확인용)
    public ParkingRecord selectByCarNum(String carNumber) throws SQLException {
        String sql = """
                SELECT * FROM parking_record WHERE car_number = ? ORDER BY entry_time DESC LIMIT 1
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToParkingRecord(rs);
                }
            }
        }
        return null;
    }


    private ParkingRecord mapToParkingRecord(ResultSet rs) throws SQLException {
        Timestamp exitTimestamp = rs.getTimestamp("exit_time");
        BigDecimal feeValue = rs.getBigDecimal("fee");
        return ParkingRecord.builder()
                .recordId(rs.getInt("record_id"))
                .carNumber(rs.getString("car_number"))
                .zoneId(rs.getInt("zone_id"))
                .entryTime(rs.getTimestamp("entry_time").toLocalDateTime())
                .exitTime(exitTimestamp != null ? exitTimestamp.toLocalDateTime() : null)
                .fee(feeValue != null ? feeValue : java.math.BigDecimal.ZERO)
                .build();
    }


}
