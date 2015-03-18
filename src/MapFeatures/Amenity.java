package MapFeatures;

import Model.ValueName;
import View.DrawAttribute;
import Model.MapFeature;

import java.awt.*;

/**
 * Created by Anders on 11-03-2015.
 */
public class Amenity extends MapFeature {
    private boolean isBuilding = false;
    public Amenity(Shape way, int layer_value, String value, boolean isBuilding) {
        super(way, layer_value, value);
        isArea = true;
        setValueAttributes();
        this.isBuilding = isBuilding;
    }

    @Override
    public void setValueAttributes() {
        if(value.equals("parking")) setValueSpecs(ValueName.WOOD);
        else if(value.equals("university") && isBuilding )setValueSpecs(ValueName.WOOD);
        //TODO: Does not work. in newSmall the university is not drawn because it is not said to be building
    }

}
