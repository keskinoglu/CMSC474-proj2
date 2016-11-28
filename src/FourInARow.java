import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;

/**
 * A wrapper class to create ordered pairs (x,y) to represent locations on the gameboard
 */
class Location 
{
	int x; //the row entry - 0 <= x <= 10
	int y; //the column entry - 0 <= y <= 9 

	/**
	 * Creates a new Location which represents piece #=(x*10+y) or, equivalently, 
	 * the piece located in the yth position of the xth row. 
	 * @param x 0 <= x <= 10 The row position
	 * @param y 0 <= y <= 9 The column position
	 */
	Location(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new Location which represents the piece with #=position
	 * and converts this into an x location and a y location.
	 * @param position 0 <= position <= 109 The numbered position on the gameboard
	 */
	Location(int position)
	{
		this.x = position/10;
		this.y = position - (x*10);
	}

	/**
	 * Converts the location object back into a numbered position on the board
	 * @return The board position
	 */
	int convert_location_to_board_position()
	{
		return x*10 + y;
	}

	@Override
	public String toString()
	{
		return this.convert_location_to_board_position() + "";
	}
}

/**
 * A wrapper class to create an ordered pair (loc1,loc2) where loc1 is the starting
 * location of a move, and loc2 is the ending location of the move.
 */
class Move 
{
	Location start; //the starting location of the move
	Location end; //the location you end up in after the move

	/**
	 * Creates a new Move object which contains the starting location of the move
	 * and the ending position of the move
	 * @param start A location representing where the piece is
	 * @param end A location representing where the piece is to go
	 */
	Move(Location start, Location end)
	{
		this.start = start;
		this.end = end;
	}

	/** Added for debugging purposes **/
	void printMove(){  
		System.out.print("(From:" + this.start.convert_location_to_board_position() + 
				", To: " + this.end.convert_location_to_board_position() + ") \n");
	}
	
	@Override
	public String toString()
	{
		return start.toString() + " " + end.toString();
	}
}

/**
 * A container class to represent a Node in the game tree. Each Node object keeps track
 * of which player's turn it is, and the locations of each player's peices. 
 */
class Node 
{
	Location[] player0_pieces; //Contains the piece locations of player 0
	Location[] player1_pieces; //Contains the piece locations of player 1
	int player_turn; //Represents which players turn it is - either 0 or 1
	
	ArrayList<Node> next_moves;
	Move suggested_move;
	
	static int our_player;
	static int other_player;
 	double value;
	
	/**
	 * Creates a new Node in the game tree. The node contains which player's turn it is,
	 * and the locations of both player0's and player1's pieces.  
	 * @param player_turn Either 0 or 1 to represent which players turn it is
	 * @param player0_pieces An array containing the locations of player0's pieces
	 * @param player1_pieces An array containing the locations of player0's pieces
	 */
	Node(int player_turn, Location[] player0_pieces, Location[] player1_pieces)
	{
		this.player_turn = player_turn;
		this.player0_pieces = player0_pieces;
		this.player1_pieces = player1_pieces;
		this.value = -9999;
	}

	/**
	 * Creates a new Node in the game tree. The node contains which player's turn it is,
	 * and the locations of both player0's and player1's pieces. This constructor will
	 * take in the numbered locations as integers and automatically convert it to
	 * a Location array.
	 * @param player_turn Either 0 or 1 to represent which players turn it is
	 * @param player0_pieces An array containing the position #s of player0's pieces 
	 * @param player1_pieces An array containing the position #s of player0's pieces 
	 */
	Node(int player_turn, int[] player0_pieces, int[] player1_pieces)
	{
		this.player_turn = player_turn;
		this.player0_pieces = new Location[4];
		this.player1_pieces = new Location[4];
		for (int i = 0; i < 4; i++)
		{
			this.player0_pieces[i] = new Location(player0_pieces[i]);
			this.player1_pieces[i] = new Location(player1_pieces[i]);
		}
	}

	
	Node(int player_turn, ArrayList<Location> player0_pieces, ArrayList<Location> player1_pieces)
	{
		this.player_turn = player_turn;
		this.player0_pieces = new Location[4];
		this.player1_pieces = new Location[4];
		for (int i = 0; i < 4; i++)
		{
			this.player0_pieces[i] = player0_pieces.get(i);
			this.player1_pieces[i] = player1_pieces.get(i);}
	}
	
