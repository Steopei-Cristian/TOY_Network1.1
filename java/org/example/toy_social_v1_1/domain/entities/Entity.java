package org.example.toy_social_v1_1.domain.entities;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID extends Comparable<ID>> implements Serializable {
    protected ID id;

    public Entity() {}

//    public Entity(ID id) {
//        this.id = id;
//    }

    public ID getID() {
        return id;
    }

    public void setID(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        if(!(obj instanceof Entity temp))
            return false;

        return id.equals(temp.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
