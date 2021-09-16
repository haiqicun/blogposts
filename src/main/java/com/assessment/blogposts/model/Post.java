package com.assessment.blogposts.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class Post {
    private long id;
    private String author;
    private long authorId;
    private long likes;
    private float popularity;
    private long reads;
    private List<String> tags;
}
