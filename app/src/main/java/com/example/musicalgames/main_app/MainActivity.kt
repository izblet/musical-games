package com.example.musicalgames.main_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.ActivityMainBinding
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.LandscapeGameActivity
import com.example.musicalgames.game_activity.PortraitGameActivity
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), IToolbarTitleUpdater {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //used when fragments change to update the title of the toolbar
    override fun updateToolbarTitle(title: String) {
       supportActionBar?.title = title
    }

    private fun setCollectFor(sharedFlow: MutableSharedFlow<Unit>, function: (Unit)->Unit) {
       lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED) {
               sharedFlow.collect { value -> function(value) }
           }
       }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val navController = findNavController(R.id.nav_host_fragment_content_main)

       val toLevelsFunction: (Unit)->Unit = {
           if (viewModel.game != null) {
               navController.navigate(R.id.action_FirstFragment_to_fragmentNewModeChoose)
           } else {
               throw IllegalStateException("The game is null, cannot navigate to levels")
           }
        }
        setCollectFor(viewModel.navigateToLevels, toLevelsFunction)

        val toGamePlayFunction: (Unit)->Unit = {
            if (viewModel.level != null) {
                navController.navigate(R.id.action_fragmentNewModeChoose_to_fragmentLevelOptions)
            } else {
                throw IllegalStateException("The level is null, cannot navigate to start game")
            }
        }
        setCollectFor(viewModel.navigateToGamePlay, toGamePlayFunction)

        val startGameFunction: (Unit)->Unit = {
            if (viewModel.gameplay != null) {
                val game = viewModel.game!!
                val activityClass = if (GameMap.isPortrait(game)) PortraitGameActivity::class.java else LandscapeGameActivity::class.java
                val intent = Intent(this@MainActivity, activityClass).apply {
                    putExtra(GameActivity.ARG_LEVEL, viewModel.level)
                    putExtra(GameActivity.ARG_GAMEPlAY_INFO, viewModel.gameplay)
                    putExtra(GameActivity.ARG_GAME_TYPE, game.name)
                }
                startActivity(intent)
            }
        }
        setCollectFor(viewModel.startGame, startGameFunction)


        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    //TODO: should probably add more options to the menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}