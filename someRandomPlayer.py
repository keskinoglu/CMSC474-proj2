import sys
import os
import random

#this is some random player

#####################################################################
# searching for possible moves
def search_move(Index, AddToIndex, BoundryCondition):
	result = []
	iterator = 0
	currentIndex = Index
	while iterator < 2 and not eval(str(currentIndex)+BoundryCondition):
		iterator += 1
		currentIndex += AddToIndex
		result.append(currentIndex)
	return result	

#for the following function we just need the index location and the location of the players, the order of the lcoation of the players doesn't matter
def possible_moves(indexLocation, goldLocation, blueLocation):
	result = []
	#add possible moves to the north
	result += search_move(indexLocation, -9, 'in [0,1,2,3,4,5,6,7,8,9,19,29,39,49,59,69,79,89,99,109]') 
	#add possible moves to south
	result += search_move(indexLocation, +9, 'in [0,10,20,30,40,50,60,70,80,90,100,101,102,103,104,105,106,107,108,109]') 
	#add possible moves to north east	
	result += search_move(indexLocation, +1, 'in [9,19,29,39,49,59,69,79,89,99,109]')
	#add possible moves to north west	
	result += search_move(indexLocation, -10, '  < 10 ')
	#add possible moves to south east	
	result += search_move(indexLocation, +10, ' >= 100 ')
	#add possible moves to south west	
	result += search_move(indexLocation, -1, ' % 10 == 0 ')
	
	result = list(set(result) - set(goldLocation) - set(blueLocation)) 
	
	return result

#####################################################################
#random player: Plays somethig random based on possible actions
def random_player(randomPlayerLocations, opposingPlayerLocations):
	moving_marker = random.choice(randomPlayerLocations)
	MyPossibleMoves = possible_moves(moving_marker, randomPlayerLocations, opposingPlayerLocations)
	while len(MyPossibleMoves) == 0:
		moving_marker = random.choice(randomPlayerLocations)
		MyPossibleMoves = possible_moves(moving_marker, randomPlayerLocations, opposingPlayerLocations)
	whereTo = random.choice(MyPossibleMoves)
	sys.stdout.write(str(moving_marker) + ' ' + str(whereTo) + '\n')
	sys.stdout.flush()
	return

def main():
	#read the inputs
	l = sys.stdin.readline().strip()
	myPlayersLocations = [int(x.strip()) for x in l.split(' ')]
	l = sys.stdin.readline().strip()
	otherPlayersLocations = [int(x.strip()) for x in l.split(' ')]
	thisPlayer = int(sys.stdin.readline().strip())

	#make some move
	random_player(myPlayersLocations, otherPlayersLocations)
	
	

main()		


