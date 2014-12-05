/**
 * Purpose: Software Engineering I: FAA OCR Project 
 * Status: Complete and thoroughly tested
 * Last update: 12/02/14
 * Submitted: 
 * Comment: Airport ADT
 * @author: Greg Richards, Kevin Dittmar
 * @version: 2014.11.20
 * 
 */

package faa_ocr.ADTs;
import faa_ocr.image_parser.PDFToImage;
import faa_ocr.image_parser.Point;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * The Airport class is an abstract data type that represents the entire Airport
 * diagram .pdf file.  It contains all of the textual identification information that
 * makes each airport diagram unique, as well as all of the nodes that are to be
 * mapped onto the .xml file.
 */
public class Airport {
    private final String pdf_file_path;
    private String name;
    private String location;
    private String abbreviation;
    private float variation;
    private ArrayList<Path> paths;
    private int pixels_per_degree_lat;
    private int pixels_per_degree_long;
    private int latitude_offset;
    private int longitude_offset;
    public Airport (String pdf_file_path)
    {
        this.pdf_file_path = pdf_file_path;
        
        /* Set the scales for pixel coordinates to latitude and longitude
         * coordinates as well as the necessary offsets from the upper left
         * corner of the diagram.
         */
        findPixelConversionScales();
    }
    
    /**
     * Get the file path of the airport diagram PDF file.
     * @return the airport diagram file path as a String.
     */
    public String getFilePath()
    {
        return pdf_file_path;
    }
    
    /**
     * Get the name of the airport.
     * @return the name of the airport as a String.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of the airport to be the given String.
     * @param name is the String representation of the Airport's name.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Get the name of the location of the airport.
     * @return the location of the airport as a String.
     */
    public String getLocation()
    {
        return location;
    }
    
    /**
     * Set the location of the airport based on the given location.
     * @param location is the String representation of the airport location.
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    /**
     * Get the abbreviation for the airport.
     * @return the abbreviation of the airport as a String.
     */
    public String getAbbreviation()
    {
        return abbreviation;
    }
    
