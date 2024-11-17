package com.iTergt.routgpstracker.ui.account.login.domain

import com.google.firebase.auth.FirebaseAuth
import com.iTergt.routgpstracker.ui.account.signup.domain.RegistrationResult
import io.reactivex.rxjava3.core.Single

class LoginUseCase(private val firebaseAuth: FirebaseAuth) {

    fun login(email: String, password: String): Single<RegistrationResult> {
        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseAuth.uid?.let {
                            emitter.onSuccess(RegistrationResult.Success(it))
                        }
                    } else {
                        task.exception?.let { exception ->
                            emitter.onError(exception)
                        }
                    }
                }
        }
    }
}