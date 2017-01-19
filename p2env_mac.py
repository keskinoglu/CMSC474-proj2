""" This program runs 2 players against eachother. The default player2 (gold) is a random player.
In the players.txt text file, if only one player is added, the second player is the default random player.
If 2 players are added, then the two players play against eachother. The first player (blue) is the program that is 
entered in the first line of players.txt. The second player (gold) is the one entered in the second line. Both programs get compiled as well.

One important note is that this environment is based on bash commands. You might want to change the assign_player_and_compile function
@ lines ~ 105-120. You can also modify this code and use the subprocess module and make everything for Windows CMD.
These complications could have been ignored if everyone was using java but since some people might want to use c++ or python we had
to do it this way.

I have used gtimeout that is in GNU coreutils. If you have a unix machine you can change gtimeout to timeout

The main game is at the very end (line > 165). 

If you have any questions, feel free to post a note on Piazza. Good Luck. --Ali Shafahi (ashafahi@cs.umd.edu), Fall 2016"""
import sys
import os
import random

######################################################################
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
######################################################################
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

####################################################################
#building dictionary for identifying winner if the winner is winning based on axis North-South
SC_NS = []
for i in range(10):
	SC_NS.append([j for j in range(i,i*10+1,9)])
for i in range(10):
	SC_NS.append([j for j in range(9+10*(i+1),101+i,9)])

whichColNS = []
for j in range(0, 110):
	whichColNS += [i for i in range(len(SC_NS)) if j in SC_NS[i]]
