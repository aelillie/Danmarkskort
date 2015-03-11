package MapFeatures;

import Model.Drawable;
import Model.MapFeature;

import java.awt.*;

/**
 * Created by Anders on 11-03-2015.
 */
public class Landuse extends MapFeature {
    public Landuse(Shape way, int layer_value, String value, boolean isArea) {
        super(way, layer_value, value);
        this.isArea = isArea;
        setValueAttributes();
    }

    @Override
    public void setValueAttributes() {
        if(value.equals("cemetery")) setValueSpecs(Drawable.whitegreen, -0.8);
        else if(value.equals("construction")) setValueSpecs(Drawable.lightgreen, -0.4);
        else if(value.equals("grass")) setValueSpecs(Drawable.whitegreen, -1.0);
        else if(value.equals("greenfield")) setValueSpecs(Drawable.darkgreen, -.8);
        else if(value.equals("industrial")) setValueSpecs(Drawable.darkgreen, -.8);
        else if(value.equals("orchard")) setValueSpecs(Drawable.darkgreen, -.8);
    }

    @Override
    public void setValueIcon() {

    }

    @Override
    public void setColorBlind() {

    }

    @Override
    public void setStandard() {

    }
}
