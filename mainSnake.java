import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class mainSnake {
	
	public static Display display;
	private static BufferStrategy bs;
	private static Graphics g;
	public static boolean[][] bakedMap;
	public static Dimension windowSize;
	public static Dimension boardSize;
	public static Point foodPoint;
	public static ArrayList<snakeInstance> allSnakes;
	public static snakeInstance smartSnake;
	public static Queue<Long> spawns;
	
	public static void main(String[] args) {
		
		int renderSpeed = 5;
		int respawnTimeMS = 2000;
		boardSize = new Dimension(30,30);
		windowSize = new Dimension(600, 600);
		display = new Display();
		foodPoint = getRandomPos();
		allSnakes = new ArrayList<snakeInstance>();
		spawns = new LinkedList<Long>();
		respawnAll();
		
		
		while(true)
		{
			long nextFrame = System.currentTimeMillis()+renderSpeed;
			while(true) 
			{
				long currentTime = System.currentTimeMillis();
				if (!spawns.isEmpty()) 
				{
					if (spawns.peek() <= System.currentTimeMillis()) {
						spawns.remove();
						spawnSnake(false);
					}					
				}
				if (currentTime >= nextFrame)
					break;
			}
			
			bakedMap = bakeInstances();
			for(int i=0; i<allSnakes.size(); i++) {
				allSnakes.get(i).tickSnake();
			}
			renderFrame();
			Genetic_Manager.totalTicks++;
			for(int i=0; i<allSnakes.size(); i++) {
				if (allSnakes.get(i).checkAlive() == false) {
					if (allSnakes.get(i) == smartSnake) {
						Genetic_Manager.roundOver();
					}
					else
					{
						allSnakes.remove(allSnakes.get(i));
						spawns.add(System.currentTimeMillis()+respawnTimeMS);						
					}
				}
			}
		}
	}
	
	public static void respawnAll() {
		allSnakes.clear();
		for(int i=0; i<10; i++) 
			spawnSnake(true);
		Point spawn = getRandomPos();
		smartSnake = new AI_smart(spawn.x, spawn.y, new Color(70, 70, 255), 15);
		allSnakes.add(smartSnake);
		spawns.clear();
	}
	
	public static void spawnSnake(boolean random) 
	{
		Point spawn;
		if (random)
			spawn = getRandomPos();
		else
			spawn = new Point(5,5);
		snakeInstance naive = new AI_simple(spawn.x, spawn.y, Color.GRAY, 10);
		allSnakes.add(naive);			
	}
	
	/*
	 * ==== getRandomPos() ====
	 * Generates a random point on the board
	 */
	
	public static Point getRandomPos() {
		int x = (int)(new Random().nextFloat() * boardSize.width);
		if (x <= 1) 
			x=2;
		else if (x >= boardSize.width-2) 
			x=boardSize.width-3;
		int y = (int)(new Random().nextFloat() * boardSize.height);
		if (y <= 1) 
			y=2;
		else if (y >= boardSize.height-2) 
			y=boardSize.height-3;
		return new Point(x, y);
	}
	
	/*
	 * ==== render(Graphics g) ====
	 * Renders background board, all snake objects, and food object
	 */
	
	public static void render(Graphics g) 
	{
		for(int i=0; i<boardSize.width; i++) 
		{
			for(int j=0; j<boardSize.height; j++) 
			{
				renderCube(g, 0, i, j, Color.BLACK);
			}
		}
		for(int i=0; i<boardSize.width; i++) {
			renderCube(g, 3, i, boardSize.height-1, Color.DARK_GRAY);
			renderCube(g, 3, i, 0, Color.DARK_GRAY);
			renderCube(g, 3, boardSize.width-1, i, Color.DARK_GRAY);
			renderCube(g, 3, 0, i, Color.DARK_GRAY);
			renderCube(g, 3, i, boardSize.height-2, Color.DARK_GRAY);
			renderCube(g, 3, i, 1, Color.DARK_GRAY);
			renderCube(g, 3, boardSize.width-2, i, Color.DARK_GRAY);
			renderCube(g, 3, 1, i, Color.DARK_GRAY);
		}
		for(int j=0; j<allSnakes.size(); j++) 
		{
			for(int i=0; i<allSnakes.get(j).bodyPoints.size(); i++)
				renderCube(g, 1, allSnakes.get(j).bodyPoints.get(i).x, allSnakes.get(j).bodyPoints.get(i).y, allSnakes.get(j).color);
			Color c = new Color(allSnakes.get(j).color.getRed()-70, allSnakes.get(j).color.getGreen()-70, allSnakes.get(j).color.getBlue()-70);
			renderCube(g, 1, allSnakes.get(j).bodyPoints.get(0).x, allSnakes.get(j).bodyPoints.get(0).y, c);
		}

		renderCube(g, 2, foodPoint.x, foodPoint.y, Color.GREEN);

	}
	
	/*
	 * ==== renderCube(Graphics g, int type, int x, int y) ====
	 * Renders a colored cube to the canvas.
	 */
	
	public static void renderCube(Graphics g, int type, int x, int y, Color color) 
	{
		Point boxSize = new Point (windowSize.width/boardSize.width, windowSize.height/boardSize.height);

		if (type == 0) 
		{
			g.setColor(Color.BLACK);
			g.drawRect(boxSize.x * x ,boxSize.y * y, boxSize.x, boxSize.y);
			return;			
		}
		else 
		{
			g.setColor(color);
			g.fillRect(boxSize.x * x ,boxSize.y * y, boxSize.x, boxSize.y);
		}
	}
	
	 /* ==== bakeInstances() ====
	 * Converts the board and all snakes into an 2D array ("empty" or "filled" elements)
	 */
	
	public static boolean[][] bakeInstances() {
		boolean[][] map = new boolean[boardSize.width][boardSize.height];
		
		for(int i=0; i<boardSize.width; i++) {
			map[i][boardSize.height-1] = map[i][0] = true;
			map[i][boardSize.height-2] = map[i][1] = true;
		}
		for(int i=0; i<boardSize.height; i++) {
			map[boardSize.width-1][i] = map[0][i] = true;
			map[boardSize.width-2][i] = map[1][i] = true;
		}
		
		for(int j=0; j<allSnakes.size(); j++)
			for(int i=0; i<allSnakes.get(j).bodyPoints.size(); i++)
				map[allSnakes.get(j).bodyPoints.get(i).x][allSnakes.get(j).bodyPoints.get(i).y] = true;
		return map;
	}
	
	/*
	 * ==== renderFrame(Graphics g) ====
	 * Technicals for rendering.
	 */
	
	public static void renderFrame() 
	{
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, windowSize.width, windowSize.height);
		render(g);
		bs.show();
		g.dispose();
	}

}
