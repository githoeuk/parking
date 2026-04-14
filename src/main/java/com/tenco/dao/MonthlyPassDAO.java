package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.MonthlyPass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                        .startDate(rs.getDate("start_date"))
                        .endDate(rs.getDate("end_date"))
                        .fee(rs.getBigDecimal("fee"))
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
    public void soft_delete(String carNum) throws SQLException {

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


} // end of class
