import java.awt.Color;
import java.awt.Point;
import java.util.Stack;

public class AI_smart extends snakeInstance {
	
	float freespaceWeight = 1;
	float repulsionWeight = 0;
	
	public AI_smart(int x, int y, Color color_in) {
		super(x, y, color_in);
	}

	public void calculateMove()
	{
		int xx = bodyPoints.get(0).x;
		int yy = bodyPoints.get(0).y;
		
		float[] scores = new float[4];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		
		// Calculate each heuristic
		float[] emptinessScores = emptinessHeuristic_calc(xx, yy);
		float[] wallflowerScores = wallflowerHeuristic_calc(xx, yy);
		//float[] repulsionScores =
		//float[] hungerScores = 
		
		// Weigh each heuristic
		for(int i=0; i<4; i++) 
		{
			scores[i] =
					((emptinessScores[i]/emptinessScores[4]) * 1.0f)
					+ ((wallflowerScores[i]/wallflowerScores[4]) * 1.0f);
		}
		
		// Determine what to do
		System.out.println(scores[0]+","+scores[1]+","+scores[2]+","+scores[3]);
		int toDo = -1;
		float maxScore = -1;
		for(int i=0; i<4; i++) 
		{
			if (scores[i] > maxScore) {
				maxScore = scores[i];
				toDo = i;
			}	
		}
		switch(toDo) 
		{
		case 0:
			bodyPoints.get(0).y ++; return;
		case 1:
			bodyPoints.get(0).x ++; return;
		case 2:
			bodyPoints.get(0).y --; return;
		case 3:
			bodyPoints.get(0).x --; return;
		}
	}
	
	float[] wallflowerHeuristic_calc(int xx, int yy) {
		float[] scores = new float[5];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		scores[4] = 1;
		
		
		
		return scores;
	}
	
	float[] emptinessHeuristic_calc(int xx, int yy) 
	{
		boolean[][] movementMap = new boolean[mainSnake.boardSize.width][mainSnake.boardSize.height];	
		movementMap[xx][yy] = true;
		
		float[] scores = new float[5];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		scores[4] = 1;
		
		// Calculate movement scores
		Stack<Point> toExplore = new Stack<Point>();
		Stack<Integer> toExplore_type = new Stack<Integer>();
		Stack<Point> nextExplore = new Stack<Point>();
		Stack<Integer> nextExplore_type = new Stack<Integer>();
		emptinessHeuristic_push(xx-1, yy, nextExplore, nextExplore_type, movementMap, 3);
		emptinessHeuristic_push(xx+1, yy, nextExplore, nextExplore_type, movementMap, 1);
		emptinessHeuristic_push(xx, yy+1, nextExplore, nextExplore_type, movementMap, 0);
		emptinessHeuristic_push(xx, yy-1, nextExplore, nextExplore_type, movementMap, 2);	
				
		while(!nextExplore.isEmpty())
		{
			toExplore = nextExplore;
			nextExplore = new Stack();
			toExplore_type = nextExplore_type;
			nextExplore_type = new Stack();
					
			while(!toExplore.isEmpty()) 
			{
				Point position = toExplore.pop();
				xx = position.x;
				yy = position.y;
				int index = toExplore_type.pop();
				scores[index] += freespaceWeight;
						
				emptinessHeuristic_push(xx-1, yy, nextExplore, nextExplore_type, movementMap, index);
				emptinessHeuristic_push(xx+1, yy, nextExplore, nextExplore_type, movementMap, index);
				emptinessHeuristic_push(xx, yy+1, nextExplore, nextExplore_type, movementMap, index);
				emptinessHeuristic_push(xx, yy-1, nextExplore, nextExplore_type, movementMap, index);
			}
		}
		
		for(int i=0; i<4; i++)
			if (scores[i] > scores[4])
				scores[4] = scores[i];

		return scores;
	}
	
	void emptinessHeuristic_push(int xx, int yy, Stack<Point> toExplore, Stack<Integer> toExplore_type, boolean[][] movementMap, int type) 
	{
		if (!(mainSnake.bakedMap[xx][yy] == true || movementMap[xx][yy] == true)) {
			toExplore.push(new Point(xx, yy));
			toExplore_type.push(type);
		}		
		movementMap[xx][yy] = true;
	}	
	
}
