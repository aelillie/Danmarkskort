package Model;

import Model.MapFeatures.*;
import Model.QuadTree.QuadTree;
import Model.Path.Graph;
import Model.Path.Vertices;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Contenthandler, which handles the .osm file written in XML.
 * Saving data from the file, which is being parsed, through keys and tags read.
 * Ultimately adding shapes to be drawn to lists in this class.
 */
public class OSMHandler extends DefaultHandler {
    private Map<String, String> keyValue_map; //relation between the keys and values in the XML file
    private LongHashMap<OSMNode> node_longMap; //Relation between a nodes' id and coordinates
    private LongHashMap<Path2D> wayId_longMap; //Map of ways and their id's
    private Graph graph;
    private Vertices vertices;
    private HashSet<String> bigRoads = new HashSet<>(Arrays.asList("motorway",
            "trunk", "primary", "secondary", "tertiary"));

    //Contains relevant places parsed as address objects linked to their coordinate.
    private Map<Address, List<Path2D>> streetMap;

    private QuadTree streetTree, buildingTree, iconTree, naturalTree, railwayTree, bigRoadTree;
    private QuadTree coastLinesTree, landuseTree, bigForestTree, bigLakeTree;
    private ArrayList<Address> addressList; //list of all the addresses in the .osm file
    private List<Long> memberReferences; //member referenced in a relation of ways
    private List<OSMNode> wayCoords; //List of referenced coordinates used to make up a single way
    private static List<Coastline> coastlines; //List of all of the coastlines to be drawn

    private Long wayId; //Id of the current way
    private OSMNode nodeCoord; //current node's coordinates
    //if a given feature is present:
    private boolean isArea, isBusstop, isMetro, isSTog, hasHouseNo, hasPostcode, hasCity, isStart;
    private String streetName, houseNumber,cityName, postCode; //address info
    private long thisNodeId;
    private OSMNode startPoint, endPoint; //coastline start point and end point
    private Rectangle2D bbox = new Rectangle2D.Double();


    public void initializeCollections(){
        coastlines = new ArrayList<>();
        memberReferences = new ArrayList<>();
        addressList = new ArrayList<>();
        wayCoords = new ArrayList<>();
        node_longMap = new LongHashMap<OSMNode>();
        keyValue_map = new HashMap<>();
        wayId_longMap = new LongHashMap<Path2D>();
        streetMap = new HashMap<>();
        vertices = new Vertices();
        graph = new Graph();
    }

