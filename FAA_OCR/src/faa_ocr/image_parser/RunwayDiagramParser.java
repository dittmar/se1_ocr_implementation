package faa_ocr.image_parser;

import faa_ocr.ADTs.*;

import java.awt.image.BufferedImage;


/**
 * 
 * @author Joe Kvedaras
 * @author Kevin Dittmar
 */
public class RunwayDiagramParser 
{
	private BufferedImage diagram;
	private Airport airport;
	private int runways_left;

	public RunwayDiagramParser()
	{
            //do nothing
	}
	
	/**
	 * Find the paths of all runways in the diagram and adds their paths to
	 * the Airport object specified.
	 * @param diagram	is the airport diagram image to parse for runways
	 * @param airport	the airport to which runway path data should be added
	 */
	public void parseRunways(BufferedImage diagram, Airport airport)
	{
            this.diagram = diagram;
            this.airport = airport;    
//TODO:Commented out for testing
//            this.runways_left = airport.numRunways();
            
            traverseImage();
	}
	
	/**
	 * Traverse image looking for black pixels. Once a black pixel is found recursively 
	 * find edges of that black square
	 */
	private void traverseImage()
	{//114, 357 is first runway
		//361, 85
            for (int y = 361; y < diagram.getHeight(); y++) 
            {
                for (int x = 85; x < diagram.getWidth(); x++) 
                {
                    Point pixel = new Point(x,y);
                    if (pixel.isBlack(diagram))
                    {
                            if (checkPixel(pixel))
                            {
                                    //see if the pixel is a runway
                                    checkCorner(pixel);
                            }
                            else
                            {
                                    //skip pixel
                            }
                    }
                    else
                    {
                            //skip pixel
                    }
                }
            }
            System.out.println("Size of diagram: " + 
                               diagram.getHeight() + " " + 
                               diagram.getWidth()
            );
	}
	
	
	/**
	 * Check the pixels to the above-right, above, above-left, and left
	 * to the parameter pixel and return True if they are not black
	 * @param Starting pixel
	 * @return true if none the pixels checked are black
	 * 			false if there is a black pixel
	 */
	private boolean checkPixel(Point pixel)
	{
            int x = pixel.getX();
            int y = pixel.getY();
            
            //Point(0,0) is the top left corner of the document so the pixels
            //above a certain point have a smaller y coordinate
            Point left = new Point(x-1, y);
            Point topLeft = new Point(x-1, y-1);
            Point top = new Point(x, y-1);
            Point topRight = new Point(x+1, y-1);
            
            if(left.isBlack(diagram)) 
            {
            	return false;
            } 
            else if(topLeft.isBlack(diagram)) 
            {
                return false;
            } 
            else if(top.isBlack(diagram)) 
            {
                return false;
            } 
            else if(topRight.isBlack(diagram)) 
            {
                return false;
            } 
            else 
            {
                //no black pixels were found
            	return true;
            } 
        }

	
	
	/**
	 * Check the pixels to the right, bottom-right, bottom, bottom-left
	 * of the parameter point. 3 pixels must be black to traverse the 
	 * two outermost. If less than 3 surrounding pixels are black, do nothing.
	 * @param pixel
	 */
	private void checkCorner(Point pixel)
	{
            int x = pixel.getX();
            int y = pixel.getY();
		
            Point bottom_left = new Point(x - 1, y + 1);
            Point bottom = new Point(x, y + 1);
            Point bottom_right = new Point(x + 1, y + 1);
            Point right = new Point(x + 1, y);

            //Check to see if pixels around the initial point are black
            boolean bottom_left_black = bottom_left.isBlack(diagram);
            boolean bottom_black = bottom.isBlack(diagram);
            boolean bottom_right_black = bottom_right.isBlack(diagram);
            boolean right_black = right.isBlack(diagram);

            //3 pixels must be black so we know it is a runway
            /* check r+br+b, bl+b+br, r+br+b+bl */
            if((bottom_right_black && bottom_black && bottom_left_black) ||
               (right_black && bottom_right_black && bottom_black) ||
               (right_black && bottom_right_black && bottom_black && 
               bottom_left_black))
            {
                    findSlope(pixel);
            }
            else
            {
                    //do nothing
            }  
	}

            
	
