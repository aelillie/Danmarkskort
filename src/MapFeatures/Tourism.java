package MapFeatures;

import Model.MapFeature;
import Model.ValueName;

import java.awt.geom.Path2D;

/**
 * Created by Anders on 11-03-2015.
 */
public class Tourism extends MapFeature{

    public Tourism(Path2D way, int layer_value, String value) {
        super(way, layer_value, value);
        setValueAttributes();
    }

    @Override
    public void setValueAttributes() {
        setValueName(ValueName.TOURISM);

    }

}