	/**
	 * Sets the static variables 'our_player' and 'other_player' so we can reference
	 * which player we are throughout the game. Sets  'our_player' = 'who_we_are'
	 * @param who_we_are 0 or 1 representing which player we are in the game.
	 */
	static void set_our_player(int who_we_are) {
		if (who_we_are == 0) {
			Node.our_player = 0;
			Node.other_player = 1;
		} else {
			Node.our_player = 1;
			Node.other_player = 0;
		}
	}
	
	
	ArrayList<Node> generate_children()
	{
		ArrayList<Node> children = new ArrayList<Node>();
		ArrayList<Move> moves = new ArrayList<Move>(Arrays.asList(this.generateMoves()));
		ArrayList<Location> p0 = new ArrayList<Location>(Arrays.asList(this.player0_pieces));
		ArrayList<Location> p1 =  new ArrayList<Location>(Arrays.asList(this.player1_pieces));
	
		if (this.player_turn == 0) {
	
			while (!moves.isEmpty()) {
				Move current = moves.get(0);
				moves.remove(0);
				int index = p0.indexOf(current.start);
				p0.set(index, current.end);
				children.add(new Node(1, p0,p1));
				p0.set(index, current.start);
			}	
			
		} else {
			while (!moves.isEmpty()) {
				Move current = moves.get(0);
				moves.remove(0);
				int index = p0.indexOf(current.start);
				p1.set(index, current.end);
				children.add(new Node(0, p0,p1));
				p1.set(index, current.start);
			}	
		}
		//max
		if(this.player_turn == our_player){
			double temp;
			for(Node i: children){
				temp = i.eval(i.our_player, i.other_player);
				i.value = temp;	
			}
			Collections.sort(children, new Comparator<Node>() {
	            @Override
	            public int compare(Node o1, Node o2) {
	            	return (int)(o1.value - o2.value);
	            }
	        });		
		}else{ //min
			double temp;
			for(Node i: children){
				temp = i.eval(Node.our_player, Node.other_player);
				i.value = temp;	
			}
			Collections.sort(children, new Comparator<Node>() {
	            @Override
	            public int compare(Node o1, Node o2) {
	            	return (int)(o2.value - o1.value);
	            }
	        });	
		}
		return children;
	}
	
	double max(double a, double b) {
		if (a >= b) return a;
		return b;
	}
	double min(double a, double b) {
		if (a <= b) return a;
		return b;
	}
	
	double alpha_beta(int depth, double alpha, double beta) {
		this.next_moves = new ArrayList<Node>();
		/*if (this.is_terminal()) {
			return the value given for a end-game board
		} */
		if (depth == 0) {
			double value = eval(Node.our_player, Node.other_player);
			return value;
			
		} else if (this.player_turn == Node.our_player) { //the current node is our move
			double v = -999999999;
			ArrayList<Node> moves = this.generate_children();
			for (Node move : moves) {
				v = max(v,move.alpha_beta(depth-1, alpha, beta));
				if (v >= beta) {
					/* A beta cutoff has occured, need to remove the proper branches from this node...
					 */
					return v;
				}
				this.next_moves.add(move);
				alpha = max(alpha, v);
			}
			return v;
		} else {
			double v = 999999999;
			ArrayList<Node> moves = this.generate_children();
			for (Node move : moves) {
				v = max(v,move.alpha_beta(depth-1, alpha, beta));
				if (v <= alpha) {
					/* An alpha cutoff has occured, need to remove the proper branches from this node...
					 */
					return v;
				}
				this.next_moves.add(move);
				beta = min(beta, v);
			}
			return v;
		}
	}
	