	/**
	 * Starting from the corner of the runway, follow the left side of
         * the rectangular runway and the right side of the rectangular
         * runway until the end of the short side is found.
	 * @param initial_point is the starting point at the corner of the
         * runway.  This point will either be the upper right corner or the
         * upper left corner of the runway depending on the runway's
         * orientation.
	 */
	private void findSlope(Point initial_point)
	{
            /* Initialize the left point and right point.  We will traverse
             * a black pixel path going left from the left point and going
             * right from the right point.  We will stop when we can no longer
             * find a black pixel in one of the paths.  This path represents
             * the width of the runway because the width is always shorter than
             * the length.
             */
            Point left_point = traverseLeft(initial_point);
            Point right_point = traverseRight(initial_point);
            
            /* We will need to know where we started for later, so we have to
             * save the initial point.
             */
            Point runway_start = initial_point;

            
            /* There is no point for the end of the width of the runway yet.
             * The best starting point for the endpoint is the initial point.
             */
            Point end_of_width = initial_point;
            
            boolean width_found = false;

            
            //Until the width is found, keep traversing.
            while (!width_found)
            {
                /* The next point on the left traversal path may be the end
                 * of the width of the runway.
                 */
                end_of_width = traverseLeft(left_point);
                if (left_point.equals(end_of_width))
                {
                    /* Our starting place should two pixels to the left of
                     * our starting place, which was the upper right corner.
                     */
                    runway_start = traverseLeft(traverseLeft(runway_start));
                    
                    width_found = true;
                    break;
                }
                else
                {
                	left_point = end_of_width;
                }
                

                /* The next point on the right traversal path may be the end
                 * of the width of the runway.
                 */
                end_of_width = traverseRight(right_point);
                if (right_point.equals(end_of_width))
                {
                    /* Our starting place should two pixels to the right of
                     * our starting place, which was the upper left corner.
                     */
                    runway_start = traverseRight(traverseRight(runway_start));
                    
                    width_found = true;
                    break;
                }
                else
                {
                	right_point = end_of_width;
                }
                
                
            }
            
            /* The width of the runway is now a line segment from the
             * Point intial_point to the Point end_of_width.  The
             * slope of the length of the runway is the negative
             * reciprocal of the slope of the width of the runway
             * since the length and width are perpendicular.  Hence, the
             * x component of the slope that we want is the difference between
             * the y components that we have, and the y component is the
             * difference between the x components that we have.
             */
            int slope_x = end_of_width.getX() - initial_point.getX();
            int slope_y = end_of_width.getY() - initial_point.getY();
            Slope slope = new Slope(slope_y, slope_x);
            slope.invertSlope();
            
            //Length of the width of the runway used when we traverse at the rate
            	//of the slope
            int width_of_runway = (int) findLength(initial_point, end_of_width);
            
            
            Point midpoint_of_runway = initial_point.findMidpoint(end_of_width);
            
            addToAirport(midpoint_of_runway, slope, width_of_runway);
            
	}
//TODO: Bug was found in traverse slope. If the runway was found while traversing left, it would return the right point	
	
	/**
	 * Traverse the runway at the rate of the slope and add those points to the airport
	 * @param midpoint of the current runway
	 * @param slope of the current runway
	 */
	private boolean addToAirport(Point midpoint, Slope slope, int width_of_runway)
	{
		Point end_point = traverseSlope(midpoint, slope, width_of_runway);
                
                double runwayLength = findLength(midpoint.getX(),
                                                 midpoint.getY(),
                                                 end_point.getX(),
                                                 end_point.getY());
                
                if(runwayLength > 100) 
                {
                	
                	
//TODO: For testing only
                	System.out.println("Found enpoint!");
                	System.out.println("X: " + end_point.getX());
                	System.out.println("Y: " + end_point.getY());

                	
//TODO: end testing                	
                	
                	
                	
                	
//TODO:COMMENTED OUT BELOW TO TEST               	
//                    //Translate the midpoint and end_point from x/y to lat/long
//                    float mid_long = airport.longitudeConversion(midpoint);
//                    float mid_lat = airport.latitudeConversion(midpoint);
//                    Node startNode = new Node(mid_long, mid_lat);
//
//                    float end_long = airport.longitudeConversion(end_point);
//                    float end_lat = airport.latitudeConversion(end_point);
//                    Node endNode = new Node(end_long, end_lat);
//
//                    /* Add points to existing Runway instance in airport
//                     * object.  Each physical runway is two runways, and they
//                     * are stored consecutively in pairs.  Hence, we add
//                     * the start and end nodes to the next two runways, since
//                     * they represent the same physical runway.
//                     */
//                    for (int i = 0; i < 2; i++)
//                    {
//                        Runway runway = airport.getRunway(
//                                airport.numRunways() - runways_left
//                        );
//                        runway.addPathNode(startNode);
//                        runway.addPathNode(endNode);
//                    }
                    return true;
                }
                //This line isn't long enough to be a runway.
                else 
                {
                    return false;
                }
                
                
	}
        
