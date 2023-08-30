package iot.github.airiot.examples.flow;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyFlowPluginApplication {

    @Autowired
    public static void main(String[] args) {
        SpringApplication.run(MyFlowPluginApplication.class, args);
    }
}
