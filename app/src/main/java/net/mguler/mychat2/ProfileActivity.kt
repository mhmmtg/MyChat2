package net.mguler.mychat2

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.mguler.mychat2.data.Users
import net.mguler.mychat2.databinding.ActivityProfileBinding
import net.mguler.mychat2.dialogs.UpdatePassFragment

// TODO: progress bar
// TODO: single update
// TODO: resim değişmeden başka bilgiler değişiyor mu?
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var store: FirebaseStorage
    private var cUser: FirebaseUser? = null

    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLaunchers()

        //Firebase
        db = Firebase.firestore
        auth = Firebase.auth
        store = Firebase.storage
        cUser = auth.currentUser

        //Click events
        binding.btnUpdate.setOnClickListener { updateUserInfo() }
        binding.btnImgEdit.setOnClickListener(::pickPhoto)
        binding.btnUpdatePassFrag.setOnClickListener {
            UpdatePassFragment().show(supportFragmentManager, "update_pass")
        }

        if (cUser != null) { getUserInfo() }
    }


    private fun updateUserInfo() {
        var photoUrl: String

        val username = binding.editUserName.text.toString()
        val gender = binding.editGender.text.toString()
        val city = binding.editCity.text.toString()
        val age = binding.editAge.text.toString()

        val photoRef = store.reference.child("users/${cUser!!.uid}")


        selectedImage?.let { uri->
            lifecycleScope.launch {
                try {
                    photoUrl = photoRef.putFile(uri).await().storage.downloadUrl.toString()

                    val user = Users(username, gender, city, age, photoUrl)
                    db.collection("users").document(cUser!!.uid).set(user).await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Info updated!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) { println(e.localizedMessage) }
            }

        }

    }

    private fun getUserInfo() {
        db.collection("users").document(cUser!!.uid).get()
            .addOnSuccessListener {
                val user = it.toObject(Users::class.java)
                if (user != null) {
                    binding.textUserLevel.text = "Level ${user.level}"
                    binding.editUserName.setText(user.username)
                    binding.editGender.setText(user.gender)
                    binding.editCity.setText(user.city)
                    binding.editAge.setText(user.age)
                    binding.imageProfile.load(user.photo) {
                        listener(
                            onStart = { binding.pbProfilePhoto.visibility = View.VISIBLE },
                            onSuccess = { _, _ -> binding.pbProfilePhoto.visibility = View.INVISIBLE },
                            onError = { _, _ -> binding.pbProfilePhoto.visibility = View.GONE }
                        )
                    }
                }
            }
    }

    private fun pickPhoto(view: View) {
        val galleryPerm = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val permOK = PackageManager.PERMISSION_GRANTED

        when {
            ContextCompat.checkSelfPermission(this, galleryPerm) == permOK -> {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            shouldShowRequestPermissionRationale(galleryPerm) -> {
                Snackbar.make(view, "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give It!") { requestPermission.launch(galleryPerm) }
                    .show()
            }
            else -> {
                requestPermission.launch(galleryPerm)
            }
        }
    }

    private fun registerLaunchers() {
        pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) { binding.imageProfile.setImageURI(uri); selectedImage = uri }
        }

        requestPermission = registerForActivityResult(RequestPermission()) {
            if (it) {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            else { Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show() }
        }
    }


}