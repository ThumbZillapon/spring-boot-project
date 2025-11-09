package com.techup.spring_demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> checkDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            response.put("status", "SUCCESS");
            response.put("connected", true);
            response.put("database", metaData.getDatabaseProductName());
            response.put("version", metaData.getDatabaseProductVersion());
            response.put("driver", metaData.getDriverName());
            response.put("driverVersion", metaData.getDriverVersion());
            response.put("url", maskUrl(metaData.getURL()));
            response.put("message", "Successfully connected to Supabase database");
            
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            response.put("status", "FAILED");
            response.put("connected", false);
            response.put("error", e.getMessage());
            response.put("sqlState", e.getSQLState());
            response.put("errorCode", e.getErrorCode());
            response.put("url", maskUrl(dataSourceProperties.getUrl()));
            
            if (e.getMessage() != null && e.getMessage().contains("UnknownHostException")) {
                response.put("issue", "HOSTNAME_RESOLUTION_FAILED");
                response.put("suggestion", "Please verify your Supabase project is active and the connection string is correct");
            }
            
            return ResponseEntity.status(503).body(response);
        }
    }

    private String maskUrl(String url) {
        if (url == null) {
            return "N/A";
        }
        return url.replaceAll("password=[^;&]+", "password=***");
    }
}


