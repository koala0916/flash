package com.powernode.order.repository;


import com.powernode.model.entity.order.OrderMonitorRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMonitorRecordRepository extends MongoRepository<OrderMonitorRecord, String> {

}
