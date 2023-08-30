package io.github.airiot.sdk.client.http.examples.controller;


import io.github.airiot.sdk.client.service.core.UserClient;
import io.github.airiot.sdk.client.service.warning.WarnClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例代码
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    /**
     * 将用户管理客户端注入到当前类中
     */
    @Autowired
    private UserClient userClient;
    /**
     * 将告警管理客户端注入到当前类中
     */
    @Autowired
    private WarnClient warnClient;

    // ... 注入其它客户端
}
