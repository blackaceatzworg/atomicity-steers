package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserObserver extends BaseObserver{
	def m_drawingForBoids = false

	def m_relogoRun = 0
	
	
	def setup(){
		m_relogoRun++
		clearAll()
		placeBoids()
		placeObstacles()
	}
	
	def placeBoids(){
		createBoids(g_numBoids){
			setShape("default")
			setColor(55)
			setSize(1)
			initialise()
		}
	}
	
	def placeObstacles(){
		createObstacles(g_numObstacles){
			setShape("circle")
			setColor(14)
			setSize(2)
			initialise()
		}
	}
	
	def go(){
		updateBoidPenMode()
		ask(boids()){
			step()
			
//			/* Flock information */
//			def flockSize = SameFlock.computeFlockSize(it)
//			if (flockSize > 1){
//				println "${it} in flock of size $flockSize"
//				def flock = SameFlock.findBoidsInFlock(this)
//				println "${it} in flock $flock"
//			}
		}
		ask(obstacles()){
			step()
		}
		tick()
	}
	
	private def updateBoidPenMode(){
		/* Start drawing */
		if (g_drawBoidTrails && !m_drawingForBoids){
			for (boid in boids()){
//				boid.setPenMode(PEN_DOWN)
				boid.penDown()
			}
			m_drawingForBoids = true
		}
		/* Stop drawing */
		else if (!g_drawBoidTrails && m_drawingForBoids){
			for (boid in boids()){
//				boid.setPenMode(PEN_UP)
				boid.penUp()
			}
			m_drawingForBoids = false
		}
	}

}