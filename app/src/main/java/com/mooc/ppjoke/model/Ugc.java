package com.mooc.ppjoke.model;

import java.io.Serializable;
import java.util.Objects;

public class Ugc implements Serializable {
    /**
     * likeCount : 1347
     * shareCount : 95
     * commentCount : 614
     * hasFavorite : false
     * hasLiked : false
     * hasdiss : false
     * hasDissed : false
     */

    public int likeCount;
    public int shareCount;
    public int commentCount;
    public boolean hasFavorite;
    public boolean hasLiked;
    public boolean hasdiss;
    public boolean hasDissed;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ugc ugc = (Ugc) o;
        return likeCount == ugc.likeCount &&
                shareCount == ugc.shareCount &&
                commentCount == ugc.commentCount &&
                hasFavorite == ugc.hasFavorite &&
                hasLiked == ugc.hasLiked &&
                hasdiss == ugc.hasdiss &&
                hasDissed == ugc.hasDissed;
    }
}
