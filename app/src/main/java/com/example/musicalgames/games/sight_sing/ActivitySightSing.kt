package com.example.musicalgames.games.sight_sing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R

class ActivitySightSing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sight_read)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, ViolinStaffFragment())
            }
        }
    //setContentView(R.layout.activity_game_floppy)

        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //val navController = navHostFragment.navController

        //val arcade = intent.getBooleanExtra(ARCADE_EXTRA, true)
        //val viewModel = ViewModelProvider(this)[ViewModel::class.java]
        //viewModel.gameType = if(arcade) GameOption.ARCADE else GameOption.LEVELS

        /*if(arcade) {
            navController.navigate(R.id.flappyGameFragment)
        }
        else {
            navController.navigate(R.id.fragmentLevelList)
        }

         */
    }

}