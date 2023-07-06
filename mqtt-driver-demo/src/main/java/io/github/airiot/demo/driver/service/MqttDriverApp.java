package io.github.airiot.demo.driver.service;

import io.github.airiot.demo.driver.model.Command;
import io.github.airiot.demo.driver.model.MqttDriverConfig;
import io.github.airiot.demo.driver.model.Settings;
import io.github.airiot.sdk.driver.DriverApp;
import io.github.airiot.sdk.driver.config.DriverSingleConfig;
import io.github.airiot.sdk.driver.data.DataSender;
import io.github.airiot.sdk.driver.listener.BatchCmd;
import io.github.airiot.sdk.driver.listener.Cmd;
import io.github.airiot.sdk.driver.model.Tag;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT 数据采集驱动实现
 */
@Component
public class MqttDriverApp implements DriverApp<DriverSingleConfig<MqttDriverConfig>, Command, Tag> {

    private final Logger logger = LoggerFactory.getLogger(MqttDriverApp.class);

    /**
     * 驱动与平台交互接口, 通过构造函数注入
     */
    private final DataSender dataSender;
    /**
     * 每个工作表对应的 mqtt listener
     */
    private final Map<String, MqttMessageListener> listeners = new HashMap<>();
    /**
     * MQTT 客户端列表
     */
    private MqttClient client;


    public MqttDriverApp(DataSender dataSender) {
        this.dataSender = dataSender;
    }

    @Override
    public String getVersion() {
        return "v4.1.0";
    }

    /**
     * 根据驱动实例的配置创建 mqtt 客户端
     *
     * @param settings 驱动实例配置
     * @return mqtt 客户端
     */
    private MqttClient createClient(String driverInstanceId, Settings settings) throws MqttException {
        MqttClient client = new MqttClient(settings.getServer(), "java_sdk_demo_" + driverInstanceId);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                logger.info("Connection created");
            }

            @Override
            public void connectionLost(Throwable cause) {
                logger.warn("Connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        client.connect();
        return client;
    }

    @Override
    public void start(DriverSingleConfig<MqttDriverConfig> driverConfig) {
        logger.info("启动驱动");

        // 释放已有的资源
        this.stop();

        MqttDriverConfig config = driverConfig.getConfig();
        if (config == null || config.getSettings() == null) {
            logger.warn("启动驱动: 驱动实例的驱动配置不正确, {}", config);
            return;
        }

        try {
            this.client = this.createClient(driverConfig.getId(), driverConfig.getConfig().getSettings());
        } catch (MqttException e) {
            logger.warn("启动驱动: 连接 MQTT 失败, {}", config, e);
            throw new IllegalStateException("连接 MQTT 失败", e);
        }

        for (DriverSingleConfig.Model<MqttDriverConfig> table : driverConfig.getTables()) {
            Settings tableSettings = table.getConfig().getSettings();
            String topic = tableSettings.getTopic();

            logger.info("订阅消息: tableId={}, topic={}", table.getId(), topic);

            try {
                MqttMessageListener listener = new MqttMessageListener(this.dataSender, table);
                this.client.subscribe(topic, listener);
                this.listeners.put(table.getId(), listener);
            } catch (Exception e) {
                logger.error("订阅消息失败: tableId={}, topic={}", table.getId(), topic, e);
            }
        }

        logger.info("启动驱动: 完成");
    }

    @Override
    public void stop() {
        logger.info("停止驱动");
        if (this.client != null) {
            try {
                this.client.close(true);
            } catch (MqttException e) {
                logger.warn("停止驱动: 关闭 mqtt 连接失败", e);
            }
            this.client = null;
        }
        logger.info("停止驱动: 已停止");
    }

    @Override
    public Object run(Cmd<Command> request) {
        String tableId = request.getModelId();
        if (!this.listeners.containsKey(tableId)) {
            logger.warn("发送指令: 未找到工作表对应的监听器, {}", tableId);
            return "未找到工作表对应的连接, " + tableId;
        }

        MqttMessageListener listener = this.listeners.get(tableId);
        Command command = request.getCommand();
        MqttMessageListener.CommandResult result = listener.invokeCommandHandler(request.getDeviceId(), request.getCommand());

        try {
            this.client.publish(result.getTopic(), result.getPayload().getBytes(StandardCharsets.UTF_8), command.getOps().get(0).getQos(), false);
        } catch (MqttException e) {
            logger.error("发送指令: 发送失败, tableId={}, deviceId={}, topic={}, payload={}",
                    tableId, request.getDeviceId(), result.getPayload(), result.getPayload());
            return e.getMessage();
        }

        return "OK";
    }

    @Override
    public Object batchRun(BatchCmd<Command> request) {
        throw new IllegalStateException("不支持批量下发指令");
    }

    @Override
    public Object writeTag(Cmd<Tag> request) {
        throw new IllegalStateException("不支持批量写数据点");
    }

    @Override
    public String schema() {
        try (InputStream is = this.getClass().getResourceAsStream("/schema.js")) {
            if (is == null) {
                throw new IllegalStateException("读取 schema 文件失败");
            }

            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("读取 schema 文件异常", e);
        }
    }
}
