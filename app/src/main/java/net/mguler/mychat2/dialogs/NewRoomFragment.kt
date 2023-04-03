package net.mguler.mychat2.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.mguler.mychat2.R
import net.mguler.mychat2.data.Rooms
import net.mguler.mychat2.databinding.FragmentNewRoomBinding

class NewRoomFragment : DialogFragment() {
    private lateinit var binding: FragmentNewRoomBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentNewRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewRoom.setOnClickListener {
            val newRoomName = binding.textNewRoom.text.toString()
            val newRoomLevel = "1"
            val newRoom = Rooms(newRoomName, newRoomLevel)

            db.collection("rooms").document(newRoomName).set(newRoom)
                .addOnSuccessListener { dismiss() }
        }
    }

}