package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.MonthlyPass;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyPassDAO {

    // 정기권 조회 , 정기권 등록 , 정기권 삭제 , 기간 내 차량 요금 면제 , 기간 연장

    // 정기권 조회
    public List<MonthlyPass> findPassList() throws SQLException {
        List<MonthlyPass> mPassList = new ArrayList<>();

        String sql = """
                SELECT * FROM monthly_pass
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                MonthlyPass mPass = MonthlyPass.builder()
                        .passId(rs.getInt("pass_id"))
                        .carNumber(rs.getString("car_number"))
                        .ownerName(rs.getString("owner_name"))
                        .phone(rs.getString("phone"))
                        .startDate(rs.getDate("start_date"))
                        .endDate(rs.getDate("end_date"))
                        .fee(rs.getBigDecimal("fee"))
                        .isAvailable(rs.getBoolean("is_available"))
                        .build();
                mPassList.add(mPass);
            } //end of while
        } // end of pstmt
        return mPassList;
    } // end of findPassList

    // 정기권 등록
    public boolean insert(MonthlyPass mPass) throws SQLException {

        String sql = """
                INSERT INTO monthly_pass ( car_number, owner_name, phone, start_date, end_date, fee)
                values ( ? , ? ,? ,? ,? ,?)
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, mPass.getCarNumber());
            pstmt.setString(2, mPass.getOwnerName());
            pstmt.setString(3, mPass.getPhone());
            pstmt.setDate(4, mPass.getStartDate());
            pstmt.setDate(5, mPass.getEndDate());
            pstmt.setBigDecimal(6, mPass.getFee());

            int rows = pstmt.executeUpdate();
            return rows >= 0;
        } // end of pstmt

    } // end of insert

    // 정기원 삭제
    public boolean soft_delete(String carNum) throws SQLException {

        String sql = """
                UPDATE monthly_pass
                set is_available = FALSE
                where car_number = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setString(1, carNum);
            pstmt.executeUpdate();

            return true;
        }
    } // end of soft_delete

    // 기간 내 차량 요금 면제

    public void check(String carNum) throws SQLException {
        List<MonthlyPass> mList = new ArrayList<>();
        String sql = """
                SELECT * FROM monthly_pass WHERE car_number = ? and is_available = 1;
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);

        ) {
            pstmt.setString(1, carNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MonthlyPass mPass = MonthlyPass.builder()
                            .passId(rs.getInt("pass_id"))
                            .carNumber(rs.getString("car_number"))
                            .ownerName(rs.getString("owner_name"))
                            .startDate(rs.getDate("start_date"))
                            .endDate(rs.getDate("end_date"))
                            .fee(rs.getBigDecimal("fee"))
                            .build();
                    mList.add(mPass);
                } // end of while
            } //end of rs
        } // end of pstmt

    } // end if check


    // 정기권 만료 알림
    public List<MonthlyPass> isNearExpiry() throws SQLException {
        List<MonthlyPass> mPass = new ArrayList<>();
        String sql = """
                SELECT * FROM monthly_pass WHERE end_date - CURRENT_DATE() <= 15
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement tstmt = conn.prepareStatement(sql);
        ) {
            try (ResultSet rs = tstmt.executeQuery()) {
                while (rs.next()) {
                    MonthlyPass mList = MonthlyPass.builder()
                            .passId(rs.getInt("pass_id"))
                            .carNumber(rs.getString("car_number"))
                            .ownerName(rs.getString("owner_name"))
                            .startDate(rs.getDate("start_date"))
                            .endDate(rs.getDate("end_date"))
                            .fee(rs.getBigDecimal("fee"))
                            .build();
                    mPass.add(mList);
                } // end of while
            } // end of rs
        } // end of pstmt

        return mPass;
    } // end of isNearExpiry

    // 정기권 유효한지
    public boolean getMonthlyPassByCarNumber(String carNumber) throws SQLException {
        String sql = """
                SELECT is_available FROM monthly_pass WHERE car_number = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_available");
                }
            }
            return false;
        }
    }

    // 정기권 연장
    public boolean getExtends(int date, String carNumber) throws SQLException {
        String sql = """
             
             UPDATE monthly_pass
             SET end_date = date_add(end_date,interval ? day)
             WHERE car_number = ?;
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setInt(1,date);
            pstmt.setString(2,carNumber);

            pstmt.executeUpdate();
            return true;
        }
    }// end of getExtends

    // 정기권 요금
    public BigDecimal mPassFee(int month){

        BigDecimal basic = BigDecimal.valueOf(100000);

        return basic.multiply(BigDecimal.valueOf(month));
    }

} // end of class
