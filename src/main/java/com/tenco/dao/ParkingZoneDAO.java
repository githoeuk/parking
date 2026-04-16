package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.ParkingZone;

import javax.swing.text.html.HTMLDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParkingZoneDAO {
    //1.zone id로 검색했을때 자리있는지
    public boolean checkZoneById(int zoneId) throws SQLException {
        String sql = """
                select is_available from parking_zone
                where zone_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, zoneId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next())
                    return rs.getBoolean("is_available");
            }

        }
        return false;
    }

    // 구역 추가
    public void insertParkingZone(String zoneCode){
        String sql = """
                INSERT INTO parking_zone (zone_code) VALUES (?)
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, zoneCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 전체 parkingZone
    public List<ParkingZone> getAllParkingZone() throws SQLException {
        String sql = """
                SELECT * FROM parking_zone
                """;
        List<ParkingZone> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                list.add(
                        ParkingZone.builder()
                                .zoneId(rs.getInt("zone_id"))
                                .zoneCode(rs.getString("zone_code"))
                                .isAvailable(rs.getBoolean("is_available"))
                                .build()
                );
            }
        }
        return list;
    }

    //2.사용가능한 zone 코드
    public List<ParkingZone> getParkingZoneList() throws SQLException {
        List<ParkingZone> list = new ArrayList<>();
        String sql = """
                select zone_code from parking_zone
                where is_available = true
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()

        ) {
            while (rs.next()) {
                list.add(
                        ParkingZone.builder()
                                .zoneCode(rs.getString("zone_code"))
                                .build()
                );

            }
        }
        return list;
    }

    //총몇자리 인지
    public int getTotalParkingZone() throws SQLException {
        int totalcount = 0;
        String sql = """
                select count(*) as total_count from parking_zone
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalcount = rs.getInt("total_count");
            }

        }
        return totalcount;
    }

    // 주차/출자 를 하면 is available이 업데이트 된다
    public void updateParkingZone(int zoneId ,boolean flag) throws SQLException {

        if (checkZoneById(zoneId) == flag){
            throw new SQLException("잘못된 요청입니다");
        }

        String sql = """
                update parking_zone
                set is_available = ?
                where zone_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setBoolean(1,flag);
            stmt.setInt(2,zoneId);
            int row = stmt.executeUpdate();
            if (row <= 0){
                throw new SQLException("업데이트 실패했습니다");
            }
        }
    }





    // 구역 삭제
    public void deleteParkingZone(int zoneId) throws SQLException {
        String sql = """
                DELETE FROM parking_zone WHERE zone_id = ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, zoneId);
            int rows = stmt.executeUpdate();
            if (rows <= 0) throw new SQLException("삭제할 구역이 없습니다.");
        }
    }

    public int getZoneIdByZoneCode(String zoneCode) throws SQLException {
        String sql = """
                SELECT zone_id FROM parking_zone WHERE zone_code = ?
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, zoneCode);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("zone_id");
                }
            }
        }
        return -1;
    }

    public String getZoneCodeByZoneId(int zoneId) throws SQLException {
        String sql = """
                SELECT zone_code FROM parking_zone WHERE zone_id = ?
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, zoneId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("zone_code");
                }
            }
        }
        return null;
    }
}
