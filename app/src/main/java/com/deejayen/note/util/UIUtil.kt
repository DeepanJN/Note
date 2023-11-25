package com.deejayen.note.util

import android.content.res.Resources

class UIUtil {

    companion object {

        fun Resources.dpToPx(dp: Int): Int {
            return (dp * this.displayMetrics.density).toInt()
        }

    }
}