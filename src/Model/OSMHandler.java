package Model;


import MapFeatures.*;
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
    private Map<Long, Point2D> node_map; //Relation between a nodes' id and coordinates
    private Map<String, String> keyValue_map; //relation between the keys and values in the XML file
    private Map<Long, Path2D> wayId_map; //Map of ways and their id's
    private Map<Address,Point2D> addressMap; //Contains relevant places parsed as address objects (e.g. a place Roskilde or an address Lauravej 38 2900 Hellerup etc.) linked to their coordinate.

    private List<MapFeature> mapFeatures; //Contains all of the mapfeature objects to be drawn
    private List<Address> addressList; //list of all the addresses in the .osm file
    private List<MapIcon> mapIcons; //contains all the icons to be drawn
    private List<Long> memberReferences; //member referenced in a relation of ways
    private List<Point2D> wayCoords; //List of referenced coordinates used to make up a single way
    private static List<Coastline> coastlines; //List of all of the coastlines to be drawn

    private Long wayId; //Id of the current way
    private Point2D nodeCoord; //current node's coordinates
    private boolean isArea, isBusstop, isMetro, isSTog, hasName, hasHouseNo, hasPostcode, hasCity, isStart; //if a given feature is present
    private String streetName, houseNumber,cityName, postCode; //address info
    private Point2D startPoint, endPoint; //coastline start point and end point
    private Rectangle2D bbox;

    /**
     * Calculates the latitude to the y-coordinate using spherical Mercator projection
     * @param aLat The latitude of the point
     * @return the y-coordinate on a plane
     */
    public static double latToY(double aLat) {
        return Math.toDegrees(Math.log(Math.tan(Math.PI/4+Math.toRadians(aLat)/2)));
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
            case "osm": { //NOTE: it's important to refresh all lists so that when you load in a new OSM-file, the old elements aren't in the lists.
                mapFeatures = new ArrayList<>();
                bbox = new Rectangle2D.Double();
                coastlines = new ArrayList<>();
                memberReferences = new ArrayList<>();
                addressList = new ArrayList<>();
                wayCoords = new ArrayList<>();
                mapIcons = new ArrayList<>();

                node_map = new HashMap<>();
                keyValue_map = new HashMap<>();
                wayId_map = new HashMap<>();
                addressMap = new HashMap<>();
            }
            case "relation":{
                keyValue_map.clear();
                memberReferences.clear();
                break;
            }
            case "node": {
                keyValue_map.clear();
                isBusstop = false; isMetro = false;  isSTog = false;
                hasName = false; hasHouseNo = false; hasPostcode = false; hasCity = false;

                double lat = Double.parseDouble(atts.getValue("lat"));
                lat = latToY(lat); //transforming according to the Mercator projection
                double lon = Double.parseDouble(atts.getValue("lon"));
                long id = Long.parseLong(atts.getValue("id"));
                Point2D coord = new Point2D.Double(lon, lat);
                nodeCoord = new Point2D.Double(lon,lat);
                node_map.put(id, coord);
                break;
            }
            case "nd": { //references in a way, to other ways
                long id = Long.parseLong(atts.getValue("ref"));
                Point2D coord = node_map.get(id); //fetches coordinate from the referenced id

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
                wayCoords.clear(); //clears list of references coordinates within a way
                isArea = false;
                hasName = false;
                isStart = true;
                wayId = Long.parseLong(atts.getValue("id"));
                break;
            case "bounds": //bounds for the given map
                double minlat = Double.parseDouble(atts.getValue("minlat"));
                minlat = latToY(minlat); //transforming according to the Mercator projection
                double minlon = Double.parseDouble(atts.getValue("minlon"));
                double maxlat = Double.parseDouble(atts.getValue("maxlat"));
                maxlat = latToY(maxlat); //transforming according to the Mercator projection
                double maxlon = Double.parseDouble(atts.getValue("maxlon"));
                bbox.setRect( new Rectangle2D.Double(minlon, minlat, maxlon - minlon, maxlat - minlat));
                break;
            case "tag": //tags define ways
                String k = atts.getValue("k");
                String v = atts.getValue("v");
                keyValue_map.put(k, v);
                if(k.equals("area") && v.equals("yes")) isArea = true;
                if(k.equals("highway") && v.equals("bus_stop")) isBusstop = true;
                if(k.equals("subway")&& v.equals("yes")) isMetro = true;
                if(k.equals("network") && v.equals("S-Tog")) isSTog = true;
                if(k.equals("name")){
                    hasName = true;
                }
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
                    if (val.equals("coastline"))
                        PathCreater.processCoastlines(way, startPoint, endPoint);
                    else mapFeatures.add(new Natural(way, fetchOSMLayer(), keyValue_map.get("natural")));
                }
                else if (keyValue_map.containsKey("waterway")) mapFeatures.add(new Waterway(way, fetchOSMLayer(), keyValue_map.get("waterway"), isArea));
                else if (keyValue_map.containsKey("leisure"))  mapFeatures.add(new Leisure(way, fetchOSMLayer(), keyValue_map.get("leisure")));
                else if (keyValue_map.containsKey("landuse"))  mapFeatures.add(new Landuse(way, fetchOSMLayer(), keyValue_map.get("landuse"), isArea));
                else if (keyValue_map.containsKey("geological")) mapFeatures.add(new Geological(way, fetchOSMLayer(), keyValue_map.get("geological")));
                else if (keyValue_map.containsKey("building")) mapFeatures.add(new Building(way, fetchOSMLayer(), keyValue_map.get("building")));
                else if (keyValue_map.containsKey("shop"))  mapFeatures.add(new Shop(way, fetchOSMLayer(), keyValue_map.get("shop")));
                else if (keyValue_map.containsKey("tourism")) mapFeatures.add(new Tourism(way, fetchOSMLayer(), keyValue_map.get("tourism")));
                else if (keyValue_map.containsKey("man_made")) mapFeatures.add(new ManMade(way, fetchOSMLayer(), keyValue_map.get("man_made")));
                else if (keyValue_map.containsKey("historic")) mapFeatures.add(new Historic(way, fetchOSMLayer(), keyValue_map.get("historic")));
                else if (keyValue_map.containsKey("craft")) mapFeatures.add(new Craft(way, fetchOSMLayer(), keyValue_map.get("craft")));
                else if (keyValue_map.containsKey("emergency")) mapFeatures.add(new Emergency(way, fetchOSMLayer(), keyValue_map.get("emergency")));
                else if (keyValue_map.containsKey("aeroway")) mapFeatures.add(new Aeroway(way, fetchOSMLayer(), keyValue_map.get("aeroway")));
                else if (keyValue_map.containsKey("amenity")) {
                    mapFeatures.add(new Amenity(way, fetchOSMLayer(), keyValue_map.get("amenity"), keyValue_map.containsKey("building")));
                    if (keyValue_map.get("amenity").equals("parking")) {
                        //mapIcons.add(new MapIcon(way, "data//parkingIcon.jpg"));
                    }
                }
                else if (keyValue_map.containsKey("barrier")) mapFeatures.add(new Barrier(way, fetchOSMLayer(), keyValue_map.get("barrier"), isArea));
                else if (keyValue_map.containsKey("boundary")) mapFeatures.add(new Boundary(way, fetchOSMLayer(), keyValue_map.get("boundary")));
                else if (keyValue_map.containsKey("highway")) mapFeatures.add(new Highway(way, fetchOSMLayer(), keyValue_map.get("highway"), isArea));
                else if (keyValue_map.containsKey("railway")) mapFeatures.add(new Railway(way, fetchOSMLayer(), keyValue_map.get("railway")));
                else if (keyValue_map.containsKey("route"))  mapFeatures.add(new Route(way, fetchOSMLayer(), keyValue_map.get("route")));

                wayId_map.put(wayId, way);
                break;

            case "relation":
                if (keyValue_map.containsKey("type")) {
                    String val = keyValue_map.get("type");
                    if (val.equals("multipolygon")) {
                        Path2D path = PathCreater.createMultipolygon(memberReferences, wayId_map);
                        if(path == null) return;
                        if (keyValue_map.containsKey("building"))
                            mapFeatures.add(new Multipolygon(path, fetchOSMLayer(), "building"));

                        if(keyValue_map.containsKey("place")){
                            //TODO islets

                        }
                        /*else if (kv_map.containsKey("natural"))
                            drawables.add(new Model.Area(path, Model.Drawable.water, -1.5));*/
                        //TODO How do draw harbor.
                    }

                        //TODO look at busroute and so forth
                }

                break;

            case "node":
               /* if (keyValue_map.containsKey("highway")) {
                    String val = keyValue_map.get("highway");
                    if (val.equals("bus_stop") && isBusstop)
                        mapIcons.add(new MapIcon(nodeCoord, "data//busIcon.png"));
                }
                else if (keyValue_map.containsKey("railway")) {
                    String val = keyValue_map.get("railway");
                    if (val.equals("station")) {
                        if (isMetro) mapIcons.add(new MapIcon(nodeCoord, "data//metroIcon.png"));
                        else if (isSTog) mapIcons.add(new MapIcon(nodeCoord, "data//stogIcon.png"));
                    }
                } else if(keyValue_map.containsKey("name")) {
                    String name = keyValue_map.get("name");
                    if(keyValue_map.containsKey("place")){
                        String place = keyValue_map.get("place");
                        name = name.toLowerCase();
                        Address addr = Address.newTown(name);
                        //System.out.println(name);
                        if(place.equals("town")){
                            addressMap.put(addr, nodeCoord);
                            addressList.add(addr);
                        } else if (place.equals("village")){
                            addressMap.put(addr, nodeCoord);
                            addressList.add(addr);
                        } else if (place.equals("suburb")){

                        }/* else if (place.equals("surburb")){

                            addressMap.put(addr,nodeCoord);
                            addressList.add(addr);
                        } else if (place.equals("locality")) {
                            addressMap.put(addr,nodeCoord);
                            addressList.add(addr);
                        } else if (place.equals("neighbourhood")){
                            addressMap.put(addr,nodeCoord);
                            addressList.add(addr);
                        }
                    }

                } else if (keyValue_map.containsKey("addr:street")){
                    if(hasHouseNo && hasCity && hasPostcode){
                        Address addr = Address.newAddress(streetName, houseNumber, postCode, cityName);
                        //System.out.println(addressString + ", " + nodeCoord);
                        //System.out.println(addr.toString());
                        addressMap.put(addr, nodeCoord);
                        addressList.add(addr);
                    }
                }*/




                //else if (keyValue_map.containsKey("addr:city")) addCityName();
                //else if (keyValue_map.containsKey("addr:postcode")) addPostcode();


                break;

            case "osm": //The end of the osm file

                Collections.sort(addressList, new AddressComparator()); //iterative mergesort. ~n*lg(n) comparisons
                PathCreater.connectCoastlines(bbox);
                mapFeatures.addAll(coastlines);
                sortLayers();
                break;

        }


    }

    /**
     * Sorts the Model.Drawable elements in the drawables list from their layer value.
     * Takes use of a comparator, which compares their values.
     */

    protected void sortLayers() {
        Comparator<MapFeature> comparator = new Comparator<MapFeature>() {
            @Override
            /**
             * Compares two Model.Drawable objects.
             * Returns a negative integer, zero, or a positive integer as the first argument
             * is less than, equal to, or greater than the second.
             */
            public int compare(MapFeature o1, MapFeature o2) {
                if (o1.getLayerVal() < o2.getLayerVal()) return -1;
                else if (o1.getLayerVal() > o2.getLayerVal()) return 1;
                return 0;
            }
        };
        Collections.sort(mapFeatures, comparator); //iterative mergesort. ~n*lg(n) comparisons
        //TODO Consider quicksort (3-way). Keep in mind duplicate keys are often encountered.
    }

    private int fetchOSMLayer() {
        int layer_val = 0; //default layer, if no value is defined in the OSM
        try {
            layer_val = Integer.parseInt(keyValue_map.get("layer")); //fetch OSM-defined layer value
        } catch (NumberFormatException e) {
            //getPreDefLayer();
        }
        return layer_val;
    }




    public void searchForAddressess(Address add){
        AddressSearcher.searchForAddresses(add, addressList, addressMap);

    }


    /**
     * Custom comparator that defines how to compare two addresses. Used when sorting a collection of addresses.
     */
    public class AddressComparator implements Comparator<Address> {
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
    public List<MapFeature> getMapFeatures() {
        return mapFeatures;
    }
    public List<MapIcon> getMapIcons() {
        return mapIcons;
    }


}
