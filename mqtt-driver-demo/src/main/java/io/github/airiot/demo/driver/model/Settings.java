package io.github.airiot.demo.driver.model;

public class Settings {

    private String server;
    private String username;
    private String password;
    private String topic;
    /**
     * 数据处脚本
     */
    private String parseScript;
    /**
     * 指令处理脚本
     */
    private String commandScript;
    /**
     * 自定义设备编号
     */
    private String customDeviceId;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getParseScript() {
        return parseScript;
    }

    public void setParseScript(String parseScript) {
        this.parseScript = parseScript;
    }

    public String getCommandScript() {
        return commandScript;
    }

    public void setCommandScript(String commandScript) {
        this.commandScript = commandScript;
    }

    public String getCustomDeviceId() {
        return customDeviceId;
    }

    public void setCustomDeviceId(String customDeviceId) {
        this.customDeviceId = customDeviceId;
    }
}
