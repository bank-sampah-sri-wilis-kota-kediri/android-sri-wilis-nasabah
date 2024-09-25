package com.bs.sriwilis.nasabah.ui.authorization.register

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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityRegisterBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.HomepageActivity
import com.bs.sriwilis.nasabah.ui.authorization.AuthViewModel
import com.bs.sriwilis.nasabah.ui.authorization.login.LoginActivity
import com.bs.sriwilis.nasabah.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private var isPasswordConfirmationVisible = false
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSudahMemilikiAkun.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupPasswordConfirmationToggle()
        setupPasswordToggle()
        setupAction()
        observeViewModel()
    }

    private fun register(name: String, phone: String, address: String, password: String) {
        viewModel.register(name, address, phone, password)

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.tv_make_sure))
            return
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.edtRegisterPasswordForm.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtRegisterPasswordForm.right - binding.edtRegisterPasswordForm.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.edtRegisterPasswordForm.selectionEnd
                    if (isPasswordVisible) {
                        binding.edtRegisterPasswordForm.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.edtRegisterPasswordForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        binding.edtRegisterPasswordForm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.edtRegisterPasswordForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.edtRegisterPasswordForm.setSelection(selection)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordConfirmationToggle() {
        binding.edtRegisterPasswordConfirmationForm.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtRegisterPasswordConfirmationForm.right - binding.edtRegisterPasswordConfirmationForm.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.edtRegisterPasswordConfirmationForm.selectionEnd
                    if (isPasswordConfirmationVisible) {
                        binding.edtRegisterPasswordConfirmationForm.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.edtRegisterPasswordConfirmationForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        binding.edtRegisterPasswordConfirmationForm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.edtRegisterPasswordConfirmationForm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.edtRegisterPasswordConfirmationForm.setSelection(selection)
                    isPasswordConfirmationVisible = !isPasswordConfirmationVisible
                    return@OnTouchListener true
                }
            }
            false
        })
    }


    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edtNameLoginForm.text.toString()
            val phone = binding.edtRegisterPhoneForm.text.toString()
            val address = binding.edtRegisterAddressForm.text.toString()
            val password = binding.edtRegisterPasswordForm.text.toString()
            val confirmPassword = binding.edtRegisterPasswordConfirmationForm.text.toString()

            if (password == confirmPassword) {
                register(name, address, phone, password)
            } else {
                showToast(getString(R.string.tv_password_do_not_match))
            }
        }
    }

    private fun validateFields(){
        val password = binding.edtRegisterPasswordForm.text.toString().trim()

        val isPasswordValid = !TextUtils.isEmpty(password) && password.length >= MIN_PASSWORD_LENGTH

        binding.btnRegister.isEnabled = isPasswordValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun observeViewModel() {
        viewModel.registerResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Sukses")
                        setMessage("Registrasi Akun Nasabah Berhasil")
                        setPositiveButton("OK") { _, _ ->
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Gagal!")
                        setMessage("Pendaftaran Akun Nasabah Gagal. ${result.error}")
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