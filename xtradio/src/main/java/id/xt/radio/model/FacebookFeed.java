package id.xt.radio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kido1611 on 11-May-16.
 */
public class FacebookFeed {

    FacebookFrom from;

    String created_time;
    String message;
    String type;
    String id;
    String update_time;

    String name;
    String picture;
    String full_picture;
    String link;
    String caption;

    List<FacebookLikes> likes;

    String description;
    int shares;

    public FacebookFeed(){
        likes = new ArrayList<FacebookLikes>();
        created_time = null;
        message = null;
        type = null;
        id = null;
        update_time = null;
        picture = null;
        full_picture = null;
        link = null;
        caption = null;
        description = null;
        shares = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacebookFrom getFrom() {
        return from;
    }
    public String getFromName(){
        return from.name;
    }

    public void setFrom(FacebookFrom from) {
        this.from = from;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getFull_picture() {
        return full_picture;
    }

    public void setFull_picture(String full_picture) {
        this.full_picture = full_picture;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<FacebookLikes> getLikes() {
        return likes;
    }

    public void setLikes(List<FacebookLikes> likes) {
        this.likes = likes;
    }

    public FacebookLikes getLikesItem(int position){
        if(likes.size()<=position) return null;

        return likes.get(position);
    }

    public void addLikes(FacebookLikes like){
        likes.add(like);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
