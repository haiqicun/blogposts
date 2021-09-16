package com.assessment.blogposts.controller;

import com.assessment.blogposts.exceptions.InvalidParameterException;
import com.assessment.blogposts.model.PingResponse;
import com.assessment.blogposts.model.Post;
import com.assessment.blogposts.model.PostsList;
import com.assessment.blogposts.service.HatchDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

public class PostControllerUnitTests {
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

    PostController postController;
    HatchDataService hatchDataServiceMock;

    @BeforeEach
    public void init() {
        hatchDataServiceMock = Mockito.mock(HatchDataService.class);
        postController = new PostController(hatchDataServiceMock);

    }
    @Test
    public void getPingResult() {
        PingResponse result = postController.getPingResult();
        assertTrue(result.isSuccess());
    }

    @Test
    public void getPostsWithTechIdAsc() {
        PostsList postsListMock = new PostsList();
        Post p1 = new Post(ID1, AUTHOR1, AUTHOR_ID1, LIKES1, POPULARITY1, READS1, TAGS1);
        Post p2 = new Post(ID2, AUTHOR2, AUTHOR_ID2, LIKES2, POPULARITY2, READS2, TAGS2);
        Post p3 = new Post(ID3, AUTHOR3, AUTHOR_ID3, LIKES3, POPULARITY3, READS3, TAGS3);
        postsListMock.setPosts(new ArrayList<Post>(Arrays.asList(p1, p2, p3)));

        when(hatchDataServiceMock.
                getPosts(TECH, ID_FIELD, ASC))
                .thenReturn(postsListMock);
        PostsList result = postController.getPosts(TECH, ID_FIELD, ASC);
        assertEquals(3, result.getPosts().size());
        assertEquals(ID1, result.getPosts().get(0).getId());
        assertEquals(ID2, result.getPosts().get(1).getId());
        assertEquals(ID3, result.getPosts().get(2).getId());
        assertEquals(LIKES2, result.getPosts().get(1).getLikes());
        assertEquals(POPULARITY3, result.getPosts().get(2).getPopularity());
    }

    @Test
    public void getPostsWithEmptyTag() {
        InvalidParameterException ex = new InvalidParameterException(TAGS_PARAM_ERROR);
        when(hatchDataServiceMock.
                getPosts("", LIKES_FIELD, DESC))
                .thenThrow(ex);

        assertThrows(InvalidParameterException.class,
                () -> {postController.getPosts("", LIKES_FIELD, DESC);});
        Throwable thrown = assertThrows(InvalidParameterException.class,
                () -> {postController.getPosts("", LIKES_FIELD, DESC);});
        assertEquals(thrown.getMessage(), TAGS_PARAM_ERROR);
    }

    @Test
    public void getPostsWithInvalidSortBy() {
        InvalidParameterException ex = new InvalidParameterException(SORT_PARAM_ERROR);
        when(hatchDataServiceMock.
                getPosts(CULTURE, "invalid", DESC))
                .thenThrow(ex);

        Throwable thrown = assertThrows(InvalidParameterException.class,
                () -> {postController.getPosts(CULTURE, "invalid", DESC);});
        assertEquals(thrown.getMessage(), SORT_PARAM_ERROR);
    }
}