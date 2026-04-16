package com.tenco.service;

import com.tenco.dao.AdminsDAO;
import com.tenco.model.Admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    AdminsDAO adminsDAO = new AdminsDAO();

    private Admin currentAdmin = null;

    // 로그인 확인?
    public Boolean isLogin(){
        return currentAdmin != null;
    } // end of isLogin

    // 로그인
    public String login(String userId, String password) throws SQLException {
        Admin admin = adminsDAO.login(userId,password);
        if ( admin == null){
            throw new SQLException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
            currentAdmin = admin;
            return admin.getName();
    } // end of login

    // 로그아웃
    public boolean logout() throws SQLException {
        if (currentAdmin == null){
            throw new SQLException("로그인 상태가 아닙니다.");
        }else {
            currentAdmin = null;
            return true;
        }
    } // logout

    // 3. 관리자 추가
    public boolean addAdmin(Admin admin) throws SQLException {

        if (admin == null){
            throw new SQLException("정보를 입력하세요");
        }
        return adminsDAO.addAdmin(admin);
    } // end of insert

    // 4. 관리자 삭제
    public boolean deleteAdmin(String userId) throws SQLException {

        if (userId == null){
            throw new SQLException("정보를 입력하세요");
        }

        return adminsDAO.deleteAdmin(userId);
    } // end of deleteAdmin


    // 5. 관리자 조회
    public List<Admin> getAdminList() throws SQLException {

        return adminsDAO.getAdminList();

    } // end of getAdminLists

} // end of class
