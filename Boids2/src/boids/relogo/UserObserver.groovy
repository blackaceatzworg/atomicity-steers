package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Stop;

class UserObserver extends BaseObserver{
	def m_drawing = false
	def m_flocksNumber = 0
	def m_flocksMaxSize = 0
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
		/* Boids */
		ask(boids()){
			step()
		}
		updatePenMode()
		updateFlockSizeAndNumber()
		
		/* Obstacles */
		ask(obstacles()){
			step()
		}
		tick()
	}
	
	private def updatePenMode(){
		/* Start drawing */
		if (g_drawTrails && !m_drawing){
			for (boid in boids()) boid.penDown()
			for (obstacle in obstacles()) obstacle.penDown()
			m_drawing = true
		}
		/* Stop drawing */
		else if (!g_drawTrails && m_drawing){
			for (boid in boids()) boid.penUp()
			for (obstacle in obstacles()) obstacle.penUp()
			m_drawing = false
		}
	}
	
	/* STATISTICS */
	def relogoRun(){
		return m_relogoRun
	}
	/**
	 * Compute the size of the current largest flock and the total number of distinct flocks
	 * in the simulation
	 * @param boidsList
	 * @return
	 */
	def updateFlockSizeAndNumber(){
		def boidsList = boids().clone()
		def maxSize = 0
		def numberOfFlocks = 0
		while (boidsList){
			def flock = SameFlock.findBoidsInFlock(boidsList[0])
			def s = flock.size()
			if (s > maxSize) maxSize = s // update maximum flock size
			numberOfFlocks++ // update number of flocks
			boidsList -= flock // remove boids of this flock from the list of boids to process
		}
		m_flocksNumber = numberOfFlocks
		m_flocksMaxSize = maxSize
	}
	def flocksMaxSize(){
		return m_flocksMaxSize
	}
	def flocksNumber(){
		return m_flocksNumber
	}
	def boidBoidCollisions(){
		if (!boids()) return 0
		def numberOfCollisions = boids().sum{ it.m_collisionsWithBoid }
		numberOfCollisions = ceiling(numberOfCollisions / 2d)
		return numberOfCollisions
	}
	def boidObstacleCollisions(){
		if (!boids()) return 0
		def numberOfCollisions = boids().sum{ it.m_collisionsWithObstacle }
		return numberOfCollisions
	}
	def timestamp(){
		ticks()
	}

}