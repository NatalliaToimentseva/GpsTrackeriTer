package com.iTergt.routgpstracker.utils

import android.util.Patterns

const val MIN_PASSWORD_LENGTH = 6
const val MAX_PASSWORD_LENGTH = 50
const val PASSWORD_PATTERN = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*"

fun passwordValidator(password: String): Boolean {
    return password.isNotBlank()
            && password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH
            && password.matches(PASSWORD_PATTERN.toRegex())
}

fun emailValidator(email: String): Boolean {
    return email.isNotBlank()
            && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun activateButton(vararg screenFields: Boolean): Boolean {
    var count = 0
    for (field in screenFields) {
        if (!field) count++
    }
    return (count == 0)
}