package io.github.airiot.demo.driver.service;

import io.github.airiot.demo.driver.model.Command;
import io.github.airiot.demo.driver.model.MqttDriverConfig;
import io.github.airiot.sdk.driver.config.Device;
import io.github.airiot.sdk.driver.config.DriverSingleConfig;
import io.github.airiot.sdk.driver.data.DataSender;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MqttMessageListener implements IMqttMessageListener {

    private final Logger logger = LoggerFactory.getLogger(MqttMessageListener.class);

    private final DataSender dataSender;
    private final String tableId;
    /**
     * 资产编号与自定义资产编号映射关系.
     * <br>
     * 如果设备没有设置自定义资产编号时, 使用资产编号作为自定义资产编号
     * <br>
     * key: 自定义资产编号<br>
     * value: 资产编号
     */
    private final Map<String, String> devices;
    /**
     * 数据处理脚本执行引擎, 当接收到 mqtt 消息时, 会调用该脚本中的 handler 函数
     */
    private final Invocable parseHandler;
    private final Invocable commandHandler;

    public MqttMessageListener(DataSender dataSender, DriverSingleConfig.Model<MqttDriverConfig> table) throws ScriptException {
        this.dataSender = dataSender;
        this.tableId = table.getId();
        this.devices = new HashMap<>(table.getDevices().size());
        for (Device<MqttDriverConfig> device : table.getDevices()) {
            MqttDriverConfig deviceConfig = device.getConfig();
            if (deviceConfig == null || deviceConfig.getSettings() == null || StringUtils.hasText(deviceConfig.getSettings().getCustomDeviceId())) {
                this.devices.put(device.getId(), device.getId());
            } else {
                this.devices.put(deviceConfig.getSettings().getCustomDeviceId(), device.getId());
            }
        }

        ScriptEngine parserScriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        parserScriptEngine.eval(table.getConfig().getSettings().getParseScript());
        ScriptEngine commandScriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        commandScriptEngine.eval(table.getConfig().getSettings().getCommandScript());
        this.parseHandler = (Invocable) parserScriptEngine;
        this.commandHandler = (Invocable) commandScriptEngine;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        logger.debug("接收到消息: tableId={}, topic={}, payload={}", this.tableId, topic, payload);
        try {
            Object result = this.parseHandler.invokeFunction("handler", topic, new String(message.getPayload(), StandardCharsets.UTF_8));
            logger.debug("接收到消息: 数据脚本处理结果, tableId={}, topic={}, payload={}, result={}", this.tableId, topic, payload, result);
            if (result instanceof ScriptObjectMirror) {
                ScriptObjectMirror scriptObject = (ScriptObjectMirror) result;
                if (!scriptObject.isArray()) {
                    logger.warn("接收到消息: 数据脚本处理结果不是个数组, tableId={}, topic={}, payload={}, result={}", this.tableId, topic, payload, result);
                    return;
                }

                for (Map.Entry<String, Object> entry : scriptObject.entrySet()) {
                    ScriptObjectMirror objectValue = (ScriptObjectMirror) entry.getValue();
                    if (!objectValue.containsKey("id") || !objectValue.containsKey("fields")) {
                        logger.warn("接收到消息: 数据脚本处理结果缺少必要信息 'id' 或 'fields', tableId={}, topic={}, payload={}, result={}", this.tableId, topic, payload, result);
                        continue;
                    }

                    String id = objectValue.get("id").toString();
                    if (!this.devices.containsKey(id)) {
                        logger.warn("接收到消息: 设备不存在, tableId={}, topic={}, deviceId={}", this.tableId, topic, id);
                        continue;
                    }

                    if (!(objectValue.get("fields") instanceof ScriptObjectMirror)) {
                        logger.warn("接收到消息: 数据脚本处理结果中的 fields 字段不是有效的 JSON 对象, tableId={}, topic={}, deviceId={}, fields={}", this.tableId, topic, id, objectValue.get("fields"));
                        continue;
                    }

                    ScriptObjectMirror fields = (ScriptObjectMirror) objectValue.get("fields");
                    Map<String, Object> fieldValues = fields.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue

                            ));

                    String deviceId = this.devices.get(id);
                    long time = new BigDecimal(objectValue.getOrDefault("time", 0L).toString()).longValue();

                    logger.debug("接收到消息: 发送数据到平台, tableId={}, deviceId={}, time={}, fields={}", this.tableId, deviceId, time, fieldValues);

                    this.dataSender.writePoint(this.tableId, this.devices.get(id), time, fieldValues);
                }
            }
        } catch (Exception e) {
            logger.warn("接收到消息: 执行数据处理脚本失败, tableId={}, topic={}, message={}", tableId, topic, payload, e);
        }
    }

    /**
     * 执行命令脚本
     *
     * @param deviceId 资产编号
     * @param command  命令内容
     * @return 指令脚本执行结果
     */
    public CommandResult invokeCommandHandler(String deviceId, Command command) {
        logger.debug("执行指令脚本: tableId={}, deviceId={}, command={}", this.tableId, deviceId, command);

        ScriptObjectMirror scriptObject;
        try {
            Object result = this.commandHandler.invokeFunction("handler", this.tableId, deviceId, command);
            logger.debug("执行指令脚本: 脚本返回结果, tableId={}, deviceId={}, command={}, result={}", this.tableId, deviceId, command, result);
            if (!(result instanceof ScriptObjectMirror)) {
                throw new IllegalStateException("脚本返回结果不是对象");
            }
            scriptObject = (ScriptObjectMirror) result;
        } catch (Exception e) {
            logger.error("执行指令脚本: 执行脚本失败, tableId={}, deviceId={}, command={}", this.tableId, deviceId, command, e);
            throw new IllegalStateException("执行脚本失败", e);
        }

        if (!scriptObject.containsKey("topic") || !scriptObject.containsKey("payload")) {
            throw new IllegalStateException("脚本返回对象中缺少 'topic' 或 'payload'");
        }

        String topic = String.valueOf(scriptObject.get("topic"));
        String payload = String.valueOf(scriptObject.get("payload"));

        return new CommandResult(topic, payload);
    }

    public static class CommandResult {
        private final String topic;
        private final String payload;

        public String getTopic() {
            return topic;
        }

        public String getPayload() {
            return payload;
        }

        public CommandResult(String topic, String payload) {
            this.topic = topic;
            this.payload = payload;
        }
    }
}