        /**
         * Find the length in pixels of two x,y coordinates
         * 
         * @param x coordinate for the first point
         * @param y coordinate for the first point
         * @param x coordinate for the second point
         * @param y coordinate for the second point
         */
        private double findLength(int x1, int y1, int x2, int y2) 
        {
            return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        }
        
        /**
         * Find the length in pixels of two points
         * 
         * @param x coordinate for the first point
         * @param y coordinate for the first point
         * @param x coordinate for the second point
         * @param y coordinate for the second point
         */
        private double findLength(Point one, Point two) 
        {
            int x1 = one.getX();
            int y1 = one.getY();
            int x2 = two.getX();
            int y2 = two.getY();
        	
        	return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        }
	
	
	
	
	
	/**
	 * Get the location of the left-most adjacent black point or the
         * location of the parameter point if all of the pixels to the left
         * are white.
	 * @return the left-most Point that is black or the given point
         * if none of the three pixels tested are black.
	 */
	private Point traverseLeft(Point point)
	{
            Point left = new Point(point.getX() - 1, point.getY());
            Point bottom_left = new Point(point.getX() - 1, point.getY() + 1);
            Point bottom = new Point(point.getX(), point.getY() + 1);
            
            if (left.isBlack(diagram))
            {
                return left;
            }
            else if (bottom_left.isBlack(diagram))
            {
                return bottom_left;
            }
            else if (bottom.isBlack(diagram))
            {
                return bottom;
            }
            
            //If no adjacent points were black, return the given point.
            else
            {
                return point;
            }
        }
		
	/**
	 * Get the location of the right-most adjacent black point or the
         * location of the parameter point if all of the pixels to the right
         * are white.
	 * @return the right-most Point that is black or the given point
         * if none of the three pixels tested are black.
	 */
	private Point traverseRight(Point point)
	{
            Point right = new Point(point.getX() + 1, point.getY());
            Point bottom_right = new Point(point.getX() + 1, point.getY() + 1);
            Point bottom = new Point(point.getX(), point.getY() + 1);
            
            if (right.isBlack(diagram))
            {
                return right;
            }
            else if (bottom_right.isBlack(diagram))
            {
                return bottom_right;
            }
            else if (bottom.isBlack(diagram))
            {
                return bottom;
            }
            
            //If no adjacent points were black, return the given point.
            else
            {
                return point;
            }	
	}
	

	/**
	 * Traverse the slope at the rate of the slope. Stop when
	 * you reach the last black point.
	 * @param initial_point
	 * @param slope
	 * @return last black point
	 */
	private Point traverseSlope(Point initial_point, Slope slope)
	{
                int slopeX = slope.getX();
                int slopeY = slope.getY();
                Point curr_point = initial_point;
                
                boolean lastBlack = false;
                while(lastBlack == false)
                {
                   //To get the next point, add the slope to the current point.
                   Point next_point = new Point(
                           curr_point.getX() + slopeX, 
                           curr_point.getY() + slopeY
                   );
                   
                   if(next_point.isBlack(diagram)) 
                   {
                       curr_point = next_point; 
                   } else 
                   {
                       lastBlack = true;
                   }
                }
                return curr_point;
	}
	
	
	
	//Return the wing point from the point given
	private Point findWing(Point point, int wing_x, int wing_y)
	{
		return new Point(point.getX() + wing_x, point.getY() + wing_y);
	}
	
	/**
	 * Return the greatest common divisor of two longs
	 */
	 private static int gcd(int a, int b) {
	   if (b == 0) 
		   return a;
	   else
		   return gcd(b, a % b);
	 } 
	
	
	
