package com.iTergt.routgpstracker.ui.account.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentSignUpBinding
import com.iTergt.routgpstracker.utils.activateButton
import com.iTergt.routgpstracker.utils.emailValidator
import com.iTergt.routgpstracker.utils.passwordValidator
import com.iTergt.routgpstracker.utils.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment() {

    private val viewModel: SignupViewModel by viewModel()
    private var binding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.operationResult = { message ->
            if (message != null) {
                binding?.signup?.snackbar(message)
            } else {
                binding?.signup?.snackbar(resources.getString(R.string.registration_success))
                findNavController().navigate(R.id.action_accountFragment_to_tabsFragment)
            }
        }

        binding?.run {
            signup.setOnClickListener {
                if (it.hasFocus()) {
                    emailSignupLayout.clearFocus()
                    passwordSignupLayout.clearFocus()
                }
            }

            binding?.run {
                emailSignupET.doAfterTextChanged { email ->
                    viewModel.setEmailValid(emailValidator(email.toString().trim()))
                    if (viewModel.isEmailValid.value == true) {
                        viewModel.setEmail(email.toString())
                        emailSignupLayout.error = null
                    } else {
                        emailSignupLayout.error = resources.getString(R.string.error_message_email)
                    }
                    signupButton.isEnabled =
                        activateButton(
                            viewModel.isEmailValid.value!!,
                            viewModel.isPasswordValid.value!!,
                            viewModel.isConfPasswordValid.value!!
                        )
                }
            }

            binding?.run {
                passwordSignupET.doAfterTextChanged { password ->
                    viewModel.setPasswordValid(passwordValidator(password.toString().trim()))
                    if (viewModel.isPasswordValid.value == true) {
                        viewModel.setPassword(password.toString())
                        passwordSignupLayout.error = null
                    } else {
                        passwordSignupLayout.error =
                            resources.getString(R.string.error_message_password)
                    }
                    signupButton.isEnabled =
                        activateButton(
                            viewModel.isEmailValid.value!!,
                            viewModel.isPasswordValid.value!!,
                            viewModel.isConfPasswordValid.value!!
                        )
                }
            }

            binding?.run {
                confPasswordSignupET.doAfterTextChanged { password ->
                    viewModel.setConfPasswordValid(viewModel.password.value == password.toString())
                    if (viewModel.isConfPasswordValid.value != true) {
                        confPasswordSignupLayout.error =
                            resources.getString(R.string.error_message_conf_password)
                    } else {
                        confPasswordSignupLayout.error = null
                    }
                    signupButton.isEnabled =
                        activateButton(
                            viewModel.isEmailValid.value!!,
                            viewModel.isPasswordValid.value!!,
                            viewModel.isConfPasswordValid.value!!
                        )
                }
            }

        }
        binding?.signupButton?.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            if (email != null && password != null) {
                viewModel.registerAccount(email, password)
            }
        }
    }
}