    /**
     * Reads start elements and handles what to be done from the data associated with the element.
     * @param uri The namespace URI
     * @param localName the local name (without prefix)
     * @param qName the qualified name (with prefix)
     * @param atts the attributes attached to the element
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        switch (qName) { //if qName.equals(case)
            case "osm": {
                //NOTE: refresh all lists so that when you load in a new OSM-file, the old elements aren't in the lists.
                initializeCollections();
            }
            case "relation":{
                keyValue_map.clear();
                memberReferences.clear();
                break;
            }
            case "node": {
                keyValue_map.clear();
                isBusstop = false; isMetro = false;  isSTog = false;
                hasHouseNo = false; hasPostcode = false; hasCity = false;

                Double lat = Double.parseDouble(atts.getValue("lat"));
                lat = MapCalculator.latToY(lat); //transforming according to the Mercator projection
                Double lon = Double.parseDouble(atts.getValue("lon"));
                long id = Long.parseLong(atts.getValue("id"));
                thisNodeId = id;
                OSMNode coord = new OSMNode(lon.floatValue(), lat.floatValue());
                nodeCoord = new OSMNode(lon.floatValue(),lat.floatValue());
                node_longMap.put(id, coord);
                break;

            }
            case "nd": { //references in a way, to other ways
                long id = Long.parseLong(atts.getValue("ref"));
                OSMNode coord = node_longMap.get(id); //fetches coordinate from the referenced id
                if(isStart){ //Saves startpoint (for use in coastlines)
                    startPoint = coord;
                    isStart = false;
                }
                 endPoint = coord;

                if (coord == null) return;
                wayCoords.add(coord);
                break;
            }
            case "way": //A way containing references to other ways and possible tags
                keyValue_map.clear();
                wayCoords.clear(); //clears list of referenced coordinates within a way
                isArea = false;
                isStart = true;
                wayId = Long.parseLong(atts.getValue("id"));
                break;
            case "bounds": //bounds for the given map
                float minlat = Float.parseFloat(atts.getValue("minlat"));
                Double double_min = MapCalculator.latToY(minlat);
                minlat = double_min.floatValue(); //transforming according to the Mercator projection
                float minlon = Float.parseFloat(atts.getValue("minlon"));
                float maxlat = Float.parseFloat(atts.getValue("maxlat"));
                Double double_max = MapCalculator.latToY(maxlat);
                maxlat = double_max.floatValue(); //transforming according to the Mercator projection
                float maxlon = Float.parseFloat(atts.getValue("maxlon"));
                Rectangle2D rect =  new Rectangle2D.Float(minlon, minlat, maxlon - minlon, maxlat - minlat);
                bbox.setRect(rect);
                streetTree = new QuadTree(bbox, 225);
                bigRoadTree = new QuadTree(bbox, 60);
                buildingTree = new QuadTree(bbox, 225);
                iconTree = new QuadTree(bbox, 40);
                naturalTree = new QuadTree(bbox, 190);
                railwayTree = new QuadTree(bbox, 75);
                coastLinesTree = new QuadTree(bbox, 200);
                landuseTree = new QuadTree(bbox, 150);
                bigForestTree = new QuadTree(bbox, 20);
                bigLakeTree = new QuadTree(bbox, 20);
                break;
            case "tag": //tags define ways
                String k = atts.getValue("k");
                String v = atts.getValue("v");
                keyValue_map.put(k, v);
                if(k.equals("area") && v.equals("yes")) isArea = true;
                if(k.equals("highway")) {
                    if(v.equals("bus_stop"))
                        isBusstop = true;
                    else if(k.equals("highway") && v.equals("traffic_signals"))
                        node_longMap.get(thisNodeId).hasTrafficSignal = true;
                }
                if(k.equals("subway")&& v.equals("yes")) isMetro = true;
                if(k.equals("network") && v.equals("S-Tog")) isSTog = true;
                if(k.equals("addr:street")){
                    streetName = v;
                }
                if(k.equals("addr:housenumber")){
                    hasHouseNo = true;
                    houseNumber = v;
                }
                if(k.equals("addr:city")){
                    hasCity = true;
                    cityName = v;
                }
                if(k.equals("addr:postcode")){
                    hasPostcode = true;
                    postCode = v;
                }
                break;
            case "member":
                memberReferences.add(Long.parseLong(atts.getValue("ref")));
                break;
        }
    }


    /**
     * Reads end elements and handles what to be done from the data associated with the element.
     * @param uri the namespace URI
     * @param localName the local name (without prefix)
     * @param qName the qualified CML name (with prefix)
     */
    public void endElement(String uri, String localName, String qName) {

        switch (qName) {
            case "way": //TODO: insert way names into the addresslist aswelll
                Path2D way = PathCreater.createWay(wayCoords);
                //start of adding shapes from keys and values

                if (keyValue_map.containsKey("natural")) {
                    String val = keyValue_map.get("natural");
                    if (val.equals("coastline")) {
                        PathCreater.processCoastlines(way, startPoint, endPoint);
                    } else {
                        Natural natural = new Natural(way, fetchOSMLayer(), val);
                        if (val.equals("forest") && (MapCalculator.exceedsPathLength(wayCoords, 12)))
                            bigForestTree.insert(natural);
                        else if (val.equals("water") && (MapCalculator.exceedsPathLength(wayCoords, 12)))
                            bigLakeTree.insert(natural);
                        else
                            naturalTree.insert(natural);
                    }
                }
                else if (keyValue_map.containsKey("waterway"))
                    naturalTree.insert(new Waterway(way, fetchOSMLayer(), keyValue_map.get("waterway"), isArea));
                else if (keyValue_map.containsKey("leisure")) {
                    if (keyValue_map.get("leisure").equals("park"))
                        naturalTree.insert(new Leisure(way, fetchOSMLayer(), "park"));
                    else buildingTree.insert(new Leisure(way, fetchOSMLayer(), keyValue_map.get("leisure")));
                }
                else if (keyValue_map.containsKey("landuse")) {
                    if (keyValue_map.get("landuse").equals("forest") && (MapCalculator.exceedsPathLength(wayCoords, 12))) {
                        bigForestTree.insert(new Landuse(way, fetchOSMLayer(), "forest", true));
                    }
                    else
                        landuseTree.insert(new Landuse(way, fetchOSMLayer(), keyValue_map.get("landuse"), isArea));
                }
                else if (keyValue_map.containsKey("geological"))
                    naturalTree.insert(new Geological(way, fetchOSMLayer(), keyValue_map.get("geological")));
                else if (keyValue_map.containsKey("building"))
                    buildingTree.insert(new Building(way, fetchOSMLayer(), keyValue_map.get("building")));
                else if (keyValue_map.containsKey("shop"))
                    buildingTree.insert(new Shop(way, fetchOSMLayer(), keyValue_map.get("shop"))); //This seems to only appear under <node
                else if (keyValue_map.containsKey("tourism")) {
                    buildingTree.insert(new Tourism(way, fetchOSMLayer(), keyValue_map.get("tourism")));
                    if (keyValue_map.get("tourism").equals("attraction")) {
                        iconTree.insert(new MapIcon(way, "attractionIcon"));
                    }
                }
                else if (keyValue_map.containsKey("man_made"))
                    naturalTree.insert(new ManMade(way, fetchOSMLayer(), keyValue_map.get("man_made")));
                else if (keyValue_map.containsKey("historic"))
                    naturalTree.insert(new Historic(way, fetchOSMLayer(), keyValue_map.get("historic")));
                else if (keyValue_map.containsKey("craft"))
                    naturalTree.insert(new Craft(way, fetchOSMLayer(), keyValue_map.get("craft")));
                else if (keyValue_map.containsKey("power"))
                    buildingTree.insert(new Power(way, fetchOSMLayer(), keyValue_map.get("power")));
                else if (keyValue_map.containsKey("emergency"))
                    naturalTree.insert(new Emergency(way, fetchOSMLayer(), keyValue_map.get("emergency")));
                else if (keyValue_map.containsKey("aeroway"))
                    naturalTree.insert(new Aeroway(way, fetchOSMLayer(), keyValue_map.get("aeroway")));
                else if (keyValue_map.containsKey("amenity")) {
                    buildingTree.insert(new Amenity(way, fetchOSMLayer(), keyValue_map.get("amenity"), keyValue_map.containsKey("building")));
                    if (keyValue_map.get("amenity").equals("parking")) {
                        iconTree.insert(new MapIcon(way, "parkingIcon"));
                    }

                }
                else if (keyValue_map.containsKey("barrier"))
                    buildingTree.insert(new Barrier(way, fetchOSMLayer(), keyValue_map.get("barrier"), isArea));
                else if (keyValue_map.containsKey("highway") && !keyValue_map.get("highway").equals("proposed")) {

                    Highway highway = new Highway(way, fetchOSMLayer(), keyValue_map.get("highway"), isArea, keyValue_map.get("name"), keyValue_map.get("maxspeed"));
                    highway.setRouteType(keyValue_map);
                    if (bigRoads.contains(keyValue_map.get("highway")))
                        bigRoadTree.insert(highway);
                    else
                        streetTree.insert(highway);
                    vertices.add(wayCoords); //create vertices for all points making up a way
                    if (keyValue_map.containsKey("junction") && keyValue_map.get("junction").equals("roundabout"))
                        highway.assignEdges(wayCoords, "yes");
                    else if (keyValue_map.containsKey("oneway"))
                        highway.assignEdges(wayCoords, keyValue_map.get("oneway"));
                    else highway.assignEdges(wayCoords, "no");

                }
                else if (keyValue_map.containsKey("place"))
                    naturalTree.insert(new Place(way, fetchOSMLayer(), keyValue_map.get("place")));
                else if (keyValue_map.containsKey("railway")) {
                    if (keyValue_map.containsKey("construction") && keyValue_map.get("construction").equals("yes"))
                        ;
                    else railwayTree.insert(new Railway(way, fetchOSMLayer(), keyValue_map.get("railway")));
                }
                else if (keyValue_map.containsKey("route"))
                    railwayTree.insert(new Route(way, fetchOSMLayer(), keyValue_map.get("route")));
                else if (keyValue_map.containsKey("name")) {
                    String name = keyValue_map.get("name").toLowerCase().trim();
                    if (keyValue_map.containsKey("highway") || keyValue_map.containsKey("cycleway") || keyValue_map.containsKey("motorroad")) {
                        Address addr = Address.newStreet(name);
                        addStreetToMap(way, addr);
                    }
                    if (keyValue_map.containsKey("boundary")) {
                        Address addr = Address.newStreet(name);
                        addr.setBoundaryLocation(way);
                        addressList.add(addr);
                    }
                }
                wayId_longMap.put(wayId, way);
                break;

            case "relation":
                if (keyValue_map.containsKey("type")) {
                    String typeValue = keyValue_map.get("type");
                    if (typeValue.equals("multipolygon")) {
                        Path2D path = PathCreater.createMultipolygon(memberReferences, wayId_longMap);
                        if (path == null) return;
                        if (keyValue_map.containsKey("building"))
                            buildingTree.insert(new Building(path, fetchOSMLayer(), "building")); //actual value = yes
                        else if (keyValue_map.containsKey("place"))
                            naturalTree.insert(new Place(path, fetchOSMLayer(), keyValue_map.get("place")));
                        else if (keyValue_map.containsKey("landuse")) {
                            if (keyValue_map.get("landuse").equals("forest"))
                                bigForestTree.insert(new Landuse(path, fetchOSMLayer(), "forest", true));
                            else
                                landuseTree.insert(new Landuse(path, fetchOSMLayer(), keyValue_map.get("landuse"), isArea));
                        }
                        else if (keyValue_map.containsKey("aeroway"))
                            naturalTree.insert(new Aeroway(path, fetchOSMLayer(), keyValue_map.get("aeroway")));
                        else if (keyValue_map.containsKey("amenity"))
                            buildingTree.insert(new Amenity(path, fetchOSMLayer(), keyValue_map.get("amenity"), keyValue_map.containsKey("building")));
                        else if (keyValue_map.containsKey("man_made"))
                            naturalTree.insert(new ManMade(path, fetchOSMLayer(), keyValue_map.get("man_made")));
                        else if (keyValue_map.containsKey("leisure")) {
                            if (keyValue_map.get("leisure").equals("park"))
                                naturalTree.insert(new Leisure(path, fetchOSMLayer(), "park"));
                            else buildingTree.insert(new Leisure(path, fetchOSMLayer(), keyValue_map.get("leisure")));
                        }
                        /*else if (keyValue_map.containsKey("natural") && keyValue_map.get("natural").equals("water")) {
                            naturalTree.insert(new Natural(path, fetchOSMLayer(), "water"));
                        }*/
                        //TODO: Natural = water

                    }
                    if (typeValue.equals("boundary")) {
                        Path2D path = PathCreater.createMultipolygon(memberReferences, wayId_longMap);
                        if (path == null) return;
                        String name = keyValue_map.get("name");
                        if (name == null) return;
                        name.toLowerCase().trim();
                        Address addr = Address.newTown(name);
                        //boundaryMap.put(addr, path);
                        addr.setBoundaryLocation(path);
                        addressList.add(addr);
                    }
                }

                break;

            case "node":
                if (keyValue_map.containsKey("highway")) {
                    String val = keyValue_map.get("highway");
                    if (val.equals("bus_stop") && isBusstop) {
                        iconTree.insert(new MapIcon(nodeCoord, "busIcon"));
                    }
                } else if (keyValue_map.containsKey("tourism")) {
                    String val = keyValue_map.get("tourism");
                    if (val.equals("hotel")) {
                        iconTree.insert(new MapIcon(nodeCoord, "hotelIcon"));
                    }
                } else if (keyValue_map.containsKey("amenity")) {
                    String val = keyValue_map.get("amenity");
                    switch (val) {
                        case "pub":
                        case "bar":
                            iconTree.insert(new MapIcon(nodeCoord, "pubIcon"));
                            break;
                        case "atm":
                            iconTree.insert(new MapIcon(nodeCoord, "atmIcon"));
                            break;
                        case "restaurant":
                            iconTree.insert(new MapIcon(nodeCoord, "restaurantIcon"));
                            break;
                        case "hospital":
                            iconTree.insert(new MapIcon(nodeCoord, "hospitalIcon"));
                            break;
                        case "cafe":
                            iconTree.insert(new MapIcon(nodeCoord, "cafeIcon"));
                            break;
                        case "toilets":
                            iconTree.insert(new MapIcon(nodeCoord, "toiletIcon"));
                            break;
                    }
                } else if (keyValue_map.containsKey("railway")) {
                    String val = keyValue_map.get("railway");
                    if (val.equals("station")) {
                        if (isMetro) iconTree.insert(new MapIcon(nodeCoord, "metroIcon"));
                        else if (isSTog) iconTree.insert(new MapIcon(nodeCoord, "stogIcon"));
                    }
                } else if (keyValue_map.containsKey("name")) {
                    String val = keyValue_map.get("name");
                    if (val.equals("7-Eleven")) {
                        iconTree.insert(new MapIcon(nodeCoord, "7elevenIcon"));
                    } else if (keyValue_map.containsKey("place")) {
                        String place = keyValue_map.get("place");
                        if (place.equals("town") || place.equals("village") || place.equals("suburb") || place.equals("locality") || place.equals("neighbourhood")) {
                            String name = keyValue_map.get("name").toLowerCase();
                            Address addr = Address.newTown(name);
                            addr.setAddressLocation(nodeCoord);
                            //addressMap.put(addr, nodeCoord);
                            addressList.add(addr);
                        }
                    }
                } else if (keyValue_map.containsKey("addr:street")) {
                    if (hasHouseNo && hasCity && hasPostcode) {
                        Address addr = Address.newAddress(streetName.toLowerCase(), houseNumber.toLowerCase(), postCode.toLowerCase(), cityName.toLowerCase());
                        addr.setAddressLocation(nodeCoord);
                        //addressMap.put(addr, nodeCoord);
                        addressList.add(addr);
                    }
                }
                //else if (keyValue_map.containsKey("addr:city")) addCityName();
                //else if (keyValue_map.containsKey("addr:postcode")) addPostcode();


                break;

            case "osm": //The end of the osm file

                long time = System.nanoTime();
                Collections.sort(addressList, new AddressComparator()); //iterative mergesort. ~n*lg(n) comparisons
                System.out.printf("sorted all addresses, time: %d ms\n", (System.nanoTime() - time) / 1000000);
                PathCreater.connectEndPoints(bbox);
                vertices.createVertexIndex();
                graph.initialize(vertices.V());
                graph.addEdges(streetEdges());
                insertCoastLines();
                //System.out.print(coastlines.size());
                wayId_longMap.clear(); //sets key and value arrays to point to null
                node_longMap.clear();
                keyValue_map.clear();
                vertices.clearMap();
                //System.out.print(coastLinesTree.query2D(bbox, false).size());
                coastlines.clear();
                break;

        }

    }

