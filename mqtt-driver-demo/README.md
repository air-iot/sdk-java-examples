# Java SDK 驱动二次开发示例项目

**该项目只可用于驱动二次开发参考, 不能用于生产环境, 否则由于造此损失和后果自负.**

## 介绍

该示例驱动实现了通过 `MQTT` 接收数据实现数据采集, 和通过向 `MQTT` 发送消息实现设备控制功能.
并且驱动中允许使用自定义 `JavaScript` 数据处理脚本对接收到的数据进行简单处理以及指令处理脚本实现平台指令转换为自定义消息内容的功能.

该示例驱动限制了每个实例只能连接一个 `MQTT` 服务器(但实际的二次开发中并没有限制), 然后允许每个工作表订阅一个 `Topic`
并定义 `数据处理脚本` 和 `指令处理脚本`, 实现数据的接收和发送.

更详细的开发说明请查阅文档 [Java SDK](https://docs.airiot.cn/development-manual/sdk/java-sdk)

> 注: 该示例驱动中未处理 `MQTT` 连接断开后的相关处理等功能.

## 运行测试

可直接在本地运行该项目代码查看效果, 演示流程如下:

1. 在平台中创建驱动实例
2. 修改驱动配置 `application.yml`
3. 运行代码
4. 创建工作表, 并配置相关信息. 添加数据点、指令等内容
5. 添加设备、数据点
6. 使用 `MQTT` 客户端工具发送测试数据
7. 在平台中查看实时数据
8. 发送指令, 并使用 `MQTT` 客户端工具接收平台发送的指令信息

> 注: 测试之前, 需要准备一个 `MQTT` 服务器, 要求运行该示例驱动的服务器能够正常连接到该 `MQTT` 服务器.

## 示例数据

使用 [数据处理示例脚本](#数据处理脚本) 时, 可使用以下数据作为测试数据.

topic: `data/javasdk/javasdk001`. 其中 `javasdk001` 为资产编号, 需要调整为实际添加资产的编号.
payload:

```json
{
  "key1": "key11",
  "key2": false,
  "key3": 1234.56
}
```

## 示例数据点配置

可直接将该内容导入到数据点配置中.

```json
[
  {
    "id": "key1",
    "name": "key1"
  },
  {
    "id": "key2",
    "name": "key2"
  },
  {
    "id": "key3",
    "name": "key3"
  }
]
```

## 示例指令配置

可直接将该内容导入到指令配置中.

```json
[
  {
    "writeIn": {
      "defaultValue": null,
      "select": null,
      "mod": null,
      "tagValue": null,
      "tag": null,
      "objectValue": null,
      "arrayValue": null,
      "tableValue": null,
      "select2": null,
      "ifRepeat": null,
      "objectValue2": null,
      "tableValue2": null,
      "ioway": "默认写入",
      "type": "string"
    },
    "writeOut": {
      "defaultValue": null,
      "select": null,
      "mod": null,
      "tagValue": null,
      "tag": null,
      "objectValue": null,
      "arrayValue": null,
      "tableValue": null,
      "select2": null,
      "ifRepeat": null,
      "objectValue2": null,
      "tableValue2": null
    },
    "name": "restart",
    "showName": "重启设备",
    "ops": {
      "topic": "cmd/",
      "message": "restart",
      "qos": 0
    }
  }
]
```

## 示例脚本

### 数据处理脚本

```javascript
/**
 * 数据处理脚本, 处理从 mqtt 接收到的数据.
 *
 * @param {string} topic 消息主题
 * @param {string} message 消息内容
 * @return 消息解析结果
 */
function handler(topic, message) {
    // 发送的 topic 为 data/javasdk/javasdk001
    // 其中 javasdk001 为设备编号
    var fields = topic.split("/");

    // 脚本返回值必须为对象数组
    // 	id: 资产编号
    //	time: 时间戳(毫秒)
    //  fields: 数据点数据. 该字段为 JSON 对象, key 为数据点标识, value 为数据点的值
    return [
        {"id": fields[2], "time": new Date().getTime(), "fields": JSON.parse(message)}
    ];
}
```

### 指令处理脚本

```javascript
/**
 * 指令处理脚本. 发送指令时会将指令内容传递给脚本, 然后由指定返回最终要发送的信息.
 *
 * @param {string} 工作表标识
 * @param {string} 资产编号
 * @param {object} 命令内容
 * @return {object} 最终要发送的消息, 及目标 topic
 */
function handler(tableId, deviceId, command) {
    var op = command.ops[0];
    var params = command.params;

    // 脚本返回值必须为下面对象结构
    //		topic: 消息发送的目标 topic
    //		payload: 消息内容
    return {
        "topic": op.topic + deviceId,
        "payload": JSON.stringify({"cmd": params[command.name]})
    };
}
```

## 驱动打包

驱动打包方式请查看二次开发文档 [数据接入驱动开发](https://docs.airiot.cn/development-manual/sdk/java-sdk/java-sdk-driver)