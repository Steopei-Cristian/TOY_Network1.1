package org.example.toy_social_v1_1.domain.entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String text;
    private Timestamp time;
    private Message reply;

    public Message() {}

    public Message(User from, List<User> to,
                   String text, Timestamp time,
                   Message reply) {
        super();

        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTime() {
        return time;
    }

    public Message getReply() {
        return reply;
    }

    @Override
    public String toString() {
        return "Message [from=" + from + ", to=" + to + ", text=" + text + ", time=" + time + "]";
    }
}
