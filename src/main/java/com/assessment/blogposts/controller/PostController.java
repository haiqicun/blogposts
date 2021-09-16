package com.assessment.blogposts.controller;

import com.assessment.blogposts.model.PingResponse;
import com.assessment.blogposts.model.PostsList;
import com.assessment.blogposts.service.HatchDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * The Rest Controller specifies the RESTful endpoint for the Blog Posts service.
 * The controller also maps method to the RESTful action specified.
 */
@RestController
@RequestMapping("/api")
public class PostController {
    private HatchDataService hatchDataService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    public PostController(HatchDataService hatchDataService) {
        this.hatchDataService = hatchDataService;
    }

    @GetMapping("/ping")
    public PingResponse getPingResult() {
        PingResponse pingResponse = new PingResponse();
        pingResponse.setSuccess(true);

        return pingResponse;
    }

    @GetMapping("/posts")
    public PostsList getPosts(@RequestParam String tags,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "asc") String direction) {
        logger.info("Get posts is called.");
        return hatchDataService.getPosts(tags, sortBy, direction);
    }
}
