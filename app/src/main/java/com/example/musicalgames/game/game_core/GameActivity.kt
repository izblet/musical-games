package com.example.musicalgames.game_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

abstract class GameActivity : AppCompatActivity() {
    companion object {
        const val ARG_GAME_TYPE = "game_type"
        const val ARG_LEVEL = "level"
        const val ARG_GAMEPlAY_INFO = "gameplay_info"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val gameType = Game.valueOf(intent.getStringExtra(ARG_GAME_TYPE)!!)


        val gameFactory = GameMap.createFactory(gameType)

        val level = intent.getParcelableExtra<Level>(ARG_LEVEL)
            ?: throw IllegalStateException("Level is null in GameActivity::onCreate")

        val gamePlay = intent.getParcelableExtra(ARG_GAMEPlAY_INFO) ?: GamePlayInstance()


        gameFactory.prepareViewModel(level, gamePlay, this)

        val action = StartGameFragmentDirections.actionStartGameFragmentToGameFragment(gameType.name)
        navController.navigate(action)

    }

    override fun onBackPressed() {
        finish()
    }
}
