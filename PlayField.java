/**
 *
 * @author 
 * Reggie Barnett
 * Michael Moore
 * David Nard
 * Graham Taylor
 * Last Updated 4/26/2015, NetBeans IDE 8.0.2
 * CMSC 495
 * Phase 2 Source
 * 
 * Week 6
 * Spring OL1 2014
 */

package tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import static tetris.Piece.pieceMaxWidth;
import static tetris.Tetris.GameState.*;

public class PlayField extends JPanel implements ActionListener
{
    public static final int playFieldWidth = 10;
    public static final int playFieldHeight = 22;
    public static final int nextFieldWidth = 6;
    public static final int nextFieldHeight = 4;

    protected Piece currentPiece;
    protected Piece nextPiece;
    
    private Timer timer;
    private boolean hasLanded = false;
    private boolean isPaused = false;
    private boolean hasStarted = false;
    private boolean gameOver = false;
    
    private int level = 0;    
    private int score = 0;
    private int linesCleared = 0;
    private int timerDelay = 800;
    
    protected static Square[][] playField;    
    protected static Square[][] nextField;
    
    private JPanel menuPanel;
    private JPanel infoPanel;
    private JButton newGameButton;
    private JButton viewHighScoresButton;
    private JButton quitButton;
    private JButton okButton;
    
    private JLabel linesLabel;
    private JLabel highScoreLabel;
    
    private Font titleFont;
    private Font scoreFont;
    
