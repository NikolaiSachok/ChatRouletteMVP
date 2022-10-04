package com.video.chat.v.love.chatroulette.presentation.makeselfie

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.databinding.PhotoPreviewDialogBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseDialog
import com.video.chat.v.love.chatroulette.presentation.interfaces.Next
import com.video.chat.v.love.chatroulette.constants.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoPreviewDialog(var nexted: Next) : BaseDialog<PhotoPreviewDialogBinding>(){
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var next : Next

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomAlertStrokeDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PhotoPreviewDialogBinding.inflate(inflater)
        next = nexted
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLocale()

    }

    private fun initLocale() {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        decode(getCurrentLang())

        binding.update.setOnClickListener {
            dismiss()
            next.onBackSkipClicked()

        }

        binding.next.setOnClickListener {
            next.onNextClicked()
        }
    }

    private fun getCurrentLang() = sharedPreferences.getString(Constants.CURRENT_LANGUAGE,
        "")


    fun decode(imageString: String?) {

        // Decode base64 string to image
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val rotatedBitmap = decodedImage.rotate(-90f)
        val cx = rotatedBitmap.width / 2f
        val cy = rotatedBitmap.height / 2f
        val flippedBitmap = rotatedBitmap.flip(-1f, 1f, cx, cy)
        binding.result.setImageBitmap(flippedBitmap)


    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.flip(x: Float, y: Float, cx: Float, cy: Float): Bitmap {
        val matrix = Matrix().apply { postScale(x, y, cx, cy) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    companion object {

        fun newInstance(next: Next): PhotoPreviewDialog {
            return PhotoPreviewDialog(next)
        }

        fun show(fragmentManager: FragmentManager, next: Next) {
            newInstance(next).apply {
                show(fragmentManager, tag)
            }
        }
    }
}