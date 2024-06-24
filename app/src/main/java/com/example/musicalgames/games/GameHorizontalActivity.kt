package com.example.musicalgames.games

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.flappy.ViewModel

class GameHorizontalActivity : AppCompatActivity() {
    companion object {
        const val GAME_ARG_NAME ="game"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val gameName: String? = intent.getStringExtra(GAME_ARG_NAME)

        if(gameName == Game.FLAPPY.name) {
            val viewModel = ViewModelProvider(this)[ViewModel::class.java]
            viewModel.setDataFromExtra(intent)
            navController.navigate(R.id.flappyGameFragment)
        }

    }

    override fun onBackPressed() {
        finish()
    }
}