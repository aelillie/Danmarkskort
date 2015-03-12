package Model;

import java.awt.*;

public abstract class MapFeature implements Colorblind, Standard {
    protected Shape way;
    protected int layer_value;
    protected Color color;
    protected double zoom_level;
    protected boolean hasIcon;
    protected int stroke_id;
    protected boolean dashed = false;
    protected String value;
    protected boolean isArea = false;

    public MapFeature(Shape way, int layer_value, String value) {
        this.way = way;
        if (layer_value == 0) setDefault();//do nothing. Apply value-specified layer_val.
        else this.layer_value = layer_value;
        this.value = value;

    }

    private void setDefault() {
        //do nothing
    }

    public void setValueSpecs(Color color, double zoom_level) {
        this.color = color;
        this.zoom_level = zoom_level;
    }

    public void setValueDashedSpecs(Color color, double zoom_level) {
        setValueSpecs(color, zoom_level);
        dashed = true;
    }

    public abstract void setValueAttributes();


    public abstract void setValueIcon();

    public void drawBoundary(Graphics2D g) {
        if(!isArea) {
            if(!dashed) {
                g.setStroke(Drawable.basicStrokes[stroke_id + 1]);
                g.setColor(Color.BLACK);
                g.draw(way);
            }
        } else {
            g.setStroke(Drawable.basicStrokes[1]);
            g.setColor(Color.BLACK);
            g.draw(way);
        }

    }

    public void drawStandard(Graphics2D g) {
        if (!isArea) {
            if (dashed) g.setStroke(Drawable.dashedStrokes[stroke_id]);
            else g.setStroke(Drawable.streetStrokes[stroke_id]);
            g.setColor(color);
            g.fill(way);
        } else {
            g.setStroke(Drawable.basicStrokes[stroke_id]);
            g.setColor(color);
            g.fill(way);
        }
    }



    public int getLayerVal() {
        return layer_value;
    }
}
