package com.bs.sriwilis.nasabah.ui.setting.profile
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.appcompat.app.AppCompatActivity
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityChangePasswordBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.setting.SettingViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory

@Suppress("DEPRECATION")
class ChangePasswordActivity : AppCompatActivity() {
    private var isOldPasswordVisible = false
    private var isNewPasswordVisible = false
    private lateinit var binding: ActivityChangePasswordBinding

    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            this.onBackPressed()
        }

        observeViewModel()

        setupPasswordToggle()
        setupTextWatchers()

        binding.btnChangeProfile.setOnClickListener {
            handleChangePassword()
        }

        binding.btnChangeProfile.isEnabled = false
        binding.btnChangeProfile.setBackgroundColor(getColor(R.color.grey_primary))
    }

    private fun observeViewModel() {
        viewModel.changePasswordResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Builder(this).apply {
                        setTitle("Sukses")
                        setMessage("Password berhasil diubah.")
                        setPositiveButton("OK") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Gagal memperbarui password: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleChangePassword() {
        val oldPassword = binding.edtOldPassword.editText?.text.toString()
        val newPassword = binding.edtNewPassword.editText?.text.toString()

        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
            viewModel.changePassword(oldPassword, newPassword)
        } else {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTextWatchers() {
        // TextWatcher untuk oldPassword
        binding.edtOldPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkPasswords() // Cek apakah kedua password memenuhi syarat
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // TextWatcher untuk newPassword
        binding.edtNewPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkPasswords() // Cek apakah kedua password memenuhi syarat
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkPasswords() {
        val oldPassword = binding.edtOldPassword.editText?.text.toString()
        val newPassword = binding.edtNewPassword.editText?.text.toString()

        binding.btnChangeProfile.isEnabled = oldPassword.length >= 8 && newPassword.length >= 8

        if(!binding.btnChangeProfile.isEnabled){
            binding.btnChangeProfile.setBackgroundColor(getColor(R.color.grey_primary))
        }else{
            binding.btnChangeProfile.setBackgroundColor(getColor(R.color.blue_primary))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        // Toggle untuk field password lama
        binding.edtOldPassword.editText?.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtOldPassword.editText!!.right - binding.edtOldPassword.editText!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.edtOldPassword.editText!!.selectionEnd
                    if (isOldPasswordVisible) {
                        // Ubah ke mode tersembunyi
                        binding.edtOldPassword.editText!!.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.edtOldPassword.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        // Ubah ke mode terlihat
                        binding.edtOldPassword.editText!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.edtOldPassword.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.edtOldPassword.editText!!.setSelection(selection)
                    isOldPasswordVisible = !isOldPasswordVisible
                    return@OnTouchListener true
                }
            }
            false
        })

        // Toggle untuk field password baru
        binding.edtNewPassword.editText?.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtNewPassword.editText!!.right - binding.edtNewPassword.editText!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.edtNewPassword.editText!!.selectionEnd
                    if (isNewPasswordVisible) {
                        // Ubah ke mode tersembunyi
                        binding.edtNewPassword.editText!!.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.edtNewPassword.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        // Ubah ke mode terlihat
                        binding.edtNewPassword.editText!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.edtNewPassword.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.edtNewPassword.editText!!.setSelection(selection)
                    isNewPasswordVisible = !isNewPasswordVisible
                    return@OnTouchListener true
                }
            }
            false
        })
    }
}
