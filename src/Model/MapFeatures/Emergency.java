package Model.MapFeatures;

import Model.MapFeature;
import Model.ValueName;

import java.awt.geom.Path2D;

public class Emergency extends MapFeature {
    public Emergency(Path2D way, int layer_value, String value) {
        super(way, layer_value, value);
        isArea = true;
    }

    @Override
    public void setPreDefLayerValues() {
        super.setPreDefLayerValues();
    }

    @Override
    public void setValueName() {
        setValueName(ValueName.EMERGENCY);
    }

}
