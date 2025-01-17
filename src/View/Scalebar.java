package View;

import Model.MapCalculator;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class Scalebar {

    private static Map<Integer,Double> zoomLevelDistances;

    private Graphics2D g;
    private int zoomLevel;
    private View view;
    private AffineTransform transform;

    private Point2D lineStart; //The start of the scalebar
    private Point2D lineEnd; //The end of the scalebar

    public Scalebar(Graphics2D g, int zoomlevel, View view, AffineTransform transform){
        this.g = g;
        this.zoomLevel = zoomlevel;
        this.view = view;
        this.transform = transform;

        drawScaleBar();
    }

    /**
     * Draws the scalebar line and the markers at each end.
     */

    public void drawScaleBar(){
        g.setTransform(new AffineTransform());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lineStart = new Point2D.Float(view.getWidth()-200,view.getContentPane().getHeight()-13); //Place the linestart at a arbitrary location to start with
        lineEnd = new Point2D.Float(view.getWidth()-65,view.getContentPane().getHeight()-13); //The endpoint is ALWAYS static

        double lineWidth = lineEnd.getX()-lineStart.getX();
        g.setColor(DrawAttribute.fadewhite);
        Double x = lineStart.getX() - 80;
        Double y = lineStart.getY() - 13;
        Double line = lineWidth + 100;
        g.fill(new Rectangle2D.Float(x.floatValue(), y.floatValue(), line.floatValue() , 20));
        double desiredDistance = zoomLevelDistances.get(zoomLevel); //The distance we want to display according to the zoomlevel
        lineStart.setLocation(getDesiredDistance(lineWidth,desiredDistance)); //Set the start location to the calculated startpoint from the desired distance.


        //Draws the line with the points calculated above
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.draw(new Line2D.Float(lineStart, lineEnd));
        g.draw(new Line2D.Float(lineStart,new Point2D.Double(lineStart.getX(),lineStart.getY()-5)));
        g.draw(new Line2D.Float(lineEnd,new Point2D.Double(lineEnd.getX(),lineStart.getY()-5)));

        displayDistanceString(desiredDistance); //Displays the meters / kilometers near the scalebar

        g.setTransform(transform);
    }

    /**
     * Calculates the desired distance in pixels by using a default start and endpoint,
     * calculating the distance between them and dividing the linewidth by this distance to find the linewidth per kilometer.
     * @param lineWidth the default linewidth
     * @param desiredDistance the desired distance in kilometers
     */
    private Point2D getDesiredDistance(double lineWidth, double desiredDistance){
        Point2D.Float transformedStart = new Point2D.Float(); //The point in which the tranformed start point will be saved in.
        Point2D.Float transformedEnd = new Point2D.Float(); //The point in which the transformed end point will be saved in.

        try {
            transform.inverseTransform(lineStart, transformedStart); //Use inverse transform to calculate the points from pixels on the screen to their corresponding lat and lon according to our transform
            transform.inverseTransform(lineEnd,transformedEnd);
        } catch (NoninvertibleTransformException e){
            e.printStackTrace();
        }

        double distance = MapCalculator.haversineDist(transformedStart, //Calculate distance between the two points
                transformedEnd);


        double lineWidthPerKm = lineWidth/distance; //Used to calculate the desired distance in pixels
        return new Point2D.Float((float) (lineEnd.getX()-desiredDistance*lineWidthPerKm),(float)lineStart.getY()); //Change the x coordinate to form the desired distance.
    }

    /**
     * Displays the distance in either meter or kilometers depending on the distance.
     * @param desiredDistance The intended distance to be displayed
     */
    private void displayDistanceString(double desiredDistance){
        double distanceInMeters = desiredDistance*1000;

        if(desiredDistance%1000 < 1){ //If the distance is less than a kilometer, display it in meters, otherwise display it in kilometers
            String meterDist = new DecimalFormat("####").format(distanceInMeters) + " m";
            if(meterDist.length() <= 4) {
                g.drawString(meterDist, (int) lineStart.getX() - 40, (int) lineStart.getY()+1);
            } else {
                g.drawString(meterDist, (int) lineStart.getX() - 45, (int) lineStart.getY()+1);
            }
        } else {
            String kilometerDist = new DecimalFormat("##.##").format(desiredDistance) + " km";
            if(kilometerDist.length() >= 5){ //Makes sure to draw it appropiately if there are more numbers.
                g.drawString(kilometerDist, (int) lineStart.getX() - 45, (int) lineStart.getY()+1);
            } else {
                g.drawString(kilometerDist, (int) lineStart.getX() - 35, (int) lineStart.getY()+1);
            }
        }
    }

    /**
     * Specifies the distance we wish to display at each zoomlevel (in km).
     */
    public static void putZoomLevelDistances(){
        zoomLevelDistances = new HashMap<>();
        zoomLevelDistances.put(20,0.025);
        zoomLevelDistances.put(19,0.030);
        zoomLevelDistances.put(18,0.050);
        zoomLevelDistances.put(17,0.075);
        zoomLevelDistances.put(16,0.1);
        zoomLevelDistances.put(15,0.15);
        zoomLevelDistances.put(14,0.2);
        zoomLevelDistances.put(13,0.3);
        zoomLevelDistances.put(12,0.45);
        zoomLevelDistances.put(11,0.6);
        zoomLevelDistances.put(10,0.9);
        zoomLevelDistances.put(9,1.0);
        zoomLevelDistances.put(8,1.5);
        zoomLevelDistances.put(7,2.0);
        zoomLevelDistances.put(6,3.0);
        zoomLevelDistances.put(5,5.0);
        zoomLevelDistances.put(4,8.0);
        zoomLevelDistances.put(3,10.0);
        zoomLevelDistances.put(2,15.0);
        zoomLevelDistances.put(1,20.0);
        zoomLevelDistances.put(0,25.0);

    }

    public Point2D getLineEnd() { return lineEnd;}
    public AffineTransform getTransform() { return transform;}
}
