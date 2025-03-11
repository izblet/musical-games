package com.example.musicalgames.game_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap.gameInfos
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.mental_intervals.MentalViewModel
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
        val gameType = Game.valueOf(intent.getStringExtra(ARG_GAME_TYPE)!!)
        val gameFactory = gameInfos[gameType]!!.gameFactory
        val viewModelType = gameFactory.getViewModelType()

        val viewModel : IntentSettable = ViewModelProvider(this)[viewModelType] as IntentSettable
        viewModel.setDataFromIntent(intent)


        val action = StartGameFragmentDirections.actionStartGameFragmentToFlappyGameFragment(gameType.name)
        navController.navigate(action)

    }

    override fun onBackPressed() {
        finish()
    }
}
