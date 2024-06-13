package com.example.musicalgames.games.flappy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.GameOption

class ActivityFlappy : AppCompatActivity() {
    companion object {
        const val ARCADE_EXTRA = "isArcade"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val arcade = intent.getBooleanExtra(ARCADE_EXTRA, true)
        val viewModel = ViewModelProvider(this)[ViewModel::class.java]
        viewModel.gameType = if(arcade) GameOption.ARCADE else GameOption.LEVELS

        if(arcade) {
            navController.navigate(R.id.flappyGameFragment)
        }
        else {
            navController.navigate(R.id.fragmentLevelList)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
