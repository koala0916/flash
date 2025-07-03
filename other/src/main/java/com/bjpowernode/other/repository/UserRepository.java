package com.bjpowernode.other.repository;

import com.bjpowernode.other.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 编写操作User集合的接口
 */
public interface UserRepository extends MongoRepository<User, ObjectId> {
}
