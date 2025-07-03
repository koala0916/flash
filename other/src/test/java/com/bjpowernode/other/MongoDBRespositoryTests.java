package com.bjpowernode.other;

import com.bjpowernode.other.entity.User;
import com.bjpowernode.other.repository.UserRepository;
import jakarta.annotation.Resource;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class MongoDBRespositoryTests {

    @Resource
    private UserRepository userRepository;

    @Test
    public void testCreate() {
        User lyf = User.builder().name("李一桐").age(34).createDate(new Date()).build();

        userRepository.save(lyf);
    }


    @Test
    public void testFindAll() {
        List<User> userList = userRepository.findAll();
        System.out.println(userList);
    }


    @Test
    public void findById(){
        //Optional类是jdk8新增的，目的是防止空指针
        Optional<User> optional = userRepository.findById(new ObjectId("6865dae05635231d3e19f345"));

        boolean present = optional.isPresent();
        if (present){
            //若能进入，则说明user是有值的
            User user = optional.get();
            System.out.println(user);
        }
    }


    @Test
    public void testFindByAge(){
        //查询的条件
        User user = User.builder().age(38).build();

        //构建条件
        Example<User> userExample = Example.of(user);

        List<User> userList = userRepository.findAll(userExample);
        System.out.println(userList);

    }


    @Test
    public void testSort() {
        //构建排序条件
        Sort sort = Sort.by(Sort.Direction.DESC, "age");
        List<User> userList = userRepository.findAll(sort);
        System.out.println(userList);
    }

    @Test
    public void testPage(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> page = userRepository.findAll(pageRequest);

        int totalPages = page.getTotalPages();

        List<User> userList = page.getContent();

        System.out.println(totalPages);
        System.out.println(userList);

    }

    @Test
    public void testUpdate(){
        //先查询再修改
        Optional<User> optional = userRepository.findById(new ObjectId("6865db399831fd445d3da831"));

        if (optional.isPresent()) {
            User user = optional.get();
            user.setName("祝绪丹");

            //保存 因为是有id的，所以会进行修改操作
            userRepository.save(user);
        }

    }


    @Test
    public void testDelete(){
       userRepository.deleteById(new ObjectId("6865db399831fd445d3da831"));

    }

}
