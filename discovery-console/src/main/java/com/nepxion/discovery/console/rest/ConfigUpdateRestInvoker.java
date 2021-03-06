package com.nepxion.discovery.console.rest;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.client.RestTemplate;

public class ConfigUpdateRestInvoker extends AbstractRestInvoker {
    private String config;
    private boolean async;

    public ConfigUpdateRestInvoker(List<ServiceInstance> serviceInstances, RestTemplate restTemplate, String config, boolean async) {
        super(serviceInstances, restTemplate);

        this.config = config;
        this.async = async;
    }

    @Override
    protected String getInfo() {
        return "Config updated";
    }

    @Override
    protected String getUrl(String host, int port) {
        return "http://" + host + ":" + port + "/config/update-" + (async ? "async" : "sync");
    }

    @Override
    protected String doRest(String url) {
        return restTemplate.postForEntity(url, config, String.class).getBody();
    }
}