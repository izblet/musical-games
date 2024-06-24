package com.example.musicalgames.games.flappy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.Game

class ActivityFlappy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val viewModel = ViewModelProvider(this)[ViewModel::class.java]
        viewModel.setDataFromExtra(intent)
        val gameType = Game.FLAPPY.name
        //TODO: this should really not be here
        val bundle = Bundle().apply { putString("game_type", gameType) }

        navController.navigate(R.id.flappyGameFragment, bundle)
    }

    override fun onBackPressed() {
        finish()
    }
}
