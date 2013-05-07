package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Obstacle extends UserTurtle {

	/**
	* Initialise attributes
	* @return
	*/
   def initialise(){
	   setLabel(getWho())
	   setxy(randomXcor(), randomYcor())
	   updateM_position()
   }
}
