package Model.MapFeatures;

import Model.MapFeature;
import Model.Model;
import Model.ValueName;
import Model.Path.Edge;
import Model.Path.Vertices;
import Model.MapCalculator;
import Model.PathCreater;
import Model.OSMNode;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used for creating edges making up the graph for path finding
 * Handles how you can travel on a street
 */
public class Highway extends MapFeature {
    private String streetName;
    private List<Edge> edges = new ArrayList<>(); //This highway's edges
    private int maxspeed;
    private boolean driveAble, bikeAble, walkAble;

    public Highway(Path2D way, int layer_value, String value, boolean isArea, String streetName, String maxspeed) {
        super(way, layer_value, value);
        this.isArea = isArea;
        if(streetName != null)
            this.streetName = streetName.intern();
        if (maxspeed == null) //no max speed defined in OSM
            setPreDefMaxSpeed(); //default values set by OSM standards
        else
            setMaxSpeed(maxspeed);
    }

    private void setMaxSpeed(String maxspeed) {
        try {
            this.maxspeed = Integer.parseInt(maxspeed);
        } catch (NumberFormatException e) {
            setPreDefMaxSpeed();
        }
    }

    private void setPreDefMaxSpeed() {
        switch (value) {
            case "motorway":
                maxspeed = 130;
                break;
            case "trunk":
                maxspeed = 80;
                break;
            case "primary":
                maxspeed = 80;
                break;
            case "secondary":
                maxspeed = 80;
                break;
            case "tertiary":
                maxspeed = 80;
                break;
            case "unclassified":
                maxspeed = 80;
                break;
            case "residential":
                maxspeed = 50;
                break;
            case "living_street":
                maxspeed = 15;
                break;
            case "pedestrian":
                maxspeed = 5;
                break;
            case "track":
                maxspeed = 5;
                break;
            case "footway":
                maxspeed = 5;
                break;
            case "cycleway":
                maxspeed = 15;
                break;
            case "bridleway":
                maxspeed = 15;
                break;
            case "steps":
                maxspeed = 3;
                break;
            case "path":
                maxspeed = 5;
                break;
            default:
                maxspeed = 50;
                break;
        }
    }


    /**
     * Create edges between all points in the way for the current highway
     */
    public void assignEdges(List<OSMNode> points, String oneWay) {
        Vertices V = Model.getModel().getVertices();
        for (int i = 0; i + 1 < points.size(); i++) {
            Point2D v;
            Point2D w;
            if(oneWay.equals("-1")){ //street in reverse one way direction
                v = points.get(i+1);
                w = points.get(i);
            }else{
                v = points.get(i);
                w = points.get(i+1);
            }
            Edge edge = new Edge(V.getIndex(v), V.getIndex(w), calcDist(v, w), edgePath(v,w), this);
            if(oneWay.equals("yes"))
                edge.setOneWay(true);
            if(oneWay.equals("-1"))
                edge.setOneWayReverse(true);
            edges.add(edge);
        }


    }

    //Distance between two points in km
    private double calcDist(Point2D v, Point2D w) {
        return MapCalculator.haversineDist(v, w);
    }

    //Makes a line between two points
    private Line2D edgePath(Point2D point1, Point2D point2) {
        return PathCreater.createWay(point1, point2);
    }

    @Override
    public void setPreDefLayerValues() {
        super.setPreDefLayerValues();
        if (value.equals("motorway") || value.equals("motorway_link")) layer_value = 17;
        else if (value.equals("trunk") || value.equals("trunk_link")) layer_value = 16;
        else if (value.equals("primary") || value.equals("primay_link")) layer_value = 15;
        else if (value.equals("secondary") || value.equals("secondary_link")) layer_value = 14;
        else if (value.equals("tertiary") || value.equals("tertiary_link")) layer_value = 13;
        else if (value.equals("residential")) layer_value = 12;
        else if (value.equals("footway") && isArea) layer_value = 11;
        else if (value.equals("path") && isArea) layer_value = 11;
    }

    @Override
    public void setValueName() {
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
        else if (value.equals("road")) setValueName(ValueName.ROAD);
        else if (value.equals("footway") && isArea) setValueName(ValueName.FOOTWAY_AREA);
        else if (value.equals("footway")) setValueName(ValueName.FOOTWAY);
        else if (value.equals("cycleway")) setValueName(ValueName.CYCLEWAY);
        else if (value.equals("bridleway")) setValueName(ValueName.BRIDLEWAY);
        else if (value.equals("steps")) setValueName(ValueName.STEPS);
        else if (value.equals("path")) setValueName(ValueName.PATH);
        else setValueName(ValueName.HIGHWAY);
    }

    public Iterable<Edge> edges() {
        return edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Point2D> getPoints() {
        Vertices vertices = Model.getModel().getVertices();
        List<Point2D> localVertices = new ArrayList<>();
        localVertices.add(vertices.getVertex(edges.get(0).v()));
        for (Edge e : edges) {
            localVertices.add(vertices.getVertex(e.w()));
        }
        return  localVertices;
    }

    public String getStreetName(){
        return streetName;
    }

    /**
     * Determines whether you can walk, ride a bike and/or drive on this highway
     * @param kv_map The key value map references from the OSM handler
     */
    public void setRouteType(Map<String, String> kv_map) {
        walkAble = (!kv_map.containsKey("foot") || kv_map.get("foot").equals("yes")) &&
                ( !kv_map.get("highway").equals("motorway") && !kv_map.get("highway").equals("trunk"));
        bikeAble = (!kv_map.containsKey("bicycle") || kv_map.get("bicycle").equals("yes")) &&
                ( !kv_map.get("highway").equals("motorway") && !kv_map.get("highway").equals("trunk"));
        driveAble = !value.equals("pedestrian") && !value.equals("footway") && !value.equals("cycleway") &&
                !value.equals("bridleway") && !value.equals("steps") && !value.equals("path");
    }

    public boolean isDriveAble() {
        return driveAble;
    }

    public boolean isBikeAble() {
        return bikeAble;
    }

    public boolean isWalkAble() {
        return walkAble;
    }

    public double getMaxspeed(){return  maxspeed;}
}
