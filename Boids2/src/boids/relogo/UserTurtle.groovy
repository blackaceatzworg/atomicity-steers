package boids.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserTurtle extends BaseTurtle{
	def m_lastCallTime = 0
	def m_deltaTime = 0
	def m_position = new Vector(0, 0)
	def m_direction = new Vector(0, 0)
	def m_velocity = new Vector(0, 0)

	/* POSITION AND DIRECTION */
	
	/**
	 * Correct current position so that the turtle is always in the current world
	 * @return
	 */
	def correctPosition(){
		def mapXLimit = floor(worldWidth() / 2)
		def mapYLimit = floor(worldHeight() / 2)
		
		if (g_bordersMode == "roundThroughBorders"){
			while (m_position.x > mapXLimit) 
				m_position.x -= mapXLimit*2
			while (m_position.y > mapYLimit)
				m_position.y -= mapYLimit*2
			while (m_position.x < -1*mapXLimit)
				m_position.x += mapXLimit*2
			while (m_position.y < -1*mapYLimit)
				m_position.y += mapYLimit*2
		}
		if (g_bordersMode == "stopAtBorders"){
			if (abs(m_position.x) > mapXLimit) 
				m_position.x = mapXLimit * ((m_position.x < 0)? -1:1) 
			if (abs(m_position.y) > mapYLimit)
				m_position.y = mapYLimit * ((m_position.y < 0)? -1:1)
		}
		
		setxy(m_position.x as double, m_position.y as double)
		setM_position(new Vector(xcor, ycor))
	}
	
	/**
	 * Correct current velocity so that the turtle doesn't move too fast
	 * @return
	 */
	def correctVelocity(){
		/* Correct magnitude */
		def speed = m_velocity.length()
		if (speed > g_maxSpeed) speed = g_maxSpeed
		
		/* Correct angle */
		m_velocity = velocityWithMaxDelta()
		m_velocity *= speed / m_velocity.length()
	}

	/**
	 * Update position vector from coordinates
	 * @return
	 */
	def updateM_position(){
		setM_position(new Vector(xcor, ycor))
	}

	/**
	 * Update direction vector from heading
	 * @return
	 */
	def updateM_direction(){
		Number[] displacement = getDisplacementFromHeadingAndDistance(heading, 1.0)
		setM_direction(new Vector(displacement))
	}

	
	/* ANGLE MANIPULATION */
	
	/**
	 *
	 * @param currentHeading
	 * @param targetDirection
	 * @param maxDeltaHeading
	 * @return newDirection
	 */
	private def velocityWithMaxDelta(){
		def maxDeltaHeading = g_rotationRate * m_deltaTime
		def currentHeading = positiveAngle(heading)
		def targetHeading = positiveAngle(atan(m_velocity.x, m_velocity.y))
		def deltaHeading = narrowestDelta(targetHeading - currentHeading)

		if (abs(deltaHeading) > maxDeltaHeading){
			deltaHeading = maxDeltaHeading * ((deltaHeading <0 )? -1:1)
		}

		def newHeading = positiveAngle(currentHeading + deltaHeading)
		Number[] displacement = getDisplacementFromHeadingAndDistance(newHeading, 1.0)
		def newDirection = new Vector(displacement)
		return newDirection
	}

	private static def narrowestDelta = {
		if (it > 180) { it -= 360 }
		else if (it < -180) { it += 360 }
		return it
	}

	static def positiveAngle = {
		if (it < 0) { it += 360 }
		else if (it > 360) { it -= 360}
		return it
	}

}