	Move associate_move(Node child)
	{
		if (this.player_turn == 0)
		{
			for (int i = 0; i < 4; i++)
			{
				if (!this.player0_pieces[i].equals(child.player0_pieces[i]))
				{
					return new Move(player0_pieces[i], child.player0_pieces[i]);
				}
			}
		}
		if (this.player_turn == 1)
		{
			for (int i = 0; i < 4; i++)
			{
				if (!this.player1_pieces[i].equals(child.player1_pieces[i]))
				{
					return new Move(player1_pieces[i], child.player1_pieces[i]);
				}
			}
		}
		return null;
	}
	
	double limited_depth_minimax(int depth)
	{
		/*if (this.is_terminal()) 
		 {
		return the value given for a end-game board
		} */
		if (depth == 0) 
		{
			return this.eval(our_player, other_player);
		}
		if (this.player_turn == Node.our_player) 
		{
			double max = -99999999;
			Move max_move = new Move(new Location(0,0), new Location(0,0));
			for (Node child : this.next_moves)
			{
				double val = child.limited_depth_minimax(depth-1);
				if (val > max) {
					max = val;
					max_move = associate_move(child);
				}
			}
			this.suggested_move = max_move;
			return max;
		} else 
		{
			double min = 99999999;
			Move min_move = new Move(new Location(0,0), new Location(0,0));
			for (Node child : this.next_moves)
			{
				double val = child.limited_depth_minimax(depth-1);
				if (val < min) {
					min = val;
					min_move = associate_move(child);
				}
			}
			this.suggested_move = min_move;
			return min;
		}
		
	}
	
	/**
	 * For each piece of the current player, generates the available moves for that piece
	 * and adds them to an array containing the available moves of all the pieces of the player. 
	 * @return  Returns an array containing the available moves for the gameboard associated with 
	 * this node.
	 */
	Move[] generateMoves()
	{
		Move[] moves = null;
		List<Move> listOfMoves = new ArrayList<Move>();

		if(player_turn == 0){
			boolean flag = false;
			List<Location> pos_moves = null;
			int[] check = new int[player1_pieces.length];

			/** Put other players actual board positions into array **/
			for(int i = 0; i<check.length; i++){
				check[i] = player1_pieces[i].convert_location_to_board_position();
			}

			/** For every piece in the locations array find the possible moves and place into the return array **/
			for(Location curr: player0_pieces){
				pos_moves = generateMovesHelper(curr);
				
				/** Checking if possible moves are legal **/
				for(Location move: pos_moves){
					if((move.convert_location_to_board_position() >= 0 && move.convert_location_to_board_position() <= 109) &&
							curr.convert_location_to_board_position() != move.convert_location_to_board_position()){  
						/** Check if other player is on the piece **/
						for(int i = 0; i< check.length; i++){
							if(check[i] == move.convert_location_to_board_position()){
								flag = true;
							}
						}
						if(flag == false){
							listOfMoves.add(new Move(curr,move)); 
						}
						flag = false;
					}
				}	
			}

			/** Put into Moves array now **/
			moves = new Move[listOfMoves.size()];
			/** Convert arraylist to array to return **/
			for(int i = 0; i<listOfMoves.size(); i++){ 
				moves[i] = listOfMoves.get(i);
			}
		}else if(player_turn == 1){ //Otherwise find the moves for the other player
			boolean flag = false;
			List<Location> pos_moves = null;
			int[] check = new int[player1_pieces.length];

			/** Put other players actual board positions into array **/
			for(int i = 0; i<check.length; i++){
				check[i] = player0_pieces[i].convert_location_to_board_position();
			}

			/** For every piece in the locations array find the possible moves and place into the return array **/
			for(Location curr: player1_pieces){
				pos_moves = generateMovesHelper(curr);
				
				/** Checking if possible moves are legal **/
				for(Location move: pos_moves){
					if((move.convert_location_to_board_position() >= 0 && move.convert_location_to_board_position() <= 109) &&
							curr.convert_location_to_board_position() != move.convert_location_to_board_position()){  
						/** Check if other player is on the piece **/
						for(int i = 0; i< check.length; i++){
							if(check[i] == move.convert_location_to_board_position()){
								flag = true;
							}
						}
						if(flag == false){
							listOfMoves.add(new Move(curr,move)); 
						}
						flag = false;
					}
				}	
			}

			/** Put into Moves array now **/
			moves = new Move[listOfMoves.size()];
			/** Convert arraylist to array to return **/
			for(int i = 0; i<listOfMoves.size(); i++){ 
				moves[i] = listOfMoves.get(i);
			}
		}
		return moves;	 
	}

