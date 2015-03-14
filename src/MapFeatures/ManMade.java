package MapFeatures;

import Model.DrawAttributes;
import Model.MapFeature;

import java.awt.*;

/**
 * Created by Kevin on 11-03-2015.
 */
public class ManMade extends MapFeature{

    public ManMade(Shape way, int layer_value, String value) {
        super(way, layer_value, value);
    }

    @Override
    public void setValueAttributes() {
        setValueSpecs(DrawAttributes.lightgrey, -1.0);
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
