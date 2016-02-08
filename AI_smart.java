import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class AI_smart extends snakeInstance {
	
	public AI_smart(int x, int y, Color color_in, int length_in) {
		super(x, y, color_in, length_in);
	}

	public void calculateMove()
	{
		int xx = bodyPoints.get(0).x;
		int yy = bodyPoints.get(0).y;
		
		float[] scores = new float[4];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		
		// Safety heuristics
		float[] emptinessScores = emptinessHeuristic_calc(xx, yy); // BFS, find "emptiest" area
		float[] wallflowerScores = wallflowerHeuristic_calc(xx, yy); // DFS, find area best for coiling
		float[] isolationScores = isolationHeuristic_calc(xx, yy); // A*, find shortest path to tile farthest away from snake heads
		float[] repulsionScores = repulsionHeuristic_calc(xx, yy); // inverted square root, repulse away from snake heads
		
		// Aggressive heuristics
		float[] hungerScores = hungerHeuristic_calc(xx, yy); // A*, find shortest path to food
		//float[] aggressionScores = aggressionHeuristic_calc(xx, yy); // attack player if necessary
		
		// Weigh each heuristic. Note to self : each one should work flawlessly independently
		for(int i=0; i<4; i++) 
		{
			scores[i] =
					((emptinessScores[i]/emptinessScores[4]) * 1.0f) + //0.7
					((wallflowerScores[i]/wallflowerScores[4]) * 1.0f) + //1.5
					((isolationScores[i]) * 1.0f) + //0.7
					((hungerScores[i]) * 1.0f) +
					((repulsionScores[i]) * -10.0f);
		}
		
		// Determine what to do
		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<Float> scr = new ArrayList<Float>();
		for(int i=0; i<4; i++) 
		{
			options.add(i);
			scr.add(scores[i]);
		}
		
		for(int i=0; i<4; i++) 
		{
			int maxIndex = -1;
			float maxScore = -999999;
			for(int j=0; j<scr.size(); j++)
			{
				if (scr.get(j) > maxScore) {
					maxScore = scr.get(j);
					maxIndex = j;
				}
			}
			
			int toDo = options.remove(maxIndex);
			scr.remove(maxIndex);
			
			switch(toDo) 
			{
				case 0:	if (!mainSnake.bakedMap[bodyPoints.get(0).x+1][bodyPoints.get(0).y]) {bodyPoints.get(0).x++; return;} break;
				case 1:	if (!mainSnake.bakedMap[bodyPoints.get(0).x][bodyPoints.get(0).y+1]) {bodyPoints.get(0).y++; return;} break;
				case 2:	if (!mainSnake.bakedMap[bodyPoints.get(0).x-1][bodyPoints.get(0).y]) {bodyPoints.get(0).x--; return;} break;
				case 3:	if (!mainSnake.bakedMap[bodyPoints.get(0).x][bodyPoints.get(0).y-1]) {bodyPoints.get(0).y--; return;} break;
			}
		}
		
		System.out.println("GG");
		bodyPoints.get(0).x++;
	}
	
	float [] repulsionHeuristic_calc(int xx, int yy)
	{
		float[] scores = new float[4];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		
		for(int j=0; j<4; j++) 
		{
			int xmod = (int)Math.cos(j/4f * 2 * Math.PI);
			int ymod = (int)Math.sin(j/4f * 2 * Math.PI);
			
			for(int i=0; i<mainSnake.allSnakes.size(); i++) 
			{
				if (mainSnake.allSnakes.get(i) != this) 
				{
					double dist = Point.distance(xx+xmod, yy+ymod, mainSnake.allSnakes.get(i).bodyPoints.get(0).x, mainSnake.allSnakes.get(i).bodyPoints.get(0).y);
					scores[j] += 1/(dist*dist);
				}
			}
		}
		return scores;
	}
	
	float [] hungerHeuristic_calc(int xx, int yy)
	{
		float[] scores = new float[4];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		
		int n = shortestPath(xx, yy, mainSnake.foodPoint);
		if (n != -1)
			scores[n] = 1.0f;
		
		return scores;
	}
	
	float [] isolationHeuristic_calc(int xx, int yy) 
	{
		float[] scores = new float[4];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		
		Point isolation_position = new Point(0, 0);
		float isolation_score = -1;
		
		for (int i=0; i<mainSnake.boardSize.width; i++) 
		{
			for (int j=0; j<mainSnake.boardSize.height; j++)
			{
				if (!mainSnake.bakedMap[i][j])
				{
					float sum = 0;
					for(int k=0; k<mainSnake.allSnakes.size(); k++) {
						if (mainSnake.allSnakes.get(k) != this)
							sum += Point.distance(i, j, mainSnake.allSnakes.get(k).bodyPoints.get(0).x, mainSnake.allSnakes.get(k).bodyPoints.get(0).y);
					}
					if (sum > isolation_score) 
					{
						isolation_position = new Point(i, j);
						isolation_score = sum;
					}
				}
			}
		}
		
		int n = shortestPath(xx, yy, isolation_position);
		if (n != -1)
			scores[n] = 1.0f;
		
		return scores;
	}
	
	int shortestPath(int xx, int yy, Point target) {
		
		boolean[][] movementMap = new boolean[mainSnake.boardSize.width][mainSnake.boardSize.height];
		movementMap[xx][yy] = true;
		
		ArrayList<Point> explore = new ArrayList<Point>();
		ArrayList<Float> explore_points = new ArrayList<Float>();
		ArrayList<Integer> explore_type = new ArrayList<Integer>();
		
		for(int i=0; i<4; i++) 
		{
			int xmod = (int)Math.cos(i/4f * 2 * Math.PI);
			int ymod = (int)Math.sin(i/4f * 2 * Math.PI);
			shortestPath_push(xx+xmod, yy+ymod, explore, explore_type, explore_points, movementMap, i, target);				
		}
		
		while(!explore.isEmpty())
		{
			float min = 9999999;
			int min_index = -1;
			for(int i=0; i<explore.size(); i++)
			{
				if (explore_points.get(i) < min)
				{
					min = explore_points.get(i);
					min_index = i;
				}
			}
			
			int type = explore_type.remove(min_index);
			if (explore_points.remove(min_index) == 0)
				return type;
			Point current = explore.remove(min_index);
			xx = current.x;
			yy = current.y;
			
			for(int i=0; i<4; i++) 
			{
				int xmod = (int)Math.cos(i/4f * 2 * Math.PI);
				int ymod = (int)Math.sin(i/4f * 2 * Math.PI);
				shortestPath_push(xx+xmod, yy+ymod, explore, explore_type, explore_points, movementMap, type, target);				
			}
		}
		return -1;
	}
	
	void shortestPath_push(int xx, int yy, ArrayList<Point> toExplore, ArrayList<Integer> toExplore_type, ArrayList<Float> toExplore_points, boolean[][] movementMap, int type, Point target) 
	{
		if (!(mainSnake.bakedMap[xx][yy] == true || movementMap[xx][yy] == true)) {
			toExplore.add(new Point(xx, yy));
			toExplore_type.add(type);
			toExplore_points.add((float)Point.distance(xx, yy, target.x, target.y));
		}		
		movementMap[xx][yy] = true;
	}	
	
	float [] wallflowerHeuristic_calc(int xx, int yy) 
	{
		boolean[][] movementMap = new boolean[mainSnake.boardSize.width][mainSnake.boardSize.height];	
		movementMap[xx][yy] = true;
		
		float[] scores = new float[5];
		scores[0] = scores[1] = scores[2] = scores[3] = 0;
		scores[4] = 1;
		
		scores[0] = wallflowerHeuristic_recurse(xx+1, yy, movementMap, 0, 1, 0);
		scores[1] = wallflowerHeuristic_recurse(xx, yy+1, movementMap, 0, 0, 1);
		scores[2] = wallflowerHeuristic_recurse(xx-1, yy, movementMap, 0, -1, 0);
		scores[3] = wallflowerHeuristic_recurse(xx, yy-1, movementMap, 0, 0, -1);
		
		for(int i=0; i<4; i++)
			if (scores[i] > scores[4])
				scores[4] = scores[i];
		
		return scores;
	}
	
	float wallflowerHeuristic_recurse(int xx, int yy, boolean[][] movementMap, int depth, int prevx, int prevy) 
	{
		if ((movementMap[xx][yy] == true || mainSnake.bakedMap[xx][yy] == true))
			return 0;
		if (!((mainSnake.bakedMap[xx+1][yy] && (prevx!=-1)) || (mainSnake.bakedMap[xx-1][yy] && (prevx!=1)) 
				|| (mainSnake.bakedMap[xx][yy+1] && (prevy!=-1)) || (mainSnake.bakedMap[xx][yy-1] && (prevy!=1))
				|| mainSnake.bakedMap[xx-1][yy-1] || mainSnake.bakedMap[xx+1][yy+1] || mainSnake.bakedMap[xx+1][yy-1] || mainSnake.bakedMap[xx-1][yy+1]))
			return 0;
		movementMap[xx][yy] = true;
		
		float sum = 1;
		for(int i=0; i<4; i++) 
		{
			int xmod = (int)Math.cos(i/4f * 2 * Math.PI);
			int ymod = (int)Math.sin(i/4f * 2 * Math.PI);
			sum += wallflowerHeuristic_recurse(xx+xmod, yy+ymod, movementMap, ++depth, xmod, ymod);
		}
		return sum;
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
		for(int i=0; i<4; i++) 
		{
			int xmod = (int)Math.cos(i/4f * 2 * Math.PI);
			int ymod = (int)Math.sin(i/4f * 2 * Math.PI);
			emptinessHeuristic_push(xx+xmod, yy+ymod, nextExplore, nextExplore_type, movementMap, i);
		}
				
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
				
				scores[index]++;
				for(int i=0; i<4; i++) 
				{
					int xmod = (int)Math.cos(i/4f * 2 * Math.PI);
					int ymod = (int)Math.sin(i/4f * 2 * Math.PI);
					emptinessHeuristic_push(xx+xmod, yy+ymod, nextExplore, nextExplore_type, movementMap, index);
				}
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
