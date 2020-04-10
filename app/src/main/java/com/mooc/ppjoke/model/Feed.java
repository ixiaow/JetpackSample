package com.mooc.ppjoke.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.Objects;

public class Feed extends BaseObservable implements Serializable {
    public static final int IMAGE_TYPE = 1;
    public static final int VIDEO_TYPE = 2;

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
        Feed feed = (Feed) o;
        return id == feed.id &&
                itemId == feed.itemId &&
                itemType == feed.itemType &&
                createTime == feed.createTime &&
                duration == feed.duration &&
                authorId == feed.authorId &&
                width == feed.width &&
                height == feed.height &&
                Objects.equals(feeds_text, feed.feeds_text) &&
                Objects.equals(activityIcon, feed.activityIcon) &&
                Objects.equals(activityText, feed.activityText) &&
                Objects.equals(url, feed.url) &&
                Objects.equals(cover, feed.cover) &&
                Objects.equals(author, feed.author) &&
                Objects.equals(topComment, feed.topComment) &&
                Objects.equals(ugc, feed.ugc);
    }
}
