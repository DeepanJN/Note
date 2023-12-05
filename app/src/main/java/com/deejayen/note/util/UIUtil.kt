package com.deejayen.note.util

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.deejayen.note.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UIUtil {

    companion object {

        fun Resources.dpToPx(dp: Int): Int {
            return (dp * this.displayMetrics.density).toInt()
        }

        fun EditText.setupAfterTextChangedListener(block: (Editable?) -> Unit) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    //
                }

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    //
                }

                override fun afterTextChanged(editable: Editable?) {
                    block.invoke(editable)
                }
            })
        }

        fun Context.showMaterialAlertDialog(
            title: Int,
            description: Int,
            positiveStringId: Int = R.string.yes,
            negativeStringId: Int = R.string.no,
            block: (Boolean) -> Unit,
        ) {
            MaterialAlertDialogBuilder(this).apply {
                setTitle(getString(title))
                setMessage(getString(description))
                setPositiveButton(getString(positiveStringId)) { dialog, _ ->
                    dialog.dismiss()
                    block.invoke(true)
                }
                setNegativeButton(getString(negativeStringId)) { dialog, _ ->
                    dialog.dismiss()
                    block.invoke(false)
                }
                show()
            }
        }

    }
}