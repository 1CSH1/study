package org.csh.study.nio.chat.myself.model;

import java.util.Date;

/**
 * 消息体
 */
public class Message extends BaseModel {

    private String content;
    private Date date;
    private User user;

    public Message(String content, Date date, User user) {
        this.content = content;
        this.date = date;
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + super.getId() +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
