package com.portfolio.server.jobs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name ="jobs")
public class Jobs {
    @Id
    @Column(length = 10)
    private String jobId;
    @Column(length = 35)
    private String jobTitle;
    @Column(precision = 8)
    private BigDecimal minSalary;
    @Column(precision = 8)
    private BigDecimal maxSalary;

}
