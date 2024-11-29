package org.example.toy_social_v1_1.repository.db_repo;

import org.example.toy_social_v1_1.domain.entities.Entity;
import org.example.toy_social_v1_1.repository.memory_repo.InMemoryRepo;

public abstract class AbstractDBRepo<ID extends Comparable<ID>, E extends Entity<ID>> extends InMemoryRepo<ID, E> {
    protected String connectionString;
    protected String user;
    protected String password;

    public AbstractDBRepo(String connectionString, String user, String password) {
        this.connectionString = connectionString;
        this.user = user;
        this.password = password;
    }

    protected abstract void read();
}
