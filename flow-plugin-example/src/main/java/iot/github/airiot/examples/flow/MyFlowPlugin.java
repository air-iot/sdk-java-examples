package iot.github.airiot.examples.flow;

import io.github.airiot.sdk.flow.plugin.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * 自定义流程插件实现类. 实现 {@link FlowPlugin} 接口.
 * <br>
 * 该示例实现了一个简单的数学运算, 计算公式 = num1^num2 / num3
 */
@Component
public class MyFlowPlugin implements FlowPlugin<Request> {

    @Override
    public String getName() {
        return "MyFlowPlugin";
    }

    @Override
    public FlowPluginType getPluginType() {
        return FlowPluginType.SERVICE_TASK;
    }

    @Override
    public FlowTaskResult execute(FlowTask<Request> task) throws FlowPluginException {
        Request request = task.getConfig();
        // 计算公式 = num1^num2 / num3
        BigDecimal result = BigDecimal.valueOf(Math.pow(request.getNum1(), request.getNum2()))
                .divide(BigDecimal.valueOf(request.getNum3()), 2, BigDecimal.ROUND_HALF_UP);
        return new FlowTaskResult("OK", "", Collections.singletonMap("output", result.doubleValue()));
    }
}
