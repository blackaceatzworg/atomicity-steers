package boids.relogo

import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory

public class UserGlobalsAndPanelFactory extends AbstractReLogoGlobalsAndPanelFactory{
	public void addGlobalsAndPanelComponents(){
	
		addButtonWL("setup", "Setup")
		addButtonWL("go", "Go Once")
		addToggleButtonWL("go", "Go")

		addChooser("g_bordersMode", ["stopAtBorders", "roundThroughBorders"])
		
		addSliderWL("g_numBoids", "Number of Boids", 1, 1, 100, 10)
		addSliderWL("g_numObstacles", "Number of Obstacles", 0, 1, 20, 2)
		addSliderWL("g_sensingRadius", "Boid sensing radius", 1, 1, 50, 6)
//		addSliderWL("g_accelerationRate", "Acceleration rate", 0.1, 0, 5, 2.5)
//		addSliderWL("g_dampingRate", "Damping rate", 0.01, 0, 1, 0.6)
//		addSliderWL("g_rotationRate", "Rotation rate", 1, 1, 360, 180)
		
		addSliderWL("g_r1Factor", "Factor 1: Cohesion", 0, 0.01, 0.1, 0.02)
		addSliderWL("g_r2Factor", "Factor 2: Separation", 0, 0.1, 5, 2)
		addSliderWL("g_r3Factor", "Factor 3: Velocity matching", 0, 0.01, 1, 0.25)
		addSliderWL("g_r4Factor", "Factor 4: Tendency towards goal", 0, 0.01, 0.1, 0.01)
		addSliderWL("g_r5Factor", "Factor 5: Away from borders", 0, 0.1, 5, 2)
		addSliderWL("g_r6Factor", "Factor 6: Away from obstacles", 0, 0.1, 5, 2)

		addGlobal("g_maxSpeed", 3) // units per second
		

		addReLogoTickCountDisplay()

	}
}