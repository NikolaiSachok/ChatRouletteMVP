package com.video.chat.v.love.chatroulette.presentation.base

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.video.chat.v.love.chatroulette.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseDialog<T : ViewBinding> : DialogFragment() {
    protected var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomAlertDialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun showNeedStorageAccessDialog() {
        showPermissionDialog("Access external storage permission")
    }

    protected fun showPermissionDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNegativeButton(R.string.not) { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton(R.string.ok) { dialog, _ ->
                openApplicationSettings()
                dialog.dismiss()
            }.show()
    }

    fun openApplicationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
}