package com.assessment.blogposts.service;

import com.assessment.blogposts.exceptions.InvalidParameterException;
import com.assessment.blogposts.model.PostsList;
import com.assessment.blogposts.util.FetchDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * The service to get Blog Posts from external Rest service endpoint:
 * https://api.hatchways.io/assessment/blog/posts
 * This service maps the result to corresponding controller methods
 */
@Service
public class HatchDataService {

    private final String PRE_URL = "https://api.hatchways.io/assessment/blog/posts?tag=";
    private FetchDataHelper fetchDataHelper;
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(HatchDataService.class);

    @Autowired
    public HatchDataService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.fetchDataHelper = new FetchDataHelper();
    }

    /**
     * The method to get posts from cache first
     * And if no corresponding result in the cache, then fetch data from external service and store in cache.
     * @param tags: tag of the post
     * @param sortBy: the result will be ordered based on the "sortBy" field
     * @param direction: the result will be sort in asc or desc order specified by "direction"
     * @return: A PostsList object contains the list of posts.
     */
    @Cacheable("PostsCache")
    public PostsList getPosts(String tags, String sortBy, String direction) {
        logger.info("No cached data found, running method to fetch data from external api");
        if (tags == null || tags.length() == 0) {
            throw new InvalidParameterException(fetchDataHelper.TAGS_PARAM_ERROR);
        }
        logger.info("tags are: " + tags);
        if (sortBy != null && !fetchDataHelper.isSortField(sortBy)) {
            throw new InvalidParameterException(fetchDataHelper.SORT_PARAM_ERROR);
        }
        logger.info("sortBy is: " + sortBy);
        if (direction != null && !fetchDataHelper.isDirectionField(direction)) {
            throw new InvalidParameterException(fetchDataHelper.DIRECT_PARAM_ERROR);
        }
        logger.info("direction is: " + direction);

        tags = tags.trim();
        String[] tagArray = tags.split(fetchDataHelper.COMMA_MARK);
        List<PostsList> lists = new ArrayList<>();
        for (String t : tagArray) {
            lists.add(restTemplate.exchange(PRE_URL + t, HttpMethod.GET, null, PostsList.class).getBody());
        }

        if (lists.size() == 1 && sortBy.equals(fetchDataHelper.ID) && direction.equals(fetchDataHelper.ASC)) return lists.get(0);
        return fetchDataHelper.mergeAndSortPosts(lists, sortBy, direction);
    }

    //To empty the cache
    @CacheEvict(cacheNames = "PostsCache", allEntries = true)
    public void emptyCache() {
        logger.info("To empty the cache");
    }

    //Scheduled task to empty the cache at midnight every day
    @Scheduled(cron = "@midnight")
    public void scheduledEmptyCache() {
        emptyCache();
    }

}
