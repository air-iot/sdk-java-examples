package io.github.airiot.demo.driver.model;

import java.util.List;
import java.util.Map;

/**
 * 指令内容
 */
public class Command {
    /**
     * 命令ID
     */
    private String id;

    /**
     * 指令名称
     */
    private String name;

    /**
     * 驱动指令配置
     */
    private List<Op> ops;

    /**
     * 数据写入
     */
    private Map<String, Object> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Op> getOps() {
        return ops;
    }

    public void setOps(List<Op> ops) {
        this.ops = ops;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
