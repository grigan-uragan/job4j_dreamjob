package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemCandidateStore implements Store<Candidate> {
    private static final MemCandidateStore INST = new MemCandidateStore();
    private final AtomicInteger candidateId = new AtomicInteger(4);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemCandidateStore() {
        candidates.put(1, new Candidate(1, "Junior java developer"));
        candidates.put(2, new Candidate(2, "Middle java developer"));
        candidates.put(3, new Candidate(3, "Senior java developer"));
    }

    public static MemCandidateStore instOf() {
        return INST;
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    @Override
    public void save(Candidate element) {
        if (element.getId() == 0) {
            element.setId(candidateId.incrementAndGet());
        }
        candidates.put(element.getId(), element);
    }

    @Override
    public Candidate findById(int id) {
        return candidates.get(id);
    }
}
