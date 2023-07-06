package edu.ncar.cisl.sage.identification;

public class IdCalculatorFactory {

    public IdCalculator createCalculator(String calculatorType) {

        IdCalculator calculator = null;

        if (calculatorType != null) {
            if ("MD5".equalsIgnoreCase(calculatorType)) {
                calculator = new md5Calculator();
            }
            else {
                System.err.println("Unknown calculator-type");
            }
        }

        return calculator;
    }
}
