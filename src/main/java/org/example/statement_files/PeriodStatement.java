package org.example.statement_files;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PeriodStatement {
    MONTH("месяц","1"), YEAR("год","2"), FULL_TIME("всё время","3");

    String name;
    String numConst;

    public static PeriodStatement getPeriodType(String periodForConvert) {
        for(PeriodStatement periodStatement: PeriodStatement.values()) {
            if(periodStatement.name.equalsIgnoreCase(periodForConvert) ||
                    periodStatement.toString().equalsIgnoreCase(periodForConvert) ||
                    periodStatement.numConst.equals(periodForConvert)) {
                System.out.println("Выбрана выписка за " + periodStatement.name);
                return periodStatement;
            }
        }
        return FULL_TIME;
    }
}
