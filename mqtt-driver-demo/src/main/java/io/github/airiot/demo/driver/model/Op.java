package io.github.airiot.demo.driver.model;

/**
 * 驱动指令配置内容
 */
public class Op {

    /**
     * 消息发送主题
     */
    private String topic;
    /**
     * Qos
     */
    private int qos;
    /**
     * 发送内容
     */
    private String message;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }
    
    public void setQos(int qos) {
        this.qos = qos;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
