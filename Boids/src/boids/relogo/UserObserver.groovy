package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserObserver extends BaseObserver{

	def relogoRun = 0
	
	
	def setup(){
		relogoRun++
		clearAll()
		placeBoids()
//		placeObstacles()
	}
	
	def go(){
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
		tick()
	}
	
	def placeBoids(){
		createBoids(g_numBoids){
			setShape("default")
			setSize(1)
			initialise()
		}
	}

}