package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemPostStore implements Store<Post> {
    private static final MemPostStore INST = new MemPostStore();
    private final AtomicInteger postId = new AtomicInteger(4);
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private MemPostStore() {
        posts.put(1, new Post(1, "Junior Java job"));
        posts.put(2, new Post(2, "Middle Java job"));
        posts.put(3, new Post(3, "Senior Java job"));
    }

    public static MemPostStore instOf() {
        return INST;
    }

    @Override
    public Collection<Post> findAll() {
        return posts.values();
    }

    @Override
    public void save(Post element) {
        if (element.getId() == 0) {
            element.setId(postId.incrementAndGet());
        }
        posts.put(element.getId(), element);
    }

    @Override
    public Post findById(int id) {
        return posts.get(id);
    }
}
