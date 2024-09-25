package com.bs.sriwilis.nasabah.ui.setting.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityChangeProfileBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.setting.SettingViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import android.util.Base64
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bs.sriwilis.nasabah.data.model.LoggedAccount
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class ChangeProfileActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityChangeProfileBinding
    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            this.onBackPressed()
        }

        observeViewModel()

        binding.apply {
            icProfileCircle.setOnClickListener{startGallery()}
            icEditPencil.setOnClickListener{startGallery()}
        }

        lifecycleScope.launch {
            viewModel.getLoggedInAccount()
        }

        binding.btnChangeProfile.setOnClickListener {
            handleChangeProfile()
        }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        return try {
            val tempFile = File(this.cacheDir, "temp_image.jpg")
            val fileOutputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun observeViewModel() {
            viewModel.loggedInAccount.observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val loggedAccount = result.data

                        // Populate fields with the current account data
                        binding.edtName.editText?.text = Editable.Factory.getInstance().newEditable(loggedAccount?.nama_nasabah)
                        binding.edtMobileNumber.editText?.text = Editable.Factory.getInstance().newEditable(loggedAccount?.no_hp_nasabah)
                        binding.edtAlamat.editText?.text = Editable.Factory.getInstance().newEditable(loggedAccount?.alamat_nasabah)

                        // Handle profile picture
                        if (loggedAccount != null) {
                            if (!loggedAccount.gambar_nasabah.isNullOrEmpty()) {
                                val bitmap = decodeBase64ToBitmap(loggedAccount.gambar_nasabah)
                                if (bitmap != null) {
                                    val tempFile = saveBitmapToFile(bitmap)
                                    if (tempFile != null) {
                                        Glide.with(this).clear(binding.icProfileCircle)
                                        Glide.with(this)
                                            .load(tempFile)
                                            .signature(ObjectKey(System.currentTimeMillis()))
                                            .into(binding.icProfileCircle)
                                    } else {
                                        binding.icProfileCircle.setImageResource(R.drawable.ic_profile)
                                    }
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Gagal memuat data: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }


        // Observe untuk hasil perubahan profil
        viewModel.changeProfileResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Gagal memperbarui profil: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleChangeProfile() {
        viewModel.loggedInAccount.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val currentAccount = result.data // Get the current logged account data

                    val updatedName = binding.edtName.editText?.text.toString()
                    val updatedPhone = binding.edtMobileNumber.editText?.text.toString()
                    val updatedAddress = binding.edtAlamat.editText?.text.toString()

                    // Determine what to send
                    val nameToSend = if (updatedName != currentAccount?.nama_nasabah) updatedName else currentAccount.nama_nasabah
                    val phoneToSend = if (updatedPhone != currentAccount?.no_hp_nasabah) updatedPhone else currentAccount.no_hp_nasabah
                    val addressToSend = if (updatedAddress != currentAccount?.alamat_nasabah) updatedAddress else currentAccount.alamat_nasabah

                    val phonePath = currentAccount?.no_hp_nasabah

                    var imageToSend = currentAccount?.gambar_nasabah ?: ""
                    Log.d("old image", imageToSend)
                    if (currentImageUri != null) {
                        imageToSend = uriToBase64(currentImageUri!!)
                    }

                    // Validate input fields
                    if (phonePath?.isNotEmpty() == true && nameToSend.isNotEmpty() && phoneToSend.isNotEmpty() && addressToSend.isNotEmpty()) {
                        viewModel.changeProfile(
                            phonePath = phonePath,
                            name = nameToSend,
                            phone = phoneToSend,
                            address = addressToSend,
                            gambar = imageToSend
                        )
                    } else {
                        Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(this, "Gagal memperbarui profil: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Optionally handle loading state
                }
            }
        }
    }



    //    Upload Image
    private val uCropFunction = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val uriInput = input[0]
            val uriOutput = input[1]

            val uCrop = UCrop.of(uriInput, uriOutput)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(800, 800)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return intent?.let { UCrop.getOutput(it) } ?: Uri.EMPTY
        }

    }

    private val cropImage = registerForActivityResult(uCropFunction) { uri ->
        if (uri != Uri.EMPTY) {
            currentImageUri = uri
            binding.icProfileCircle.setImageURI(uri)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            val uriOutput = File(filesDir, "croppedImage.jpg").toUri()
            val listUri = listOf(uri, uriOutput)
            cropImage.launch(listUri)
        }
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    //    End of Function Upload Image
}
