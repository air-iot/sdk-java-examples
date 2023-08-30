package io.github.airiot.sdk.client.http.examples.entity;


import io.github.airiot.sdk.client.annotation.Field;
import io.github.airiot.sdk.client.annotation.WorkTable;

/**
 * 工作表学生信息记录
 */
@WorkTable("student")
public class Student {

    /**
     * 学生ID
     */
    private String id;
    /**
     * 学生姓名
     */
    private String name;
    /**
     * 学生年龄
     */
    private Integer age;
    /**
     * 学生性别
     * <br>
     * 这里使用 {@link Field} 注解指定该字段在工作表定义中的名称, 类属性的名称与工作表定义中的名称不一致时可以使用该注解指定
     */
    @Field("sex")
    private String gender;

    public Student() {
    }

    public Student(String name, Integer age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public Student(String id, String name, Integer age, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}
