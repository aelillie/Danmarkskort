package View;

import Model.ValueName;

/**
 * View configuration for the standard and default view presentation
 */
public class StandardConfig {
    private DrawAttribute[] standardView;

    public StandardConfig(DrawAttribute[] standardView) {
        this.standardView = standardView;
        defineStandardView();
    }

    public void defineStandardView() {
        //standardView[ValueName.VALUENAME.ordinal()] =     new DrawAttribute(isDashed, strokeId, color, zoomLevel);
        //AEROWAY
        standardView[ValueName.AEROWAY.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.lightgrey, 12);
        standardView[ValueName.TERMINAL.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.purple, 12);
        standardView[ValueName.RUNWAY.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.teal, 12);
        standardView[ValueName.TAXIWAY.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.teal, 12);
        //AMENITY
        standardView[ValueName.AMENITY.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 14);
        standardView[ValueName.PARKING.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.moccasin, 14);
        standardView[ValueName.PUB.ordinal()]     =         new DrawAttribute(false, 1, DrawAttribute.cl_pink, 13);
        standardView[ValueName.BAR.ordinal()]     =         new DrawAttribute(false, 1, DrawAttribute.cl_pink, 13);
        standardView[ValueName.UNIVERSITY.ordinal()] =      new DrawAttribute(false, 1, DrawAttribute.lightgrey, 12);
        standardView[ValueName.SCHOOL_AREA.ordinal()] =      new DrawAttribute(false, 1, DrawAttribute.lightyellow, 12);
        standardView[ValueName.PHARMACEUTICAL.ordinal()] =  new DrawAttribute(false, 1, DrawAttribute.bluegreen, 14);
        //BARRIER
        standardView[ValueName.BARRIER.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 15);
        standardView[ValueName.HENCE.ordinal()] =           new DrawAttribute(true, 1, DrawAttribute.neongreen, 15);
        standardView[ValueName.FENCE.ordinal()] =           new DrawAttribute(true, 1, DrawAttribute.lightblue, 15);
        standardView[ValueName.KERB.ordinal()] =            new DrawAttribute(true, 1, DrawAttribute.lightblue, 15);
        //BOUNDS
        standardView[ValueName.BOUNDS.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.whiteblue,-5);
        //BOUNDARY
        standardView[ValueName.BOUNDARY.ordinal()] =        new DrawAttribute(true, 0, DrawAttribute.white, 8);
        //BRIDGE
        standardView[ValueName.BRIDGE.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.grey, 10);
        //BUILDING
        standardView[ValueName.BUILDING.ordinal()] =        new DrawAttribute(false, 0, DrawAttribute.lightgrey, 12);
        //CRAFT
        standardView[ValueName.CRAFT.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.lightgrey, 15);
        //EMERGENCY
        standardView[ValueName.EMERGENCY.ordinal()] =       new DrawAttribute(false, 0, DrawAttribute.lightgrey, 15);
        //GEOLOGICAL
        standardView[ValueName.GEOLOGICAL.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.lightgrey, 15);
        //HIGHWAY
        standardView[ValueName.HIGHWAY.ordinal()] =         new DrawAttribute(false, 1, DrawAttribute.white, 12);
        standardView[ValueName.MOTORWAY.ordinal()] =        new DrawAttribute(false, 5, DrawAttribute.lightblue, 0);
        standardView[ValueName.TRUNK.ordinal()] =           new DrawAttribute(false, 3, DrawAttribute.lightblue, 0);
        standardView[ValueName.PRIMARY.ordinal()] =         new DrawAttribute(false, 4, DrawAttribute.babyred, 0);
        standardView[ValueName.SECONDARY.ordinal()] =       new DrawAttribute(false, 3, DrawAttribute.lightred, 4);
        standardView[ValueName.TERTIARY.ordinal()] =        new DrawAttribute(false, 3, DrawAttribute.lightyellow, 7);
        standardView[ValueName.UNCLASSIFIED.ordinal()] =    new DrawAttribute(false, 1, DrawAttribute.white, 12);
        standardView[ValueName.RESIDENTIAL.ordinal()] =     new DrawAttribute(false, 2, DrawAttribute.white, 10);
        standardView[ValueName.SERVICE.ordinal()] =         new DrawAttribute(false, 2, DrawAttribute.white, 12);
        standardView[ValueName.LIVING_STREET.ordinal()] =   new DrawAttribute(false, 1, DrawAttribute.grey, 12);
        standardView[ValueName.PEDESTRIAN.ordinal()] =      new DrawAttribute(false, 1, DrawAttribute.lightergrey, 12);
        standardView[ValueName.TRACK.ordinal()] =           new DrawAttribute(true, 0, DrawAttribute.brown, 12);
        standardView[ValueName.ROAD.ordinal()] =            new DrawAttribute(false, 1, DrawAttribute.grey, 12);
        standardView[ValueName.FOOTWAY.ordinal()] =         new DrawAttribute(true, 1, DrawAttribute.red, 14);
        standardView[ValueName.FOOTWAY_AREA.ordinal()] =    new DrawAttribute(false, 1, DrawAttribute.lightergrey, 14);
        standardView[ValueName.CYCLEWAY.ordinal()] =        new DrawAttribute(true, 1, DrawAttribute.lightblue, 14);
        standardView[ValueName.BRIDLEWAY.ordinal()] =       new DrawAttribute(true, 0, DrawAttribute.lightgreen, 14);
        standardView[ValueName.STEPS.ordinal()] =           new DrawAttribute(true, 3, DrawAttribute.red, 14);
        standardView[ValueName.PATH.ordinal()] =            new DrawAttribute(true, 1, DrawAttribute.red, 14);
        //HISTORIC
        standardView[ValueName.HISTORIC.ordinal()] =        new DrawAttribute(false, 0, DrawAttribute.lightgrey, 15);
        standardView[ValueName.ARCHAEOLOGICAL_SITE.ordinal()] = new DrawAttribute(false, 0, DrawAttribute.fadegreen,15) ;
        //LANDUSE
        standardView[ValueName.LANDUSE.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 16);
        standardView[ValueName.FOREST.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.darkgreen, 4);
        standardView[ValueName.CEMETERY.ordinal()] =        new DrawAttribute(false, 0, DrawAttribute.lightgreen, 13);
        standardView[ValueName.CONSTRUCTION.ordinal()] =    new DrawAttribute(false, 0, DrawAttribute.pink, 15);
        standardView[ValueName.GRASS.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.whitegreen, 10);
        standardView[ValueName.GREENFIELD.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.bluegreen, 10);
        standardView[ValueName.BROWNFIELD.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.lightbrown, 12);
        standardView[ValueName.INDUSTRIAL.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.bluegreen, 14);
        standardView[ValueName.ORCHARD.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.bluegreen, 14);
        standardView[ValueName.RESERVOIR.ordinal()] =       new DrawAttribute(false, 0, DrawAttribute.whiteblue, 14);
        standardView[ValueName.BASIN.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.lightblue, 10);
        standardView[ValueName.ALLOTMENTS.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.whiteblue, 16);
        //LEISURE
        standardView[ValueName.LEISURE.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 12);
        standardView[ValueName.GARDEN.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.whitegreen, 12);
        standardView[ValueName.COMMON.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.neongreen, 12);
        standardView[ValueName.PARK.ordinal()] =            new DrawAttribute(false, 0, DrawAttribute.whitegreen, 8);
        standardView[ValueName.PITCH.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.greenblue, 12);
        standardView[ValueName.PLAYGROUND.ordinal()] =      new DrawAttribute(false, 0, DrawAttribute.bluegreen, 12);
        //MANMADE
        standardView[ValueName.MANMADE.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 9);
        //NATURAL
        standardView[ValueName.NATURAL.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightblue, 10);
        standardView[ValueName.WOOD.ordinal()] =            new DrawAttribute(false, 0, DrawAttribute.lightgreen, 10);
        standardView[ValueName.SCRUB.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.lightgreen,10);
        standardView[ValueName.HEATH.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.skincolor, 10);
        standardView[ValueName.GRASSLAND.ordinal()] =       new DrawAttribute(false, 0, DrawAttribute.bluegreen, 10);
        standardView[ValueName.SAND.ordinal()] =            new DrawAttribute(false, 0, DrawAttribute.sand, 9);
        standardView[ValueName.SCREE.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.pink, 9);
        standardView[ValueName.FELL.ordinal()] =            new DrawAttribute(false, 0, DrawAttribute.orange, 10);
        standardView[ValueName.WATER.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.whiteblue, 9);
        standardView[ValueName.WETLAND.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.greenblue, 9);
        standardView[ValueName.BEACH.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.sand, 9);
        standardView[ValueName.COASTLINE.ordinal()] =       new DrawAttribute(false, 0, DrawAttribute.ground, 0);
        //PLACE
        standardView[ValueName.PLACE.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightergrey, 13);
        standardView[ValueName.ISLAND.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightergrey, 13);
        standardView[ValueName.ISLET.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightergrey, 13);
        //RAILWAY
        standardView[ValueName.RAILWAY.ordinal()] =         new DrawAttribute(true, 1, DrawAttribute.cl_grey1, 8);
        standardView[ValueName.LIGHT_RAIL.ordinal()] =      new DrawAttribute(true, 1, DrawAttribute.cl_grey1, 8);
        standardView[ValueName.RAIL.ordinal()] =            new DrawAttribute(true, 1, DrawAttribute.cl_grey1, 8);
        standardView[ValueName.TRAM.ordinal()] =            new DrawAttribute(true, 1, DrawAttribute.cl_grey1, 8);
        standardView[ValueName.SUBWAY.ordinal()] =          new DrawAttribute(true, 1, DrawAttribute.cl_grey1, 8);
        //ROUTE
        standardView[ValueName.ROUTE.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.white, 12);
        standardView[ValueName.FERRY.ordinal()] =           new DrawAttribute(true, 1, DrawAttribute.lightblue, 12);
        //SHOP
        standardView[ValueName.SHOP.ordinal()] =            new DrawAttribute(false, 0, DrawAttribute.lightgrey, 16);
        //TOURISM
        standardView[ValueName.TOURISM.ordinal()] =         new DrawAttribute(false, 0, DrawAttribute.lightgrey, 16);
        //WATERWAY
        standardView[ValueName.WATERWAY.ordinal()] =        new DrawAttribute(false, 0, DrawAttribute.lightblue, 9);
        standardView[ValueName.RIVERBANK.ordinal()] =       new DrawAttribute(false, 0, DrawAttribute.lightblue, 9);
        standardView[ValueName.STREAM.ordinal()] =          new DrawAttribute(false, 0, DrawAttribute.lightblue, 9);
        standardView[ValueName.CANAL.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.whiteblue, 9);
        standardView[ValueName.RIVER.ordinal()] =           new DrawAttribute(false, 0, DrawAttribute.lightblue, 9);
        standardView[ValueName.DAM.ordinal()] =             new DrawAttribute(false, 0, DrawAttribute.lightblue, 9);
    }
}
