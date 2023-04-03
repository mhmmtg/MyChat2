package net.mguler.mychat2.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.mguler.mychat2.databinding.FragmentUpdatePassBinding

class UpdatePassFragment : DialogFragment() {
private lateinit var binding: FragmentUpdatePassBinding
private lateinit var auth: FirebaseAuth
private var cUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        cUser = auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentUpdatePassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButton2.setOnClickListener { dismiss() }
        binding.btnUpdatePass.setOnClickListener {
            if (cUser != null) { updateSensitiveData() }
        }
    }

    private fun updateSensitiveData() {
        val email = cUser!!.email.toString()
        val pass = binding.editCurrentPass.text.toString()

        if (pass.isNotBlank()) {
            //authenticate
            val credential = EmailAuthProvider.getCredential(email, pass)
            cUser!!.reauthenticate(credential)
                .addOnSuccessListener {
                    val newEmail = binding.editNewEmail.text.toString()
                    val newPass = binding.editNewPass.text.toString()

                    //Update pass
                    if (newPass.isNotBlank()) {
                        cUser!!.updatePassword(newPass)
                            .addOnSuccessListener { makeToast("Password updated!") }
                            .addOnFailureListener { makeToast(it.localizedMessage) }
                    }

                    //Update email
                    if (newEmail.isNotBlank()) {
                        cUser!!.updateEmail(newEmail)
                            .addOnSuccessListener { makeToast("Email updated!") }
                            .addOnFailureListener { makeToast(it.localizedMessage) }
                    }
                }
                .addOnFailureListener { makeToast(it.localizedMessage) }
        }
    }

    private fun makeToast(str: String?) {
        Toast.makeText(requireContext(), str.toString(), Toast.LENGTH_LONG).show()
    }


}