package com.example.musicalgames.game_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.play_by_ear.EarViewModel

class GameActivity : AppCompatActivity() {
    companion object {
        const val ARG_GAME_TYPE = "game_type"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val gameType = intent.getStringExtra(ARG_GAME_TYPE)

        if(gameType == Game.FLAPPY.name) {
            val viewModel = ViewModelProvider(this)[FlappyViewModel::class.java]
            viewModel.setDataFromExtra(intent)
        } else if(gameType == Game.PLAY_BY_EAR.name) {
            val viewModel = ViewModelProvider(this)[EarViewModel::class.java]
            viewModel.setDataFromIntent(intent)
        }
        else {
            throw Exception("There is no game with the specified type $gameType")
        }

        //TODO: the name should not be here
        val bundle = Bundle().apply { putString("game_type", gameType) }
        navController.navigate(R.id.flappyGameFragment, bundle)
    }

    override fun onBackPressed() {
        finish()
    }
}
