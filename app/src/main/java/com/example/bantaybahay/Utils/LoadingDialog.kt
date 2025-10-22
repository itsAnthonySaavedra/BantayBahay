package com.example.bantaybahay.Utils
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.example.bantaybahay.R

class LoadingDialog(private val context: Context) {

    private var dialog: Dialog? = null

    fun show(message: String = "Loading...") {
        dismiss() // Dismiss any existing dialog

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        val loadingMessage = dialogView.findViewById<TextView>(R.id.tvLoadingMessage)
        loadingMessage.text = message

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogView)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    fun isShowing(): Boolean {
        return dialog?.isShowing ?: false
    }
}