package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Stub
public class PostRepository {

    private Map<Long, String> map = new HashMap<>();
    private long count = 0;
    NotFoundException exception = new NotFoundException();
    private Lock ThreadLock;
    private Condition condition;

    //ПОТОКОБЕЗОПАСНОСТЬ + РЕФАКТОРИНГ
    public List<Post> all() {
        Iterator<Map.Entry<Long, String>> iterator = map.entrySet().iterator();
        List<Post> collectionPost = new ArrayList<>();
        ThreadLock.lock();
        try {
            condition.await();
            while (iterator.hasNext()) {
                Map.Entry<Long, String> entry = iterator.next();
                Long key = entry.getKey();
                String value = entry.getValue();
                Post post = new Post(key, value);
                collectionPost.add(post);
            }
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            ThreadLock.unlock();
        }
        return collectionPost;
    }

    public Optional<Post> getById(long id) {

        Post post = new Post(id, map.get(id));
        Optional<Post> optionalPost = Optional.ofNullable(post);
        return optionalPost;
    }

    public Post save(Post post) {
        ThreadLock.lock();
        try {
            condition.await();
            if (post.getId() == 0) {
                count += 1;
                map.put(count, post.getContent());
                post.setId(count);
                post.setContent(map.get(post.getId()));
            } else {
                Iterator<Map.Entry<Long, String>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, String> entry = iterator.next();
                    Long key = entry.getKey();
                    if (key == post.getId()) {
                        map.put(key, post.getContent());
                        post.setId(key);
                        post.setContent(map.get(post.getId()));
                    } else {
                        exception.printStackTrace();
                    }
                }
            }
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            ThreadLock.unlock();
        }
        return post;
    }

    public void removeById(long id) {
        Iterator<Map.Entry<Long, String>> iterator = map.entrySet().iterator();
        ThreadLock.lock();
        try {
            condition.await();
            while (iterator.hasNext()) {
                Map.Entry<Long, String> entry = iterator.next();
                Long key = entry.getKey();
                if (key == id) {
                    map.remove(id);
                } else {
                    exception.printStackTrace();
                }
            }
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            ThreadLock.unlock();
        }
    }
}
