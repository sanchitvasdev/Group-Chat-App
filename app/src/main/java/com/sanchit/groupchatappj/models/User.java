package com.sanchit.groupchatappj.models;

import org.jetbrains.annotations.NotNull;

/**
 * Model class containing details of a particular user.
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */
public final class User {
    @NotNull
    private final String name;
    @NotNull
    private final String state;
    @NotNull
    private final String city;
    @NotNull
    private final String imageUrl;
    @NotNull
    private final String thumbImage;
    @NotNull
    private final String uid;
    @NotNull
    private final String joinedDate;

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getState() {
        return this.state;
    }

    @NotNull
    public final String getCity() {
        return this.city;
    }

    @NotNull
    public final String getImageUrl() {
        return this.imageUrl;
    }

    @NotNull
    public final String getThumbImage() {
        return this.thumbImage;
    }

    @NotNull
    public final String getUid() {
        return this.uid;
    }

    @NotNull
    public final String getJoinedDate() {
        return this.joinedDate;
    }

    public User(@NotNull String name, @NotNull String state, @NotNull String city, @NotNull String imageUrl, @NotNull String thumbImage, @NotNull String uid, @NotNull String joinedDate) {
        super();
        this.name = name;
        this.state = state;
        this.city = city;
        this.imageUrl = imageUrl;
        this.thumbImage = thumbImage;
        this.uid = uid;
        this.joinedDate = joinedDate;
    }
}
