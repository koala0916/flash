package com.bjpowernode.other.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Data
@Document("user") //指定MongoDB中集合的名字
public class User {

    //mongodb的主键
    @Id
    private ObjectId id;

    private String name;
    private Integer age;
    private String email;
    private Date createDate;

}
