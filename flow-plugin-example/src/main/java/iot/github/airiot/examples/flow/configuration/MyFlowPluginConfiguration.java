package iot.github.airiot.examples.flow.configuration;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MyFlowPluginProperties.class)
public class MyFlowPluginConfiguration {
    
}
