package net.mguler.mychat2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.mguler.mychat2.data.Messages
import net.mguler.mychat2.data.Users
import net.mguler.mychat2.databinding.MessagesRecyclerRow2Binding
import net.mguler.mychat2.databinding.MessagesRecyclerRowBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessagesAdapter(var messagesList: ArrayList<Messages>) : RecyclerView.Adapter<MessagesAdapter.MessagesHolderHome>() {

    sealed class MessagesHolderHome(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        class MessagesHolder1(var binding: MessagesRecyclerRowBinding) : MessagesHolderHome(binding)
        class MessagesHolder2(var binding: MessagesRecyclerRow2Binding) : MessagesHolderHome(binding)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesHolderHome {
        return if (viewType == 1) {
            val binding =
                MessagesRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessagesHolderHome.MessagesHolder1(binding)
        } else {
            val binding =
                MessagesRecyclerRow2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessagesHolderHome.MessagesHolder2(binding)
        }
    }

    override fun onBindViewHolder(holder: MessagesHolderHome, position: Int) {

        when (holder) {
            is MessagesHolderHome.MessagesHolder1 -> {
                holder.binding.textRecylerMessage1.text = messagesList[position].messageBody


                val userId = messagesList[position].userId
                val db = Firebase.firestore
                db.collection("users").document(userId!!).get()
                    .addOnSuccessListener {
                        val user = it.toObject(Users::class.java)
                        holder.binding.textRecyclerUsername1.text = user?.username
                        holder.binding.imageRecyclerProfile1.load(user?.photo)
                    }
                    .addOnFailureListener { println(it.localizedMessage) }


                if (position > 0) {
                    if (userId == messagesList[position-1].userId) {
                        holder.binding.imageRecyclerProfile1.visibility = View.GONE
                        holder.binding.textRecyclerUsername1.visibility = View.GONE
                    }
                }

                //val messageTimestamp = post.key?.toLong()
                //val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
                val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                val messageDate = dateFormat.format(messagesList[position].messageTime)
                holder.binding.textRecylerTime1.text = messageDate
            }

            is MessagesHolderHome.MessagesHolder2 -> {
                //myHolder = holder as MessagesHolderHome

                holder.binding.textRecylerMessage2.text = messagesList[position].messageBody


                val userId = messagesList[position].userId
                val db = Firebase.firestore
                db.collection("users").document(userId!!).get()
                    .addOnSuccessListener {
                        val user = it.toObject(Users::class.java)
                        holder.binding.textRecyclerUsername2.text = user?.username
                        holder.binding.imageRecyclerProfile2.load(user?.photo)
                    }
                    .addOnFailureListener { println(it.localizedMessage) }


                if (position > 0) {
                    if (userId == messagesList[position-1].userId) {
                        holder.binding.imageRecyclerProfile2.visibility = View.GONE
                        holder.binding.textRecyclerUsername2.visibility = View.GONE
                    }
                }

                //val messageTimestamp = post.key?.toLong()
                //val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
                val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                val messageDate = dateFormat.format(messagesList[position].messageTime)
                holder.binding.textRecylerTime2.text = messageDate
            }
        }



    }
    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messagesList[position].userId == Firebase.auth.currentUser?.uid) {
            return 1
        }
       return 0
    }
}
