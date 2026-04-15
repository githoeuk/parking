package com.tenco.service;

import com.tenco.dao.MonthlyPassDAO;
import com.tenco.dao.ParkingRecordDAO;
import com.tenco.model.ParkingRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FeeCalculator {
    private final ParkingRecordDAO parkingRecordDAO = new ParkingRecordDAO();
    private final MonthlyPassDAO monthlyPassDAO = new MonthlyPassDAO();
    // 요금부과
    public BigDecimal calculateFee(String carNumber) throws SQLException {
        // select문으로 start_time 가져와야
        ParkingRecord record = parkingRecordDAO.selectByCarNum(carNumber);
        LocalDateTime start_time = record.getEntryTime();
        LocalDateTime current_time = LocalDateTime.now();
        double mDiff = ChronoUnit.MINUTES.between(start_time, current_time);

        double fee = 0.0;

        if(mDiff < 10) {
            // fee = 0 회차처리
            return BigDecimal.valueOf(fee);
        } else {
            // fee 계산 로직
            // mDiff 몇분 주차했냐
            if(monthlyPassDAO.getMonthlyPassByCarNumber(carNumber)){
                return BigDecimal.valueOf(fee);
            }

            if(mDiff > 10 && mDiff <= 30){
                fee = 1000;
                return BigDecimal.valueOf(fee);
            }

            fee = ((mDiff - 30) / 10) * 500 + 1000;
            return BigDecimal.valueOf(fee);
        }
    }



}
