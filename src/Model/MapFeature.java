package Model;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class MapFeature implements Colorblind, Standard, Serializable {
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

    public void setLineSpecs(Color color, double zoom_level, int stroke_id) {
        setValueSpecs(color, zoom_level);
        this.stroke_id = stroke_id;
    }

    public void setValueDashedSpecs(Color color, double zoom_level, int stroke_id) {
        setLineSpecs(color, zoom_level, stroke_id);
        dashed = true;
    }
    public abstract void setValueAttributes();


    public abstract void setValueIcon();

    public void drawBoundary(Graphics2D g) {
        if(!isArea) {
            if(!dashed) {
                g.setStroke(DrawAttributes.streetStrokes[stroke_id + 1]);
                g.setColor(Color.BLACK);
                g.draw(way);
            }
        } else {
            g.setStroke(DrawAttributes.basicStrokes[1]);
            g.setColor(Color.BLACK);
            g.draw(way);
        }

    }

    public void drawStandard(Graphics2D g) {
        if(color == null) return;
        if (isArea) {
            g.setColor(color);
            g.fill(way);
        } else {
            if (dashed) g.setStroke(DrawAttributes.dashedStrokes[stroke_id]);
            else g.setStroke(DrawAttributes.streetStrokes[stroke_id]);
            g.setColor(color);
            g.draw(way);
        }
    }


    public double getZoom_level(){
        return zoom_level;
    }

    public int getLayerVal() {
        return layer_value;
    }
}
