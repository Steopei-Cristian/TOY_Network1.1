package org.example.toy_social_v1_1.repository;

import org.example.toy_social_v1_1.domain.entities.Entity;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

public interface PagedRepo<ID extends Comparable<ID>, E extends Entity<ID>> extends Repo<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
