package org.example.toy_social_v1_1.repository.memory_repo;

import org.example.toy_social_v1_1.domain.entities.Entity;
import org.example.toy_social_v1_1.repository.Repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepo<ID extends Comparable<ID>, E extends Entity<ID>> implements Repo<ID, E> {
    protected Map<ID, E> elems;
    protected ID lastID;

    public InMemoryRepo() {
        elems = new HashMap<ID, E>();
        lastID = null;
    }

    @Override
    public Optional<E> find(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Null ID");

        return Optional.ofNullable(elems.get(id));
    }

    @Override
    public Iterable<E> getAll() {
        return elems.values();
    }

    @Override
    public Optional<E> add(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null entity");

        if(elems.containsKey(entity.getID())) // id exists => Optional<Entity>
            return Optional.of(elems.get(entity.getID()));

        elems.put(entity.getID(), entity);

        if(lastID == null)
            lastID = entity.getID();
        else if(lastID.compareTo(entity.getID()) < 0)
            lastID = entity.getID();

        return Optional.empty(); // new entity added => Optional.empty()
    }

    @Override
    public Optional<E> delete(ID id) {
        getAll().forEach(System.out::println);

        if(id == null)
            throw new IllegalArgumentException("Null ID");

        E res = elems.remove(id);
        System.out.println("MEMORY REPO DELETE: " + res);

        return Optional.ofNullable(res);
    }

    @Override
    public Optional<E> update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null entity");

        E res = elems.replace(entity.getID(), entity);
        if(res == null)
            return Optional.of(entity); // id invalid => return given entity

        return Optional.empty(); // update done
    }

    public ID getLastID() {
        return lastID;
    }

    public void setLastID(ID lastID) {
        this.lastID = lastID;
    }
}
