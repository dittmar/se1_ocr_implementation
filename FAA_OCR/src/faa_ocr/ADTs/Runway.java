/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package faa_ocr.ADTs;
import java.util.*;

/**
 *
 * @author g_ric_000
 */
public class Runway extends Path 
{
    private int elevation;
    private float heading;
    private Node threshold;
    
    public Runway(int elevation, float heading, String name)
    {
        this.elevation = elevation;
        this.heading = heading;
        this.name = name;
        paths = new ArrayList<Node>();
        intersections = new ArrayList<Node>();
    }
    
    public int getElevation()
    {
        return elevation;
    }
    
    public float getHeading()
    {
        return heading;
    }
    
    public Node getThreshold()
    {
        return threshold;
    }
    
    public void setThreshold(Node threshold)
    {
        this.threshold = threshold;
    }
    
    public boolean hasThreshold()
    {
        return threshold == null;
    }
    
    public String toString()
    {
        return heading;
    }
    
}
