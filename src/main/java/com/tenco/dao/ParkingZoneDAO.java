package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.ParkingZone;

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

                return rs.getBoolean("is_available");
            }

        }


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


    public static void main(String[] args) {
        ParkingZoneDAO zone = new ParkingZoneDAO();
        try {
            System.out.println(zone.getTotalParkingZone());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
