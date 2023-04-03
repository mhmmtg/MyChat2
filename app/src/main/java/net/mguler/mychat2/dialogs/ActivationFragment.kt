package net.mguler.mychat2.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.mguler.mychat2.R
import net.mguler.mychat2.databinding.FragmentActivationBinding

class ActivationFragment : DialogFragment() {
    private lateinit var binding: FragmentActivationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentActivationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButton.setOnClickListener { dismiss() }

        binding.btnResendVerification.setOnClickListener {
            auth.currentUser!!.sendEmailVerification()
                .addOnSuccessListener {
                    Toast.makeText(context, "Activation mail has been sent!", Toast.LENGTH_LONG).show()
                    dismiss()
            }
        }
    }

}