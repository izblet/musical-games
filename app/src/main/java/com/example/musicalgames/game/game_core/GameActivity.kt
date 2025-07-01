package com.example.musicalgames.game_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GameFactory
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


        val gameFactory = gameInfos[gameType]!!.gameFactoryProvider()

        val level = intent.getParcelableExtra<Level>(ARG_LEVEL)
            ?: throw IllegalStateException("Level is null in GameActivity::onCreate")

        gameFactory.makeViewModel(level, this)

        val action = StartGameFragmentDirections.actionStartGameFragmentToFlappyGameFragment(gameType.name)
        navController.navigate(action)

    }

    override fun onBackPressed() {
        finish()
    }
}
