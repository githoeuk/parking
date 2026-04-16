package com.tenco.dao;

import com.tenco.db.DBConnection;
import com.tenco.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminsDAO {

    // 1. 로그인
    // 2. 관리자 추가
    // 3. 관리자 삭제
    // 4. 관리자 조회


    // 1. 로그인--------------------------------------------
    public Admin login(String userId,String password) throws SQLException {
        String sql = """
                SELECT * FROM admins WHERE user_id = ? and password =?
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1, userId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 1. 다중 행인지 단일 행인지 쿼리 출력값 확인
                // 단일행 -> 1 row가 나오거나 아예 안나오거나
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setUserId(rs.getString("user_id"));
                    admin.setPassword(rs.getString("password"));
                    admin.setName(rs.getString("name"));
                    return admin;
                } else {
                    return null;
                }
            } // end of rs
        } // end if pstmt
    } // end of login

    // 2. 관리자 추가 --------------------------
    public boolean addAdmin(Admin admin) throws SQLException {
        String sql = """
                INSERT INTO admins ( user_id , password, name) VALUES (? , ? ,?)
                """;
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1,admin.getUserId());
            pstmt.setString(2,admin.getPassword());
            pstmt.setString(3,admin.getName());


            int rows =  pstmt.executeUpdate();

        return rows > 0;
        } // end of pstmt
    } // end of updateAdmin

    // 3. 관리자 삭제 -------------------------------
    public boolean deleteAdmin(String userId ) throws SQLException {
        String sql = """
                
                UPDATE admins 
                SET is_available = false
                WHERE user_id = ?
                
                """;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1,userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }

    }


    // 4. 관리자 조회 -----------------------------
    public List<Admin> getAdminList() throws SQLException {
       List<Admin> adminList = new ArrayList<>();
        String sql = """
                SELECT * FROM admins WHERE is_available = TRUE
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()){
                Admin admin = Admin.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getString("user_id"))
                        .name(rs.getString("name"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                adminList.add(admin);
            }
        }
        return adminList;
    }

} // end of class