	/**
	 * Helper function to find possible move for each players piece
	 * Possible locations depending on the position of the markers
	 */
	List<Location> generateMovesHelper(Location curr){
		List<Location> locations = new ArrayList<Location>();
			if(curr.x+1 < 11 && curr.y < 10 && curr.x+1 >= 0 && curr.y >= 0){
				locations.add(new Location(curr.x+1,curr.y));
			}

			if(curr.x-1 < 11 && curr.y < 10 && curr.x-1 >= 0 && curr.y >= 0){
				locations.add(new Location(curr.x-1,curr.y));
			}

			if(curr.x-1 < 11 && curr.y+1 < 10 && curr.x-1 >= 0 && curr.y+1 >= 0){
				locations.add(new Location(curr.x-1,curr.y+1));
			}

			if(curr.x+1 < 11 && curr.y-1 < 10 && curr.x+1 >= 0 && curr.y-1 >= 0){
				locations.add(new Location(curr.x+1,curr.y-1));
			}

			if(curr.x < 11 && curr.y-1 < 10 && curr.x >= 0 && curr.y-1 >= 0){
				locations.add(new Location(curr.x,curr.y-1));
			} 

			if(curr.x < 11 && curr.y-2 < 10 && curr.x >= 0 && curr.y-2 >= 0){
				locations.add(new Location(curr.x,curr.y-2));
			}

			if(curr.x < 11 && curr.y+2 < 10 && curr.x >= 0 && curr.y+2 >= 0){
				locations.add(new Location(curr.x,curr.y+2));
			}

			if(curr.x < 11 && curr.y+1 < 10 && curr.x >= 0 && curr.y+1 >= 0){
				locations.add(new Location(curr.x,curr.y+1));
			}

			if(curr.x+2 < 11 && curr.y-2 < 10 && curr.x+2 >= 0 && curr.y-2 >= 0){
				locations.add(new Location(curr.x+2,curr.y-2));
			}

			if(curr.x-2 < 11 && curr.y+2 < 10 && curr.x-2 >= 0 && curr.y+2 >= 0){
				locations.add(new Location(curr.x-2,curr.y+2));
			}

			if(curr.x+2 < 11 && curr.y < 10 && curr.x+2 >= 0 && curr.y >= 0){
				locations.add(new Location(curr.x+2,curr.y));
			}

			if(curr.x-2 < 11 && curr.y < 10 && curr.x-2 >= 0 && curr.y >= 0){
				locations.add(new Location(curr.x-2,curr.y));
			}
		return locations; 
	}
	
	/*
	 * enter int of 0 or 1 for which player we are and opponent
	 */
	double eval(int us, int opponent) {
		// return eval_player(player_0) - eval_player(player_1);
		return eval_player(us) - eval_player(opponent);
	}
	
	/*
	 * Perform least squares on a given player's pieces
	 * @param 0 or 1 for player 1 or player 2
	 */
	double eval_player(int player) {
		int sum_x = 0;
		int sum_y = 0;
		int sum_xy = 0;
		int sum_xx = 0;
		int sum_yy = 0;
		
		// Correlation coefficient
		double r = 0;
		
		if (player == 0) {
			for (Location i : this.player0_pieces) {
				sum_x += i.x;
				sum_y += i.y;
				sum_xx += i.x * i.x;
				sum_yy += i.y * i.y;
			}
			
			r = (4*sum_xy - sum_x*sum_y) / (Math.sqrt((4*sum_xx - sum_x*sum_x)*(4*sum_yy - sum_y*sum_y)));
		} else {
			for (Location i : this.player1_pieces) {
				sum_x += i.x;
				sum_y += i.y;
				sum_xx += i.x * i.x;
				sum_yy += i.y * i.y;
			}
			
			r = (4*sum_xy - sum_x*sum_y) / (Math.sqrt((4*sum_xx - sum_x*sum_x)*(4*sum_yy - sum_y*sum_y)));
		}
		
		return r*r;
	}
	
