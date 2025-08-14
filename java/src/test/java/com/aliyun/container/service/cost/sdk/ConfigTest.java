package com.aliyun.container.service.cost.sdk;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigTest {
    private Config config;

    @Before
    public void setUp() {
        config = new Config();
    }

    @Test
    public void testConfigCreation() {
        assertNotNull("Config should be created", config);
    }

    @Test
    public void testEndpointSetterAndGetter() {
        String endpoint = "http://test.endpoint.com";
        config.setEndpoint(endpoint);
        assertEquals("Endpoint should match", endpoint, config.getEndpoint());
    }

    @Test
    public void testDefaultEndpoint() {
        // 默认端点应该是本地地址
        assertEquals("Default endpoint should be local", "http://127.0.0.1:8080", config.getEndpoint());
    }
}