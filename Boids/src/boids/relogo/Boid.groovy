package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;

class Boid extends UserTurtle {
	def m_lastCallTime = 0
	def m_deltaTime = 0

	/**
	 * Initialise attributes
	 * @return
	 */
	def initialise(){
		setLabel(getWho())
		setxy(randomXcor(), randomYcor())
		setHeading(random(360.0))
		updateM_direction()
		updateM_position() 
		m_velocity = new Vector(
			getDisplacementFromHeadingAndDistance(heading, 1d) as Number[])
	}

	/**
	 * Step function defining the boid's behaviour
	 * @return
	 */
	def step(){
		println "\n--${this}-------------------------------------------------"
		/* Reinitialise links */
		SameFlock.removeSameFlockLinks(this)

		/* Compute modifications to trajectory from flocking rules */
		def v1 = rule1(), v2 = rule2(), v3 = rule3(), v4 = rule4(), v5 = rule5()
//		println "v1 = $v1"
//		println "v2 = $v2"
//		println "v3 = $v3"
//		println "v4 = $v4"
//		println "v5 = $v5"
		def velocityModifier = v1 + v2 + v3 + v4 +v5
		
		/* Move */
		updateVelocity(velocityModifier)
		correctVelocity()
		updatePositionAndDirection()
		correctPosition()
		
	}
	
	/**
	 * 
	 * @param velocityModifier
	 * @return
	 */
	def updateVelocity(velocityModifier){
		
		/* Update timer */
		def timerVal = timer()
		if (! m_lastCallTime) m_lastCallTime = timerVal // 1st update
		m_deltaTime = timerVal - m_lastCallTime
		m_lastCallTime = timerVal
		
		/* Determine velocity */
		m_velocity += velocityModifier * m_deltaTime
		println "velocity  = $m_velocity"
	}
	
	/**
	 * Compute the new position and direction of the boid based on its velocity and current position
	 * @return
	 */
	def updatePositionAndDirection(){
		def newPosition = m_position + m_velocity * m_deltaTime
		def newHeading = positiveAngle(atan(m_velocity.x, m_velocity.y))
		
		/* Update direction and position */
		setHeading(newHeading)
		setM_direction(m_velocity.normalize())
		setxy(newPosition.x, newPosition.y)
		setM_position(newPosition)
	}

	/**
	 * Rule 1: Cohesion 
	 * Compute vector towards the centre of mass of the flock boids
	 * @return towardsCentreOfMass
	 */
	def rule1(){
		Vector centreOfMass = new Vector(0, 0)
		def count = 0
		for (nearBoid in inRadius(boids(), g_sensingRadius)){
			if (nearBoid != this){
				createSameFlockWith(nearBoid)
				centreOfMass += nearBoid.m_position
				count++
			}
		}
		if (! count){ return centreOfMass }
		centreOfMass /= count
		def towardsCentreOfMass = (centreOfMass - m_position) * g_r1Factor
		return towardsCentreOfMass
	}

	/**
	 * Rule 2: Separation
	 * Compute vector away from boids too close
	 * @return awayFromCloseBoids
	 */
	def rule2(){
		Vector awayFromCloseBoids = new Vector(0, 0)
		for (nearBoid in inRadius(boids(), g_r2Threshold)){
			if (nearBoid != this){
				def towardsThisBoid = nearBoid.m_position - m_position
				awayFromCloseBoids -= towardsThisBoid
			}
		}
		awayFromCloseBoids *= g_r2Factor
		return awayFromCloseBoids
	}

	/**
	 * Rule 3: Velocity matching
	 * Compute average velocity of the flock boids
	 * @return averageVelocity
	 */
	def rule3(){
		Vector averageVelocity = new Vector(0, 0)
		def count = 0
		for (nearBoid in inRadius(boids(), g_sensingRadius)){
			if (nearBoid != this){
				averageVelocity += nearBoid.m_velocity
				count++
			}
		}
		if (! count){ return averageVelocity }
		averageVelocity /= count
		averageVelocity *= g_r3Factor
		return averageVelocity
	}

	/**
	 * Rule 4: Tendency towards a particular place (or direction)
	 * @return towardsGoal
	 */
	def rule4(){
		/* This should be modified depending on strategy observed */
		Vector goal = m_position + m_velocity.normalize() 
		
		Vector towardsGoal = (goal - m_position) * g_r4Factor
		return towardsGoal
	}

	/**
	 * Rule 5: Away from borders
	 * @return awayFromBorders
	 */
	def rule5(){
		def farEnough = 0.75d
		def mapXLimit = (int) (worldWidth() / 2)
		def mapYLimit = (int) (worldHeight() / 2)
		def awayFromBorders = new Vector(0, 0)

		if (m_position.x > mapXLimit * farEnough){
			awayFromBorders.x -= mapXLimit
			awayFromBorders.x /= abs(mapXLimit * farEnough - m_position.x)
		}
		if (m_position.y > mapYLimit * farEnough){
			awayFromBorders.y -= mapYLimit
			awayFromBorders.y /= abs(mapYLimit * farEnough - m_position.y)
		}
		if (m_position.x < -1 * mapXLimit * farEnough){
			awayFromBorders.x += mapXLimit
			awayFromBorders.x /= abs(-1 * mapXLimit * farEnough - m_position.x)
		}
		if (m_position.y < -1 * mapYLimit * farEnough){
			awayFromBorders.y += mapYLimit
			awayFromBorders.y /= abs(-1 * mapYLimit * farEnough - m_position.y)
		}
		awayFromBorders *= g_r5Factor
		return awayFromBorders
	}

}