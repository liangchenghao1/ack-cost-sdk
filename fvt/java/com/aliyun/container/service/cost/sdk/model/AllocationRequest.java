package com.aliyun.container.service.cost.sdk.model;

import java.util.HashMap;
import java.util.Map;

public class AllocationRequest {
    private String window;
    private String filter;
    private String aggregate;
    
    public String getWindow() {
        return window;
    }
    
    public void setWindow(String window) {
        this.window = window;
    }
    
    public String getFilter() {
        return filter;
    }
    
    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    public String getAggregate() {
        return aggregate;
    }
    
    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (window != null) {
            map.put("window", window);
        }
        if (filter != null) {
            map.put("filter", filter);
        }
        if (aggregate != null) {
            map.put("aggregate", aggregate);
        }
        return map;
    }
}