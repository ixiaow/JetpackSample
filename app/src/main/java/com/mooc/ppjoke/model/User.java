package com.mooc.ppjoke.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    /**
     * id : 1250
     * userId : 1578919786
     * name : 、蓅哖╰伊人为谁笑
     * avatar : http://qzapp.qlogo.cn/qzapp/101794421/FE41683AD4ECF91B7736CA9DB8104A5C/100
     * description : 这是一只神秘的jetpack
     * likeCount : 8
     * topCommentCount : 0
     * followCount : 2
     * followerCount : 49
     * qqOpenId : FE41683AD4ECF91B7736CA9DB8104A5C
     * expires_time : 1586695789903
     * score : 0
     * historyCount : 2342
     * commentCount : 40
     * favoriteCount : 1
     * feedCount : 0
     * hasFollow : false
     */

    public long id;
    public long userId;
    public String name;
    public String avatar;
    public String description;
    public int likeCount;
    public int topCommentCount;
    public int followCount;
    public int followerCount;
    public String qqOpenId;
    public long expires_time;
    public int score;
    public int historyCount;
    public int commentCount;
    public int favoriteCount;
    public int feedCount;
    public boolean hasFollow;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                userId == user.userId &&
                likeCount == user.likeCount &&
                topCommentCount == user.topCommentCount &&
                followCount == user.followCount &&
                followerCount == user.followerCount &&
                expires_time == user.expires_time &&
                score == user.score &&
                historyCount == user.historyCount &&
                commentCount == user.commentCount &&
                favoriteCount == user.favoriteCount &&
                feedCount == user.feedCount &&
                hasFollow == user.hasFollow &&
                Objects.equals(name, user.name) &&
                Objects.equals(avatar, user.avatar) &&
                Objects.equals(description, user.description) &&
                Objects.equals(qqOpenId, user.qqOpenId);
    }
}
