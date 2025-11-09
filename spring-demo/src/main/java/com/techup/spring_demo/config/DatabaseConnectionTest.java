package com.techup.spring_demo.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @PostConstruct
    public void testConnection() {
        log.info("=".repeat(60));
        log.info("Testing Supabase Database Connection...");
        log.info("=".repeat(60));
        
        String url = dataSourceProperties.getUrl();
        String username = dataSourceProperties.getUsername();
        
        log.info("Database URL: {}", maskUrl(url));
        log.info("Database Username: {}", username != null ? username.substring(0, Math.min(username.length(), 20)) + "***" : "N/A");
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            log.info("✅ Connection SUCCESSFUL!");
            log.info("Database Product: {}", metaData.getDatabaseProductName());
            log.info("Database Version: {}", metaData.getDatabaseProductVersion());
            log.info("Driver Name: {}", metaData.getDriverName());
            log.info("Driver Version: {}", metaData.getDriverVersion());
            log.info("JDBC URL: {}", metaData.getURL());
            log.info("Connection Status: Connected");
            log.info("=".repeat(60));
            
        } catch (SQLException e) {
            log.error("❌ Connection FAILED!");
            log.error("Error Message: {}", e.getMessage());
            log.error("SQL State: {}", e.getSQLState());
            log.error("Error Code: {}", e.getErrorCode());
            
            // Check if it's a hostname resolution issue
            if (e.getMessage() != null && e.getMessage().contains("UnknownHostException")) {
                log.error("");
                log.error("⚠️  HOSTNAME RESOLUTION ISSUE DETECTED");
                log.error("The hostname '{}' could not be resolved.", extractHostname(url));
                log.error("Please verify:");
                log.error("  1. The Supabase project is active");
                log.error("  2. The connection string format is correct");
                log.error("  3. Network connectivity to Supabase");
                log.error("  4. Try using the connection pooler: {}.pooler.supabase.com:6543", extractProjectRef(url));
            }
            
            log.error("=".repeat(60));
            // Don't throw exception - let the app start but log the error
            log.warn("Application will continue, but database connection is not available.");
        }
    }
    
    private String extractHostname(String url) {
        if (url == null) return "N/A";
        try {
            int start = url.indexOf("//") + 2;
            int end = url.indexOf(":", start);
            if (end == -1) end = url.indexOf("/", start);
            if (end == -1) end = url.indexOf("?", start);
            return url.substring(start, end > 0 ? end : url.length());
        } catch (Exception e) {
            return url;
        }
    }
    
    private String extractProjectRef(String url) {
        String hostname = extractHostname(url);
        if (hostname.contains(".")) {
            return hostname.split("\\.")[0];
        }
        return "unknown";
    }

    private String maskUrl(String url) {
        if (url == null) {
            return "N/A";
        }
        // Mask password in URL if present
        return url.replaceAll("password=[^;&]+", "password=***");
    }
}

