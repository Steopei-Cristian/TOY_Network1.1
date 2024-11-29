package org.example.toy_social_v1_1.dto;

import org.example.toy_social_v1_1.domain.entities.Message;
import org.example.toy_social_v1_1.domain.entities.User;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class MessageDTO {
    private Long id;
    private User from;
    private String text;
    private Timestamp time;
    private Message reply;

    public MessageDTO(Long id,
                      User from, String text,
                      Timestamp time, Message reply) {
        this.id = id;
        this.from = from;
        this.text = text;
        this.time = time;
        this.reply = reply;
    }

    public MessageDTO(Message message) {
        this.id = message.getID();
        this.from = message.getFrom();
        this.text = message.getText();
        this.time = message.getTime();
        this.reply = message.getReply();
    }

    public Long getId() {
        return id;
    }

    public User getFrom() {
        return from;
    }

    public String getText() {
        return text;
    }

    public Message getReply() {
        return reply;
    }

    @Override
    public String toString() {
        return text;
    }
}
