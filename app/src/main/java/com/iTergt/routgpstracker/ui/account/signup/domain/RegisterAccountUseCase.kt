package com.iTergt.routgpstracker.ui.account.signup.domain

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Single

class RegisterAccountUseCase(private val firebaseAuth: FirebaseAuth) {

    fun register(email: String, password: String): Single<RegistrationResult> {
        return Single.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
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