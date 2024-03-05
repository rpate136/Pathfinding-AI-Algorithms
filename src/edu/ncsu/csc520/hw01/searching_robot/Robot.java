package edu.ncsu.csc520.hw01.searching_robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.*;

/**
 * Represents an intelligent agent moving through a particular room.
 * The robot only has one sensor - the ability to get the status of any
 * tile in the environment through the command env.getTileStatus(row, col).
 * 
 * Your task is to modify the getAction method below so that is reached the
 * TARGET POSITION with a minimal number of steps. There is only one (1)
 * target position, which you can locate using env.getTargetRow() and
 * env.getTargetCol()
 */

public class Robot {
	private Environment env;
	private int posRow;
	private int posCol;
	private String searchAlgorithm;
	private int index;
	private ArrayList<Action> plan;

	/**
	 * Initializes a Robot on a specific tile in the environment.
	 */
	public Robot(Environment env, int posRow, int posCol, String searchAlgorithm) {
		this.env = env;
		this.posRow = posRow;
		this.posCol = posCol;
		this.searchAlgorithm = searchAlgorithm;
		this.index = 0;
	}

	public int getPosRow() {
		return posRow;
	}

	public int getPosCol() {
		return posCol;
	}

	public void incPosRow() {
		posRow++;
	}

	public void decPosRow() {
		posRow--;
	}

	public void incPosCol() {
		posCol++;
	}

	public void decPosCol() {
		posCol--;
	}

	/**
	 * Construct search tree before Robot start moving.
	 */

	public void plan() {
		int startRow = 0;
		int startCol = 0;
		int tragetRow = env.getTargetRow();
		int thargetCol = env.getTargetCol();

		switch (searchAlgorithm) {
			case "AStar":
				Astar(startRow, startCol, tragetRow, thargetCol);
				break;
			case "OLDFS":
				OnlineDFS(startRow, startCol, tragetRow, thargetCol);
			case "OLHillClimbing":
			case "OLImprovedHillClimbing":
			default:
				break;
		}
	}

	/**
	 * Simulate the passage of a single time-step.
	 * At each time-step, the Robot decides which direction
	 * to move.
	 */
	public Action getAction() {
		// you can get a tile's status with
		TileStatus status = env.getTileStatus(posRow, posCol);

		if (status == TileStatus.TARGET) {
			/**
			 * Print out required information here
			 */
			System.out.println("Yayyyyy!");

		}

		Action action = Action.DO_NOTHING;

		switch (searchAlgorithm) {
			case "OLDFS":
	
				action = plan.get(index);

				break;
			case "OLHillClimbing":

				break;
			case "OLImprovedHillClimbing":

				break;
			case "AStar":

				action = plan.get(index);

				break;
			default:
				break;
		}

		 index++;
		return action;
	}

	public void Astar(int startRow, int startCol, int targetRow, int targetCol) {

		LinkedList<Node> closedlist = new LinkedList<Node>();

		Comparator<Node> Fvalue = Comparator.comparing(Node::getF_cost);

		PriorityQueue<Node> openlist = new PriorityQueue<>(Fvalue); // how to break ties

		Node start = new Node(startRow, startCol);
		start.setG_cost(0);
		start.setH_cost(0);
		Node goal = new Node(targetRow, targetCol);

		openlist.add(start);

		while (!openlist.isEmpty()) {

			// System.out.println((openlist.peek()));
			Node current = openlist.poll();

			openlist.remove(current);
			closedlist.add(current);

			if (current.compare(goal)) {
				System.out.println("Found Goal");
				robotmovement(start, current);
				break;
			}

			OUTTER: for (Node i : getNeighbour(current)) {
				int newMovementCose = (current.getG_cost() + calcDistance(current, i));

				for (Node e : closedlist) {
					if (e.compare(i)) {
						continue OUTTER;
					}
				}

				i.setH_cost(calcDistance(i, goal));
				i.setG_cost(newMovementCose);
				i.setParent(current);
				openlist.add(i);

				for (Node e : openlist) {
					if (e.compare(i)) {
						if (i.getG_cost() <= newMovementCose) {
							continue OUTTER;
						}
					}
				}

			}
			closedlist.add(current);

		}
	}

