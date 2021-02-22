package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store<Post> store = PsqlPostStore.instOf();
        Store<Candidate> candidateStore = PsqlCandidateStore.instOf();
        Candidate candidate = new Candidate(0, "jun");
        candidateStore.save(candidate);
        Post post = new Post(0, "java junior");
        store.save(post);
        Post middle = new Post(1, "middle");
        store.save(middle);
        candidate.setName("middle");
        System.out.println(store.findAll());
        System.out.println(store.findById(1));
        System.out.println(candidateStore.findAll());
        System.out.println(candidateStore.findById(1));
        candidateStore.save(candidate);
        System.out.println(candidateStore.findAll());
    }
}
