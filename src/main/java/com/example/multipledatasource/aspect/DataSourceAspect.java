package com.example.multipledatasource.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.multipledatasource.config.AbstractRoutingDatasourceConnections;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(-1)
public class DataSourceAspect {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	//defining where the jointpoint need to be applied
	@Pointcut("execution(* com.example.multipledatasource.controller.*.*(..))")
	public void requestsPointCut() {
	}

	// setting the connection using the header passed on the request
	@Before("requestsPointCut()")
	public void before() throws InterruptedException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String region = request.getHeader("Region");

		if(region != null){
			AbstractRoutingDatasourceConnections.setDatabaseName(region);
			logger.info("Switch DataSource to {}", region);

			if (region.equals("UK")) {
				Thread.sleep(5000);
			}
		}
	}
}