	/*
	 * Returns -1 if there is no winner currently, 0 if tie and
	 * returns 1 or 2 if a player has won.
	 */
	int winner() {
		double player_0 = 0;
		double player_1 = 0;

		player_0 = Math.abs(eval_player(0));
		player_1 = Math.abs(eval_player(1));

		if (player_0 == 1 && !blocked(0)) {
			if (player_1 == 1 && !blocked(1)) {
				return 0;
			}
			return 1;
		}

		if (player_1 == 1 && !blocked(1)) {
			return 2;
		}
		return -1;
	}

	boolean blocked(int player) {
		int player_0_max_x = 0;
		int player_0_min_x = 10;

		int player_0_max_y = 0;
		int player_0_min_y = 9;

		int player_1_max_x = 0;
		int player_1_min_x = 10;

		int player_1_max_y = 0;
		int player_1_min_y = 9;

		for (Location i : this.player0_pieces) {
			if (i.x > player_0_max_x) {
				player_0_max_x = i.x;
			}
			if (i.x < player_0_min_x) {
				player_0_min_x = i.x;
			}
			if (i.y > player_0_max_y) {
				player_0_max_y = i.y;
			}
			if (i.y < player_0_min_y) {
				player_0_min_y = i.y;
			}
		}

		for (Location i : this.player1_pieces) {
			if (i.x > player_1_max_x) {
				player_1_max_x = i.x;
			}
			if (i.x < player_1_min_x) {
				player_1_min_x = i.x;
			}
			if (i.y > player_1_max_y) {
				player_1_max_y = i.y;
			}
			if (i.y < player_1_min_y) {
				player_1_min_y = i.y;
			}
		}

		if (player == 0) {
			for (Location i : this.player0_pieces) {
				for (Location j : this.player1_pieces) {
					if (i.x == j.x && i.y < player_1_max_y && i.y > player_1_min_y) {
						return true;
					}
					if (i.y == j.y && i.x < player_1_max_x && i.x > player_1_min_x) {
						return true;
					}
				}
			}
		} else {
			for (Location i : this.player1_pieces) {
				for (Location j : this.player0_pieces) {
					if (i.x == j.x && i.y < player_0_max_y && i.y > player_0_min_y) {
						return true;
					}
					if (i.y == j.y && i.x < player_0_max_x && i.x > player_0_min_x) {
						return true;
					}
				}
			}
		}
		return false;
	}
}


public class FourInARow {
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in); //for reading game data
		int[] player0_moves = new int[4]; //holds the positions of player0's moves
		int[] player1_moves = new int[4]; //holds the positions of player1's moves

		for (int i = 0; i < 4; i++) //reads in player0's moves 
		{
			player0_moves[i] = scanner.nextInt();
		}
		for (int i = 0; i < 4; i++) //reads in player1's moves 
		{
			player1_moves[i] = scanner.nextInt();
		}
		int turn = scanner.nextInt(); //reads in which player's turn it is

		scanner.close(); //close the scanner, no longer needed -> might remove for speed

		//Creates a Node for the gameboard according to the incoming input
		Node starting_node = new Node(turn, player0_moves, player1_moves);
		Node.set_our_player(turn); //ASSUMPTION: our player is whoever turn it is
		
		int depth = 1;
		while (true)
		{
			starting_node.alpha_beta(depth, -999999999, 999999999);
			starting_node.limited_depth_minimax(depth);
			System.out.println(starting_node.suggested_move);
		}
	}
}
