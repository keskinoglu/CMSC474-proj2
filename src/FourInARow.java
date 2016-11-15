import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

		/** DEBUGGING PURPOSES, RUN WHEN YOU NEED TO SEE POSSIBLE MOVES
		 *  Comment this out when moving forward
		 */
		Move[] temp= starting_node.generateMoves();
		for(int i = 0; i< temp.length;i++){
			temp[i].printMove();
		}
	}
}