package com.nepxion.discovery.console.rest;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.nepxion.discovery.console.entity.ResultEntity;
import com.nepxion.discovery.console.handler.ConsoleErrorHandler;

public abstract class AbstractRestInvoker {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestInvoker.class);

    protected List<ServiceInstance> serviceInstances;
    protected RestTemplate restTemplate;

    public AbstractRestInvoker(List<ServiceInstance> serviceInstances, RestTemplate restTemplate) {
        this.serviceInstances = serviceInstances;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> invoke() {
        if (CollectionUtils.isEmpty(serviceInstances)) {
            LOG.warn("No service instances found");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No service instances found");
        }

        List<ResultEntity> resultEntityList = new ArrayList<ResultEntity>();
        for (ServiceInstance serviceInstance : serviceInstances) {
            String serviceId = serviceInstance.getServiceId().toLowerCase();
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();

            String url = getUrl(host, port);
            String result = doRest(url);

            if (!StringUtils.equals(result, "OK")) {
                ConsoleErrorHandler errorHandler = (ConsoleErrorHandler) restTemplate.getErrorHandler();
                result = errorHandler.getCause();
            }

            ResultEntity resultEntity = new ResultEntity();
            resultEntity.setServiceId(serviceId);
            resultEntity.setUrl(url);
            resultEntity.setResult(result);

            resultEntityList.add(resultEntity);
        }

        String info = getInfo();
        LOG.info(info + " results=\n{}", resultEntityList);

        return ResponseEntity.ok().body(resultEntityList);
    }

    protected abstract String getInfo();

    protected abstract String getUrl(String host, int port);

    protected abstract String doRest(String url);
}