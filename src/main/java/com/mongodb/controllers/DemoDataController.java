package com.mongodb.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.models.DemoData;
import com.mongodb.repository.DemoDataRepository;

@RestController
@RequestMapping("/mongodb")
public class DemoDataController {
	
	

	
	private final DemoDataRepository demoDataRepository;
    private final RedisTemplate<String, DemoData> redisTemplate; // Inject RedisTemplate

    @Autowired
    public DemoDataController(DemoDataRepository demoDataRepository, RedisTemplate<String, DemoData> redisTemplate) {
        this.demoDataRepository = demoDataRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDemoData(@RequestBody DemoData demoData) {
        try {
            demoDataRepository.save(demoData); // Save in MongoDB
            redisTemplate.opsForValue().set(demoData.getId(), demoData); // Save in Redis
            return ResponseEntity.status(HttpStatus.CREATED).body("Data saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save data");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getDemoData(@PathVariable String id) {
        Optional<DemoData> demoDataOptional = demoDataRepository.findById(id);
        if (demoDataOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(demoDataOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found");
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<Object> getAllDemoData() {
        List<DemoData> allDemoData = demoDataRepository.findAll();
        if (!allDemoData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(allDemoData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found");
        }
    }
    
    @GetMapping("/getFromRedis/{id}")
    public ResponseEntity<Object> getDemoDataFromRedis(@PathVariable String id) {
        DemoData demoData = fetchDemoDataFromRedis(id);
        if (demoData != null) {
            return ResponseEntity.status(HttpStatus.OK).body(demoData); // Correct
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found in Redis");
        }
    }

    // Ensure that your getDemoDataFromRedis method returns DemoData
    public DemoData fetchDemoDataFromRedis(String id) {
        return redisTemplate.opsForValue().get(id);
    }

    @GetMapping("/redis/getAllData")
    public Map<String, Object> getAllDataFromRedis() {
        // Fetch all keys and their corresponding values from Redis
        Map<Object, Object> allData = redisTemplate.opsForHash().entries("*");

        // Convert the keys to strings and create a new map
        Map<String, Object> allDataAsStringKeys = new HashMap<>();
        for (Map.Entry<Object, Object> entry : allData.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            allDataAsStringKeys.put(key, value);
        }

        return allDataAsStringKeys;
    }


    
   
}
