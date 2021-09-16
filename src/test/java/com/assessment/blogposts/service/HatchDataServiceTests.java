package com.assessment.blogposts.service;

import com.assessment.blogposts.exceptions.InvalidParameterException;
import com.assessment.blogposts.model.Post;
import com.assessment.blogposts.model.PostsList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HatchDataServiceTests {
    private static final String TECH = "tech";
    private static final String CULTURE = "culture";
    private static final String HISTORY = "history";
    private static final String STARTUPS = "startups";

    private static final String ID_FIELD = "id";
    private static final String LIKES_FIELD = "likes";

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    private static final String TAGS_PARAM_ERROR = "tags parameter is required";
    private static final String SORT_PARAM_ERROR = "sortBy parameter is invalid";
    private static final String DIRECT_PARAM_ERROR = "direction parameter is invalid";

    private final String PRE_URL = "https://api.hatchways.io/assessment/blog/posts?tag=";

    private static final long ID1 = 1;
    private static final long ID2 = 2;
    private static final long ID3 = 3;
    private static final String AUTHOR1 = "Hatways1";
    private static final String AUTHOR2 = "Hatways2";
    private static final String AUTHOR3 = "Hatways3";
    private static final long AUTHOR_ID1 = 1;
    private static final long AUTHOR_ID2 = 2;
    private static final long AUTHOR_ID3 = 3;
    private static final long LIKES1 = 2000;
    private static final long LIKES2 = 1000;
    private static final long LIKES3 = 1200;
    private static final float POPULARITY1 = 0.78f;
    private static final float POPULARITY2 = 0.85f;
    private static final float POPULARITY3 = 0.92f;
    private static final long READS1 = 5000;
    private static final long READS2 = 8000;
    private static final long READS3 = 2200;
    private static final List<String> TAGS1 =
            new ArrayList<>(Arrays.asList(TECH, CULTURE));
    private static final List<String> TAGS2 =
            new ArrayList<>(Arrays.asList(TECH, HISTORY));
    private static final List<String> TAGS3 =
            new ArrayList<>(Arrays.asList(TECH,STARTUPS, CULTURE));

    HatchDataService hatchDataService;
    RestTemplate restTemplateMock;

    @BeforeEach
    public void init() throws Exception {
        RestTemplateBuilder restTemplateBuilderMock = Mockito.mock(RestTemplateBuilder.class);
        restTemplateMock = Mockito.mock(RestTemplate.class);
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        hatchDataService = new HatchDataService(restTemplateBuilderMock);
    }


    @Test
    public void getPostsTechIdAsc() {
        PostsList postsListMock = new PostsList();
        Post p1 = new Post(ID1, AUTHOR1, AUTHOR_ID1, LIKES1, POPULARITY1, READS1, TAGS1);
        Post p3 = new Post(ID3, AUTHOR3, AUTHOR_ID3, LIKES3, POPULARITY3, READS3, TAGS3);
        postsListMock.setPosts(new ArrayList<Post>(Arrays.asList(p1, p3)));
        ResponseEntity responseEntity = new ResponseEntity(postsListMock, HttpStatus.ACCEPTED);
        when(restTemplateMock
                .exchange(PRE_URL+CULTURE, HttpMethod.GET, null, PostsList.class))
                .thenReturn(responseEntity);

        PostsList result = hatchDataService.getPosts(CULTURE, ID_FIELD, ASC);
        assertEquals(result.getPosts().size(), 2);
        assertEquals(result.getPosts().get(0).getId(), ID1);
        assertEquals(result.getPosts().get(1).getId(), ID3);
        assertEquals(result.getPosts().get(0).getLikes(), LIKES1);
        assertEquals(result.getPosts().get(1).getPopularity(), POPULARITY3);
        verify(restTemplateMock).exchange(PRE_URL+CULTURE, HttpMethod.GET, null, PostsList.class);
    }

    @Test
    public void getPostsTwoTagsLikesDesc() {
        PostsList postsListMock1 = new PostsList();
        PostsList postsListMock2 = new PostsList();
        Post p1 = new Post(ID1, AUTHOR1, AUTHOR_ID1, LIKES1, POPULARITY1, READS1, TAGS1);
        Post p2 = new Post(ID2, AUTHOR2, AUTHOR_ID2, LIKES2, POPULARITY2, READS2, TAGS2);
        Post p3 = new Post(ID3, AUTHOR3, AUTHOR_ID3, LIKES3, POPULARITY3, READS3, TAGS3);
        postsListMock1.setPosts(new ArrayList<Post>(Arrays.asList(p2)));
        postsListMock2.setPosts(new ArrayList<Post>(Arrays.asList(p1, p3)));
        ResponseEntity responseEntity1 = new ResponseEntity(postsListMock1, HttpStatus.ACCEPTED);
        ResponseEntity responseEntity2 = new ResponseEntity(postsListMock2, HttpStatus.ACCEPTED);
        when(restTemplateMock
                .exchange(PRE_URL+HISTORY, HttpMethod.GET, null, PostsList.class))
                .thenReturn(responseEntity1);
        when(restTemplateMock
                .exchange(PRE_URL+CULTURE, HttpMethod.GET, null, PostsList.class))
                .thenReturn(responseEntity2);

        PostsList result = hatchDataService.getPosts(HISTORY+","+CULTURE, LIKES_FIELD, DESC);
        assertEquals(result.getPosts().size(), 3);
        assertEquals(result.getPosts().get(0).getLikes(), LIKES1);
        assertEquals(result.getPosts().get(1).getLikes(), LIKES3);
        assertEquals(result.getPosts().get(2).getLikes(), LIKES2);
        verify(restTemplateMock).exchange(PRE_URL+HISTORY, HttpMethod.GET, null, PostsList.class);
        verify(restTemplateMock).exchange(PRE_URL+CULTURE, HttpMethod.GET, null, PostsList.class);
    }

    @Test
    public void getPostsWithEmptyTag() {
        Throwable thrown = assertThrows(InvalidParameterException.class,
                () -> {hatchDataService.getPosts("", LIKES_FIELD, DESC);});
        assertEquals(thrown.getMessage(), TAGS_PARAM_ERROR);
    }

    @Test
    public void getPostsWithInvalidSortBy() {
        Throwable thrown = assertThrows(InvalidParameterException.class,
                () -> {hatchDataService.getPosts(TECH, "invalid", DESC);});
        assertEquals(thrown.getMessage(), SORT_PARAM_ERROR);
    }

    @Test
    public void getPostsWithInvalidDirection() {
        Throwable thrown = assertThrows(InvalidParameterException.class,
                () -> {hatchDataService.getPosts(TECH, LIKES_FIELD, "invalid");});
        assertEquals(thrown.getMessage(), DIRECT_PARAM_ERROR);
    }
}