import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public abstract class snakeInstance {

	ArrayList<Point> bodyPoints;
	boolean expand;
	public Color color;
	
	public snakeInstance(int x, int y, Color color_in) {
		expand = false;
		bodyPoints = new ArrayList<Point>();
		bodyPoints.add(new Point(x,y));
		color = color_in;
	}
	
	public abstract void calculateMove();
	
	public boolean checkAlive() {
		
		for(int i=0; i<mainSnake.allSnakes.size(); i++) 
		{
			for(int j=0; j<mainSnake.allSnakes.get(i).bodyPoints.size(); j++) 
			{
				if ((bodyPoints.get(0).x == mainSnake.allSnakes.get(i).bodyPoints.get(j).x)
					&& (bodyPoints.get(0).y == mainSnake.allSnakes.get(i).bodyPoints.get(j).y))
					{
						if ((j==0) && (mainSnake.allSnakes.get(i)==this))
							continue;
						else
							return false;
					}
			}
		}
		
		if ((bodyPoints.get(0).x == 0) || (bodyPoints.get(0).y == 0) || (bodyPoints.get(0).y == mainSnake.boardSize.height-1) || (bodyPoints.get(0).x == mainSnake.boardSize.width-1))
			return false;
		
		return true;
		
	}
	
	/*
	 * ==== tickSnake() ====
	 * Technicals for processing a generic snake
	 */
	
	public void tickSnake() {
		
		Point lastPos = new Point(bodyPoints.get(bodyPoints.size()-1));
		for(int i=bodyPoints.size()-1; i>0; i--) 
		{
			bodyPoints.set(i, new Point(bodyPoints.get(i-1)));
		}
		calculateMove();
		if (expand) 
		{
			bodyPoints.add(lastPos);
			expand = false;
		}
		if ((mainSnake.foodPoint.y == bodyPoints.get(0).y) && (mainSnake.foodPoint.x == bodyPoints.get(0).x)) 
		{
			mainSnake.foodPoint = mainSnake.getRandomPos();
			expand = true;
		}
	}
	
}
