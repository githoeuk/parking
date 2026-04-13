package com.tenco.model;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class MonthlyPass {
    private int passId;
    private String carNumber;
    private String ownerName;
    private Date startDate;
    private Date endDate;
    private BigDecimal fee;
}
