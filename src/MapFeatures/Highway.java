package MapFeatures;

import Model.MapFeature;
import Model.ValueName;

import java.awt.geom.Path2D;

public class Highway extends MapFeature {
    public Highway(Path2D way, int layer_value, String value, boolean isArea) {
        super(way, layer_value, value);
        this.isArea = isArea;
        setValueAttributes();
    }

    @Override
    public void setValueAttributes() {
        if (value.equals("motorway") || value.equals("motorway_link")) setValueName(ValueName.MOTORWAY);
        else if (value.equals("trunk") || value.equals("trunk_link")) setValueName(ValueName.TRUNK);
        else if (value.equals("primary") || value.equals("primay_link")) setValueName(ValueName.PRIMARY);
        else if (value.equals("secondary") || value.equals("secondary_link")) setValueName(ValueName.SECONDARY);
        else if (value.equals("tertiary") || value.equals("tertiary_link")) setValueName(ValueName.TERTIARY);
        else if (value.equals("unclassified")) setValueName(ValueName.UNCLASSIFIED);
        else if (value.equals("residential")) setValueName(ValueName.RESIDENTIAL);
        else if (value.equals("service")) setValueName(ValueName.SERVICE);
        else if (value.equals("living_street")) setValueName(ValueName.LIVING_STREET);
        else if (value.equals("pedestrian")) setValueName(ValueName.PEDESTRIAN);
        else if (value.equals("track")) setValueName(ValueName.TRACK);
        //else if (value.equals("bus_guideway")) setValueName(ValueName.BUS_GUIDEWAY);
        else if (value.equals("road")) setValueName(ValueName.ROAD);
        else if (value.equals("footway") && isArea) setValueName(ValueName.FOOTWAY_AREA);
        else if (value.equals("footway")) setValueName(ValueName.FOOTWAY);
        else if (value.equals("cycleway")) setValueName(ValueName.CYCLEWAY);
        else if (value.equals("bridleway")) setValueName(ValueName.BRIDLEWAY);
        else if (value.equals("steps")) setValueName(ValueName.STEPS);
        else if (value.equals("path")) setValueName(ValueName.PATH);
        else setValueName(ValueName.HIGHWAY);
    }
}
