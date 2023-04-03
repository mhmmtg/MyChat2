package net.mguler.mychat2.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.mguler.mychat2.ChatActivity
import net.mguler.mychat2.data.Rooms
import net.mguler.mychat2.databinding.RoomsRecyclerRowBinding

// TODO: adapter out for loop

class RoomsAdapter(var roomsList: ArrayList<Rooms>) : RecyclerView.Adapter<RoomsAdapter.RoomsHolder>() {
    private val colors = arrayOf("#ff7a35", "#f8413d", "#d6247a", "#a12ba9", "#683fb0", "#226add")

    class RoomsHolder(var binding: RoomsRecyclerRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomsHolder {
        val binding = RoomsRecyclerRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomsHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomsHolder, position: Int) {
        holder.binding.textRoomName.text = roomsList[position].roomName
        val level = StringBuilder("Level ")
        holder.binding.textRoomLevel.text = level.append(roomsList[position].roomLevel)
        val myColor = Color.parseColor(colors[position % 6])
        val drawable = holder.binding.clRoomsRow.background as GradientDrawable
        drawable.setColor(myColor)

        if (roomsList[position].roomMessages.equals("0")) {
            holder.binding.textMessageCount.text = "No new messages!"
        }
        else {
            val newMessages = StringBuilder(roomsList[position].roomMessages!!)
            holder.binding.textMessageCount.text = newMessages.append(" new message(s)")
        }


        holder.itemView.setOnClickListener {
            val roomIntent = Intent(holder.itemView.context, ChatActivity::class.java)
            roomIntent.putExtra("roomId", roomsList[position].roomName)
            holder.itemView.context.startActivity(roomIntent)
        }
    }

    override fun getItemCount(): Int {
        return roomsList.size
    }
}