package net.mguler.mychat2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.mguler.mychat2.databinding.ActivityMainBinding
import net.mguler.mychat2.dialogs.ActivationFragment
import net.mguler.mychat2.dialogs.LoadingFragment
import net.mguler.mychat2.dialogs.LoginHelpFragment

// TODO: progress bar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var goToRooms: Intent
    private lateinit var loading: LoadingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToRooms = Intent(this, RoomsActivity::class.java)
        loading = LoadingFragment()
        loading.isCancelable = false

        //Firebase
        auth = Firebase.auth

        //Click events
        binding.imageHelp.setOnClickListener { LoginHelpFragment().show(supportFragmentManager, "showLoginHelp") }
        binding.btnRegister.setOnClickListener(::registerUser)
        binding.btnLogin.setOnClickListener(::loginUser)
        binding.btnResetPassword.setOnClickListener(::resetPassword)
    }

    private fun resetPassword(view: View?) {
        val email = binding.editEmail.text.toString()
        if (email.isNotEmpty()) {
            loading.show(supportFragmentManager, "loading")

            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                loading.dismiss()
                val message = "Password recovery mail has been sent!"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
                .addOnFailureListener {
                    loading.dismiss()
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loginUser(view: View?) {
        //Get fields
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        //Login
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loading.show(supportFragmentManager, "loading")

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    loading.dismiss()
                    if (auth.currentUser?.isEmailVerified == true) {
                        startActivity(goToRooms)
                        finish()
                    }
                    else {
                        ActivationFragment().show(supportFragmentManager, "not_activated")
                    }
                }
                .addOnFailureListener {
                    loading.dismiss()
                    val message = it.localizedMessage
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }

        }
    }

    private fun registerUser(view: View?) {
        loading.show(supportFragmentManager, "loading")

        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    loading.dismiss()
                    sendVerification()
                    auth.signOut()
                }
                .addOnFailureListener {
                    loading.dismiss()
                    val message = it.localizedMessage
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }

        }
    }

    private fun sendVerification() {
        auth.currentUser!!.sendEmailVerification().addOnSuccessListener {
            val message = "Registered successfully! Please check your mail to verify your account!"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            startActivity(goToRooms)
            finish()
        }
    }

}