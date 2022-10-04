package com.video.chat.v.love.chatroulette.presentation.base

import android.content.pm.PackageManager
import androidx.annotation.NavigationRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

open class BaseFragment<T : ViewBinding> : Fragment() {
    protected var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.root.requestLayout()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    protected fun navigateToDirections(
        direction: NavDirections,
    ) {
        findNavController().navigate(direction)
    }

    protected fun isDestinationValid(destinationId: Int): Boolean {
        return findNavController().currentDestination?.id == destinationId
    }

    protected fun changeGraph(@NavigationRes graphId: Int) {
        findNavController().setGraph(graphId)
    }

    protected fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}