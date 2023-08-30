package iot.github.airiot.examples.flow.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "myplugin")
public class MyFlowPluginProperties {

    private String helloWorld1;
    private String helloWorld2;
    private String helloWorld3;
    private String helloWorld4;

    public String getHelloWorld1() {
        return helloWorld1;
    }

    public void setHelloWorld1(String helloWorld1) {
        this.helloWorld1 = helloWorld1;
    }

    public String getHelloWorld2() {
        return helloWorld2;
    }

    public void setHelloWorld2(String helloWorld2) {
        this.helloWorld2 = helloWorld2;
    }

    public String getHelloWorld3() {
        return helloWorld3;
    }

    public void setHelloWorld3(String helloWorld3) {
        this.helloWorld3 = helloWorld3;
    }

    public String getHelloWorld4() {
        return helloWorld4;
    }

    public void setHelloWorld4(String helloWorld4) {
        this.helloWorld4 = helloWorld4;
    }

    @Override
    public String toString() {
        return "MyFlowPluginProperties{" +
                "helloWorld1='" + helloWorld1 + '\'' +
                ", helloWorld2='" + helloWorld2 + '\'' +
                ", helloWorld3='" + helloWorld3 + '\'' +
                ", helloWorld4='" + helloWorld4 + '\'' +
                '}';
    }
}
