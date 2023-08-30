package io.github.airiot.sdk.client.http.examples.controller;


import io.github.airiot.sdk.client.builder.Query;
import io.github.airiot.sdk.client.service.core.UserClient;
import io.github.airiot.sdk.client.service.core.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo/query")
public class QueryDemoController {

    @Autowired
    private UserClient client;

    /**
     * 使用自定义查询条件查询用户信息
     */
    @GetMapping("/")
    public List<User> query() {
        Query query = Query.newBuilder()
                .select("name", "model")
                .selectSubFields("warning", "hasWarning")

                .filter()
                    .eq("name", "Tom")
                    .eq("modelId", "5c6121b9982d2073b1a828a1")
                    .regex("fullname", "la")
                .end()
                .or()
                    .gt("score", 70)
                    .lt("score", 90)
                    .gte("views", 1000)
                .end()

                .orderAsc("posts").orderDesc("age")

                .limit(30)
                .skip(20)
                .withCount()
                .build();

        return this.client.query(query).unwrap();
    }

}
