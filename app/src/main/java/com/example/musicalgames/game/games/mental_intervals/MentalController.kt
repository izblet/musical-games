package com.example.musicalgames.games.mental_intervals

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.musicalgames.utils.components.palettes.interval_palette.IntervalPaletteListener
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.music_model.Interval
//oh noo, why does it hold a reference to the viewwwww
class MentalController(private val view: MentalView) : GameController, IntervalPaletteListener {

    private var viewModel: MentalViewModel? = null
    //TODO: temporary, viewmodel should be in constructor, view should have viewmodel in constructor
    override fun setViewModel(viewModel: ViewModel) {
        if(viewModel !is MentalViewModel)
            throw Exception("viewmodel is of wrong type")

        this.viewModel = viewModel
        view.setViewModel(viewModel)
        view.setIntervalListener(this)
        viewModel.registerUI(view)
    }

    override fun initGame(context: Context, listener: GameListener) { viewModel!!.registerEndListener(listener) }

    override fun startGame(owner: LifecycleOwner) { viewModel!!.startGame() }

    override fun pauseGame() { }

    override fun endGame() { viewModel?.releaseNoteInput() }

    override fun getScore(): Int { return viewModel!!.score }
    override fun getEndDescription(): String {
        return ""
    }

    override fun onClicked(interval: Interval) {
       viewModel?.select(interval)
    }
}