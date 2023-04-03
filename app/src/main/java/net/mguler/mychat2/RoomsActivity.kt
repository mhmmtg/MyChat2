package net.mguler.mychat2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.mguler.mychat2.adapter.RoomsAdapter
import net.mguler.mychat2.data.Rooms
import net.mguler.mychat2.databinding.ActivityRoomsBinding
import net.mguler.mychat2.dialogs.NewRoomFragment

class RoomsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Chat Rooms"

        //binding
        binding = ActivityRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        //Click events
        binding.fab.setOnClickListener {
            NewRoomFragment().show(supportFragmentManager, "new_room")
        }

    }

    private fun getRoomNames() {
        lifecycleScope.launch {
            val roomList = ArrayList<Rooms>()

            val rooms = db.collection("rooms").get().await()

            rooms?.forEach { room ->

                val outTimeRef = room.reference.collection("users")
                    .document(auth.currentUser!!.uid).get().await()
                val logoutTime = outTimeRef.get("logout") as Long? ?: 0

                val unreadCount = room.reference.collection("messages")
                    .whereGreaterThan("messageTime", logoutTime).count()
                    .get(AggregateSource.SERVER).await().count

                val room1 = Rooms(room.id, "1", unreadCount.toString())
                roomList.add(room1)
            }

            binding.roomsRecycler.layoutManager = GridLayoutManager(this@RoomsActivity, 2)
            binding.roomsRecycler.adapter = RoomsAdapter(roomList)

        }
    }

    override fun onResume() {
        super.onResume()

        getRoomNames()

        //sign-in check
        if (auth.currentUser == null) {
            val goToMain = Intent(this, MainActivity::class.java)
            startActivity(goToMain)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val goToSettings = Intent(this, ProfileActivity::class.java)
                startActivity(goToSettings)
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                val goToMain = Intent(this, MainActivity::class.java)
                startActivity(goToMain)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}