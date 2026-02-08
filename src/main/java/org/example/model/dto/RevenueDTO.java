package org.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RevenueDTO {
    private Date date;
    private int totalOrders;
    private double revenue;

}