    private Collection<Highway> streetEdges() {
        Collection<MapData> mapData = streetTree.query2D(bbox, false);
        mapData.addAll(bigRoadTree.query2D(bbox, false));
        Collection<Highway> highways = (Collection<Highway>)(Collection<?>) mapData;
        return highways;
    }

    /**
     * Some map features have pre defined layer values, some don't
     * If a map feature doesn't state a layer value, it gets
     * handled in the individual map features
     * @return integer between -5 and 5 for the layer value of a map feature
     */
    private int fetchOSMLayer() {
        int layer_val = 0; //default layer, if no value is defined in the OSM
        String keyValue = keyValue_map.get("layer");
        if (keyValue == null)
            return layer_val; //stops function immediately and returns 0
        else try {
            layer_val = Integer.parseInt(keyValue); //fetch OSM-defined layer value
        } catch (NumberFormatException e) {
            //should never happen, since keyValue isn't null
            System.out.println("Fetching layer value for " + keyValue + " failed...");
        }
        return layer_val; //won't be 0
    }

    private void insertCoastLines(){
        for(MapFeature coastline : coastlines){
            coastLinesTree.insert(coastline);
        }
    }



    private void addStreetToMap(Path2D way, Address street){
        List<Path2D> existingList = streetMap.get(street);
        if (existingList == null) {
            List<Path2D> list = new ArrayList<>();
            list.add(way);
            streetMap.put(street, list); //Make sure the street is not added again
            addressList.add(street);
        } else {
            List<Path2D> list = streetMap.get(street);
            list.add(way);
        }
    }

