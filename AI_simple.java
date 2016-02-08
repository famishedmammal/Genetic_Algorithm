import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class AI_simple extends snakeInstance {
	
	public AI_simple(int x, int y, Color color_in) {
		super(x, y, color_in);
	}

	public void calculateMove()
	{
		int x = bodyPoints.get(0).x;
		int y = bodyPoints.get(0).y;
		
		// Move towards food
		if ((mainSnake.foodPoint.x > x) && (!mainSnake.bakedMap[x+1][y])) {
			bodyPoints.get(0).x ++; return;
		}
		if ((mainSnake.foodPoint.x < x) && (!mainSnake.bakedMap[x-1][y])) {
			bodyPoints.get(0).x --; return;
		}
		if ((mainSnake.foodPoint.y > y) && (!mainSnake.bakedMap[x][y+1])) {
			bodyPoints.get(0).y ++; return;
		}
		if ((mainSnake.foodPoint.y < y) && (!mainSnake.bakedMap[x][y-1])) {
			bodyPoints.get(0).y --; return;
		}
		
		// Plan B, avoid direct collisions
		if ((!mainSnake.bakedMap[x+1][y])) {
			bodyPoints.get(0).x ++; return;
		}
		if ((!mainSnake.bakedMap[x-1][y])) {
			bodyPoints.get(0).x --; return;
		}
		if ((!mainSnake.bakedMap[x][y+1])) {
			bodyPoints.get(0).y ++; return;
		}
		if ((!mainSnake.bakedMap[x][y-1])) {
			bodyPoints.get(0).y --; return;
		}
		
		switch((new Random()).nextInt(4)) {
			case 0: bodyPoints.get(0).x ++; return;
			case 1: bodyPoints.get(0).x --; return;
			case 2: bodyPoints.get(0).y ++; return;
			case 3: bodyPoints.get(0).y --; return;
		}
		
	}

}
