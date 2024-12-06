package org.example.toy_social_v1_1.repository;

import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepo extends PagedRepo<Long, Friendship> {
    Long getLastId();

    void removeUserFriendships(Long userId);
    Optional<Friendship> removeFriendship(Long id1, Long id2);
    Optional<Friendship> getFriendshipByUsers(Long id1, Long id2);

    List<Long> getUserFriends(Long id1);
    Page<Friendship> findAllUserFriendshipsOnPage(Pageable pageable, Long userId);
}
