/**
 *
 * @author 
 * Reggie Barnett
 * Michael Moore
 * David Nard
 * Graham Taylor
 * Last Updated 4/19/2015, NetBeans IDE 8.0.2
 * CMSC 495
 * Phase 1 Source
 * 
 * Week 5
 * Spring OL1 2014
 */

package tetris;

import java.awt.Color;
import java.util.Random;

public class Piece 
{
    public enum gamePieces { emptyPiece, JPiece, LPiece, TPiece, ZPiece, SPiece, OPiece, IPiece };
    
    public static final int pieceMaxWidth = 4;

    protected int[][][] matrixArray;
    protected int[][][] rotateMatrix;
    
    private gamePieces shape;
    private Color color;
   
    public Piece() 
    {        
        setPiece(gamePieces.emptyPiece, Square.colors[0]);        
    }
    
    public Piece(gamePieces newPiece)
    {
        setPiece(newPiece, Square.colors[newPiece.ordinal()]);
    }

    public void setPiece(gamePieces newShape, Color newColor) 
    {
        // Set initial x,y for each Square
        // (10 / 2) - (4 / 2) = 3
        int leftX = (PlayField.playFieldWidth / 2) - (pieceMaxWidth / 2);
                
        // Every piece holds the data to be converted to any other piece. 
        //    The 1st dimension of the array matches the enum gamePieces ordinal value
        //    { emptyPiece, JPiece, LPiece, TPiece, ZPiece, SPiece, OPiece, IPiece };

        matrixArray = new int[][][] 
        {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
            { { leftX, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 }, { leftX, PlayField.playFieldHeight - 1 } },
            { { leftX, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 1 } },
            { { leftX, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 1 } },

            { { leftX, PlayField.playFieldHeight - 1 }, { leftX + 1, PlayField.playFieldHeight - 1 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 } },           
            { { leftX + 2, PlayField.playFieldHeight - 1 }, { leftX + 1, PlayField.playFieldHeight - 1 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX, PlayField.playFieldHeight - 2 } },            
            
            { { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 1 }, { leftX + 2, PlayField.playFieldHeight - 1 } },
            { { leftX + 3, PlayField.playFieldHeight - 2 }, { leftX + 2, PlayField.playFieldHeight - 2 }, { leftX + 1, PlayField.playFieldHeight - 2 }, { leftX, PlayField.playFieldHeight - 2 } }
        };

        shape = newShape;
        color = newColor;
        
        //[x][1][1] is always false, it is always occupied and will be used as a temp space for swapping positions
        //    { emptyPiece, JPiece, LPiece, TPiece, ZPiece, SPiece, OPiece, IPiece };
        rotateMatrix = new int[][][]
        {
            { { 0, 0 },  { 0, 0 }, { 0, 0 },  { 0, 0 } },
            { { -1, 0 }, { 0, 0 }, { 1, 0 },  { -1, 1 } },                      
            { { -1, 0 }, { 0, 0 }, { 1, 0 },  { 1, 1 } },                        
            { { -1, 0 }, { 0, 0 }, { 1, 0 },  { 0, 1 } },    
            
            { { -1, 0 }, { 0, 0 }, { 0, -1 }, { 1, -1 } },            
            { { 1, 0 },  { 0, 0 }, { 0, -1 }, { -1, -1 } },    
            
            { { 1, 0 },  { 0, 0 }, { 0, 1 },  { 1, 1 } },            
            { { 1, 0 },  { 0, 0 }, { -1, 0 }, { -2, 0 } }            
        };
    }
    
    public gamePieces getPiece()
    {
        return shape;
    }
    