    public Address[] searchForAddressess(Address add, int type){
        return AddressSearcher.searchForAddresses(add, addressList, type);
    }

    /**
     * Custom comparator that defines how to compare two addresses. Used when sorting a collection of addresses.
     */
    private class AddressComparator implements Comparator<Address> {
        @Override
        public int compare(Address addr1, Address addr2) {
            return addr1.compareTo(addr2);
        }
    }

    public static List<Coastline> getCoastlines() {
        return coastlines;
    }
    public Rectangle2D getBbox() {
        return bbox;
    }
    public List<QuadTree> getQuadTrees() {
        ArrayList<QuadTree> quadTrees = new ArrayList<>();
        quadTrees.add(streetTree);
        quadTrees.add(naturalTree);
        quadTrees.add(buildingTree);
        quadTrees.add(iconTree);
        quadTrees.add(railwayTree);
        quadTrees.add(bigRoadTree);
        quadTrees.add(coastLinesTree);
        quadTrees.add(landuseTree);
        quadTrees.add(bigForestTree);
        quadTrees.add(bigLakeTree);
        return quadTrees;
    }

    public List<OSMNode> getWayCoords(){return wayCoords;}
    public LongHashMap<Path2D> getWayIdMap(){return wayId_longMap;}
    public LongHashMap<OSMNode> getNodeMap(){return node_longMap;}

