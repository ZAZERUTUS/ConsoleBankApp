package org.example.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Config {
    protected String nameDb;
    protected String adminPassword;
    protected String adminUser;
    protected String hostDb;
    protected int portDb;
    protected double percent;
    protected int periodVerifyPercentMls;
}
