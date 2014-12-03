/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package faa_ocr.ADTs;

/**
 *
 * @author Kevin Dittmar
 */
public class Slope
{
    private int x_component;
    private int y_component;
    
    public Slope(int y_component, int x_component)
    {
        this.y_component = y_component;
        this.x_component = x_component;
    }
    
    /**
     * Invert the slope, which means that the y component will become the x
     * component and vice-versa.  Y should always be positive because we will
     * always be traversing down the runway (in the positive direction), since
     * we will always be starting at the top.  X will flip signs.
     */
    public void invertSlope()
    {
        int temp = y_component;
        y_component = Math.abs(x_component);
        x_component = -1 * temp;
    }
    
    /**
     * Get the x component of the slope.
     * @return the x component of the slope. 
     */
    public int getX()
    {
        return x_component;
    }
    
    /**
     * Get the y component of the slope.
     * @return the y component of the slope.
     */
    public int getY()
    {
        return y_component;
    }
}