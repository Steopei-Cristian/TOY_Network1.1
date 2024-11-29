package org.example.toy_social_v1_1.domain.entities;

import java.sql.Date;

public class FriendRequest extends Entity<Long> {
    private Long user1;
    private Long user2;
    private Date sent;
    private Boolean accepted;

    public FriendRequest(Long user1, Long user2,
                         Date sent, Boolean accepted) {
        this.user1 = user1;
        this.user2 = user2;
        this.sent = sent;
        this.accepted = accepted;
    }

    public Long getUser1() {
        return user1;
    }
    public Long getUser2() {
        return user2;
    }
    public Date getSent() {
        return sent;
    }
    public Boolean getAccepted() {
        return accepted;
    }
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "FriendshipRequest [id = " + id + " user1=" + user1 + ", user2=" + user2 + ", sent=" + sent + ", accepted=" + accepted + "]";
    }
}
