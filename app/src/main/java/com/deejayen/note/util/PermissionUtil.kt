package com.deejayen.note.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkPermission

object PermissionUtil {

    fun getReadImagesPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    }

    fun hasReadImagesPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            selfCheckPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else selfCheckPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    fun selfCheckPermission(context: Context, vararg permissions: String): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        return allPermitted
    }

}