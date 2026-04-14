package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.ParkingRecord;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ParkingRecordDAO {


    // 입차 시 INSERT
    public void parking(String carNumber, int zoneId) throws SQLException {
        Connection conn = null;
        String insertSql = """
                INSERT INTO parking_record(car_number, zone_id, entry_time)
                VALUES (?, ?, NOW())
                """;

        String updateSql = """
                update parking_zone
                set is_available = ?
                where zone_id = ?
                """;
        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(insertSql)){
                stmt.setString(1, carNumber);
                stmt.setInt(2, zoneId);
                stmt.executeUpdate();
            }

            try(PreparedStatement stmt = conn.prepareStatement(updateSql)){
                stmt.setBoolean(1, false);
                stmt.setInt(2, zoneId);
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e){
            if(conn != null){
                conn.rollback();
                throw new SQLException("입차 실패");
            }
        } finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<ParkingRecord> getCurrentParkingRecords() throws SQLException {
        String sql = """
                select pz.zone_code, pr.car_number, pr.entry_time
                from parking_record pr
                right join parking_zone pz on pr.zone_id = pz.zone_id
                """;

        List<ParkingRecord> records = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                records.add(ParkingRecord.builder()
                        .zoneCode(rs.getString("zone_code"))
                        .carNumber(rs.getString("car_number"))
                        .entryTime(rs.getTimestamp("entry_time") == null ? null : rs.getTimestamp("entry_time").toLocalDateTime())
                        .build()
                );
            }
        }

        return records;
    }

    public List<ParkingRecord> getParkingRecords() throws SQLException {
        String sql = """
                select pr.car_number, pz.zone_code, pr.entry_time, pr.exit_time, pr.fee
                from parking_record pr
                join parking_zone pz on pr.zone_id = pz.zone_id
                """;

        List<ParkingRecord> records = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                records.add(ParkingRecord.builder()
                        .carNumber(rs.getString("car_number"))
                        .zoneCode(rs.getString("zone_code"))
                        .entryTime(rs.getTimestamp("entry_time").toLocalDateTime())
                        .exitTime(rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null)
                        .fee(rs.getBigDecimal("fee"))
                        .build()
                );
            }
        }
        return records;
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

    // 출차 시간 기록
    public void exiting(String carNumber) throws SQLException {
        String selectSql = """
                select * from parking_record where car_number = ? and exit_time is null
                """;
        List<ParkingRecord> records = new ArrayList<>();

        String updateSql = """
                UPDATE parking_record SET exit_time = NOW() WHERE car_number = ? AND exit_time IS NULL
                """;

        String updateSql2 = """
                UPDATE parking_zone SET is_available = true WHERE car_number = ?
                """;

        Connection conn = null;

        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(updateSql)){
                stmt.setString(1, carNumber);
                stmt.executeUpdate();
            }

            try(PreparedStatement stmt = conn.prepareStatement(updateSql2)){
                stmt.setString(1, carNumber);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e){
            if(conn != null){
                conn.rollback();
                throw new SQLException("출차 실패");
            }
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
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
