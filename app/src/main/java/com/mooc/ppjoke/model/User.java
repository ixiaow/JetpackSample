package com.mooc.ppjoke.model;

import java.io.Serializable;

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

    public int id;
    public int userId;
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
}