    public void createPiece()
    {
        // Generate a new random piece with correct color
        Random randomInt = new Random();
        int randomPiece = Math.abs(randomInt.nextInt()) % 7 + 1;
        gamePieces[] pieceArray = gamePieces.values(); 
        setPiece(pieceArray[randomPiece], Square.colors[randomPiece]);
    }    
    
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void rotate(boolean rotateLeft)
    {   
        int swapX;
        int swapY;
        int pieceCounter = shape.ordinal();        
        
        if (pieceCounter > 0 && pieceCounter < 4) 
        {
            // If the pivot point is next to either wall, do not allow
            if ((matrixArray[pieceCounter][1][0] > 0) && (matrixArray[pieceCounter][1][0] < PlayField.playFieldWidth - 1) &&
                (matrixArray[pieceCounter][1][1] > 0) && (matrixArray[pieceCounter][1][1] < PlayField.playFieldHeight - 1))
            {
                int[][] checkRotateMatrix = new int[4][2];
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];

                    if (rotateLeft)
                    {
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] + (-swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] + (swapX);
                    }
                    else
                    {
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] + (swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] + (-swapX);                        
                    }
                    if (PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isOccupied() && 
                        !PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isActive())
                    {
                        System.out.println("RETURN");
                        return;
                    }
                    else
                    {
                        System.out.println(checkRotateMatrix[outerCounter][0] + " " + checkRotateMatrix[outerCounter][1]);
                    }
                }    
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];
                    
                    if (rotateLeft)
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = -swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = swapX ;
                    }
                    else
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = -swapX ;                       
                    }
                    matrixArray[pieceCounter][outerCounter][0] = (checkRotateMatrix[outerCounter][0]);
                    matrixArray[pieceCounter][outerCounter][1] = (checkRotateMatrix[outerCounter][1]);

                    if (matrixArray[pieceCounter][outerCounter][0] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldWidth)
                    {
                        System.out.println("Side FALSE");
                    }
                    if (matrixArray[pieceCounter][outerCounter][1] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldHeight)
                    {
                        System.out.println("Top FALSE");
                    }
                }
            }
        }    
        else if (pieceCounter > 3 && pieceCounter < 6)
        {
            boolean goBack = false;
            // If the pivot point is next to either wall, do not allow
            if ((matrixArray[pieceCounter][1][0] > 0) && (matrixArray[pieceCounter][1][0] < PlayField.playFieldWidth - 1) &&
                (matrixArray[pieceCounter][1][1] > 0) && (matrixArray[pieceCounter][1][1] < PlayField.playFieldHeight - 1))
            {
                int[][] checkRotateMatrix = new int[4][2];
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];
                    
                    if (((pieceCounter == 4) && (rotateMatrix[pieceCounter][0][0] == -1) && (rotateMatrix[pieceCounter][0][1] == 0)) ||
                        ((pieceCounter == 5) && (rotateMatrix[pieceCounter][0][0] == 1) && (rotateMatrix[pieceCounter][0][1] == 0)))
                    {
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] + (-swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] + (swapX);
                    }
                    else
                    {
                        goBack = true;
                        System.out.println("BACK");
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] - (-swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] - (swapX);                            
                    }
                    if (PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isOccupied() && 
                        !PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isActive())
                    {
                        System.out.println("RETURN");
                        return;
                    }
                    else
                    {
                        System.out.println(checkRotateMatrix[outerCounter][0] + " " + checkRotateMatrix[outerCounter][1]);
                    }
                }                            
                
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];
                    
                    if (!goBack)
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = -swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = swapX ;
                    }
                    else
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = -swapX ;
                        
                    }
                    matrixArray[pieceCounter][outerCounter][0] = (checkRotateMatrix[outerCounter][0]);
                    matrixArray[pieceCounter][outerCounter][1] = (checkRotateMatrix[outerCounter][1]);

                    if (matrixArray[pieceCounter][outerCounter][0] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldWidth)
                    {
                        System.out.println("Side FALSE");
                    }
                    if (matrixArray[pieceCounter][outerCounter][1] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldHeight)
                    {
                        System.out.println("Top FALSE");
                    }
                }
            }   
        }
        else if (pieceCounter == 7)
        {
            boolean goBack = false;
            // If the pivot point is next to either wall, do not allow
            if ((matrixArray[pieceCounter][1][0] > 0) && (matrixArray[pieceCounter][1][0] < PlayField.playFieldWidth - 1) &&
                (matrixArray[pieceCounter][1][1] > 0) && (matrixArray[pieceCounter][1][1] < PlayField.playFieldHeight - 1))
            {
                int[][] checkRotateMatrix = new int[4][2];
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];
                    
                    if ((rotateMatrix[pieceCounter][0][0] == 1) && (rotateMatrix[pieceCounter][0][1] == 0))
                    {
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] + (-swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] + (swapX);
                    }
                    else
                    {
                        goBack = true;
                        System.out.println("BACK");
                        checkRotateMatrix[outerCounter][0] = matrixArray[pieceCounter][1][0] - (-swapY);
                        checkRotateMatrix[outerCounter][1] = matrixArray[pieceCounter][1][1] - (swapX);                            
                    }
                    if ((PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isOccupied() && 
                        !PlayField.playField[checkRotateMatrix[outerCounter][0]][checkRotateMatrix[outerCounter][1]].isActive()) || 
                        ((rotateMatrix[pieceCounter][0][0] == 0) && (matrixArray[pieceCounter][outerCounter][0] < 2)))
                    {
                        System.out.println("RETURN");
                        return;
                    }
                    else
                    {
                        System.out.println(checkRotateMatrix[outerCounter][0] + " " + checkRotateMatrix[outerCounter][1]);
                    }
                }                            
                
                
                for (int outerCounter = 0; outerCounter < 4; outerCounter++)
                {
                    swapX = rotateMatrix[pieceCounter][outerCounter][0];
                    swapY = rotateMatrix[pieceCounter][outerCounter][1];
                    
                    if (!goBack)
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = -swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = swapX ;
                    }
                    else
                    {
                        rotateMatrix[pieceCounter][outerCounter][0] = swapY;
                        rotateMatrix[pieceCounter][outerCounter][1] = -swapX ;
                        
                    }
                    matrixArray[pieceCounter][outerCounter][0] = (checkRotateMatrix[outerCounter][0]);
                    matrixArray[pieceCounter][outerCounter][1] = (checkRotateMatrix[outerCounter][1]);

                    if (matrixArray[pieceCounter][outerCounter][0] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldWidth)
                    {
                        System.out.println("Side FALSE");
                    }
                    if (matrixArray[pieceCounter][outerCounter][1] < 0 || matrixArray[pieceCounter][outerCounter][0] >= PlayField.playFieldHeight)
                    {
                        System.out.println("Top FALSE");
                    }
                }
            }               
        }
    }
}