package com.example.musicalgames
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BluetoothActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        var buttonCreate = findViewById<Button>(R.id.button_make_discoverable)
        buttonCreate.setOnClickListener{
            launchCreateActivity()
        }
        var buttonJoin = findViewById<Button>(R.id.button_search_for_devices)
        buttonJoin.setOnClickListener{
            launchJoinActivity()
        }
    }
    private fun launchCreateActivity() {
        val intent = Intent(this, GameCreateActivity::class.java)
        startActivity(intent)
    }
    private fun launchJoinActivity() {
        val intent = Intent(this, GameJoinActivity::class.java)
        startActivity(intent)
    }
}
