package com.iTergt.routgpstracker.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.snackbarWithListener(message: String, actionName: String, listener: ()-> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).setAction(actionName) { listener.invoke() }.show()
}