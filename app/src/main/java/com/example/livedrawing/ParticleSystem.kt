package com.example.livedrawing

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin

class ParticleSystem {
    private var duration :Float =0f
    private var particles : ArrayList<Particle> =ArrayList()
    private val random=Random()
    var isRunning=false
    fun initParticles(numParticles:Int){
        //Create the particles
        for(i in 0 until numParticles){
            var angle : Double=random.nextInt(360).toDouble()
            angle *= (3.14/180)
            //1-- slow particles
            //val speed=random.nextFloat()/3
            //2-- speed particles
            val speed=random.nextInt(10)+1
            val direction : PointF
            direction=PointF(cos(angle).toFloat()*speed, sin(angle).toFloat()*speed)
            particles.add(Particle(direction))
        }
    }
    fun update(fps:Long){
        duration -=1f/fps
        for(p in particles){
            p.update()
        }
        if(duration<0){
            isRunning=false
        }
    }
    fun emitParticles(startPosition : PointF){
        isRunning=true
        //option 1 -- lasts for half a minute
        duration=30f
        //option 2 -- lasts for 2 seconds
        //duration =2f
        for ( p in particles){
            p.position.x=startPosition.x
            p.position.y=startPosition.y
        }
    }
    fun draw(canvas: Canvas,paint: Paint){
        for(p in particles){
            //option 1-- colored particles
            //paint.setARGB(255,random.nextInt(256),random.nextInt(256),random.nextInt(256))

            //option 2-- White particles
            paint.color =Color.argb(255,255,255,255)
            //how big are the particles

            //option 1--- big particles
            //val sizeX=25f
            //val sizeY=25f

            //option 2-- medium particles
            //val sizeX=10f
            //val sizeY=10f

            // option3-- tiny particles
            val sizeX=12f
            val sizeY=12f

            //Draw the particles
            //option 1 - square particles
            //canvas.drawRect(p.position.x,p.position.y,p.position.x+sizeX,p.position.y+sizeY,paint)
            //option 2-- circular particles
            canvas.drawCircle(p.position.x,p.position.y,sizeX,paint)

        }
    }

}