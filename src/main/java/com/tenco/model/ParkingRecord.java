package com.tenco.model;

import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingRecord {
    private int recordId;
    private String carNumber;
    private int zoneId;
    private String zoneCode;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal fee;
}
