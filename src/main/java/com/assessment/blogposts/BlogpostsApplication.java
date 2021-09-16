package com.assessment.blogposts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * The REST API Endpoint Service to provide Blog Posts.
 * This service caches constant request results in internal cache implemented with ConcurrentHashMap
 * The Cache updates every 1 hour in the background.
 * To ping the service: http://localhost:8080/ping
 * To get the organized posts:
 * http://localhost:8080/api/posts?tags=history,tech&sortBy=likes&direction=desc
 * (The "tags" parameter is required and "sortBy", "direction" parameters are optional
 * @author: Lian Li
 * @version: 1.0
 * @date: 2021-07-20
 *
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class BlogpostsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogpostsApplication.class, args);
	}

}
