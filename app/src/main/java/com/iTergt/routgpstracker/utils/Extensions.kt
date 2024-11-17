package com.iTergt.routgpstracker.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.iTergt.routgpstracker.R

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.snackbarWithListener(message: String, actionName: String, listener: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionName) { listener.invoke() }.show()
}

fun Fragment.findTopNavController(): NavController {
    val topLevelNavController =
        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
    return topLevelNavController?.navController ?: findNavController()
}