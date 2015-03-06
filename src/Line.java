import java.awt.*;
import java.io.Serializable;

public class Line extends Drawable implements Serializable {
    int stroke_id;
    private boolean dashed = false;

    /**
     * Sets up the Line with a shape, color and stroke.
     * @param shape Shape of Line
     * @param color Color of Line
     * @param drawLevel When to draw.
     */
    public Line(Shape shape, Color color, int stroke_id, double drawLevel, int layerVal) {
        super(shape, color, drawLevel, layerVal);
        this.stroke_id = stroke_id;
    }
    public Line(){
        super(null,null,0.0,0);
    }

    /**
     * Draws the Shape with predefined colors and stroke.
     * @param g Graphic 2D with correct transform
     */
    public void draw(Graphics2D g) {
        g.setStroke(strokes[stroke_id]);
        g.setColor(color);
        g.draw(shape);
    }

    /**
     * Draws the boundary of the shape with predefined stroke and black.
     * @param g Graphic 2D with correct transform
     */
    public void drawBoundary(Graphics2D g) {
        if(!dashed) {
            g.setStroke(strokes[stroke_id + 1]);
            g.setColor(Color.BLACK);
            g.draw(shape);
        }
    }


    /**
     * Toggle if the line should be dashed.
     */
    public void setDashed(){
        dashed = true;
    }

    public boolean isDashed(){
        return dashed;
    }



}