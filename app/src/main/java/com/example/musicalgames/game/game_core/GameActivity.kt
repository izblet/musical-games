package com.example.musicalgames.game_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap.gameInfos

class GameActivity : AppCompatActivity() {
    companion object {
        const val ARG_GAME_TYPE = "game_type"
        const val ARG_LEVEL = "level"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_floppy)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val gameType = Game.valueOf(intent.getStringExtra(ARG_GAME_TYPE)!!)
        val gameFactory = gameInfos[gameType]!!.gameFactory
        val viewModelType = gameFactory.getViewModelType()

        val viewModel : GameViewModel = ViewModelProvider(this)[viewModelType] as GameViewModel
        val level = intent.getParcelableExtra<Level>(ARG_LEVEL)
        viewModel.setLevel(level!!)


        val action = StartGameFragmentDirections.actionStartGameFragmentToFlappyGameFragment(gameType.name)
        navController.navigate(action)

    }

    override fun onBackPressed() {
        finish()
    }
}
