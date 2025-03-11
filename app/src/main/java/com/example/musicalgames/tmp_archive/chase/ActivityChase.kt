package com.example.musicalgames.games.chase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.R

class ActivityChase : AppCompatActivity() {
    private lateinit var viewModel: ViewModel
    companion object {
        const val SERVER_EXTRA = "create"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        setContentView(R.layout.multiplayer_activity)

        //so that we can find the first fragment (join or create)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val create = intent.getBooleanExtra(SERVER_EXTRA, true)
        if(create)
            navController.navigate(R.id.gameCreateFragment)
        else
            navController.navigate(R.id.gameJoinFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        //we want to release the bluetooth exactly when the activity dies
        // in the future there will be a chance for a rematch
        viewModel.bluetoothManager?.releaseResources()
    }

}