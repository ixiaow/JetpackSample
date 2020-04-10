package com.mooc.ppjoke.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.Objects;

public class Comment extends BaseObservable implements Serializable {
    public int id;
    public long itemId;
    public long commentId;
    public long userId;
    public int commentType;
    public long createTime;
    public int commentCount;
    public int likeCount;
    public String commentText;
    public String imageUrl;
    public String videoUrl;
    public int width;
    public int height;
    public boolean hasLiked;
    public User author;
    public Ugc ugc;

    @Bindable
    public Ugc getUgc() {
        if (ugc == null) {
            ugc = new Ugc();
        }
        return ugc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id &&
                itemId == comment.itemId &&
                commentId == comment.commentId &&
                userId == comment.userId &&
                commentType == comment.commentType &&
                createTime == comment.createTime &&
                commentCount == comment.commentCount &&
                likeCount == comment.likeCount &&
                width == comment.width &&
                height == comment.height &&
                hasLiked == comment.hasLiked &&
                Objects.equals(commentText, comment.commentText) &&
                Objects.equals(imageUrl, comment.imageUrl) &&
                Objects.equals(videoUrl, comment.videoUrl) &&
                Objects.equals(author, comment.author) &&
                Objects.equals(ugc, comment.ugc);
    }

}