    public void setQuadTrees(List<QuadTree> quadTrees) {
        if(quadTrees.size() != 10){
            throw new IllegalArgumentException("List must contain the 10 QuadTrees");
        }
        this.streetTree = quadTrees.get(0);
        this.naturalTree = quadTrees.get(1);
        this.buildingTree = quadTrees.get(2);
        this.iconTree = quadTrees.get(3);
        this.railwayTree = quadTrees.get(4);
        this.bigRoadTree = quadTrees.get(5);
        this.coastLinesTree = quadTrees.get(6);
        this.landuseTree = quadTrees.get(7);
        this.bigForestTree = quadTrees.get(8);
        this.bigLakeTree = quadTrees.get(9);
    }

    public Map<Address, List<Path2D>> getStreetMap() {return streetMap;}
    public List<Address> getAddressList(){return addressList;}

    public void setStreetMap(Map<Address, List<Path2D>> streetMap) {
        this.streetMap = streetMap;
    }

    public void setAddressList(ArrayList<Address> addressList) {
        this.addressList = addressList;
    }

    public QuadTree getStreetTree() {
        return streetTree;
    }

    public QuadTree getBuildingTree() {
        return buildingTree;
    }

    public QuadTree getIconTree() {
        return iconTree;
    }

    public QuadTree getNaturalTree() {
        return naturalTree;
    }

    public QuadTree getBigLakeTree() {
        return bigLakeTree;
    }

    public QuadTree getLanduseTree() { return landuseTree; }

    public QuadTree getBigForestTree() {return bigForestTree;}

    public QuadTree getBigRoadTree(){ return bigRoadTree;}

    public QuadTree getCoastLinesTree(){
        return coastLinesTree;
    }

    public Vertices getVertices() {
        return vertices;
    }

    public Graph getGraph() {
        return graph;
    }

    public QuadTree getRailwayTree() {return railwayTree; }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setVertices(Vertices vertices) {
        this.vertices = vertices;
    }
}