	public void OnlineDFS(int startRow, int startCol, int targetRow, int targetCol) {

		LinkedList<Node> visited = new LinkedList<Node>();
		Stack<Node> nodeStack = new Stack<Node>();
		
		Node start = new Node(startRow, startCol);
		Node goal = new Node(targetRow, targetCol);
		Node current;
		nodeStack.push(start);
		visited.add(start);
		System.out.println("We are here");

		while(!nodeStack.isEmpty()) {

			current = nodeStack.pop();
			visited.add(current);

			if (current.compare(goal)){
				System.out.println("Found Goal");
				robotmovement(start, current);
				break;
			}
			for(Node e: getNeighbour(current)){
				if(searchLinkedList(visited, e)){
					continue;
				}
				else{
				e.setParent(current);
				nodeStack.push(e);
				}

			}
		}
	}

	public int calcDistance(Node one, Node two) {

		int NodeOne_Y = one.getRow();
		int NodeOne_X = one.getCol();

		int NodeTwo_Y = two.getRow();
		int NodeTwo_X = two.getCol();

		int distance = Math.abs(NodeOne_Y - NodeTwo_Y) + Math.abs(NodeOne_X - NodeTwo_X);

		return distance;
	}

	public boolean isTileValid(Node current) {

		int col = current.getCol();
		int row = current.getRow();

		TileStatus status = env.getTileStatus(row, col);

		if (status.toString().equalsIgnoreCase("IMPASSABLE")) {
			return false;
		}

		if (row >= 0 && row < 10 && col >= 0 && col < 10) {
			return true;
		}

		return false;
	}

	public List<Node> getNeighbour(Node current) {

		int col = current.getCol();
		int row = current.getRow();

		List<Node> listOfNodes = new ArrayList<Node>();

		Node UP_child = new Node(row - 1, col);
		Node DOWN_child = new Node(row + 1, col);
		Node LEFT_child = new Node(row, col - 1);
		Node RIGHT_child = new Node(row, col + 1);

		if (isTileValid(UP_child)) {
			listOfNodes.add(UP_child);
			// UP_child.setF_cost(F_cost(UP_child));

		}
		if (isTileValid(DOWN_child)) {
			listOfNodes.add(DOWN_child);
			// DOWN_child.setF_cost(F_cost(DOWN_child));

		}
		if (isTileValid(LEFT_child)) {
			listOfNodes.add(LEFT_child);
			// LEFT_child.setF_cost(F_cost(LEFT_child));
		}
		if (isTileValid(RIGHT_child)) {
			listOfNodes.add(RIGHT_child);
			// RIGHT_child.setF_cost(F_cost(RIGHT_child));
		}

		return listOfNodes;
	}

	public ArrayList<Action> robotmovement(Node start, Node goal) {

		ArrayList<Node> movementPlan = new ArrayList<Node>();
		plan = new ArrayList<Action>();

		Node currrent = goal;

		while (!currrent.compare(start)) {
			movementPlan.add(currrent);
			currrent = currrent.parent;
		}

		Collections.reverse(movementPlan);
		//System.out.println("Movement Plan: " + movementPlan);

		int currentRow = 0;
		int currentCol = 0;

		for (Node i : movementPlan) {

			if (i.getCol() == 0 && i.getRow() == 0) {
				continue;
			}

			else if (i.getCol() - currentCol == 0 && i.getRow() - currentRow == 1) {
				// MOVE_DOWN
				plan.add(Action.MOVE_DOWN);
			}

			else if (i.getCol() - currentCol == 0 && i.getRow() - currentRow == -1) {
				// MOVE_UP
				plan.add(Action.MOVE_UP);

			}

			else if (i.getCol() - currentCol == 1 && i.getRow() - currentRow == 0) {
				// MOVE_RIGHT
				plan.add(Action.MOVE_RIGHT);

			}

			else if (i.getCol() - currentCol == -1 && i.getRow() - currentRow == 0) {
				// MOVE_LEFT
				plan.add(Action.MOVE_LEFT);

			}

			currentRow = i.getRow();
			currentCol = i.getCol();

		}
		return plan;
	}

	public boolean searchLinkedList(LinkedList<Node> temp, Node tempNode){

		for(Node i : temp){
			if(i.compare(tempNode)){
				return true;
			}
		}
		return false;
	}
}