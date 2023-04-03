package net.mguler.mychat2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import net.mguler.mychat2.adapter.MessagesAdapter
import net.mguler.mychat2.data.Messages
import net.mguler.mychat2.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var roomId: String
    private lateinit var cUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var messageList: ArrayList<Messages>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("roomId") as String
        supportActionBar?.title = roomId

        //Adapter
        messageList = ArrayList()
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)
        binding.messagesRecycler.adapter = MessagesAdapter(messageList)

        //Firebase
        db = Firebase.firestore
        auth = Firebase.auth
        cUser = auth.currentUser!!
        storage = Firebase.storage

        binding.btnSendMessage.setOnClickListener { sendMessage() }

        getMessages()
    }

    private fun setLogoutTime() {
        val log = hashMapOf("logout" to System.currentTimeMillis())
        db.collection("rooms").document(roomId).collection("users").document(auth.currentUser!!.uid).set(log)
    }


    private fun getMessages() {
        db.collection("rooms").document(roomId).collection("messages")
            .addSnapshotListener { value, error ->
                messageList.clear()

                value?.forEach {
                    val message = it.toObject(Messages::class.java)
                    messageList.add(message)
                }

                binding.messagesRecycler.adapter?.notifyItemInserted(messageList.size)
                binding.messagesRecycler.scrollToPosition(messageList.size-1)

                println(error?.localizedMessage)
            }
    }

    private fun sendMessage() {
        val messageBody = binding.editMessageBody.text.toString()
        val messageTime = System.currentTimeMillis()

        val message = Messages(cUser.uid, messageBody, messageTime)
        db.collection("rooms").document(roomId).collection("messages")
            .document(messageTime.toString()).set(message)
            .addOnSuccessListener {
                binding.editMessageBody.setText("")
                binding.messagesRecycler.scrollToPosition(messageList.size-1)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setLogoutTime()
    }
}