package ru.netology.repository;


import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Repository
public class PostRepository {

    private Map<Long, String> map = new ConcurrentHashMap<>();
    private AtomicInteger count = new AtomicInteger();

    public List<Post> all() {
        List<Post> collectionPost = new ArrayList<>();
        Post post = (Post) map.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.toList());
        collectionPost.add(post);
        return collectionPost;
    }

    public Optional<Post> getById(long id) {

        Post post = new Post(id, map.get(id));
        Optional<Post> optionalPost = Optional.ofNullable(post);
        return optionalPost;
    }

    public Post save(Post post) {

        if (post.getId() == 0) {
            count.getAndIncrement();
            map.put((long) count.get(), post.getContent());
            post.setId(count.get());
            post.setContent(map.get(post.getId()));
        } else {

            if (map.get(post.getId()) != null) {
                map.put(post.getId(), post.getContent());
                post.setId(post.getId());
                post.setContent(map.get(post.getId()));
            }

        }

        return post;
    }

    public void removeById(long id) {
        map.remove(id);

    }
}
