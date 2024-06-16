package com.example.musicalgames.games.flappy.game_view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.wrappers.sound_recording.PitchRecogniser
import java.util.concurrent.atomic.AtomicReference

class Bird(private val pitchRecogniser: PitchRecogniser, private val minPitch: Double, private val maxPitch: Double, pitchSize: Float) {
    //should be passed in the constructor
    private var x: Float = 0.5f
    private var y: Float = 0.1f
    private val radius: Float= (pitchSize/4)
    private val downwardSpeed = 0.020f
    private val moveSpeedDiv = 20
    private var targetY = AtomicReference(0f)
    private val paint = Paint()
    init {
        paint.color = Color.RED
    }
    fun draw(canvas: Canvas, screenHeight: Float, screenWidth: Float) {
        canvas.drawCircle(x*screenWidth, y*screenHeight, radius*screenHeight, paint)
    }
    fun passing(pipe: Pipe) : Boolean {
        return x > pipe.x + Pipe.WIDTH && x < pipe.x + Pipe.WIDTH + Pipe.SPEED
    }
    fun intersects(pipe: Pipe, width:Float, height:Float):Boolean {
        // the radius is vertical radius, when displayed on the screen it has to be scaled
        val horizontalRadius = radius*(width/height)
        val birdRect = RectF(
            x - horizontalRadius,
            y - radius,
            x + horizontalRadius,
            y + radius
        )
        val topPipeRect = pipe.getTopRect()
        val bottomPipeRect = pipe.getBottomRect()

        return birdRect.intersect(topPipeRect) || birdRect.intersect(bottomPipeRect)
    }
    fun updateTarget() {
        //pitch is -1 if does not exist because of an error or low confidence level
        //otherwise it is a number between 0 and 1 corresponding to spice value of the note
        val pitch = pitchRecogniser.getPitch()

        if(pitch == -1f)
            targetY.set(y+downwardSpeed)
        else {
            val normalizedPitch = MusicUtil.normalize(pitch.toDouble(), minPitch, maxPitch)
            targetY.set(1-normalizedPitch.toFloat())
        }
    }

    fun updatePosition() {
        //the position of the bird display will be calculated (maxCoordinate*value)
        targetY.let {
            val deltaY = (it.get() - y) / moveSpeedDiv
            if(y+deltaY>0 && y+deltaY<1)
                y += deltaY
        }
    }
}