package com.example.musicalgames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.viewmodels.MultiplayerViewModel
import com.example.musicalgames.wrappers.BluetoothClientManager
import com.example.musicalgames.wrappers.BluetoothServerManager

class MultiplayerActivity : AppCompatActivity() {
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

}