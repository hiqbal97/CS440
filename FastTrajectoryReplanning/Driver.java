import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Driver {
	public static int grid_size = 5;
	public static ArrayList<Node> path;
	public static int runs = 0;
	
	public static void main(String[] args) {
		
		Node[][] maze = new Node[grid_size][grid_size];
		path = new ArrayList<Node>();
		ArrayList<Node> closed_list = new ArrayList<Node>();
		
		//create walls in maze
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				maze[i][j] = new Node(i, j);
				if(ThreadLocalRandom.current().nextInt(1, 11) <= 3)
					maze[i][j].makeWall();
			}
		}
		
		//Start and End points
		int Ax = ThreadLocalRandom.current().nextInt(0, grid_size);
		int Ay = ThreadLocalRandom.current().nextInt(0, grid_size);
		while(maze[Ax][Ay].isBlocked()) {
			Ax = ThreadLocalRandom.current().nextInt(0, grid_size);
			Ay = ThreadLocalRandom.current().nextInt(0, grid_size);
		}
		
		int Tx = ThreadLocalRandom.current().nextInt(0, grid_size);
		int Ty = ThreadLocalRandom.current().nextInt(0, grid_size);
		while(maze[Tx][Ty].isBlocked()) {
			Tx = ThreadLocalRandom.current().nextInt(0, grid_size);
			Ty = ThreadLocalRandom.current().nextInt(0, grid_size);
		}

		System.out.println("Start: " + maze[Ax][Ay].toString() + "\nTarget: " + maze[Tx][Ty].toString());
		//Start point can be the same as an end point- and the most efficient solution would be to not move.		
		
		//Load heuristic values
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				maze[i][j].h = Math.abs(maze[i][j].x - Tx) + Math.abs(maze[i][j].y - Ty);
			}
		}
		
		//print grid world
		System.out.println("|| INITIAL GRID WORLD ||");
		printMaze(maze);
		
		path.add(maze[Ax][Ay]);
		A_Star(maze, Ax, Ay, Tx, Ty, closed_list, new NodeComparator());
		//printMaze_path(maze, Tx, Ty);
		System.out.println("Forwards A* with normal tie breaking completed in " + runs + " runs.");
		
		runs = 0;
		path.clear();
		closed_list.clear();
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				maze[i][j].resest();
			}
		}
		A_Star(maze, Ax, Ay, Tx, Ty, closed_list, new AltNodeComparator());
		System.out.println("Forwards A* with alternate tie breaking completed in " + runs + " runs.");
		
		runs = 0;
		path.clear();
		closed_list.clear();
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				maze[i][j].resest();
			}
		}
		A_Star(maze, Tx, Ty, Ax, Ay, closed_list, new NodeComparator());
		System.out.println("Backwards A* with normal tie breaking completed in " + runs + " runs.");
		
		runs = 0;
		path.clear();
		closed_list.clear();
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				maze[i][j].resest();
			}
		}
		Adaptive_A_Star(maze, Tx, Ty, Ax, Ay, closed_list, new NodeComparator());
		System.out.println("Adaptive A* with normal tie breaking completed in " + runs + " runs.");
		
	}
	
	static void A_Star(Node[][] maze, int Ax, int Ay, int Tx, int Ty, ArrayList<Node> closed_list, Comparator<Node> cmp) {
		//closed list defined in main (so walls can be tracked)
		PriorityQueue<Node> open_list = new PriorityQueue<Node>(cmp);
		runs++;
		
		//Check local walls
		if(Ay > 0){
			if(maze[Ax][Ay-1].isBlocked()) 
				closed_list.add(maze[Ax][Ay-1]);
		}
		if(Ay < grid_size-1){
			if(maze[Ax][Ay+1].isBlocked()) 
				closed_list.add(maze[Ax][Ay+1]);
		}
		if(Ax < grid_size -1){
			if(maze[Ax+1][Ay].isBlocked()) 
				closed_list.add(maze[Ax+1][Ay]);
		}
		if(Ax > 0){
			if(maze[Ax-1][Ay].isBlocked())
				closed_list.add(maze[Ax-1][Ay]);
		}
		
		maze[Ax][Ay].g = 0;
		maze[Ax][Ay].f = maze[Ax][Ay].h;
		//Predicts path based on known walls and local walls
		closed_list.add(maze[Ax][Ay]);
		
		//clear duplicates in closed list
		Set<Node> temp = new LinkedHashSet<Node>();
		temp.addAll(closed_list);
		closed_list.clear();
		closed_list.addAll(temp);
		
		computePath(maze, Ax, Ay, Tx, Ty, open_list, closed_list);
		if(path.get(path.size() - 1).x != Tx || path.get(path.size() - 1).y != Ty)	//no solution was found
			return;
		
		//check for blockages along path
		Node block = null;				//will hold earliest blocked cell
		
		for(Node p : path) {
			closed_list.remove(p);		//remove from closed list so possible to visit again (only keeps walls)
			if(block == null && p.isBlocked())
				block = p;
		}
		
		//block now holds the first blocked cell
		if(block != null) {
			//Delete everything after block
			path.subList(path.indexOf(block), path.size()).clear();
			
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					maze[i][j].resest();
				}
			}
			
			/*//reset things after block so as if never visited
			Node q = path;
			Node l = null;
			while(q != block) {
				l = q;
				q = q.parent;
				l.resest();
			}
			
			p = path;
			path = block.parent;
			
			//reset
			ArrayList<Node> temp1 = new ArrayList<Node>();
			//p = path;
			while(p != null) {
				temp1.add(p);
				p = p.parent;
			}
			
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					if(!temp1.contains(maze[i][j]))
						maze[i][j].resest();
					else
						maze[i][j].reset();
				}
			}
			temp1.clear();
			*/
			
			closed_list.add(block);
			Ax = path.get(path.size() - 1).x;
			Ay = path.get(path.size() - 1).y;
			A_Star(maze, Ax, Ay, Tx, Ty, closed_list, cmp);
		}
		//else complete
		
	}
	
	static void Adaptive_A_Star(Node[][] maze, int Ax, int Ay, int Tx, int Ty, ArrayList<Node> closed_list, Comparator<Node> cmp) {
		//closed list defined in main (so walls can be tracked)
		PriorityQueue<Node> open_list = new PriorityQueue<Node>(cmp);
		runs++;
		
		//Check local walls
		if(Ay > 0){
			if(maze[Ax][Ay-1].isBlocked()) 
				closed_list.add(maze[Ax][Ay-1]);
		}
		if(Ay < grid_size-1){
			if(maze[Ax][Ay+1].isBlocked()) 
				closed_list.add(maze[Ax][Ay+1]);
		}
		if(Ax < grid_size -1){
			if(maze[Ax+1][Ay].isBlocked()) 
				closed_list.add(maze[Ax+1][Ay]);
		}
		if(Ax > 0){
			if(maze[Ax-1][Ay].isBlocked())
				closed_list.add(maze[Ax-1][Ay]);
		}
		
		maze[Ax][Ay].g = 0;
		maze[Ax][Ay].f = maze[Ax][Ay].h;
		//Predicts path based on known walls and local walls
		closed_list.add(maze[Ax][Ay]);
		
		//clear duplicates in closed list
		//Set<Node> temp = new LinkedHashSet<Node>();
		//temp.addAll(closed_list);
		closed_list.clear();
		//closed_list.addAll(temp);
		
		computePath(maze, Ax, Ay, Tx, Ty, open_list, closed_list);
		if(path.get(path.size() - 1).x != Tx || path.get(path.size() - 1).y != Ty)	//no solution was found
			return;
		
		closed_list.clear();
		//check for blockages along path
		Node block = null;				//will hold earliest blocked cell
		
		for(Node p : path) {
			//closed_list.remove(p);		//remove from closed list so possible to visit again (only keeps walls)
			//adaptive
			p.h = path.size() - p.g;
			if(block == null && p.isBlocked())
				block = p;
		}
		
		//block now holds the first blocked cell
		if(block != null) {
			//Delete everything after block
			path.subList(path.indexOf(block), path.size()).clear();
			
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					maze[i][j].resest();
				}
			}
			
			/*//reset things after block so as if never visited
			Node q = path;
			Node l = null;
			while(q != block) {
				l = q;
				q = q.parent;
				l.resest();
			}
			
			p = path;
			path = block.parent;
			
			//reset
			ArrayList<Node> temp1 = new ArrayList<Node>();
			//p = path;
			while(p != null) {
				temp1.add(p);
				p = p.parent;
			}
			
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					if(!temp1.contains(maze[i][j]))
						maze[i][j].resest();
					else
						maze[i][j].reset();
				}
			}
			temp1.clear();
			*/
			
			closed_list.add(block);
			if(path.isEmpty()) {
				System.out.println("No solution.");
				return;
			}
			Ax = path.get(path.size() - 1).x;
			Ay = path.get(path.size() - 1).y;
			A_Star(maze, Ax, Ay, Tx, Ty, closed_list, cmp);
		}
		//else complete
		
	}
	
	static void computePath(Node[][] maze, int x, int y, int Tx, int Ty, PriorityQueue<Node> open_list, ArrayList<Node> closed_list) {
		int g = 1;	//the g value that will be assigned to each node

		while(x != Tx || y != Ty) {
			
			if(y > 0 && !closed_list.contains(maze[x][y-1])){
				if(maze[x][y-1].g == -1 || g < maze[x][y-1].g) {
					maze[x][y-1].g = g;
					maze[x][y-1].f = maze[x][y-1].g + maze[x][y-1].h;
				}
				//maze[x][y-1].g = g;
				//maze[x][y-1].f = maze[x][y-1].g + maze[x][y-1].h;
				//if(maze[x][y].parent != null) {
					//if(!maze[x][y].parent.equals(maze[x][y-1]))
						//maze[x][y-1].parent = path;
				//}
				//else
					//maze[x][y-1].parent = path;
				open_list.add(maze[x][y-1]);
			}
			if(y < grid_size-1 && !closed_list.contains(maze[x][y+1])){
				if(maze[x][y+1].g == -1 || g < maze[x][y+1].g) {
					maze[x][y+1].g = g;
					maze[x][y+1].f = maze[x][y+1].g + maze[x][y+1].h;
				}/*
				maze[x][y+1].g = g;
				maze[x][y+1].f = maze[x][y+1].g + maze[x][y+1].h;
				if(maze[x][y].parent != null) {
					if(!maze[x][y].parent.equals(maze[x][y+1]))
						maze[x][y+1].parent = path;
				}
				else
					maze[x][y+1].parent = path;*/
				open_list.add(maze[x][y+1]);
			}
			if(x < grid_size-1 && !closed_list.contains(maze[x+1][y])){
				if(maze[x+1][y].g == -1 || g < maze[x+1][y].g) {
					maze[x+1][y].g = g;
					maze[x+1][y].f = maze[x+1][y].g + maze[x+1][y].h;
				}/*
				maze[x+1][y].g = g;
				maze[x+1][y].f = maze[x+1][y].g + maze[x+1][y].h;
				if(maze[x][y].parent != null) {
					if(!maze[x][y].parent.equals(maze[x+1][y]))
						maze[x+1][y].parent = path;
				}
				else
					maze[x+1][y].parent = path;*/
				open_list.add(maze[x+1][y]);
			}
			if(x > 0 && !closed_list.contains(maze[x-1][y])){
				if(maze[x-1][y].g == -1 || g < maze[x-1][y].g) {
					maze[x-1][y].g = g;
					maze[x-1][y].f = maze[x-1][y].g + maze[x-1][y].h;
				}/*
				maze[x-1][y].g = g;
				maze[x-1][y].f = maze[x-1][y].g + maze[x-1][y].h;
				if(maze[x][y].parent != null) {
					if(!maze[x][y].parent.equals(maze[x-1][y]))
						maze[x-1][y].parent = path;
				}
				else
					maze[x-1][y].parent = path;*/
				open_list.add(maze[x-1][y]);
			}
			
			if(open_list.isEmpty()) {
				System.out.println("No Solution.");
				return;
			}
			else {
				x = open_list.peek().x;
				y = open_list.peek().y;
				closed_list.add(open_list.peek());
				if(path.contains(open_list.peek())) {
					path.subList(path.indexOf(open_list.peek()), path.size());
				}
				path.add(open_list.poll());
			}
			
			g++;
		}
		
	}
	
	static void printMaze(Node[][] maze) {
		
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				if(maze[i][j].isBlocked())
					System.out.print("X\t");
				else
					System.out.print("0\t");
			}
			System.out.print("\n");
		}
	}
	
	static void printMaze_path(Node[][] maze, int Tx, int Ty) {
		if(path.get(path.size() - 1).x != Tx || path.get(path.size() - 1).y != Ty) return;
		int c = 0;
		for(Node p : path) {
			maze[p.x][p.y].c = '.';
			c++;
		}
		
		System.out.println();
		for(int i = 0; i < grid_size; i++) {
			for(int j = 0; j < grid_size; j++) {
				System.out.print(maze[i][j].c + "\t");
			}
			System.out.print("\n");
		}
		System.out.println(c + " nodes are on the calculated path.");
		
		for(Node p : path) {
			maze[p.x][p.y].c = '0';
		}
	}
}
