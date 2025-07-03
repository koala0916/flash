package com.bjpowernode.other;

import com.bjpowernode.other.entity.User;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@SpringBootTest
class MongoDBTemplateTests {

    @Resource
    private MongoTemplate mongoTemplate;


    @Test
    public void testSave() {
        User user = User.builder().name("王一博").age(38).email("caixukun@163.com").build();

        mongoTemplate.insert(user);

        System.out.println(user);
    }


    @Test
    public void testFindAll() {
        List<User> userList = mongoTemplate.findAll(User.class);
        System.out.println(userList);
    }


    @Test
    public void testFindById() {
        User user = mongoTemplate.findById("6865dae05635231d3e19f345", User.class);
        System.out.println(user);
    }

    @Test
    public void testDelete() {
        //构建标准 hibernate 全orm
        Criteria criteria = Criteria.where("_id").is("6865e4fd84cfb9b799e03c68");

        //构建查询对象
        Query query = new Query(criteria);

        //删除获取结果
        DeleteResult deleteResult = mongoTemplate.remove(query, User.class);
        long deletedCount = deleteResult.getDeletedCount();
        System.out.println(deletedCount);
    }

    @Test
    public void testSelectByNameAndAge() {
        //构建标准
        Criteria criteria = Criteria.where("name").is("王一博").and("age").is(38);
        Query query = new Query(criteria);
        List<User> userList = mongoTemplate.find(query, User.class);
        System.out.println(userList);
    }

    @Test
    public void testUpdate(){
        Criteria criteria = Criteria.where("_id").is("6865e7e2a15d177d2da33b7e");
        Query query = new Query(criteria);

        //update对象
        Update update = new Update();

        update.set("name", "肖战");
        update.set("height", "180");

        UpdateResult result = mongoTemplate.upsert(query, update, User.class);
        System.out.println(result.getModifiedCount());
    }
}