    public PlayField() 
    {
        titleFont = new Font("Courier", Font.BOLD, 48);
        scoreFont = new Font("Courier", Font.BOLD, 24);
        
        newGameButton = new JButton("New Game");
        viewHighScoresButton = new JButton("View High Scores");
        quitButton = new JButton("Quit");
        
        menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.add(newGameButton);
        menuPanel.add(viewHighScoresButton);
        menuPanel.add(quitButton);
        add(menuPanel);
        
        highScoreLabel = new JLabel("HIGH SCORES!!!!!!!!");
        add(highScoreLabel);
        highScoreLabel.setVisible(false);
        
        okButton = new JButton("OK");
        add(okButton);
        okButton.setVisible(false);
        
        linesLabel = new JLabel("Lines: " + linesCleared);
        linesLabel.setFont(scoreFont);

        infoPanel = new JPanel();
        infoPanel.add(linesLabel);
        infoPanel.setVisible(false);
        infoPanel.setBackground(Color.WHITE);
        add(infoPanel);
        
        ButtonListener buttonListener = new ButtonListener();
        newGameButton.addActionListener(buttonListener);
        viewHighScoresButton.addActionListener(buttonListener);
        quitButton.addActionListener(buttonListener);
        okButton.addActionListener(buttonListener);
        
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new KeyPresses());
    }    
    
    class ButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event) 
        {
            if (event.getSource() == newGameButton)
            {
                menuPanel.setVisible(false);        
                Tetris.gameState = gamePlaying;
                infoPanel.setVisible(true);
                gameOver = false;
                hasLanded = false;
                isPaused = false;
                level = 0;
                linesCleared = 0;
                score = 0;
                start();
            }
            else if (event.getSource() == viewHighScoresButton)
            {
                menuPanel.setVisible(false); 
                highScoreLabel.setVisible(true);
                okButton.setVisible(true);
                Tetris.gameState = highScores;
            }
            else if (event.getSource() == okButton)
            {
                menuPanel.setVisible(true); 
                highScoreLabel.setVisible(false);
                okButton.setVisible(false);
                Tetris.gameState = mainMenu;
            }
            else
            {
                System.exit(0);
            }
        }
    }
       
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        repaint();
    }
    
    public void createNewPiece()
    {
        // Next piece becomes current, generate new next, place new current on board, start timer
        timer.stop();
        removeNextPieceFromBoard();
        currentPiece = nextPiece;
        nextPiece = new Piece();
        nextPiece.createPiece();
        setPieceToBoard(true);
        setNextPieceToBoard();
        timer.start();        
    }

    public void hidePlayField()
    {
        for (int width = 0; width < playFieldWidth; width++)
        {
            for (int height = 0; height < playFieldHeight; height++)
            {
                playField[width][height].setActive(false);
                playField[width][height].setColor(Color.WHITE);
                playField[width][height].setOccupied(false);
            }
        }
    }
    
    @Override
    public void paint(Graphics g)
    { 
        // If the game is paused or viewing high scores, do not paint the playfield
        if ((Tetris.gameState == gamePlaying) || (gameOver == true))
        {
            // Color each Square with it's color
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Paint the score area
            Color color = Square.colors[0];
           
            int statsStringX = 600;
            int statsStringY = 70;

            g2d.setFont(scoreFont);
            g2d.drawString("TOP", statsStringX, statsStringY);
            // Change this to top high score
            //g2d.drawString(String.valueOf(score), statsStringX, statsStringY + 25);
            g2d.drawString("SCORE", statsStringX, statsStringY + 60);
            g2d.drawString(String.valueOf(score), statsStringX, statsStringY + 85);
            
            // Paint the next piece area
            statsStringX = 600;
            statsStringY = 290;
            
            g2d.setFont(titleFont);
            g2d.drawString("NEXT", statsStringX, statsStringY);
            
            // Paint the level area
            g2d.setFont(scoreFont);
            g2d.drawString("LEVEL", statsStringX, statsStringY + 200);
            g2d.drawString(String.valueOf(level), statsStringX + 35, statsStringY + 225);
            
            int squareDimension = 30;
            // Center-ish the playfield
            int x = (800 - (squareDimension * playFieldWidth)) / 2;
            int y = (750 - (squareDimension * playFieldHeight)) / 2;

            int nextX = 575;
            int nextY = 300;
            
            //color = Square.colors[0];

            for (int height = playFieldHeight - 1; height >= 0; height--) 
            {
                for (int width = 0; width < playFieldWidth; width++) 
                {
                    
                    g2d.setColor(color);
                    g2d.fillRect(x + (width * squareDimension), y, squareDimension, squareDimension);
                    g2d.drawRect(x + (width * squareDimension), y, squareDimension, squareDimension);
                }

                y += squareDimension;
            }  
            
            for (int height = nextFieldHeight - 1; height >= 0; height--)
            {
                for (int width = 0; width < nextFieldWidth; width++)
                {
                    g2d.setColor(color);
                    g2d.fillRect(nextX + (width * squareDimension), nextY, squareDimension, squareDimension);
                    g2d.drawRect(nextX + (width * squareDimension), nextY, squareDimension, squareDimension);                    
                }                
                nextY += squareDimension;
            }
            
            x = (800 - (squareDimension * playFieldWidth)) / 2;
            y = (750 - (squareDimension * playFieldHeight)) / 2;
            for (int height = playFieldHeight - 1; height >= 0; height--) 
            {
                for (int width = 0; width < playFieldWidth; width++) 
                {
                    if (playField[width][height].isOccupied())
                    {
                        // Draw a solid square, then a black outline so we can see the seperate squares
                        color = playField[width][height].getColor();
                        g2d.setColor(color);  
                        g2d.fillRect(x + (width * squareDimension), y, squareDimension, squareDimension);
                        g2d.setColor(Square.colors[0]);
                        g2d.drawRect(x + (width * squareDimension), y, squareDimension, squareDimension);
                    }
                }

                y += squareDimension;
            } 

            nextX = 575;
            nextY = 300;
            
            // Next piece area
            for (int height = nextFieldHeight - 1; height >= 0; height--)
            {
                for (int width = 0; width < nextFieldWidth; width++)
                {
                    if (nextField[width][height].isOccupied())
                    {
                        // Draw a solid square, then a black outline so we can see the seperate squares
                        color = nextField[width][height].getColor();
                        g2d.setColor(color);  
                        g2d.fillRect(nextX + (width * squareDimension), nextY, squareDimension, squareDimension);
                        g2d.setColor(Square.colors[0]);
                        g2d.drawRect(nextX + (width * squareDimension), nextY, squareDimension, squareDimension);
                    }
                }
                
                nextY += squareDimension;
            }                        
        }
        else
        {
            super.paint(g);
        }
    }    
    
    public void setPieceToBoard(boolean stillActive)
    {
        // The Squares the currentPiece occupy need to be marked with the Square 
        //    attributes. If stillActive is false, the piece has landed
        int pieceCounter = currentPiece.getPiece().ordinal();
        
        int x;
        int y;
        
        for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
        {
            // Get the current x,y of the piece
            x = currentPiece.matrixArray[pieceCounter][outerCounter][0];
            y = currentPiece.matrixArray[pieceCounter][outerCounter][1];
            
            // Set the attributes of the x,y
            playField[x][y].setActive(stillActive);
            playField[x][y].setColor(currentPiece.getColor());
            playField[x][y].setOccupied(true);
        }        
    }
    
    public void removePieceFromBoard()
    {
        // The piece is active, reset the squares that it occupies so it can be
        //    reset on its new position
        
        int pieceCounter = currentPiece.getPiece().ordinal();
        
        int x;
        int y;
        for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
        {
            // Get the x,y of the squares
            x = currentPiece.matrixArray[pieceCounter][outerCounter][0];
            y = currentPiece.matrixArray[pieceCounter][outerCounter][1];
            
            // Reset thoses squares
            playField[x][y].setActive(false);
            playField[x][y].setColor(Color.BLACK);
            playField[x][y].setOccupied(false);
        }             
    }

    public void setNextPieceToBoard()
    {
        // The Squares the currentPiece occupy need to be marked with the Square 
        //    attributes. If stillActive is false, the piece has landed
        int pieceCounter = nextPiece.getPiece().ordinal();
        
        int x;
        int y;
        
        for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
        {
            if (((pieceCounter > 0) && (pieceCounter < 4)) || (pieceCounter == 6))
            {
                // Get the current x,y of the piece
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 2;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 1;
            }
            else if ((pieceCounter > 3) && (pieceCounter < 6))
            {
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 2;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 2;
            }
            else
            {
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 3;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 2;
            }

            // Set the attributes of the x,y
            nextField[x][y].setColor(nextPiece.getColor());
            nextField[x][y].setOccupied(true);
        }        
    }
    
    public void removeNextPieceFromBoard()
    {
        // The piece is active, reset the squares that it occupies so it can be
        //    reset on its new position
        
        int pieceCounter = nextPiece.getPiece().ordinal();
        
        int x;
        int y;
        for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
        {
            if (((pieceCounter > 0) && (pieceCounter < 4)) || (pieceCounter == 6))
            {
                // Get the current x,y of the piece
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 2;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 1;
            }
            else if ((pieceCounter > 3) && (pieceCounter < 6))
            {
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 2;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 2;
            }
            else
            {
                x = nextPiece.rotateMatrix[pieceCounter][outerCounter][0] + 3;
                y = nextPiece.rotateMatrix[pieceCounter][outerCounter][1] + 2;
            }
            
            // Reset thoses squares
            nextField[x][y].setColor(Color.BLACK);
            nextField[x][y].setOccupied(false);
        }             
    }    
    
    public void start()
    {
        Tetris.gameState = gamePlaying;
        
        // Initalize the playField
        playField = new Square[playFieldWidth][playFieldHeight];       
        for (int width = 0; width < playFieldWidth; width++) 
        {
            for (int height = 0; height < playFieldHeight; height++) 
            {
                playField[width][height] = new Square();
            }
        }
        
        nextField = new Square[nextFieldWidth][nextFieldHeight];
        
        for (int width = 0; width < nextFieldWidth; width++)
        {
            for (int height = 0; height < nextFieldHeight; height++)
            {
                nextField[width][height] = new Square();
            }
        }
        
        currentPiece = new Piece();
        currentPiece.createPiece();
        nextPiece = new Piece();
        nextPiece.createPiece();        
        setPieceToBoard(true);
        setNextPieceToBoard();
        
        timer = new Timer(timerDelay, this);
        timer.start();

        repaint();
    }
    
    public void pauseGame()
    {
        // Toggles isPaused on proper keypress. While paused, the play field is hidden (NO CHEATING!)
        
        // Get the top-level JFrame so we can use .setTitle();
        Component c = this.getParent();
        
        // Keep going until we reach the JFrame
        while (c.getParent() != null)
        {
            c = c.getParent();
        }

        // Cast the JFrame so we can use it
        JFrame topFrame = (JFrame)c;
        
        if (!isPaused)
        {
            Tetris.gameState = gamePaused;
            isPaused = true;
            topFrame.setTitle("******PAUSED******");
            timer.removeActionListener(this);
        }
        else
        {
            Tetris.gameState = gamePlaying;
            isPaused = false;
            topFrame.setTitle("CMSC495 Tetris Group -- Spring 2015");            
            timer.addActionListener(this);
        }        
    }

    public void moveDown(boolean hardDrop)
    {
        // Check to see if any non-active but occpuied space is blocking the piece
        //    If not, move it down.
        
        boolean canMoveDown = true;
        
        do
        {
            int pieceCounter = currentPiece.getPiece().ordinal();

            int x = 0;
            int y = 0;
            for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
            {
                // Get the x,y of every Square in the piece
                x = currentPiece.matrixArray[pieceCounter][outerCounter][0];
                y = currentPiece.matrixArray[pieceCounter][outerCounter][1];
                
                // If the piece has a Square on the bottom row, it can not move down
                if (y == 0)
                {
                    canMoveDown = false;
                }
                else
                {
                    // If a Square being checked is not part of the piece, and is occupied, it is blocked
                    if ((playField[x][y - 1].isActive() == false) && (playField[x][y - 1].isOccupied()))
                    {
                        canMoveDown = false;
                    }
                }
            }

            // If it can move down, move it
            if (canMoveDown)
            {
                // Reset the occupied Squares' attributes
                removePieceFromBoard();
                
                // Change the location of the squares
                for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
                {
                    currentPiece.matrixArray[pieceCounter][outerCounter][1]--;
                }
                
                // Update the Squares on the new positions
                setPieceToBoard(true);
            }
            else
            {
                // It can not move any further and has landed due to the player moving it too far down
                hasLanded = true;
                setPieceToBoard(false);
                timer.stop();
                
                clearLines();
                
                // Has a Square landed in the deployment zone? If so, game over
                if (y >= (playFieldHeight - 2))
                {
                    gameOver = true;
                    hidePlayField();
                    removeNextPieceFromBoard();
                    infoPanel.setVisible(false);
                    menuPanel.setVisible(false); 
                    highScoreLabel.setVisible(true);
                    okButton.setVisible(true);
                    Tetris.gameState = highScores;    
                    timer.stop();
                }
                else
                {
                    createNewPiece();
                }
            }
          // If hardDrop == true, keep dropping until landed
        } while (hasLanded == false && hardDrop);
        
        // Reset for next piece
        hasLanded = false;        
    }
    
    public void moveSide(int direction)
    {
        // Check to see if the piece can move left (direction < 0) or if the piece
        //     can move right (direction > 0) by comparing it to adjacent squares
        //     if they are not active but occupied
        
        boolean canMoveSide = true;
        
        int pieceCounter = currentPiece.getPiece().ordinal();
        
        int x = 0;
        int y = 0;
        for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
        {
            // Get x,y
            x = currentPiece.matrixArray[pieceCounter][outerCounter][0];
            y = currentPiece.matrixArray[pieceCounter][outerCounter][1];

            // If moving left and x = 0, it is against the left wall and cannot move
            // If moving right and x = width - 1, it is against the right wall and cannot move
            if (((x == 0) && (direction < 0)) || ((x == (playFieldWidth - 1))) && (direction > 0))
            {
                canMoveSide = false;
            }
            else
            {
                // Else, if the adjcent Squares are not active but occupied, it can not move
                if ((playField[x + direction][y].isActive() == false) && (playField[x + direction][y].isOccupied()))
                {
                    canMoveSide = false;
                }
            }
        }
        
        // If it can move, remove it from the board, reset those Squares, update the position, reset the piece
        if (canMoveSide)
        {
            removePieceFromBoard();
            for (int outerCounter = 0; outerCounter < pieceMaxWidth; outerCounter++)
            {
                currentPiece.matrixArray[pieceCounter][outerCounter][0] += direction;
            }
            setPieceToBoard(true);
        }
    }
    
    public void rotate(boolean rotateLeft)
    {
        //System.out.println(currentPiece.getPiece().ordinal());
        if (currentPiece.getPiece().ordinal() != 6)
        {
            {
                removePieceFromBoard();
                currentPiece.rotate(rotateLeft);
                setPieceToBoard(true);
            }
        }
    }
    
    public void clearLines()
    {
        // Check each Y and X for occupied squares on the playfield
        //If all square are occupied then clear the playfield
        //System.out.println("Inside clear Lines");
        boolean isCleared = false;
        
        int multiplier = 0;//multiplier variable applied to clearing a line 
        int x;
        int y;
        int x1;
        int y1;
        
        for ( y = 0; y < playFieldHeight; y++  )
        {
            //check if each horizontal is occupied
            // System.out.println("Inside the Loop + Y = " + y);
            isCleared = true;
            for ( x = 0; x <playFieldWidth; x++)
            {
                //if any x is not occupied then move onto to the next y line
                if(   playField[x][y].isOccupied() == false)
                {
                   //  System.out.println("Inside the if: check for is cleared");
                   isCleared = false;
                }

                //If all squares on the line are occupied up the multiplier which is used to calculate score 
                // and used for lines cleared
                if( isCleared == true && x == playFieldWidth-1)
                {
                    multiplier = multiplier + 1;
                    // System.out.println("Resetting the boxes");
                    //reset all the squares on the line to default color                      
                    for(x1 = 0; x1 < playFieldWidth ; x1 ++)
                    {
                       playField[x1][y].setOccupied(false);
                       playField[x1][y].setColor(Color.BLACK);
                    }
                    //reset all the square on the line above the line cleared to drop the pieces                      
                    for(y1 = y+1; y1 < playFieldHeight; y1++)
                    {

                        for(x1 = 0; x1 < playFieldWidth ; x1++)
                        {
                        // System.out.println("Get ready were inside at Y:" + y1 + "X1: " + x1);
                        playField[x1][y1-1].setOccupied(playField[x1][y1].isOccupied());
                        playField[x1][y1-1].setColor(playField[x1][y1].getColor());
                        playField[x1][y1].setOccupied(false);
                        playField[x1][y1].setColor(Color.BLACK);
                        }                        
                    }
                    y = y - 1;
                }
            }
        }
        
        //System.out.println("Outside the loop");
        //Update score/ level when multiplier is >0
        if(multiplier > 0 )
        {
            updateGameStats(multiplier);
        }
    }
    
    public void updateGameStats(int multiplier)
    {
        int newPoints = 0; // used to get the amount of points that is being added to curren score
        int oldLevel = level;
        
        //Update the # of lineCleared
        linesCleared = linesCleared + multiplier;
        
        //Update the level if applicable
       
        level = Math.floorDiv(linesCleared, 100);
        
        if (level < 9)
        {
            if (level > oldLevel)
            {
                timerDelay -= 83;
            }
        }
        else if (level == 9)
        {
            timerDelay -= 166;            
        }
        
        else if (level >= 10 && level <= 12)
        {
            timerDelay -= 83;
        }
        else if (level >= 13 && level <= 15)
        {
            timerDelay -= 83;
        }
        else if (level >= 16 && level <= 18)
        {
            timerDelay -= 83;
        }
        else if (level >= 19 && level <= 28)
        {
            timerDelay -= 83;
        }
        else
        {
            timerDelay = 83;
        }

        timer.setDelay(timerDelay);
        // System.out.println("timer =  " + timer.getDelay());
        
        //Update the score
        switch(multiplier)
        {
            case 1: newPoints = 40 * ( level + 1); break;
            case 2: newPoints = 100 * ( level + 1); break;  
            case 3: newPoints = 300 * ( level + 1); break;  
            case 4: newPoints = 1200 * ( level + 1); break;  
        }

        //Testing scoring logic
        score = score + newPoints;
        //System.out.println("Your score is " + score);
        //System.out.println("Your # of lines cleared is " + linesCleared);
        //System.out.println("Your level is " + level);
        //Update what is displayed
        linesLabel.setText("Lines: " + linesCleared);
        //levelLabel.setText("Level: " + level);
        //scoreLabel.setText("Score: " + score);
    }
       
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (!gameOver)
        {
            if (hasLanded)
            {
                // If the piece has landed, lock it on the game board as active = false
                setPieceToBoard(false);
                
                clearLines();
                
                // Check for a gameOver condition -- Might be redudant
                for (int y = playFieldHeight - 2; y < playFieldHeight; y++)
                {
                    for (int x = 0; x < playFieldWidth; x++)
                    {
                        if (playField[x][y].isOccupied())
                        {
                            gameOver = true;
                            timer.stop();
                        }
                    }
                }
                if (!gameOver)
                {
                    hasLanded = false;
                    createNewPiece();
                }            
            }
            else
            {
                if (!isPaused)
                {
                    moveDown(false);
                }
            }
        }
        else
        {
            hidePlayField();
            Tetris.gameState = highScores;
        }
    }
    
    class KeyPresses extends KeyAdapter 
    {       
        // Class for handling key presses
        @Override
        public void keyPressed(KeyEvent e) 
        {
            int keyPress = e.getKeyCode();

            if (keyPress == 'p' || keyPress == 'P') 
            {
                pauseGame();
            }

            if (!isPaused && Tetris.gameState == gamePlaying)
            {
                switch (keyPress) 
                {
                    case 'z':
                    case 'Z':
                        rotate(true);
                        break;
                    case 'x':
                    case 'X':
                        rotate(false);
                        break;
                    case KeyEvent.VK_LEFT: 
                        moveSide(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveSide(1);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown(false);
                        break;
                    case KeyEvent.VK_SPACE:
                        moveDown(true);
                        break;
                }
            }
        }
    }    
}