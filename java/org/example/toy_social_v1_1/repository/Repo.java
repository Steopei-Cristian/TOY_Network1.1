package org.example.toy_social_v1_1.repository;

import org.example.toy_social_v1_1.domain.entities.Entity;
import org.example.toy_social_v1_1.exceptions.ValidationException;

import java.util.Optional;

public interface Repo<ID extends Comparable<ID>, E extends Entity<ID>> {
    /**
     * CRUD operations repository interface
     * @param <ID> - type E must have an attribute of type ID
     * @param <E> - type of entities saved in repository
     */

    /**
     *
     * @param id -the id of the entity to be returned
     * id must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     * @throws IllegalArgumentException
     * if id is null.
     */
    Optional<E> find(ID id);
    /**
     *
     * @return all entities
     */
    Iterable<E> getAll();
    /**
     *
     * @param entity
     * entity must be not null
     * @return an {@code Optional} - null if the entity was saved,
     * - the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null. *
     */
    Optional<E> add(E entity);
    /**
     * removes the entity with the specified id
     * @param id
     * id must be not null
     * @return an {@code Optional}
     * - null if there is no entity with the given id,
     * - the removed entity, otherwise
     * @throws IllegalArgumentException
     * if the given id is null.
     */
    Optional<E> delete(ID id);
    /**
     *
     * @param entity
     * entity must not be null
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity.
     * @throws IllegalArgumentException
     * if the given entity is null.
     * @throws ValidationException
     * if the entity is not valid.
     */
    Optional<E> update(E entity);
}