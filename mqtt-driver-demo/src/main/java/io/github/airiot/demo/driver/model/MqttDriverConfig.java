package io.github.airiot.demo.driver.model;

import java.util.List;

public class MqttDriverConfig {

    /**
     * 驱动配置信息
     */
    private Settings settings;
    /**
     * 数据点列表
     */
    private List<Tag> tags;

    public Settings getSettings() {
        return settings;
    }
    
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "MqttDriverConfig{" +
                "settings=" + settings +
                ", tags=" + tags +
                '}';
    }
}
