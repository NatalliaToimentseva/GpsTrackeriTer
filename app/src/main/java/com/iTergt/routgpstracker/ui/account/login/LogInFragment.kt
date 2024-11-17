package com.iTergt.routgpstracker.ui.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentLogInBinding
import com.iTergt.routgpstracker.utils.activateButton
import com.iTergt.routgpstracker.utils.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInFragment : Fragment() {

    private var binding: FragmentLogInBinding? = null
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.operationResult = { message ->
            if (message != null) {
                binding?.login?.snackbar(message)
            } else {
                binding?.login?.snackbar(resources.getString(R.string.login_success))
                findNavController().navigate(R.id.action_accountFragment_to_tabsFragment)
            }
        }

        binding?.run {
            login.setOnClickListener {
                if (it.hasFocus()) {
                    emailLoginLayout.clearFocus()
                    passwordLoginLayout.clearFocus()
                }
            }
        }

        binding?.run {
            emailLoginET.doAfterTextChanged { email ->
                if (email.toString().isNotBlank()) {
                    viewModel.setEmailValid(true)
                }
                if (viewModel.isEmailValid.value == true) {
                    viewModel.setEmail(email.toString())
                    emailLoginLayout.error = null
                } else {
                    emailLoginLayout.error = resources.getString(R.string.error_empty_field)
                }
                loginButton.isEnabled = activateButton(
                    viewModel.isEmailValid.value!!,
                    viewModel.isPasswordValid.value!!
                )
            }
        }

        binding?.run {
            passwordLoginET.doAfterTextChanged { password ->
                if (password.toString().isNotBlank()) {
                    viewModel.setPasswordValid(true)
                }
                if (viewModel.isPasswordValid.value == true) {
                    viewModel.setPassword(password.toString())
                    passwordLoginLayout.error = null
                } else {
                    passwordLoginLayout.error = resources.getString(R.string.error_empty_field)
                }
                loginButton.isEnabled = activateButton(
                    viewModel.isEmailValid.value!!,
                    viewModel.isPasswordValid.value!!
                )
            }
        }

        binding?.loginButton?.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            if (email != null && password != null) {
                viewModel.loginUser(email, password)
            }
        }
    }
}