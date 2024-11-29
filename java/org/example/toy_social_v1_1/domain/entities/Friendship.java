package org.example.toy_social_v1_1.domain.entities;

import java.sql.Date;

public class Friendship extends Entity<Long> {
    private Long id1;
    private Long id2;
    private Date since;

    public Friendship() {}

    public Friendship(Long id1, Long id2,
                      Date since) {
        this.id1 = id1;
        this.id2 = id2;
        this.since = since;
    }

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }

    public Date getSince() {
        return since;
    }


    @Override
    public String toString() {
        return "Friendship {" + "id1=" + id1 + ", id2=" + id2 + '}';
    }
}
