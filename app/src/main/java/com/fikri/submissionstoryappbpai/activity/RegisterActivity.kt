package com.fikri.submissionstoryappbpai.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityRegisterBinding
import com.fikri.submissionstoryappbpai.other_class.LoadingModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.view_model.RegisterViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var viewModel: RegisterViewModel

    private val responseModal = ResponseModal()
    private val loadingModal = LoadingModal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        if (viewModel.firstAppeared) {
            viewModel.firstAppeared = false
            playAnimation()
        }

        binding.apply {
            etRegisterName.addTextChangedListener(onTextChanged = { p0, _, _, _ ->
                if (p0.toString().trim().isEmpty()) etRegisterName.error = resources.getString(
                    R.string.name_error
                )
                setLoginEnableOrDisable()
            })
            etRegisterEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                setLoginEnableOrDisable()
            })
            etRegisterPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                setLoginEnableOrDisable()
            })

            btnRegister.setOnClickListener {
                viewModel.register(
                    binding.etRegisterName.text.toString().trim(),
                    binding.etRegisterEmail.text.toString().trim(),
                    binding.etRegisterPassword.text.toString().trim()
                )
            }

            btnLogin.setOnClickListener {
                finish()
            }

            btnAppearance.setOnClickListener {
                startActivity(
                    Intent(
                        this@RegisterActivity,
                        DisplayConfigurationActivity::class.java
                    )
                )
            }
        }

        viewModel.apply {
            isShowLoading.observe(this@RegisterActivity) { isShowLoading ->
                if (isShowLoading) {
                    loadingModal.showLoadingModal(
                        this@RegisterActivity,
                        LoadingModal.TYPE_GENERAL,
                        resources.getString(R.string.registering_message)
                    )
                } else {
                    loadingModal.dismiss()
                }
            }
            isShowResponseModal.observe(this@RegisterActivity) { isShowingResponseModal ->
                if (isShowingResponseModal) {
                    responseModal.showResponseModal(
                        this@RegisterActivity,
                        responseType,
                        responseMessage
                    ) {
                        viewModel.dismissResponseModal()
                        if (responseType == ResponseModal.TYPE_SUCCESS) {
                            val intent = Intent()
                            intent.putExtra(
                                LoginActivity.REGISTER_EMAIL_RESULT,
                                binding.etRegisterEmail.text.toString().trim()
                            )
                            intent.putExtra(
                                LoginActivity.REGISTER_PASSWORD_RESULT,
                                binding.etRegisterPassword.text.toString().trim()
                            )
                            setResult(LoginActivity.REGISTER_RESULT, intent)
                            finish()
                        }
                    }
                } else {
                    responseModal.dismiss()
                }
            }
        }
    }

    private fun setLoginEnableOrDisable() {
        binding.apply {
            if (etRegisterEmail.isValid && etRegisterPassword.isValid && etRegisterName.text.toString()
                    .trim().isNotEmpty()
            ) {
                btnRegister.isEnabled = true
                btnRegister.contentDescription =
                    resources.getString(R.string.register_your_new_account)
            } else {
                btnRegister.isEnabled = false
                btnRegister.contentDescription =
                    resources.getString(R.string.register_button_not_available)
            }

        }
    }

    private fun playAnimation() {
        binding.apply {
            tvRegister.alpha = 0f
            tvRegisterDesc.alpha = 0f
            divider.alpha = 0f
            tvNameLabel.alpha = 0f
            etRegisterName.alpha = 0f
            tvEmailLabel.alpha = 0f
            etRegisterEmail.alpha = 0f
            tvPasswordLabel.alpha = 0f
            etRegisterPassword.alpha = 0f
            btnRegister.alpha = 0f
            tvAskLogin.translationY = 1000f
            btnLogin.translationY = 1000f
            btnAppearance.translationY = 1000f
        }
        val registerText =
            ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)
        val registerDescText =
            ObjectAnimator.ofFloat(binding.tvRegisterDesc, View.ALPHA, 1f).setDuration(500)
        val divider =
            ObjectAnimator.ofFloat(binding.divider, View.ALPHA, 1f).setDuration(100)

        val registerForm = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvNameLabel, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.etRegisterName, View.ALPHA, 1F)
                    .setDuration(500),
                ObjectAnimator.ofFloat(binding.tvEmailLabel, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.etRegisterEmail, View.ALPHA, 1F)
                    .setDuration(500),
                ObjectAnimator.ofFloat(binding.tvPasswordLabel, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.etRegisterPassword, View.ALPHA, 1F)
                    .setDuration(500),
                ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.tvAskLogin, View.TRANSLATION_Y, 0F)
                    .setDuration(1000),
                ObjectAnimator.ofFloat(binding.btnLogin, View.TRANSLATION_Y, 0F).setDuration(1000),
                ObjectAnimator.ofFloat(binding.btnAppearance, View.TRANSLATION_Y, 0F)
                    .setDuration(1000),
            )
        }

        AnimatorSet().apply {
            playSequentially(registerText, registerDescText, divider, registerForm)
            startDelay = 500
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        responseModal.dismiss()
        loadingModal.dismiss()
    }
}