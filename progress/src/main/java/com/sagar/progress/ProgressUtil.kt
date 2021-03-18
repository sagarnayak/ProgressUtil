@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.sagar.progress

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.sagar.progress.databinding.ProgressLayoutBinding
import com.sagar.utils.UiUtil
import java.util.*

class ProgressUtil(private val context: Context) {

    companion object {
        const val UPLOAD_PROGRESS = "uploadProgress"
    }

    private lateinit var dialog: Dialog
    private var count = 0

    fun isAnyThingInProgress(): Boolean {
        return count != 0
    }

    fun show(numberOfLoader: Int) {
        if (count != 0) {
            count += numberOfLoader
            return
        }

        dialog = Dialog(context, R.style.progressBarTheme)

        val dialogBinding = ProgressLayoutBinding.inflate(LayoutInflater.from(context))

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.show()

        UiUtil.hideSoftKeyboard(context)

        count += numberOfLoader
    }

    fun hide() {
        count--
        if (count > 0) {
            return
        }

        hideForced()
    }

    fun hideForced() {
        @Suppress("SENSELESS_COMPARISON")
        if (this::dialog.isInitialized && dialog.isShowing)
            dialog.dismiss()

        count = 0
    }

    private lateinit var broadcastAction: String

    fun showProgress(broadcastAction: String = ""): String {
        if (broadcastAction == "") {
            this.broadcastAction = UUID.randomUUID().toString()
        } else {
            this.broadcastAction = broadcastAction
        }

        registerForBroadcast()
        return this.broadcastAction
    }

    private fun registerForBroadcast() {
        context.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        setProgress(
                            it.getIntExtra(
                                UPLOAD_PROGRESS,
                                0
                            )
                        )
                    }
                }
            },
            IntentFilter(
                this.broadcastAction
            )
        )
    }

    fun setProgress(progress: Int) {
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        if (progress == 0 || progress == 100) {
            progressBar.isIndeterminate = true
        } else {
            progressBar.isIndeterminate = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                progressBar.setProgress(progress, true)
            else
                progressBar.progress = progress
        }
    }
}