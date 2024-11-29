//package org.example.toy_social_v1_1.repository.file_repo;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import org.example.toy_social_v1_1.domain.entities.Friendship;
//import org.example.toy_social_v1_1.repository.FriendshipRepo;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.function.Predicate;
//
//public class FriendshipFileRepo extends AbstractFileRepo<Long, Friendship> implements FriendshipRepo {
//
//    public FriendshipFileRepo(Path path) throws IOException {
//        super(path);
//        //network = new HashMap<>();
//        //constructNetwork(getAll());
//    }
//
////    private void constructNetwork(Iterable<Friendship> friendships) {
////        friendships.forEach(fr -> {
////            // Predicate<Long> seen = x -> network.containsKey(x);
////            // TODO: don t use IF's anymore
////            if(!network.containsKey(fr.getId1())) {
////                network.put(fr.getId1(), new ArrayList<>());
////            }
////            if(!network.containsKey(fr.getId2())) {
////                network.put(fr.getId2(), new ArrayList<>());
////            }
////            network.get(fr.getId1()).add(fr.getId2());
////            network.get(fr.getId2()).add(fr.getId1());
////        });
////    }
//
////    public HashMap<Long, List<Long>> getNetwork() {
////        return network;
////    }
//
//    @Override
//    protected void read() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        List<Friendship> friendships = mapper.readValue(path.toFile(),
//                mapper.getTypeFactory().constructCollectionType(List.class, Friendship.class));
//        friendships.forEach(this::add); // not necessary
//    }
//
//    @Override
//    protected void write() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.writeValue(path.toFile(), getAll());
//    }
//
//    @Override
//    public void removeUserFriendships(Long userID) {
//        Predicate<Friendship> hasUser = fr -> fr.getId1().equals(userID) ||
//                fr.getId2().equals(userID);
//        elems.values().removeIf(hasUser);
//
//        try{
//            write();
//        } catch (IOException e) {
//            System.out.println("Could not write to file");
//        }
//    }
//
//    @Override
//    public Long getLastId() {
//        return lastID;
//    }
//
//    @Override
//    public Optional<Friendship> getFriendshipByUsers(Long id1, Long id2){
//        Iterable<Friendship> friendships = elems.values();
//        for(Friendship f : friendships){
//            if(f.getId1().equals(id1) && f.getId2().equals(id2))
//                return Optional.of(f);
//        }
//        return Optional.empty();
//    }
//
//    public void removeFriendship(Long userID, Long friendID) {
//        Optional<Friendship> temp = getFriendshipByUsers(userID, friendID);
//        temp.ifPresent(friendship -> delete(friendship.getID()));
//    }
//
//
//}
