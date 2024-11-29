//package org.example.toy_social_v1_1.repository.file_repo;
//
//import org.example.toy_social_v1_1.domain.entities.Entity;
//import org.example.toy_social_v1_1.repository.memory_repo.InMemoryRepo;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.Optional;
//
//public abstract class AbstractFileRepo<ID extends Comparable<ID>, E extends Entity<ID>> extends InMemoryRepo<ID, E> {
//    protected Path path;
//
//    public AbstractFileRepo(Path path) throws IOException {
//        this.path = path;
//        read();
//    }
//
//    protected abstract void read() throws IOException;
//    protected abstract void write() throws IOException;
//
//    @Override
//    public Optional<E> add(E entity) {
//        Optional<E> res = super.add(entity);
//        if(res.isPresent()) {
//            throw new RuntimeException("Entity already exists");
//        }
//
//        try {
//            write();
//        } catch (IOException e) {
//            throw new RuntimeException("Error writing to file");
//        }
//
//        return res;
//    }
//
//    @Override
//    public Optional<E> delete(ID id) {
//        Optional<E> res = super.delete(id);
//        if(!res.isPresent()) {
//            throw new RuntimeException("Invalid id");
//        }
//
//        try {
//            write();
//        } catch (IOException e) {
//            throw new RuntimeException("Error writing to file");
//        }
//        return res;
//    }
//
//    @Override
//    public Optional<E> update(E entity) {
//        Optional<E> res = super.update(entity);
//        if(res.isPresent()) {
//            throw new RuntimeException("Invalid id");
//        }
//
//        try {
//            write();
//        } catch (IOException e) {
//            throw new RuntimeException("Error writing to file");
//        }
//        return res;
//    }
//}
