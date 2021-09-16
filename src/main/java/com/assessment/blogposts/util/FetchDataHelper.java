package com.assessment.blogposts.util;

import com.assessment.blogposts.model.Post;
import com.assessment.blogposts.model.PostsList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchDataHelper {
    public static final String ID = "id";
    public static final String READS = "reads";
    public static final String LIKES = "likes";
    public static final String POPULARITY = "popularity";
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    public static final String COMMA_MARK = ",";
    public static final String TAGS_PARAM_ERROR = "tags parameter is required";
    public static final String SORT_PARAM_ERROR = "sortBy parameter is invalid";
    public static final String DIRECT_PARAM_ERROR = "direction parameter is invalid";
    private final Set<String> SORT_FIELDS = Stream.of(ID, READS, LIKES, POPULARITY)
            .collect(Collectors.toCollection(HashSet::new));
    private final Set<String> DIRECT_FIELDS = Stream.of(ASC, DESC)
            .collect(Collectors.toCollection(HashSet::new));

    //To check the "sortBy" field is valid or not.
    public boolean isSortField(String sortBy) {
        return SORT_FIELDS.contains(sortBy);
    }

    //To check the "direction" field is valid or not.
    public boolean isDirectionField(String direction) {
        return DIRECT_FIELDS.contains(direction);
    }

    //To merge and sort the lists of posts
    public PostsList mergeAndSortPosts(List<PostsList> lists, String sortBy, String direction) {
        if (lists.size() == 1) return sortPosts(lists.get(0).getPosts(), sortBy, direction);
        return sortPosts(mergePosts(lists), sortBy, direction);
    }

    //To sort the posts based on "sortBy" field in "asc" or "desc" order indicated by direction
    private PostsList sortPosts(List<Post> posts, String sortBy, String direction) {
        if (sortBy.equals(ID)) {
            Comparator<Post> compareById =
                    (Post p1, Post p2) -> Long.compare(p1.getId(), p2.getId());
            if (direction.equals(ASC)) posts.sort(compareById);
            else posts.sort(compareById.reversed());
        }
        if (sortBy.equals(READS)) {
            Comparator<Post> compareByReads =
                    (Post p1, Post p2) -> Long.compare(p1.getReads(), p2.getReads());
            if (direction.equals(ASC)) posts.sort(compareByReads);
            else posts.sort(compareByReads.reversed());
        }
        if (sortBy.equals(LIKES)) {
            Comparator<Post> compareByLikes =
                    (Post p1, Post p2) -> Long.compare(p1.getLikes(), p2.getLikes());
            if (direction.equals(ASC)) posts.sort(compareByLikes);
            else posts.sort(compareByLikes.reversed());
        }
        if (sortBy.equals(POPULARITY)) {
            Comparator<Post> compareByPopularity =
                    (Post p1, Post p2) -> Float.compare(p1.getPopularity(), p2.getPopularity());
            if (direction.equals(ASC)) posts.sort(compareByPopularity);
            else posts.sort(compareByPopularity.reversed());
        }

        PostsList sortedList = new PostsList();
        sortedList.setPosts(posts);
        return sortedList;
    }

    //To merge the posts list without duplicates
    private List<Post> mergePosts(List<PostsList> lists) {
        Map<Long, Post> map = new HashMap<>();
        for (PostsList list : lists) {
            List<Post> posts = list.getPosts();
            for (Post post : posts) {
                map.putIfAbsent(post.getId(), post);
            }
        }
        return new ArrayList<>(map.values());
    }
}
