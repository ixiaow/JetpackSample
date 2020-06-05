package com.mooc.ppjoke.exts

import android.graphics.Color

/**
 * 将带有颜色的[String]转换为[Int]类型的[Color]
 */
fun String.parserColor(): Int = Color.parseColor(this)