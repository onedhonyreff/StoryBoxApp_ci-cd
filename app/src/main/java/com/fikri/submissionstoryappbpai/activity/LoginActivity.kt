package com.fikri.submissionstoryappbpai.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityLoginBinding
import com.fikri.submissionstoryappbpai.other_class.LoadingModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.view_model.LoginViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    companion object {
        const val REGISTER_RESULT = 110
        const val REGISTER_EMAIL_RESULT = "register_email_result"
        const val REGISTER_PASSWORD_RESULT = "register_password_result"
    }

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    private val responseModal = ResponseModal()
    private val loadingModal = LoadingModal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(this)
            )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        if (viewModel.firstAppeared) {
            viewModel.firstAppeared = false
            playAnimation()
        }

        binding.apply {
            etLoginEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                setLoginEnableOrDisable()
            })
            etLoginPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                setLoginEnableOrDisable()
            })

            btnLogin.setOnClickListener {
                viewModel.login(
                    etLoginEmail.text.toString().trim(),
                    etLoginPassword.text.toString().trim()
                )
            }

            btnRegister.setOnClickListener {
                launcherIntentRegister.launch(
                    Intent(this@LoginActivity, RegisterActivity::class.java)
                )
            }

            btnAppearance.setOnClickListener {
                startActivity(Intent(this@LoginActivity, DisplayConfigurationActivity::class.java))
            }
        }

        viewModel.apply {
            isShowLoading.observe(this@LoginActivity) { isShowLoading ->
                if (isShowLoading) {
                    loadingModal.showLoadingModal(
                        this@LoginActivity,
                        LoadingModal.TYPE_GENERAL,
                        resources.getString(R.string.message_on_login)
                    )
                } else {
                    loadingModal.dismiss()
                }
            }
            isShowResponseModal.observe(this@LoginActivity) { isShowingResponseModal ->
                if (isShowingResponseModal) {
                    responseModal.showResponseModal(
                        this@LoginActivity,
                        responseType,
                        responseMessage
                    ) {
                        dismissResponseModal()
                    }
                } else {
                    responseModal.dismiss()
                }
            }
            isTimeToHome.observe(this@LoginActivity) {
                if (it) {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            HomeBottomNavigationActivity::class.java
                        )
                    )
                    finish()
                }
            }
        }
    }

    private fun setLoginEnableOrDisable() {
        binding.apply {
            if (etLoginEmail.isValid && etLoginPassword.isValid) {
                btnLogin.isEnabled = true
                btnLogin.contentDescription = resources.getString(R.string.login_to_account_now)
            } else {
                btnLogin.isEnabled = false
                btnLogin.contentDescription =
                    resources.getString(R.string.login_button_not_available)
            }
        }
    }

    private fun playAnimation() {
        binding.apply {
            tvLogin.alpha = 0f
            tvLoginDesc.alpha = 0f
            divider.alpha = 0f
            tvEmailLabel.alpha = 0f
            etLoginEmail.alpha = 0f
            tvPasswordLabel.alpha = 0f
            etLoginPassword.alpha = 0f
            btnLogin.alpha = 0f
            tvAskRegister.translationY = 1000f
            btnRegister.translationY = 1000f
            btnAppearance.translationY = 1000f
        }

        val loginText = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val loginDescText =
            ObjectAnimator.ofFloat(binding.tvLoginDesc, View.ALPHA, 1f).setDuration(500)
        val divider =
            ObjectAnimator.ofFloat(binding.divider, View.ALPHA, 1f).setDuration(100)

        val loginForm = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvEmailLabel, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.etLoginEmail, View.ALPHA, 1F)
                    .setDuration(500),
                ObjectAnimator.ofFloat(binding.tvPasswordLabel, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.etLoginPassword, View.ALPHA, 1F)
                    .setDuration(500),
                ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1F).setDuration(500),
                ObjectAnimator.ofFloat(binding.tvAskRegister, View.TRANSLATION_Y, 0F)
                    .setDuration(1000),
                ObjectAnimator.ofFloat(binding.btnRegister, View.TRANSLATION_Y, 0F)
                    .setDuration(1000),
                ObjectAnimator.ofFloat(binding.btnAppearance, View.TRANSLATION_Y, 0F)
                    .setDuration(1000),
            )
        }

        AnimatorSet().apply {
            playSequentially(loginText, loginDescText, divider, loginForm)
            startDelay = 500
            start()
        }
    }

    private val launcherIntentRegister = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == REGISTER_RESULT) {
            binding.apply {
                etLoginEmail.setText(result.data?.getStringExtra(REGISTER_EMAIL_RESULT))
                etLoginPassword.setText(result.data?.getStringExtra(REGISTER_PASSWORD_RESULT))
            }
            setLoginEnableOrDisable()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        responseModal.dismiss()
        loadingModal.dismiss()
    }
}