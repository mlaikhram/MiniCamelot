/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicamelot;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author Matthew Laikhram
 * Defines the class that represents a board configuration as an array of ints.
 * Moves are used on a board to change the configuration.
 * The board is also responsible for calculating all possible moves given a
 * coordinate to a piece.
 */
public class Board {
    
    //Creates a new game board with pieces in the initial position
    public Board() {
        mustCapture = false;
        board = new int[Constants.ROWS][Constants.COLS];
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if ((row < 3 || row > 10) && (col == 0 || col == Constants.COLS - 1)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;
                else if ((row < 2 || row > 11) && (col == 1 || col == 6)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;
                else if ((row < 1 || row > 12) && (col == 2 || col == 5)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;
                else if (row == 4 && col > 1 && col < 6) board[row][col] = Constants.WHITE;
                else if (row == 5 && col > 2 && col < 5) board[row][col] = Constants.WHITE;
                else if (row == 8 && col > 2 && col < 5) board[row][col] = Constants.BLACK;
                else if (row == 9 && col > 1 && col < 6) board[row][col] = Constants.BLACK;               
            }
        }
    }
    
    //TESTING ONLY
    public Board(int i) {
        mustCapture = false;
        board = new int[Constants.ROWS][Constants.COLS];
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if ((row < 3 || row > 10) && (col == 0 || col == Constants.COLS - 1)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;
                else if ((row < 2 || row > 11) && (col == 1 || col == 6)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;
                else if ((row < 1 || row > 12) && (col == 2 || col == 5)) board[row][col] = row > Constants.ROWS / 2 ? -1 : -2;        
                //else if (row == 4 && col > 1 && col < 6) board[row][col] = Constants.WHITE;
                //else if (row == 5 && col > 2 && col < 5) board[row][col] = Constants.WHITE;
                //else if (row == 8 && col > 2 && col < 5) board[row][col] = Constants.BLACK;
                //else if (row == 9 && col > 1 && col < 6) board[row][col] = Constants.BLACK;               
            }
        }
        //board[8][4] = 0;
        //board[7][4] = Constants.BLACK;
        
        //board[4][3] = 0;
        //board[3][3] = Constants.WHITE;
        
        board[4][4] = Constants.WHITE;
        board[5][4] = Constants.WHITE;
        board[5][3] = Constants.WHITE;
        board[6][3] = Constants.WHITE;
        board[5][2] = Constants.WHITE;
        board[5][1] = Constants.WHITE;
        
        board[8][6] = Constants.BLACK;
        board[8][5] = Constants.BLACK;
        board[8][4] = Constants.BLACK;
        board[8][3] = Constants.BLACK;
        board[9][3] = Constants.BLACK;
        board[9][2] = Constants.BLACK;
        
    }
    
    //build board based on another board
    public Board(Board b) {
        mustCapture = b.mustCapture;
        board = new int[Constants.ROWS][Constants.COLS];
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                board[row][col] = b.board[row][col];
            }
        }
    }
    
    //determines if cor value is in range of the board
    public boolean isValid(Cor loc) {
        if (loc.x < 0 || loc.x > Constants.COLS - 1 || loc.y < 0 || loc.y > Constants.ROWS - 1 || board[loc.y][loc.x] <= -1) {
            return false;
        }
        return true;
    }
    
    //applies a given move to the board, if the move is legal
    public void doMove(Move m) {
        //check to see if move contains a valid piece
        if (!isValid(m.piece) || board[m.piece.y][m.piece.x] == 0) return;

        //record the color of the current piece
        int pColor = board[m.piece.y][m.piece.x];

        Cor newPos = new Cor(m.piece.add(m.dir));

        //check for moving out of bounds
        if (!isValid(newPos)) return;

        //if space is not occupied, move to it
        if (board[newPos.y][newPos.x] == 0) {
            board[m.piece.y][m.piece.x] = 0;
            board[newPos.y][newPos.x] = pColor;
        }

        //otherwise try moving one space beyond that point
        else {

            //record the piece in the adjacent tile
            int hopColor = board[newPos.y][newPos.x];

            //keep track of and increment the newPos
            Cor hopPos = new Cor(newPos);
            newPos = newPos.add(m.dir);

            //check for hopping out of bounds
            if (!isValid(newPos)) return;

            if (board[newPos.y][newPos.x] == 0) {
                board[m.piece.y][m.piece.x] = 0;
                board[newPos.y][newPos.x] = pColor;

                //check to see if it was a capturing move
                if (hopColor != pColor) {
                    board[hopPos.y][hopPos.x] = 0;
                }
            }
        }
        mustCapture = mustCapture(opposite(pColor));
    }
    
    
    //applies a given move to the board, including all chained parts
    //this is used for the absearch
    public void doFullMove(Move m) {
        //check to see if move contains a valid piece
        if (!isValid(m.piece) || board[m.piece.y][m.piece.x] == 0) return;

        //record the color of the current piece
        int pColor = board[m.piece.y][m.piece.x];

        Cor newPos = new Cor(m.piece.add(m.dir));

        //check for moving out of bounds
        if (!isValid(newPos)) return;

        //if space is not occupied, move to it
        if (board[newPos.y][newPos.x] == 0) {
            board[m.piece.y][m.piece.x] = 0;
            board[newPos.y][newPos.x] = pColor;
        }

        //otherwise try moving one space beyond that point
        else {

            //record the piece in the adjacent tile
            int hopColor = board[newPos.y][newPos.x];

            //keep track of and increment the newPos
            Cor hopPos = new Cor(newPos);
            newPos = newPos.add(m.dir);

            //check for hopping out of bounds
            if (!isValid(newPos)) return;

            if (board[newPos.y][newPos.x] == 0) {
                board[m.piece.y][m.piece.x] = 0;
                board[newPos.y][newPos.x] = pColor;

                //check to see if it was a capturing move
                if (hopColor != pColor) {
                    board[hopPos.y][hopPos.x] = 0;
                }
            }
        }
        // check if there are chain moves to do
        if (m.chain != null) {
            doFullMove(m.chain);
        }
        else {
            mustCapture = mustCapture(opposite(pColor));
        }
    }
    
    //calculates the legal moves a given piece can do, ignoring chain moves
    public LinkedList<Move> calcMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        //if a capture move exists, then you must capture
        if (mustCapture) {
            ans.addAll(calcCaptureMoves(piece));
            return ans;
        }
        //otherwise you can do a plain move or canter move
        else {
            ans.addAll(calcPlainMoves(piece));
            ans.addAll(calcCanterMoves(piece));
            return ans;
        }
    }    
    
    //calculates the legal moves including every possible chain combination
    public LinkedList<Move> calcAllMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        //if a capture move exists, then you must capture
        if (mustCapture) {
            LinkedList<Move> captureMoves = calcCaptureMoves(piece);
            
            //find all chain moves for each capture move
            for (Move m : captureMoves) {
                
                //create board with the current move
                Board b = new Board(this);
                b.doMove(m);
                
                //calculate destination tile
                Cor dest = m.piece;
                dest = dest.add(m.dir);
                dest = dest.add(m.dir);
                
                calcChainsRecurse(b, m, dest, new LinkedList<Cor>(), ans);                
            }
            return ans;
        }
        //otherwise you can do a plain move or canter move
        else {
            ans.addAll(calcPlainMoves(piece));
            
            LinkedList<Move> canterMoves = calcCanterMoves(piece);
            
            //find all chain moves for each capture move
            for (Move m : canterMoves) {
                
                //create board with the current move
                Board b = new Board(this);
                b.doMove(m);
                
                //calculate destination tile
                Cor dest = m.piece;
                dest = dest.add(m.dir);
                dest = dest.add(m.dir);
                
                calcChainsRecurse(b, m, dest, new LinkedList<>(Arrays.asList(piece)), ans);
            }
            return ans;
        }
    }
    
    //recursively store all chain moves in moves
    private void calcChainsRecurse(Board b, Move m, Cor piece, LinkedList<Cor> prev, LinkedList<Move> moves) {
        LinkedList<Move> chains = b.calcSingleChainMoves(piece, prev);
        
        //if there are no chain moves to do, add the current move in and return
        if (chains.isEmpty()) {
            moves.add(new Move(m));
            return;
        }
        
        LinkedList<Move> captureMoves = b.calcCaptureMoves(piece);
        
        //loop over all chain moves and recursively add them to moves
        for (Move c : chains) {
            //if the chain move is a stationary move, then just add the current move
            if (c.dir.equals(Constants.X)) {
                moves.add(new Move(m));
            }
            else {
                LinkedList<Cor> prevVals = new LinkedList<>(prev);
                //add move to prevList
                prevVals.add(new Cor(piece));
                //if this is a capture move, clear the prev list
                if (captureMoves.contains(c)) {
                    prevVals.clear();
                }
                
                //create board with the current move
                Board newboard = new Board(b);
                newboard.doMove(c);
                
                //calculate destination tile
                Cor dest = c.piece;
                dest = dest.add(c.dir);
                dest = dest.add(c.dir);
                
                Move move = new Move(m, c);
                
                //recurse
                calcChainsRecurse(newboard, move, dest, prevVals, moves);
            }
        }
    }
    
    //calculates the legal plain moves a piece can do
    public LinkedList<Move> calcPlainMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        Cor dest;
        
        //loop over all compass directions
        for (Cor dir : Constants.compass) {
            dest = piece.add(dir);
            
            //check if destination is valid and empty
            if (isValid(dest) && board[dest.y][dest.x] == 0) {
                ans.add(new Move(piece, dir));
            }
        }  
        return ans;
    }
    
    //calculates the legal canter moves a piece can do
    public LinkedList<Move> calcCanterMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        Cor cantered; //tile to be cantered over
        Cor dest;
        
        //loop over all compass directions
        for (Cor dir : Constants.compass) {
            cantered = piece.add(dir);
            dest = cantered.add(dir);
            
            //check if destination is valid and empty and check if cantered piece is the same color as the current piece
            if (isValid(dest) && board[dest.y][dest.x] == 0 && board[cantered.y][cantered.x] == board[piece.y][piece.x]) {
                ans.add(new Move(piece, dir));
            }
        }  
        return ans;
    }
    
    //return the opposite color of the piece, assuming BLACK or WHITE
    private int opposite(int color) {
        if (color == Constants.BLACK) return Constants.WHITE;
        if (color == Constants.WHITE) return Constants.BLACK;
        return -1;
    }
    
    //calculates the legal capture moves a piece can do
    public LinkedList<Move> calcCaptureMoves(Cor piece) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        Cor captured; //tile to be cantered over
        Cor dest;
        
        //loop over all compass directions
        for (Cor dir : Constants.compass) {
            captured = piece.add(dir);
            dest = captured.add(dir);
            
            //check if destination is valid and empty and check if cantered piece is the opposite color as the current piece
            if (isValid(dest) && board[dest.y][dest.x] == 0 && opposite(board[captured.y][captured.x]) == board[piece.y][piece.x]) {
                ans.add(new Move(piece, dir));
            }
        }  
        return ans;
    }
    
    //calculates the legal single hop chain moves based on previous tilse within the chain and whether or not there is a capture move
    public LinkedList<Move> calcSingleChainMoves(Cor piece, LinkedList<Cor> pastTiles) {
        LinkedList<Move> ans = new LinkedList<>();
        
        //check to see if piece is valid
        if (!isValid(piece) || board[piece.y][piece.x] == 0) return ans;
        
        //check if there are capture moves
        ans.addAll(calcCaptureMoves(piece));
        if (!ans.isEmpty()) {
            return ans;
        }
        //get the canter moves that go to tiles you haven't been to in this chain
        LinkedList<Move> canters = calcCanterMoves(piece);
        for (Move m : canters) {
            Cor dest = new Cor(piece);
            dest = dest.add(m.dir);
            dest = dest.add(m.dir);
            
            if (!pastTiles.contains(dest)) {
                ans.add(m);
            }
        }
        //if it's possible to canter you can choose to stop the chain at this point
        if (!ans.isEmpty()) {
            ans.add(new Move(piece, Constants.X));
        }
        return ans;
    }
    
    //determine if the current player musst make a capture move, assuming its their turn
    public boolean mustCapture(int color) {
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (board[row][col] == color && !calcCaptureMoves(new Cor(col, row)).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //checks to see if anyone won
    //return the color of the player who won, 0 if its a tie, or -1 if the game is not over yet
    public int checkVictory() {
        //check if black captured castle
        if (board[0][(Constants.COLS - 1) / 2] == Constants.BLACK && board[0][Constants.COLS - 1 - ((Constants.COLS - 1) / 2)] == Constants.BLACK) {
            return Constants.BLACK;
        }
        //check if white captured castle
        if (board[Constants.ROWS - 1][(Constants.COLS - 1) / 2]== Constants.WHITE && board[Constants.ROWS - 1][Constants.COLS - 1 - ((Constants.COLS - 1) / 2)] == Constants.WHITE) {
            return Constants.WHITE;
        }
        int white = 0;
        int black = 0;
        //count pieces for each color
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (board[row][col] == Constants.BLACK) {
                    ++black;
                }
                else if (board[row][col] == Constants.WHITE) {
                    ++white;
                }
                //if both players have more than 1 piece, the game has not ended yet
                if (white > 1 && black > 1) {
                    return -1;
                }
            }
        }
        //if both players have less than 2 pieces, it's a draw
        if (white < 2 && black < 2) {
            return 0;
        }
        else if (white < 1) {
            return Constants.BLACK;
        }
        else if (black < 1) {
            return Constants.WHITE;
        }
        return -1;
    }
    
    
    public void print() {
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                System.out.print(board[row][col] + " ");  
            }
            System.out.println();
        }
        System.out.println();
    }
    
    //accessors
    public int get(int row, int col) {
        if (!isValid(new Cor(col, row))){
            return row > Constants.ROWS / 2 ? -1 : -2;
        }
        return board[row][col];
    }
    
    public int get(Cor c) {
        if (!isValid(c)){
            return c.y > Constants.ROWS / 2 ? -1 : -2;
        }
        return board[c.y][c.x];
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Board other = (Board) obj;
        
        for (int row = 0; row < Constants.ROWS; ++row) {
            for (int col = 0; col < Constants.COLS; ++col) {
                if (this.board[row][col] != other.board[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Arrays.deepHashCode(this.board);
        return hash;
    }
    
    private int[][] board; //array representation of the board
    private boolean mustCapture; //determines if the pieces must make a capturing move when calculating valid moves
}
