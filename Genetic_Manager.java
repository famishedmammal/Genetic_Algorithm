
public class Genetic_Manager {

	//Types
	//0 = naive AI
	//1 = aggressive AI
	//2 = randomized AI_smart
	
	public static int maxOperands = 5;
	public static int tests_perRound = 30;
	public static float[][] roundRatios = {{1.0f, 0.0f, 0.0f},
							{0.0f, 1.0f, 0.0f},
							{1.0f, 0.0f, 1.0f},
							{0.3f, 0.3f, 0.3f},
							{0.5f, 0.0f, 0.5f},
							{0.0f, 0.5f, 0.5f},
							{0.5f, 0.5f, 0.0f}};
	
	public static int totalTicks = 0;
	public static int round = 0;
	
	public static void roundOver() {
		mainSnake.respawnAll();
		
		Genetic_Manager.totalTicks = 0;
	}
	
}
