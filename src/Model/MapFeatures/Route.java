package Model.MapFeatures;

import Model.MapFeature;
import Model.ValueName;

import java.awt.geom.Path2D;


public class Route extends MapFeature{

    public Route(Path2D way, int layer_value, String value) {
        super(way, layer_value, value);
    }

    @Override
    public void setPreDefLayerValues() {
        super.setPreDefLayerValues();
    }

    @Override
    public void setValueName() {
        if(value.equals("ferry"))setValueName(ValueName.FERRY);
        else setValueName(ValueName.ROUTE);
    }

}
