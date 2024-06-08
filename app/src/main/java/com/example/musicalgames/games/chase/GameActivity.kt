package com.example.musicalgames.games.chase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R
import com.example.musicalgames.games.chase.connection.MultiplayerViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var viewModel: MultiplayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MultiplayerViewModel::class.java)

        setContentView(R.layout.multiplayer_activity)
        // Initialize the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Navigate to the ModeChooseFragment as the initial destination
        navController.navigate(R.id.modeChooseFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.bluetoothManager?.releaseResources()
    }

}