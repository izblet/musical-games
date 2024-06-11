package com.example.musicalgames.games.flappy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R

class GameActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val arcade = intent.getBooleanExtra("isArcade", true)

        if(arcade)
            navController.navigate(R.id.flappyGameFragment)
        else
            navController.navigate(R.id.fragmentLevelList)
    }
}