    /**
     * Set the abbreviation of the airport.
     * @param abbreviation is the airport abbreviation as a String.
     */
    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }
    
    /**
     * Get the value that represents that variation from compass north and
     * true north.
     * @return the variation from true north.
     */
    public float getVariation()
    {
        return variation;
    }
    
    /**
     * Set the variation from true north.
     * @param variation is the variation from true north represented as a
     * float.
     */
    public void setVariation(float variation)
    {
        this.variation = variation;
    }
    /*
     * Get a Path object located at a specified index number
     * @param the int index value of the Path you wish to retrieve
     * @return the Path object at index i
     */
    public Path getPath(int i)
    {
        return paths.get(i);
    }
    
    /**
     * get the number of Path objects in this Airport
     * @return the integer value of how many Paths are in the collection paths 
     */
    public int numPaths()
    {
        return paths.size();
    }
    
    /*
     * Add a Path object to the collection paths in the instance of Airport
     * @param the Path object to be added to the collection
     */
    public void addPath(Path path)
    {
        paths.add(path);
    }
    
    /**
     * Get the String representation of an Airport object, including all of the
     * current values of the object's data fields in an organized labeled fashion.
     * @return the String representation of the airport.
     */
    @Override
    public String toString()
    {
        String airportString = "";
        airportString += "File Path: " + pdf_file_path + "\n"
                        + "Name: " + name + "\n"
                        + "Location: " + location + "\n"
                        + "Abbreviation: " + abbreviation + "\n"
                        + "Variation: " + variation + "\n";
        for(int i = 0; i < numPaths(); i++) {
            airportString += "Path: " + getPath(i).toString() + "\n";
        }
        return airportString;
    }
    
    /**
     * Gets the conversion factor between pixel length and longitude.
     * @param file_path the path to the airport diagram PDF.
     * @return the number of pixels in one unit of longitude (may be a half
     * degree or a whole degree).
     */
    private void findPixelConversionScales()
    {
        BufferedImage diagram = PDFToImage.makeImage(this.pdf_file_path);
        
        /* The margins are the distance from edge of the diagram to the
         * first whitespace pixel of the diagram.  The x_margin is the
         * length of the left and right margins in pixels, and the y_margin
         * is the length of the top and bottom margins in pixels.
         */
        int x_margin;
        int y_margin;
        
        /* The longer side has a margin of 61 pixels, and the shorter side
         * has a margin of 25 pixels for all airport diagrams.  In other
         * words, the first whitespace pixel of the actual diagram is at
         * (25, 61) if the diagram is portrait and (61, 25) if landscape.
        */
        //landscape
        if (diagram.getWidth() > diagram.getHeight())
        {
            x_margin = 61;
            y_margin = 25;
        }
        //portrait
        else
        {
            x_margin = 25;
            y_margin = 61;
        }
        
        //The right end of the diagram.
        int end_width = diagram.getWidth() - x_margin - 1;
        
        //The bottom end of the diagram.
        int end_height = diagram.getHeight() - y_margin - 1;
        
        //Try to find the longitude scale at the top of the diagram.
        if (!findLongitudeScale(new Point(x_margin, y_margin), end_width))
        {
            //Try to find the longitude scale at the bottom of the diagram.
            if (!findLongitudeScale(new Point(x_margin, end_height),end_width))
            {
                //We failed to find a usable scale.
                System.err.println("Error:  Longitude could not be found.");
                System.exit(1);
            }
        }
        
        //Try to find the latitude scale on the left side of the diagram.
        if (!findLatitudeScale(new Point(x_margin, y_margin), end_height))
        {
            //Try to find the latitude scale on the right side of the diagram.
            if (!findLatitudeScale(new Point(end_width, y_margin), end_height))
            {
                //We failed to find a usable scale.
                System.err.println("Error:  Latitude could not be found.");
                System.exit(1);
            }
        }
        
    }
    
    /**
     * Try to find the longitude scale and report the success of the attempt.
     * @param current is the point to start searching from.
     * @param diagram_width_end is the end of the diagram when approaching
     * from the left.
     * @return true if a valid scale is found before the end of the diagram
     * and false otherwise.
     */
    private boolean findLongitudeScale(Point current, int diagram_width_end)
    {
        int black_pixels_found = 0;
        
        /* unit_in_pixels is a counter for the length of one unit on the
         * diagram's grid, which is probably one degree.
         */
        int unit_in_pixels = 0;
        
        /* There is an offset between the start of the diagram and the start
         * of a usable portion of the unit grid.
         */
        int grid_offset = 0;
        
        //We found the scale if we find two black pixels when searching.
        while (black_pixels_found < 2)
        {
            /* We found the start of a full unit's marker on the grid, so add
             * to the counter
             */
            if (black_pixels_found > 0)
            {
                unit_in_pixels++;
            }
            /* We haven't found the starting marker yet, so this is still part
             * of the offset of the grid.
             */
            else
            {
                grid_offset++;
            }
            //Advance to the next pixel to the right.
            current = new Point(current.getX() + 1, current.getY());
            /* If we are still looking for the end of the unit at the end of
             * the diagram, then we failed to find the scale.
             */
            if (current.getX() >= diagram_width_end)
            {
                return false;
            }
        }
        
        //Set the offset from the left side of the diagram to our unit marker.
        this.longitude_offset = grid_offset;
        
        //Set the pixels-to-longitude conversion factor
        this.pixels_per_degree_long = unit_in_pixels;
        
        //We found our scale information.
        return true;
    }
    
    /**
     * Try to find the latitude scale and report the success of the attempt.
     * @param current is the point to start searching from.
     * @param diagram_height_end is the end of the diagram when approaching
     * from the top.
     * @return true if a valid scale is found before the end of the diagram
     * and false otherwise.
     */
    private boolean findLatitudeScale(Point current, int diagram_height_end)
    {
        int black_pixels_found = 0;
        
        /* unit_in_pixels is a counter for the length of one unit on the
         * diagram's grid, which is probably one degree.
         */
        int unit_in_pixels = 0;
        
        /* There is an offset between the start of the diagram and the start
         * of a usable portion of the unit grid.
         */
        int grid_offset = 0;
        
        //We have found the scale if we find two black pixels.
        while (black_pixels_found < 2)
        {
            /* We found the start of a full unit's marker on the grid, so add
             * to the counter.
             */
            if (black_pixels_found > 0)
            {
                unit_in_pixels++;
            }
            /* We haven't found the starting marker yet, so this is still part
             * of the offset of the grid.
             */
            else
            {
                grid_offset++;
            }
            //Advance to the next pixel downward.
            current = new Point(current.getX(), current.getY() + 1);
            /* If we are still looking for the end of the unit at the end of
             * the diagram, then we failed to find the scale.
             */
            if (current.getY() >= diagram_height_end)
            {
                return false;
            }
        }
        
        //Set the offset from the top of the diagram to our unit marker.
        this.latitude_offset = grid_offset;
        
        //Set the pixels-to-latitude conversion factor.
        this.pixels_per_degree_lat = unit_in_pixels;
        
        //We found our scale information.
        return true;
    }
}//end Airport