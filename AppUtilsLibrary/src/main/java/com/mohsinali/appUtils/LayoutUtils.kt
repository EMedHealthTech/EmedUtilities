package com.mohsinali.appUtils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object LayoutUtils {
    fun setImageView(context: Context, imageView: ImageView, url: String, placeholder: Int, error: Int) {
        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)
        progressBar.visibility = View.VISIBLE
        Glide.with(context.applicationContext)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .into(imageView)

    }

    fun setImageViewRounded(context: Context, imageView: ImageView, url: String, placeholder: Int, error: Int) {
        Glide.with(context.applicationContext)
                .load(url)
                .error(error)
                .placeholder(placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)

    }


}