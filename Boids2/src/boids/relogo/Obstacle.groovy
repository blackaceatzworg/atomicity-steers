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
		setHeading(random(360.0))
		updateM_direction()
		updateM_position()
		m_velocity = new Vector(
				getDisplacementFromHeadingAndDistance(heading, 1d) as Number[])
	}


	/**
	 * Step function defining the obstacle's behaviour
	 * @return
	 */
	def step(){
		println "\n++${this}+++++++++++++++++++++++++++++++++++++++++++++++++"

		/* Update timer */
		def timerVal = timer()
		if (! m_lastCallTime) m_lastCallTime = timerVal // 1st update
		m_deltaTime = timerVal - m_lastCallTime
		m_lastCallTime = timerVal

		/* Reinitialise links */


		/* Move */
		updateVelocity()
		updatePositionAndDirection()
		correctPosition()

	}

	/**
	 *
	 * @return
	 */
	def updateVelocity(){
		/* Bounce against borders when needed */
		def mapXLimit = floor(worldWidth() / 2)
		def mapYLimit = floor(worldHeight() / 2)
		
		if (abs(m_position.x) >= mapXLimit)
			m_velocity.x = - m_velocity.x
		if (abs(m_position.y) >= mapYLimit)
			m_velocity.y = - m_velocity.y
	}


	/**
	 * Compute the new position and direction of the obstacle based on its current position
	 * @return
	 */
	def updatePositionAndDirection(){

		def newPosition = m_position + (m_velocity*5d) * m_deltaTime // 5 times faster than boids at most
		def newHeading = positiveAngle(atan(m_velocity.x, m_velocity.y))

		/* Update direction and position */
		setHeading(newHeading)
		setM_direction(m_velocity.normalize())
		setxy(newPosition.x, newPosition.y)
		setM_position(newPosition)
	}
}

