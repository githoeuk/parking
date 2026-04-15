package com.tenco.service;

import com.tenco.dao.MonthlyPassDAO;
import com.tenco.model.MonthlyPass;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class MonthlyPassService {
    // 정기권 등록 (차량 번호, 소유자, 시작일, 종료일,
    MonthlyPassDAO monthlyPassDAO = new MonthlyPassDAO();

    // 정기권 등록
    public boolean monthInsert(MonthlyPass mPass) throws SQLException {

        if (mPass == null){
            throw new SQLException("등록이 실패했습니다.");
        }
        return monthlyPassDAO.insert(mPass);
    } // end of monthInsert

    // 정기권 삭제
    public boolean monthDelete(String carNum) throws SQLException {

        if (carNum.trim().isEmpty()){
            throw new SQLException("정확한 차량번호를 입력해주세요");
        }
        return monthlyPassDAO.soft_delete(carNum);

    } // end of monthDelete

    // 정기권 조회
    public List<MonthlyPass> monthSubList() throws SQLException {
       return monthlyPassDAO.findPassList();
    } // end of monthSubList

    // 정기권 만료 임박 알림
    public List<MonthlyPass> isNearExpiry() throws SQLException {
       List<MonthlyPass> list = monthlyPassDAO.isNearExpiry();
        if (list == null){
            throw new SQLException("만료 임박 차량이 없습니다.");
        }
        return list;
    } // end of isNearExpiry

    // 유효한 정기권 보유 여부 (출차 화면 정기권 확인용)
    public boolean hasValidPass(String carNumber) throws SQLException {
        return monthlyPassDAO.getMonthlyPassByCarNumber(carNumber);
    }

    // 정기권 기간 연장
    public boolean isExtends(int date,String carNumber) throws SQLException {
        return monthlyPassDAO.getExtends(date,carNumber);
    }

}// end of class
