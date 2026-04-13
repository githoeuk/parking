package com.tenco.model;

import lombok.*;


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
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
}
