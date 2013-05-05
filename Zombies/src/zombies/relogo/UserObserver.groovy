package zombies.relogo

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
		setDefaultShape(Human, "person")
		createHumans(numHumans){
			setxy(randomXcor(), randomYcor())
		}
		setDefaultShape(Zombie, "zombie")
		createZombies(numZombies){
			setxy(randomXcor(), randomYcor())
			size = 2
		}
	}
	
	def go(){
		ask(zombies()){
			step()
		}
		ask(humans()){
			step()
		}
		tick()
	}
	
	def remainingHumans(){
		count(humans())
	}
	
	def remainingZombies(){
		count(zombies())
	}
	
	def timestamp(){
		ticks()
	}
}