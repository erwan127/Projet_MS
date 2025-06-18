package com.example.statisticsservice.client;

import com.example.statisticsservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8083")
public interface UserServiceClient {
    
    @GetMapping("/api/users")
    List<UserDTO> getAllUsers();
    
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") String id);
    
    @GetMapping("/api/users/active")
    List<UserDTO> getActiveUsers();
    
    @GetMapping("/api/users/with-rentals")
    List<UserDTO> getUsersWithActiveRentals();
} 