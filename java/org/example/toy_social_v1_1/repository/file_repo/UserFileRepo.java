//package org.example.toy_social_v1_1.repository.file_repo;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import org.example.toy_social_v1_1.domain.entities.User;
//import org.example.toy_social_v1_1.repository.UserRepo;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.List;
//
//public class UserFileRepo extends AbstractFileRepo<Long, User> implements UserRepo {
//    public UserFileRepo(Path path) throws IOException {
//        super(path);
//    }
//
//    @Override
//    protected void read() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        List<User> users = mapper.readValue(path.toFile(),
//                mapper.getTypeFactory().constructCollectionType(List.class, User.class));
//        users.forEach(this::add);
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
//    public Long getLastId() {
//        return lastID;
//    }
//
//}
