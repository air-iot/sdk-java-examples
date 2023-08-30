package io.github.airiot.example.algorithm.entity;

public class AddParams {

    /**
     * 参数1
     */
    private Double num1;
    /**
     * 参数2
     */
    private Double num2;

    public Double getNum1() {
        return num1;
    }

    public Double getNum2() {
        return num2;
    }

    @Override
    public String toString() {
        return "AddParams{" +
                "num1=" + num1 +
                ", num2=" + num2 +
                '}';
    }
}
