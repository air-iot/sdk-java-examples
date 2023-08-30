package io.github.airiot.example.algorithm;

import io.github.airiot.example.algorithm.entity.AddParams;
import io.github.airiot.example.algorithm.entity.AddResult;
import io.github.airiot.sdk.algorithm.AlgorithmApp;
import io.github.airiot.sdk.algorithm.AlgorithmException;
import io.github.airiot.sdk.algorithm.annotation.AlgorithmFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

/**
 * 自定义算法服务实现
 */
@Component
public class AlgorithmExampleApp implements AlgorithmApp {

    private final Logger logger = LoggerFactory.getLogger(AlgorithmExampleApp.class);

    @Override
    public void start() {
        logger.info("算法服务已启动");
        // do something
    }

    @Override
    public void stop() {
        logger.info("算法服务已停止");
    }

    /**
     * add 算法函数. 该示例演示带有自定义类参数类型的算法函数.
     *
     * @param projectId 项目ID
     * @param params    参数
     * @return 计算结果
     */
    @AlgorithmFunction("add")
    public AddResult add(String projectId, AddParams params) throws AlgorithmException {
        if (params.getNum1() == null || params.getNum2() == null) {
            throw new AlgorithmException("the num1 and num2 must not be null");
        }
        // 将请求参数中的两个数值相加并返回计算结果
        return new AddResult(params.getNum1() + params.getNum2());
    }

    /**
     * recvMap 算法函数. 使用 Map 接收请求参数.
     *
     * @param projectId 项目ID
     * @param params    请求参数
     * @return 返回值
     * @throws AlgorithmException 如果执行异常, 则抛出此异常
     */
    @AlgorithmFunction("recvMap")
    public Map<String, Object> recvMap(String projectId, Map<String, Object> params) throws AlgorithmException {
        return Collections.singletonMap("result", String.format("hello, %s", params.get("name")));
    }

    /**
     * recvString 算法函数. 使用 String 接收请求参数.
     *
     * @param projectId 项目ID
     * @param params    请求参数
     * @return 返回值
     */
    @AlgorithmFunction("recvString")
    public Map<String, Object> recvString(String projectId, String params) {
        return Collections.singletonMap("result", params);
    }

    /**
     * now 算法函数. 该示例演示不带参数的算法函数.
     *
     * @param projectId 项目ID
     * @return 当前时间
     */
    @AlgorithmFunction("now")
    public Map<String, Object> getSysDate(String projectId) {
        return Collections.singletonMap("sysdate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * 没有 projectId 参数的算法函数, 启动报错.
     */
//    @AlgorithmFunction("noProjectId")
    public Map<String, Object> noProjectId() {
        return Collections.emptyMap();
    }

    /**
     * projectId 参数类型不正确的算法函数, 启动报错.
     */
//    @AlgorithmFunction("InvalidProjectIdType")
    public Map<String, Object> invalidProjectIdType(Integer projectId) {
        return Collections.emptyMap();
    }

    /**
     * 参数类型不正确的算法函数, 该种情况启动时不会报错, 但是调用该函数时会报错.
     * <br>
     * 参数类型只能为 {@code String}, {@code Map<String, Object>} 或自定义类型.
     */
//    @AlgorithmFunction("InvalidParamsType")
    public Map<String, Object> invalidParamsType(String projectId, Double params) {
        return Collections.emptyMap();
    }

    /**
     * 默认请求处理函数.
     * <br>
     * 当未找到使用 {@link AlgorithmFunction} 标注的函数时, 会调用此函数处理请求.
     *
     * @param projectId 发起请求的项目ID
     * @param function  函数名
     * @param params    请求参数
     * @return 处理结果
     * @throws AlgorithmException 如果执行异常, 则抛出此异常
     */
    @Override
    public Object run(String projectId, String function, Map<String, Object> params) throws AlgorithmException {
        if ("abs".equals(function)) {
            Object num1 = params.get("num1");
            if (num1 == null) {
                throw new AlgorithmException("the num1 cannot be null");
            }

            if (!(num1 instanceof Number)) {
                throw new AlgorithmException("the num1 must be a number");
            }

            double val = ((Number) num1).doubleValue();
            return Collections.singletonMap("res", Math.abs(val));
        }

        throw new AlgorithmException("不支持的函数: " + function);
    }

    @Override
    public String schema() {
        try (InputStream is = this.getClass().getResourceAsStream("/schema.js")) {
            if (is == null) {
                throw new IllegalStateException("未找到算法 schema.js 文件");
            }
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            logger.error("加载算法 schema 失败", e);
            throw new IllegalStateException("读取 schema.js 文件异常", e);
        }
    }
}
