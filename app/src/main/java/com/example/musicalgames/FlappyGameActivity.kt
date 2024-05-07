package com.example.musicalgames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FlappyGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FlappyGameFragment())
                .commit()
        }
    }
}
