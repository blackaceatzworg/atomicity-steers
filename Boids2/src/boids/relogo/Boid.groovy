package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;

class Boid extends UserTurtle {
	def m_collisionsWithBoid = 0
	def m_collisionsWithObstacle = 0

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
		
		/* Update timer */
		def timerVal = timer()
		if (! m_lastCallTime) m_lastCallTime = timerVal // 1st update
		m_deltaTime = timerVal - m_lastCallTime
		m_lastCallTime = timerVal
		
		/* Reinitialise links and collisions */
		SameFlock.removeSameFlockLinks(this)
		m_collisionsWithBoid = 0
		m_collisionsWithObstacle = 0

		/* Move */
		updateVelocity()
		correctVelocity()
		updatePositionAndDirection()
		correctPosition()

	}

	/**
	 * Compute the new velocity of the vector from its behavioural rules
	 * @return
	 */
	def updateVelocity(){

		/* Compute modifications to trajectory from flocking rules */
		def v1 = rule1(), v2 = rule2(), v3 = rule3()
		def v4 = rule4(), v5 = rule5(), v6 = rule6()
		def velocityModifier = v1 + v2 + v3 + v4 + v5 + v6
		
		/* Determine target velocity */
		m_velocity += velocityModifier * m_deltaTime
	}

	/**
	 * Compute the new position and direction of the boid based on its velocity 
	 * and current position
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
		for (nearBoid in inRadius(boids(), g_sensingRadius * g_avoidBySensing)){
			if (nearBoid != this){
				/* Collision? */
				def distance = (m_position - nearBoid.m_position).length()
				if (distance < getSize()) m_collisionsWithBoid++ 
				
				def awayFromNearBoid = moveAwayFrom(nearBoid.m_position, g_sensingRadius * g_avoidBySensing)
				awayFromCloseBoids += awayFromNearBoid
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
		/* Nothing to do if boids can go through borders */
		if (g_bordersMode == "roundThroughBorders")
			return new Vector(0, 0)
		
		/* Determine location to "run away from" on the world borders */
		def mapXLimit = floor(worldWidth() / 2)
		def mapYLimit = floor(worldHeight() / 2)
		def origin = new Vector(0, 0)
		def locationOnBorder = new Vector(m_position)
		if (mapXLimit - abs(m_position.x) < g_sensingRadius * g_avoidBySensing)
			locationOnBorder.x = mapXLimit * ((locationOnBorder.x < 0)? -1:1)
		if (mapYLimit - abs(m_position.y) < g_sensingRadius * g_avoidBySensing)
			locationOnBorder.y = mapYLimit * ((locationOnBorder.y < 0)? -1:1)
		
		/* Nothing to do if the borders are far enough */
		if (abs(locationOnBorder.x) != mapXLimit && abs(locationOnBorder.y) != mapYLimit)
			return new Vector(0, 0)
		
		Vector awayFromBorders = moveAwayFrom(locationOnBorder, g_sensingRadius, origin)
		awayFromBorders *= g_r5Factor
		return awayFromBorders
	}

	/**
	 * Rule 6: Away from obstacles
	 * @return awayFromObstacles
	 */
	def rule6(){
		Vector awayFromObstacles = new Vector(0, 0)
		for (nearObstacle in inRadius(obstacles(), g_sensingRadius * g_avoidBySensing)){
			/* Collision? */
			def distance = (m_position - nearObstacle.m_position).length()
			if (distance < (getSize() + nearObstacle.getSize()) / 2d) m_collisionsWithObstacle++ 
			
			def awayFromNearObstacle = moveAwayFrom(nearObstacle.m_position, g_sensingRadius * g_avoidBySensing)
			awayFromObstacles += awayFromNearObstacle
		}

		awayFromObstacles *= g_r6Factor
		return awayFromObstacles
	}

	/**
	 * Move away from a chosen location, with a push-back effect increasing 
	 * as the distance to this location decreases, and with a fall-back location
	 * in case the boid is already on the location to avoid 
	 * @param locationToAvoid
	 * @param spottingDistance
	 * @param fallBack
	 * @return awayFromLocation
	 */
	def moveAwayFrom(def locationToAvoid, def spottingDistance, def fallBack=null){
		/* Case of collision */
		if (m_position == locationToAvoid) {
			if (!fallBack || fallBack == m_position)
				fallBack = new Vector(randomXcor(), randomYcor())
			return (fallBack - m_position).normalize() * spottingDistance
		}
		
		/* Case of non-zero distance to location to avoid */
		def awayFromLocation = - (locationToAvoid - m_position)
		
		def distance = awayFromLocation.length()
		awayFromLocation *= (spottingDistance - distance) / distance
		return awayFromLocation
	}


}