package org.example.toy_social_v1_1.service.network;

import org.example.toy_social_v1_1.domain.entities.Friendship;
import org.example.toy_social_v1_1.domain.entities.MyGraph;
import org.example.toy_social_v1_1.domain.entities.User;
import org.example.toy_social_v1_1.service.entity.FriendshipService;
import org.example.toy_social_v1_1.service.entity.UserService;
import org.example.toy_social_v1_1.util.paging.Page;
import org.example.toy_social_v1_1.util.paging.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.sql.Date;

public class Network implements NetworkService {
    protected UserService userService;
    protected FriendshipService friendshipService;
    protected MyGraph<Long, User> network;

    public Network(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        constructNetwork();
    }

    protected void constructNetwork() {
        List<List<User>> links = new ArrayList<>();
        friendshipService.getAll().forEach(fr -> {
            Optional<User> u1 = userService.find(fr.getId1());
            Optional<User> u2 = userService.find(fr.getId2());
            if(u1.isEmpty() || u2.isEmpty()) {
                throw new RuntimeException("Invalid friendship");
            }
            links.add(Arrays.asList(u1.get(), u2.get()));
        });
        network = new MyGraph<>(userService.getAll(), links);
    }

    @Override
    public int groupCount() {
        network.countComps();
        return network.getCompCount();
    }

    @Override
    public List<User> maxComp() {
        return network.maxComp();
    }

    @Override
    public Optional<User> removeUser(Long userId){
        Optional<User> user = userService.delete(userId); // delete from user repo
        if(user.isEmpty()) {
            throw new RuntimeException("Invalid user");
        }

        try { // unlink friends from network
            network.unlinkEntity(user.get());
            friendshipService.removeUserFriendships(userId);
        } catch (NullPointerException e) {
            System.out.println("Deleted user had no friends");
        }

        constructNetwork();
        return user;
    }

    @Override
    public Optional<User> addUser(String username, String password) {
        User newUser = new User(username, password);
        Optional<User> res = userService.add(newUser); // add in user list
        network.setAll(userService.getAll()); // update network (vertex with degree 0)
        return userService.find(userService.getLastId());
    }

    @Override
    public Optional<Friendship> removeFriend(Long idUser, Long idFriend) {
        Optional<User> user = userService.find(idUser);
        if(user.isEmpty()) {
            throw new RuntimeException("Invalid user Id");
        }

        Optional<User> friend = userService.find(idFriend);
        if(friend.isEmpty()) {
            throw new RuntimeException("Invalid friend Id");
        }

        network.unlink(user.get(), friend.get());
        return friendshipService.removeFriendship(idUser, idFriend);
    }

    @Override
    public Optional<Friendship> addFriend(Long idUser, Long idFriend) {
        Optional<User> user = userService.find(idUser);
        if(user.isEmpty()) {
            throw new RuntimeException("Invalid user Id");
        }

        Optional<User> friend = userService.find(idFriend);
        if(friend.isEmpty()) {
            throw new RuntimeException("Invalid friend Id");
        }

        if(!network.link(user.get(), friend.get()))
            return Optional.empty();

        Friendship newFr = new Friendship(idUser, idFriend, Date.valueOf(LocalDate.now()));
        newFr.setID(friendshipService.getLastId() + 1);
        return friendshipService.add(newFr);
    }

    @Override
    public List<User> getUserFriends(Long idUser) {
        List<User> res = new ArrayList<>();
        friendshipService.getUserFriends(idUser).forEach(friendId -> {
            Optional<User> user = userService.find(friendId);
            user.ifPresent(res::add);
        });
        return res;
    }

    public Page<User> findAllUsersFriendshipsOnPage(Pageable pageable, Long userId) {
        List<User> friends = new ArrayList<>();
        Page<Friendship> friendshipPage = friendshipService.findAllUserFriendshipsOnPage(pageable, userId);
        friendshipPage.getElementsOnPage().forEach(friendship -> {
            Optional<User> user = userService.find(friendship.getId2());
            user.ifPresent(friends::add);
        });
        return new Page<>(friends, friendshipPage.getTotalNumberOfElements());
    }
}
