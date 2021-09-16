package com.assessment.blogposts.controller;

import com.assessment.blogposts.exceptions.InvalidParameterException;
import com.assessment.blogposts.model.Post;
import com.assessment.blogposts.model.PostsList;
import com.assessment.blogposts.service.HatchDataService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PostControllerIntegrationTest {
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
    private static final long LIKES1 = 1000;
    private static final long LIKES2 = 2000;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HatchDataService hatchDataServiceMock;


    @Test
    public void getPingResult() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void getPostsWithTech() throws Exception {
        PostsList postsListMock = new PostsList();
        Post p1 = new Post(ID1, AUTHOR1, AUTHOR_ID1, LIKES1, POPULARITY1, READS1, TAGS1);
        Post p2 = new Post(ID2, AUTHOR2, AUTHOR_ID2, LIKES2, POPULARITY2, READS2, TAGS2);
        Post p3 = new Post(ID3, AUTHOR3, AUTHOR_ID3, LIKES3, POPULARITY3, READS3, TAGS3);
        postsListMock.setPosts(new ArrayList<Post>(Arrays.asList(p1, p2, p3)));

        when(hatchDataServiceMock.
                getPosts(TECH, ID_FIELD, ASC))
                .thenReturn(postsListMock);
        mockMvc.perform(get("/api/posts?tags=tech"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].id").value(ID1))
                .andExpect(jsonPath("$.posts[1].id").value(ID2))
                .andExpect(jsonPath("$.posts[2].id").value(ID3))
                .andExpect(jsonPath("$.posts[0].tags", Matchers.hasItem(TECH)))
                .andExpect(jsonPath("$.posts[1].tags", Matchers.hasItem(TECH)))
                .andExpect(jsonPath("$.posts[2].tags", Matchers.hasItem(TECH)))
                .andExpect(jsonPath("$.posts[1].likes").value(LIKES2))
                .andExpect(jsonPath("$.posts[2].popularity").value(POPULARITY3));

        verify(hatchDataServiceMock).getPosts(TECH, ID_FIELD, ASC);
    }
    @Test
    public void getPostsWithCultureLikesDesc() throws Exception {
        PostsList postsListMock = new PostsList();
        Post p1 = new Post(ID1, AUTHOR1, AUTHOR_ID1, LIKES1, POPULARITY1, READS1, TAGS1);
        Post p3 = new Post(ID3, AUTHOR3, AUTHOR_ID3, LIKES3, POPULARITY3, READS3, TAGS3);
        postsListMock.setPosts(new ArrayList<Post>(Arrays.asList(p3, p1)));

        when(hatchDataServiceMock.
                getPosts(CULTURE,LIKES_FIELD, DESC))
                .thenReturn(postsListMock);
        mockMvc.perform(get("/api/posts?tags=culture&sortBy=likes&direction=desc"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].tags", Matchers.hasItem(CULTURE)))
                .andExpect(jsonPath("$.posts[1].tags", Matchers.hasItem(CULTURE)))
                .andExpect(jsonPath("$.posts[0].id").value(ID3))
                .andExpect(jsonPath("$.posts[1].id").value(ID1))
                .andExpect(jsonPath("$.posts[0].likes").value(LIKES3))
                .andExpect(jsonPath("$.posts[1].likes").value(LIKES1));

        verify(hatchDataServiceMock).getPosts(CULTURE, LIKES_FIELD, DESC);
    }

    @Test
    public void getPostsWithEmptyTags() throws Exception {
        InvalidParameterException ex = new InvalidParameterException(TAGS_PARAM_ERROR);
        when(hatchDataServiceMock.
                getPosts("", ID_FIELD, ASC))
                .thenThrow(ex);
        mockMvc.perform(get("/api/posts?tags="))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(TAGS_PARAM_ERROR));
        verify(hatchDataServiceMock).getPosts("", ID_FIELD, ASC);
    }

    @Test
    public void getPostsWithoutTags() throws Exception {
        mockMvc.perform(get("/api/posts?"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(TAGS_PARAM_ERROR));
    }

    @Test
    public void getPostsWithInvalidSortBy() throws Exception {
        InvalidParameterException ex = new InvalidParameterException(SORT_PARAM_ERROR);
        when(hatchDataServiceMock.
                getPosts(TECH, "invalid", ASC))
                .thenThrow(ex);
        mockMvc.perform(get("/api/posts?tags=tech&sortBy=invalid&direction=asc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(SORT_PARAM_ERROR));
        verify(hatchDataServiceMock).getPosts(TECH, "invalid", ASC);
    }
}
