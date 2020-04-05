package com.mooc.ppjoke.model;

import java.io.Serializable;

public class Feed implements Serializable {

    /**
     * id : 484
     * itemId : 1581239433864
     * itemType : 2
     * createTime : 1581239433891
     * duration : 14
     * feeds_text : 是时候表演真正的技术了
     * authorId : 1578919786
     * activityIcon : null
     * activityText : 2020新年快乐
     * width : 960
     * height : 528
     * url : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/zhenjishu.mp4
     * cover : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/zhenjishu2.png
     * author : {"id":1250,"userId":1578919786,"name":"、蓅哖╰伊人为谁笑","avatar":"http://qzapp.qlogo.cn/qzapp/101794421/FE41683AD4ECF91B7736CA9DB8104A5C/100","description":"这是一只神秘的jetpack","likeCount":8,"topCommentCount":0,"followCount":2,"followerCount":49,"qqOpenId":"FE41683AD4ECF91B7736CA9DB8104A5C","expires_time":1586695789903,"score":0,"historyCount":2342,"commentCount":40,"favoriteCount":1,"feedCount":0,"hasFollow":false}
     * topComment : null
     * ugc : {"likeCount":1347,"shareCount":95,"commentCount":614,"hasFavorite":false,"hasLiked":false,"hasdiss":false,"hasDissed":false}
     */

    public int id;
    public long itemId;
    public int itemType;
    public long createTime;
    public int duration;
    public String feeds_text;
    public int authorId;
    public String activityIcon;
    public String activityText;
    public int width;
    public int height;
    public String url;
    public String cover;
    public User author;
    public Comment topComment;
    public Ugc ugc;

}