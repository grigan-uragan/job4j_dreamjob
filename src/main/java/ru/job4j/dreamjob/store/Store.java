package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {
    private static final Store INST = new Store();
    private static AtomicInteger postId = new AtomicInteger(4);
    private static AtomicInteger candidateId = new AtomicInteger(4);
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private Store() {
        posts.put(1, new Post(1, "Junior Java job"));
        posts.put(2, new Post(2, "Middle Java job"));
        posts.put(3, new Post(3, "Senior Java job"));
        candidates.put(1, new Candidate(1, "Junior java developer"));
        candidates.put(2, new Candidate(2, "Middle java developer"));
        candidates.put(3, new Candidate(3, "Senior java developer"));
    }

    public static Store instOf() {
        return INST;
    }

    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    public void savePost(Post post) {
        post.setId(postId.incrementAndGet());
        posts.put(post.getId(), post);
    }

    public void saveCandidate(Candidate candidate) {
        candidate.setId(candidateId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
    }
}
