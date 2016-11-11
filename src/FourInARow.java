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
		this.x = position % 10;
		this.y = position - (position / 10);
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
	
}

public class FourInARow {

	public static void main(String[] args) 
	{
		//Read input into Node object
	}

}
