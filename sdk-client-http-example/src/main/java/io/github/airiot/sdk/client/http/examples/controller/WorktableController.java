package io.github.airiot.sdk.client.http.examples.controller;


import io.github.airiot.sdk.client.builder.Query;
import io.github.airiot.sdk.client.dto.BatchInsertResult;
import io.github.airiot.sdk.client.dto.InsertResult;
import io.github.airiot.sdk.client.dto.ResponseDTO;
import io.github.airiot.sdk.client.dto.UpdateOrDeleteResult;
import io.github.airiot.sdk.client.http.examples.entity.Student;
import io.github.airiot.sdk.client.service.core.SpecificTableDataClient;
import io.github.airiot.sdk.client.service.core.TableDataClientFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 工作表管理接口
 */
@RestController
@RequestMapping("/worktable")
public class WorktableController {

    /**
     * 管理学生信息的工作表记录管理客户端
     */
    private final SpecificTableDataClient<Student> studentClient;

    /**
     * 注入工作表记录客户端构建工厂
     */
    public WorktableController(TableDataClientFactory factory) {
        // 创建指定工作表记录管理客户端
        // 方式一: 使用工作表实体类(该类使用 @WorkTable 注解指定工作表名称)
        this.studentClient = factory.newClient(Student.class);

        // 方式二: 通过参数指定工作表名称
        // this.client = factory.newClient("student", Student.class);
    }

    /**
     * 添加记录
     * @param student 学生信息
     * @return 学生ID
     */
    @PostMapping("/create")
    public String create(@RequestBody Student student) {
        ResponseDTO<InsertResult> response = this.studentClient.create(student);
        if (!response.isSuccess()) {
            throw new IllegalStateException("添加学生信息失败, " + response.getFullMessage());
        }
        return response.getData().getInsertedID();
    }

    /**
     * 批量添加学生记录
     */
    @PostMapping("/create/batch")
    public List<String> createBatch() {
        List<Student> students = Arrays.asList(
                new Student("张三", 18, "male"),
                new Student("李四", 17, "female"),
                new Student("王五", 19, "male")
        );
        ResponseDTO<BatchInsertResult> response = this.studentClient.create(students);
        if (!response.isSuccess()) {
            throw new IllegalStateException("批量添加学生信息失败, " + response.getFullMessage());
        }
        return response.getData().getInsertedIDs();
    }

    /**
     * 更新学生信息
     *
     * @param student 学生信息
     */
    @PutMapping("/update")
    public void update(@RequestBody Student student) {
        ResponseDTO<UpdateOrDeleteResult> response = this.studentClient.update(student.getId(), student);
        if (!response.isSuccess()) {
            throw new IllegalStateException("更新学生信息失败, " + response.getFullMessage());
        }
    }

    /**
     * 根据ID查询学生信息
     *
     * @param id 学生ID
     * @return 学生信息
     */
    @GetMapping("/query/{id}")
    public Student queryById(@PathVariable String id) {
        // 使用 unwrap() 方法获取响应数据, 如果请求失败则会抛出异常
        return this.studentClient.queryById(id).unwrap();
    }

    /**
     * 查询所有学生信息
     *
     * @return 学生信息列表
     */
    @GetMapping("/query/all")
    public List<Student> queryAll() {
        // 不指定查询条件, 查询所有记录
        return this.studentClient.query(Query.newBuilder()
                .select(Student.class)
                .build()).unwrap();
    }

    /**
     * 查询指年龄范围折学生
     *
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 符合年龄范围的学生信息列表
     */
    @GetMapping("/query/age")
    public List<Student> queryRangeAge(int minAge, int maxAge) {
        // 指定查询条件, 查询年龄在 minAge 和 maxAge 之间的学生信息
        return this.studentClient.query(Query.newBuilder()
                .select(Student.class)
                .filter()
                .between("age", minAge, maxAge)
                .end()
                .build()).unwrap();
    }

    /**
     * 自定义复杂查询条件
     */
    @GetMapping("/query/complex")
    public List<Student> queryComplex() {
        return this.studentClient.query(Query.newBuilder()
                .select("id", "name", "age")    // 手动指定查询字段列表
                .select(Student::getGender)
                .filter()
                .lte(Student::getAge, 18)   // 年龄小于等于 18
                .eq(Student::getGender, true)  // 性别为男
                .regex("name", "张.*")  // 姓名以张开头
                .end()
                .build()).unwrap();
    }

    /**
     * 删除指定学生
     *
     * @param id 学生ID
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        this.studentClient.deleteById(id).unwrap();
    }
}
