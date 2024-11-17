package com.iTergt.routgpstracker.ui.account.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.ui.account.login.domain.LoginUseCase
import com.iTergt.routgpstracker.ui.account.signup.domain.RegistrationResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sharedPreference: SharedPreferencesRepository
) : ViewModel() {

    var operationResult: ((message: String?) -> Unit)? = null

    private val disposable = CompositeDisposable()

    private val _email = MutableLiveData<String?>(null)
    val email get() = _email

    private val _password = MutableLiveData<String?>(null)
    val password get() = _password

    private var _isEmailValid = MutableLiveData(false)
    val isEmailValid: LiveData<Boolean> = _isEmailValid

    private val _isPasswordValid = MutableLiveData(false)
    val isPasswordValid: LiveData<Boolean> = _isPasswordValid

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setEmailValid(isValid: Boolean) {
        _isEmailValid.value = isValid
    }

    fun setPasswordValid(isValid: Boolean) {
        _isPasswordValid.value = isValid
    }

    fun loginUser(email: String, password: String) {
        disposable.add(
            loginUseCase.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        handleResult(result)
                    },
                    { error ->
                        error.message?.let { handleResult(RegistrationResult.Error(it)) }
                    }
                )
        )
    }

    private fun handleResult(result: RegistrationResult) {
        when (result) {
            is RegistrationResult.Error -> {
                operationResult?.invoke(result.message)
            }

            is RegistrationResult.Success -> {
                operationResult?.invoke(null)
                sharedPreference.setUserUid(result.uid)
            }
        }
    }
}