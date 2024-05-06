package com.example.musicalgames

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.musicalgames.wrappers.BluetoothClientManager
import com.example.musicalgames.wrappers.BluetoothConnectionManager
import com.example.musicalgames.wrappers.BluetoothServerManager

interface ServerSettable { fun setConnectionManager(manager: BluetoothServerManager) }
interface ClientSettable { fun setConnectionManager(manager: BluetoothClientManager)}
interface ServerFragmentListener { fun onCreated(fragment: ServerSettable) }
interface ClientFragmentListener { fun onCreated(fragment: ClientSettable)}
interface GameTypeListener { fun modeChosen(isServer: Boolean)}

class MultiplayerActivity : AppCompatActivity(), ServerFragmentListener, ClientFragmentListener, GameTypeListener {
    private var connectionManager: BluetoothConnectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiplayer_activity)
        // Initialize the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Navigate to the ModeChooseFragment as the initial destination
        navController.navigate(R.id.modeChooseFragment)
    }

    override fun onCreated(fragment: ServerSettable) {
        //TODO: Exception if cannot be cast probably
        if(connectionManager!=null)//this can happen (rotating the screen for example)
            fragment.setConnectionManager(connectionManager!! as BluetoothServerManager)
    }

    override fun onCreated(fragment: ClientSettable) {
        //TODO: Exception if cannot be cast probably
        if(connectionManager!=null)
            fragment.setConnectionManager(connectionManager!! as BluetoothClientManager)
    }

    override fun modeChosen(isServer: Boolean) {
        connectionManager =
            if(isServer)
                BluetoothServerManager(this, activityResultRegistry)
            else
                BluetoothClientManager(this, activityResultRegistry)

    }
}