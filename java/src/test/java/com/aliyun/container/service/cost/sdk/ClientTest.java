package com.aliyun.container.service.cost.sdk;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;
import java.util.HashMap;

public class ClientTest {
    private Client client;
    private Config config;

    @Before
    public void setUp() {
        config = new Config();
        config.setEndpoint("http://127.0.0.1:8080");
        client = new Client(config);
    }

    @Test
    public void testClientCreation() {
        assertNotNull("Client should be created", client);
        assertNotNull("Config should be set", client.getConfig());
        assertEquals("Endpoint should match", "http://127.0.0.1:8080", client.getConfig().getEndpoint());
    }

    @Test
    public void testGetCostV2WithMockData() {
        Map<String, Object> request = new HashMap<>();
        request.put("window", "today");
        
        Map<String, Object> response = client.getCostV2(request);
        
        assertNotNull("Response should not be null", response);
        assertTrue("Response should contain code", response.containsKey("code"));
        // 由于使用本地地址，应该返回模拟数据
        assertEquals("Should return mock data with code 200", 200, response.get("code"));
        assertTrue("Should contain data", response.containsKey("data"));
    }

    @Test
    public void testGetAllocationWithMockData() {
        Map<String, Object> request = new HashMap<>();
        request.put("window", "today");
        
        Map<String, Object> response = client.getAllocation(request);
        
        assertNotNull("Response should not be null", response);
        assertTrue("Response should contain code", response.containsKey("code"));
        // 由于使用本地地址，应该返回模拟数据
        assertEquals("Should return mock data with code 200", 200, response.get("code"));
        assertTrue("Should contain data", response.containsKey("data"));
    }

    @Test
    public void testGetCostV2WithNamespaceAggregation() {
        Map<String, Object> request = new HashMap<>();
        request.put("window", "today");
        request.put("aggregate", "namespace");
        
        Map<String, Object> response = client.getCostV2(request);
        
        assertNotNull("Response should not be null", response);
        assertEquals("Should return mock data with code 200", 200, response.get("code"));
        assertTrue("Should contain data", response.containsKey("data"));
    }

    @Test
    public void testGetAllocationWithNamespaceAggregation() {
        Map<String, Object> request = new HashMap<>();
        request.put("window", "today");
        request.put("aggregate", "namespace");
        
        Map<String, Object> response = client.getAllocation(request);
        
        assertNotNull("Response should not be null", response);
        assertEquals("Should return mock data with code 200", 200, response.get("code"));
        assertTrue("Should contain data", response.containsKey("data"));
    }
}