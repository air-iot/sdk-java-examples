package iot.github.airiot.examples.flow;

/**
 * 自定义流程插件的请求参数
 */
public class Request {

    /**
     * 数值1
     */
    private Double num1;
    /**
     * 数值2
     */
    private Integer num2;
    /**
     * 数值3
     */
    private Double num3;

    public Double getNum1() {
        return num1;
    }

    public void setNum1(Double num1) {
        this.num1 = num1;
    }

    public Integer getNum2() {
        return num2;
    }

    public void setNum2(Integer num2) {
        this.num2 = num2;
    }

    public Double getNum3() {
        return num3;
    }

    public void setNum3(Double num3) {
        this.num3 = num3;
    }

    @Override
    public String toString() {
        return "Request{" +
                "num1=" + num1 +
                ", num2=" + num2 +
                ", num3=" + num3 +
                '}';
    }
}
