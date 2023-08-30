package io.github.airiot.sdk.client.http.examples.controller;


import io.github.airiot.sdk.client.builder.Query;
import io.github.airiot.sdk.client.context.RequestContext;
import io.github.airiot.sdk.client.dto.ResponseDTO;
import io.github.airiot.sdk.client.service.core.UserClient;
import io.github.airiot.sdk.client.service.core.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户信息管理接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 将用户管理客户端注入到当前类中
     */
    @Autowired
    private UserClient userClient;

    /**
     * 根据用户ID查询用户信息
     * <br>
     * 该请求使用配置文件中指定的默认项目ID
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/{username}")
    public User queryByUsername(@PathVariable String username) {
        List<User> users = this.userClient.queryByName(username).unwrap();
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    /**
     * 查询指定项目中所有用户信息
     *
     * @param projectId 查询的目标项目ID
     * @return 用户信息列表
     */
    @GetMapping("/all")
    public List<User> queryAll(@RequestParam("projectId") String projectId) {
        // 设置本次请求项目ID
        RequestContext.setDefaultProjectId(projectId);
        try {
            ResponseDTO<List<User>> response = userClient.query(Query.newBuilder()
                    .select(User.class).build());

            // 查询成功
            if (response.isSuccess()) {
                return response.getData();
            }

            throw new IllegalStateException("查询用户信息失败, " + response.getFullMessage());
        } finally {
            // 清除本次请求项目ID
            RequestContext.clearProjectId();
        }
    }
}
