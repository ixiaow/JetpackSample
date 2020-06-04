package com.mooc.annotation

class Destination {

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Activity(
        val pageUrl: String,
        val isNeedLogin: Boolean = false,
        val asStarter: Boolean = false
    )

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Fragment(
        val pageUrl: String,
        val isNeedLogin: Boolean = false,
        val asStarter: Boolean = false
    )
}