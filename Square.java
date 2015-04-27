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

public class Square 
{   
    // NONE,  JShape, LShape, ZShape, SShape, TShape, OShape, IShape
    // black, blue,   orange, red,    green,  purple, yellow, cyan
    public static Color colors[] = {       
        new Color(0, 0, 0), new Color(0, 0, 255), 
        new Color(255, 140, 0), new Color(255, 0, 0), 
        new Color(0, 128, 0), new Color(128, 0, 128), 
        new Color(255, 255, 0), new Color(0, 255, 255) };
    
    private Color color;
    
    // Active means the Square is part of the activly moving piece
    private boolean active;
    private boolean occupied;
    
    public Square()
    {
        color = colors[0];
        occupied = false;
        active = false;
    }
    
    public Square(Color newColor, boolean newActive)
    {
        // Any Square that has a non-black color is occupied and only the default constructer creates black squares
        color = newColor;
        active = newActive;
        occupied = true;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
    public boolean isOccupied()
    {
        return occupied;
    }
    
    public void setOccupied(boolean newOccupied)
    {
        occupied = newOccupied;
    }            
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive(boolean newActive)
    {
        active = newActive;
    }
}