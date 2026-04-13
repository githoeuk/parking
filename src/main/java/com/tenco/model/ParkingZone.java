package com.tenco.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingZone {
    private int zoneId;
    private String zoneCode;
    private boolean isAvailable;
}