#dictionary for mapping the indices to North-South column ids
colNS = dict(zip(range(110), whichColNS))
#####################################################################
#finding winner
def winner_found(playerLocations, opposingPlayerLocations, PlayerInitialLocations):
	#identify which of the 3 axis the player falls within
	playerLocations.sort()
	diff_with_smallest = [playerLocations[i] - playerLocations[0] for i in range(1,4)]
	#check to see if the player's markers are all on the North-South axis
	if all(x % 9 == 0 for x in diff_with_smallest):
		col = colNS[playerLocations[0]]
		searchCol = SC_NS[col]
		if all(x in searchCol for x in playerLocations[1:]) and not any(x in searchCol for x in opposingPlayerLocations):
			if not any(x in PlayerInitialLocations for x in playerLocations):
				return True
	#check to see if the player wins based on NW-SE axis:
	elif all(x % 10 == 0 for x in diff_with_smallest):
		col = playerLocations[0] % 10
		searchCol = range(col, 101 + col, 10)
		if all(x in searchCol for x in playerLocations[1:]) and not any(x in searchCol for x in opposingPlayerLocations):	
			if not any(x in PlayerInitialLocations for x in playerLocations):
				return True
	else:	#check to see if winning player on NE-SW axis
		col = int(playerLocations[0]//10)
		searchCol = range(col*10, col*10 + 10, 1)
		if all(x in searchCol for x in playerLocations[1:]) and not any(x in searchCol for x in opposingPlayerLocations):
			if not any(x in PlayerInitialLocations for x in playerLocations):
				return True
	return False

#####################################################################
#random player: Plays somethig random based on possible actions
def random_player(randomPlayerLocations, opposingPlayerLocations):
	moving_marker = random.choice(randomPlayerLocations)
	mypossibleMoves = possible_moves(moving_marker, randomPlayerLocations, opposingPlayerLocations)
	while len(mypossibleMoves) == 0:
		moving_marker = random.choice(randomPlayerLocations)
		mypossibleMoves = possible_moves(moving_marker, randomPlayerLocations, opposingPlayerLocations)
	whereTo = random.choice(mypossibleMoves)
	sys.stdout.write(str(moving_marker) + ' ' + str(whereTo) + '\n')
	f = open('output.out', 'w')
	f.write(str(moving_marker) + ' ' + str(whereTo) + '\n')
	f.close()
	return

####################################################################
#compiling files and assigning them to players
def assign_player_and_compile(txtfileLine):
	if txtfileLine.split('.')[-1].strip() == 'cpp':
		os.system("g++ " + txtfileLine.strip() + " -O2 -o " + txtfileLine.strip())
		this_player = "gtimeout 1s " + "./" + txtfileLine.strip() + " < input.in > output.out"
	elif txtfileLine.split('.')[-1].strip() == 'java':
		os.system("javac " + txtfileLine.strip())
		this_player = "gtimeout 1s " + "java " + txtfileLine.split('.')[0].strip() + " < input.in > output.out"
	else:
		#"timeout 1s " + 
		this_player = "gtimeout 1s " + " python " + txtfileLine.strip() + " < input.in > output.out"

	return this_player

####################################################################
#here is where the program outputs are processed. This function, figures out how to update the location of the markers
#it returns a list: [currentMarkerLocation, FutureMarkerLocation]
def process_output(playerLocations, opposingPlayerLocations):
	actions = open('output.out','r').readlines()
	#find final action
	while len(actions) > 0 and actions[-1].split(' ')[0].strip() == '':
		del actions[-1]
	#if no actions reported	
	if len(actions) == 0:
		print 'No actions -> Making random move'
		#get what random player would do here!!!!!!!
		return random_player(playerLocations, opposingPlayerLocations)
	
	#if an action was reported, store it
	else:	
		lastactions = [int(a.strip()) for a in actions[-1].split(' ')[:2]]

	#check to see if action is acceptable
	if lastactions[1] not in possible_moves(lastactions[0], playerLocations, opposingPlayerLocations) or lastactions[0] not in playerLocations:
		print 'action', lastactions, 'is an illegal action -> Making random move'
		#get what random player would do here!!!!!!!
		return	random_player(playerLocations, opposingPlayerLocations)	
	
	return [lastactions[0], lastactions[1]]
	
####################################################################
#this function prepares the input for the programs and prints them
#the input is prepared for the player with PlayerLocations
def input_preparer(playerLocations, opposingPlayerLocations):
	f = open('input.in', 'w')
	f.write(str(playerLocations[0]) + ' ' + str(playerLocations[1]) + ' ' + str(playerLocations[2]) + ' ' + str(playerLocations[3]) +  '\n')
	f.write(str(opposingPlayerLocations[0]) + ' ' + str(opposingPlayerLocations[1]) + ' ' + str(opposingPlayerLocations[2]) + ' ' + str(opposingPlayerLocations[3]) +  '\n')
	f.write(str(0)+'\n')
	f.close()	

	sys.stdout.write(str(playerLocations[0]) + ' ' + str(playerLocations[1]) + ' ' + str(playerLocations[2]) + ' ' + str(playerLocations[3]) +  '\n')
	sys.stdout.write(str(opposingPlayerLocations[0]) + ' ' + str(opposingPlayerLocations[1]) + ' ' + str(opposingPlayerLocations[2]) + ' ' + str(opposingPlayerLocations[3]) +  '\n')
	sys.stdout.write(str(0)+'\n')
	sys.stdout.flush()

	return

####################################################################
#************************************************     Here is the game       ***************************
#open the players.txt file and compile and assign players
f = open('players.txt','r').readlines()
while f[-1].strip() == '':
	del f[-1]
blue_player = assign_player_and_compile(f[0])
if len(f) == 1: #here if in the players.txt there is only one file name then it will play against a random player
	gold_player = random_player
else: #if there are two players, they will play against eachother
	gold_player = assign_player_and_compile(f[1]) 
#initialize	
m = 1
init_gold = [0,9,103,106]
init_blue = [3,6,100,109]
blue = init_blue[:]
gold = init_gold[:]
#play game 200 iterations at most
while m <= 200:
	print '\n\n--------------------------\n iteration', m
	m += 1
	#let player blue make a move
	print 'blues turn'
	input_preparer(blue, gold)
	os.system(blue_player)
	fromTo = process_output(blue, gold)
	print 'the marker change:', fromTo
	blue[blue.index(fromTo[0])] = fromTo[1]
	#let gold make move
	print
	print 'golds turn'
	input_preparer(gold, blue)
	if gold_player == random_player:
		gold_player(gold, blue)
	else:
		os.system(gold_player)	
	fromTo = process_output(gold, blue)
	print 'the marker change:', fromTo
	gold[gold.index(fromTo[0])] = fromTo[1]
	#search for winner
	BlueWon = winner_found(blue, gold, init_blue)
	GoldWon = winner_found(gold, blue, init_gold)
	if BlueWon or GoldWon:
		if BlueWon and GoldWon:
			print 'Tie'
			break
		elif BlueWon:
			print 'blue wins'
			break
		else:
			print 'gold wins'
			break
else:
	print 'Tie'
