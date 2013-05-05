package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseLink;
import repast.simphony.relogo.Directed;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Undirected;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

@Undirected
class SameFlock extends BaseLink {
	
	/**
	 * Remove all links of type "SameFlock" for a boid
	 * @return
	 */
	static def removeSameFlockLinks(Boid b){
		for (l in b.mySameFlocks())
			l.die()
	}
	
	/**
	 * 
	 * @param b
	 * @return
	 */
	static def computeFlockSize(Boid b){
		def flock = findBoidsInFlock(b)
		return flock.size()
	}
	
	/**
	 * 
	 * @param b
	 * @param alreadyInFlock
	 * @return alreadyInFlock
	 */
	static def findBoidsInFlock(Boid b, alreadyInFlock=[]){
		alreadyInFlock.add(b)
		for (neighbor in b.sameFlockNeighbors()) // undiscovered neighbors
			if (! alreadyInFlock.contains(neighbor))
				alreadyInFlock = findBoidsInFlock(neighbor, alreadyInFlock)
		return alreadyInFlock
	}
}
