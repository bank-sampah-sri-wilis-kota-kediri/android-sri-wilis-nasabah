package com.bs.sriwilis.nasabah.ui.authorization.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityLoginBinding
import com.bs.sriwilis.nasabah.ui.authorization.AuthViewModel
import com.bs.sriwilis.nasabah.ui.authorization.register.RegisterActivity
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.HomepageActivity
import com.bs.sriwilis.nasabah.ui.splashscreen.WelcomeViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val welcomeViewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBelumMemilikiAkun.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        setupPasswordToggle()
        observeViewModel()
        setupAction()
    }

    private fun login(phone: String, password: String) {
        viewModel.login(phone, password)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.edtEmailPasswordForm.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtEmailPasswordForm.right - binding.edtEmailPasswordForm.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.edtEmailPasswordForm.selectionEnd
                    if (isPasswordVisible) {
                        binding.edtEmailPasswordForm.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.edtEmailPasswordForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        binding.edtEmailPasswordForm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.edtEmailPasswordForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.edtEmailPasswordForm.setSelection(selection)
                    isPasswordVisible = !isPasswordVisible
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private val phoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            //validatePhone()
            validateFields()
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            validateFields()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val phone = binding.edtEmailLoginForm.text.toString()
            val password = binding.edtEmailPasswordForm.text.toString()

            login(phone, password)
        }
    }

    private fun validateFields(){
        val password = binding.edtEmailPasswordForm.text.toString().trim()

        val isPasswordValid = !TextUtils.isEmpty(password) && password.length >= MIN_PASSWORD_LENGTH

        binding.btnLogin.isEnabled = isPasswordValid
    }


    private fun observeViewModel() {
        viewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    lifecycleScope.launch {
                        welcomeViewModel.syncData()
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Sukses")
                            setMessage("Anda Berhasil Masuk")
                            setPositiveButton("OK") { _, _ ->
                                val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }


                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Gagal!")
                        setMessage("Email atau sandi yang dimasukkan salah")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        })
    }



    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}