package id.neotica.bert.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import id.neotica.bert.R

fun getProfileIcon(context: Context, isLocalUser: Boolean): Drawable {
    val drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_tag_faces_black_24dp)
            ?: throw IllegalStateException("Could not get user profile image")

    if (isLocalUser) {
        DrawableCompat.setTint(drawable.mutate(), Color.BLUE)
    } else {
        DrawableCompat.setTint(drawable.mutate(), Color.RED)
    }

    return drawable
}