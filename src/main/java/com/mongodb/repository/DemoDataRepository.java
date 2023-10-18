package com.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.mongodb.models.DemoData;

public interface DemoDataRepository extends MongoRepository<DemoData, String> {
}