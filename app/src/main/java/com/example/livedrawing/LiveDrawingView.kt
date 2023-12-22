package com.example.livedrawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView

class LiveDrawingView(context: Context, scaleX:Int):SurfaceView(context),Runnable {
    private val debugging=true
    private lateinit var canvas: Canvas
    private  val paint:Paint= Paint()
    private var fps:Long=0
    private val millisInSecond:Long=1000
    private val fontSize:Int =scaleX/20
    private val fontMargin:Int =scaleX/75
    private lateinit var thread: Thread
    @Volatile
    private var drawing:Boolean=false
    private var paused=true
    private var resetButton : RectF
    private var togglePauseButton : RectF
    private val particleSystems = ArrayList<ParticleSystem>()
    private var nextSystem =0
    private val maxSystems=1000
    private val particlesPerSystem=100
    init {
        //initialize the buttons
        resetButton= RectF(0f,0f,100f,100f)
        togglePauseButton=RectF(0f,150f,100f,250f)
        //Initialize the particles and their systems
        for(i in 0 until maxSystems){
            particleSystems.add(ParticleSystem())
            particleSystems[i].initParticles(particlesPerSystem)
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        //if user moved a finger
        if(motionEvent!!.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE){
            particleSystems[nextSystem].emitParticles(PointF(motionEvent.x,motionEvent.y))
            nextSystem++
            if (nextSystem==maxSystems){
                nextSystem=0
            }

        }
        if(motionEvent!!.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN){
            //if the user pressed the reset button
            if(resetButton.contains(motionEvent.x,motionEvent.y)){
                nextSystem=0
            }
            //if the user pressed the toggle pause button
            if(togglePauseButton.contains(motionEvent.x,motionEvent.y)){
                paused = !paused
            }
        }
        return true
    }
    fun pause(){
        drawing=false
        try{
            thread.join()
        }catch (e:InterruptedException){
            Log.e("Error","joining thread")
        }
    }
    fun resume(){
        drawing=true
        thread=Thread(this)
        thread.start()
    }

    private fun draw(){
        if(holder.surface.isValid){
            canvas=holder.lockCanvas()
            canvas.drawColor(Color.argb(255,0,0,0))
            paint.color=Color.argb(255,255,255,255)
            paint.textSize=fontSize.toFloat()
            //Draw the particlesSystems
            for(i in 0 until nextSystem){
                particleSystems[i].draw(canvas,paint)
            }
            canvas.drawRect(resetButton,paint)
            canvas.drawRect(togglePauseButton,paint)
            if(debugging){
                printDebuggingText()
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }
    private fun printDebuggingText(){
        val debugSize =fontSize/2
        val debugStart =150
        paint.textSize=debugSize.toFloat()
        paint.color=Color.argb(255,255,255,255)
        canvas.drawText("fps: $fps",200f,(debugStart+debugSize).toFloat(),paint)
        canvas.drawText("Systems: $nextSystem",200f,(fontMargin+debugStart+debugSize*2).toFloat(),paint)
        canvas.drawText("Particles: ${nextSystem*particlesPerSystem}",200f,(fontMargin+debugStart+debugSize*3).toFloat(),paint)

    }

    override fun run() {
            while(drawing){
                //to know the current time
                val frameStartTime=System.currentTimeMillis()
                //if app isnot paused calll the update function
                if(!paused){
                    update()
                }
                //draw the scene
                draw()
                val timeThisFrame=System.currentTimeMillis()-frameStartTime
                if(timeThisFrame>0){
                    fps=millisInSecond/timeThisFrame
                }
            }
    }

    private fun update() {
            //update the particles
        for(i in 0 until particleSystems.size){
            if(particleSystems[i].isRunning){
                particleSystems[i].update(fps)
            }
        }
    }
}