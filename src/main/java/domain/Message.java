package domain;

import java.io.Serializable;
import java.security.cert.CertPathBuilder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Message implements Serializable, Comparable <Message> {
    private Long id;
    private String text;
    private String userNF, userNT;
    private Calendar moment;

    @Override
    public String toString() {
        return new StringBuilder("<p><b>")
                .append(userNF)
                .append((userNT.length() != 0) ? ("-> " + userNT) : "")
                .append(":</b><br /><message>")
                .append(text)
                .append("</message><br /> <div style='text-align:right;font-size:small;'>")
                .append((new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy"))
                        .format(moment.getTime().getTime()))
                .append("</div><br /></p>")
                .toString();
    }

    private Message() {}
    private Message(Builder builder) {
        setId(builder.id);
        setMoment(builder.moment);
        setText(builder.text);
        setUserNF(builder.userNameFrom);
        setUserNT(builder.userNameTo);
    }

    public static Builder newMessage() {
        return new Builder();
    }

    public static class Builder { //создает обьект message
        private Long id;
        private String text;
        private String userNameFrom, userNameTo;
        private Calendar moment;
        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder text(String text) {
            this.text = text;
            return this;
        }
        public Builder moment(Calendar moment) {
            this.moment = moment;
            return this;
        }
        public Builder from(String  userNameFrom) {
            this.userNameFrom = userNameFrom;
            return this;
        }
        public Builder to(String userNameTo) {
            this.userNameTo = userNameTo;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

    
    public void setId(Long id) {
        this.id = id;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setUserNF(String userNF) {
        this.userNF = userNF;
    }
    public void setUserNT(String userNT) {
        this.userNT = userNT;
    }
    public void setMoment(Calendar moment) {
        this.moment = moment;
    }
    public Long getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public String getUserNF() {
        return userNF;
    }
    public String getUserNT() {
        return userNT;
    }
    public Calendar getMoment() {
        return moment;
    }
    public int compareTo(Message o) {
        if (getMoment().equals(o.getMoment()))
            return getId().compareTo(o.getId());
        else
            return getMoment().compareTo(o.getMoment());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return getId().equals(message.getId()) &&
                getText().equals(message.getText()) &&
                getUserNF().equals(message.getUserNF()) &&
                getUserNT().equals(message.getUserNT()) &&
                getMoment().equals(message.getMoment());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getUserNF(), getUserNT(), getMoment());
    }
}
