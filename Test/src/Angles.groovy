import java.lang.System;
import java.lang.Math.*;

class Angles {
	
	public static void main(String[] args){
		def currentHeading, targetHeading, maxDeltaHeading, newHeading
		def targetDirection
		Scanner sc = new Scanner(System.in);
		
		while (currentHeading != -1){
			print "Current heading: "
			currentHeading = sc.nextDouble() as double
			print "Target heading: "
			targetHeading = sc.nextDouble() as double
			print "Max delta heading: "
			maxDeltaHeading = sc.nextDouble() as double
			
			newHeading = headingWithMaxDelta(currentHeading, targetHeading, maxDeltaHeading)
			println "New heading will be ${newHeading}.\n"
		}
		
		
	}
	
	

	static def headingWithMaxDelta(Double currentHeading, Double targetHeading, Double maxDeltaHeading){
		def narrowestDelta = { 
			if (it > 180) { it -= 360 }
			else if (it < -180) { it += 360 }
			return it
		}
		def positiveAngle = { 
			if (it < 0) { it += 360 }
			else if (it > 360) { it -= 360}
			return it 
		}
		
		currentHeading = positiveAngle(currentHeading)
		targetHeading = positiveAngle(targetHeading)
		def deltaHeading = narrowestDelta(targetHeading - currentHeading)
		
		if (Math.abs(deltaHeading) > maxDeltaHeading){
			deltaHeading = maxDeltaHeading * ((deltaHeading <0 )? -1:1)
		}
		
		def newHeading = positiveAngle(currentHeading + deltaHeading)
		return newHeading
	}

}