	/**
	 * Traverse the slope at the rate of the slope. Stop when
	 * you reach the last black point.
	 * @param initial_point
	 * @param slope
	 * @return last black point
	 */
	private Point traverseSlope(Point initial_point, Slope slope, int width_of_runway)
	{
                int slopeX = slope.getX();
                int slopeY = slope.getY();
                Point curr_point = initial_point;
                
                //Find the slope of the width of the runway
                slope.invertSlope();
                int slope_width_X = slope.getX();
                int slope_width_Y = slope.getY();
                //simplify slope with gcd.
                int gcd = gcd(slope_width_X, slope_width_Y);
                slope_width_X = slope_width_X / gcd;
                slope_width_Y = slope_width_Y / gcd;
                
                
                //Find the equation for finding the wings of the point after every traversal
                Point wing_right;
                Point wing_left;
                wing_left = new Point(curr_point.getX() + slope_width_X, curr_point.getY() + slope_width_Y);
                wing_right = new Point(curr_point.getX() - slope_width_X, curr_point.getY() - slope_width_Y);
//                do{
//                	wing_right = new Point(curr_point.getX() + slope_width_X, curr_point.getY() + slope_width_Y);
//                    wing_left = new Point(curr_point.getX() - slope_width_X, curr_point.getY() - slope_width_Y);
//                } while (findLength(wing_left, wing_right) > width_of_runway);
//TODO: logic of the above will only happen once. Needs to be changed
                
                //We have arbitrary wings for initial midpoint. Now calculate the x and y we need to add/subtract
                //from the point we are at to find the "wings" of that point
                int left_wingx = wing_left.getX() - curr_point.getX();
                int left_wingy = wing_left.getY() - curr_point.getY();
                int right_wingx = wing_right.getX() - curr_point.getX();
                int right_wingy = wing_right.getX() - curr_point.getX();
                
                Point left_wing_calculate = new Point(left_wingx, left_wingy);
                Point right_wing_calculate = new Point(right_wingx, right_wingy);
                
                Point left_wing = new Point(curr_point.getX() - slope_width_X, curr_point.getY() - slope_width_Y);
                Point right_wing = new Point(curr_point.getX() + slope_width_X, curr_point.getY() + slope_width_Y);
                
                
                boolean lastBlack = false;
                while(lastBlack == false)
                {
                   //To get the next point, add the slope to the current point.
                   Point next_point = new Point(
                           curr_point.getX() + slopeX, 
                           curr_point.getY() + slopeY
                   );
                   
                   //calculate wings of the next point
//TODO: as of now, calculating wings of curr_point and not next_point
//TODO: something to think about because we use next_point in the if statements
//                   left_wing = findWing(curr_point, left_wingx, left_wingy);
//                   right_wing = findWing(curr_point, right_wingx, right_wingy);
                   left_wing = findWing(next_point, left_wingx, left_wingy);
                   right_wing = findWing(next_point, right_wingx, right_wingy);
                   
                   
                   
 //TODO: I can check the wings first and then the middle point. This will save from duplicating code
                   if(next_point.isBlack(diagram)) 
                   {
                       if(left_wing.isBlack(diagram) && right_wing.isBlack(diagram))
                    	   //Both wings are black so we are still in the middle of the runway.
                    	   //continue forward
                       {
                    	   curr_point = next_point;
                       }
                       else if (left_wing.isBlack(diagram) && !right_wing.isBlack(diagram))
                    	   //Left wing is black and the right wing is not.
                    	   //Correct ourselves to the left so we stay in the middle of the runway
                       {
                    	   curr_point = new Point(next_point.getX() - 2, next_point.getY());
                       }
                       else if (!left_wing.isBlack(diagram) && right_wing.isBlack(diagram))
                    	   //right wing is black and the left wing is not.
                    	   //Correct ourselves to the right so we stay in the middle of the runway
                       {
                    	   curr_point = new Point(next_point.getX() + 2, next_point.getY());
                       }
                       else
                    	   //Both wings are white. We will go forward assuming we are still
                    	   //in the middle of the runway.
                       {
                    	   lastBlack = true;
                    	   //curr_point = next_point;
                       }
                       
                   } 
                   else 
                	   /*
                	    * The next point is not black. We will check to see if both the wing points
                	    * are black. If they are, we can assume we are still on the runway. If 1 or the
                	    * other wing is black, we can still safely assume we are on the runway and
                	    * we will traverse forward and correct ourselves so we stay in the middle.
                	    * If all 3 points are are white. We must look around and make a decision if
                	    * we are at the end of the runway or in white space in the middle of the runway
                	    * depending on the pixels around us
                	    */
                   {
                	   if(left_wing.isBlack(diagram) && right_wing.isBlack(diagram))
                    	   //Both wings are black so we are still in the middle of the runway.
                    	   //continue forward
                       {
                    	   curr_point = next_point;
                       }
                	   else if (left_wing.isBlack(diagram) && !right_wing.isBlack(diagram))
                    	   //Left wing is black and the right wing is not.
                    	   //Correct ourselves to the left so we stay in the middle of the runway
                       {
                    	   curr_point = new Point(next_point.getX() - 2, next_point.getY());
                       }
                       else if (!left_wing.isBlack(diagram) && right_wing.isBlack(diagram))
                    	   //right wing is black and the left wing is not.
                    	   //Correct ourselves to the right so we stay in the middle of the runway
                       {
                    	   curr_point = new Point(next_point.getX() + 2, next_point.getY());
                       }
                       else
                       {
//TODO: look around and decide what to do
                    	   lastBlack = true;
                       }
                   }
                }
                return curr_point;
	}
}
