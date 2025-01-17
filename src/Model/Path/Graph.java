package Model.Path;


import Model.MapFeatures.Highway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The grid of edges and vertices, used for path finding
 */
public class Graph implements Serializable{
    private static final long serialVersionUID = 2;
    private int V; //Total amount of vertices
    private int E; //Total amount of edges
    private ArrayList<Edge>[] adj; //a bag for each vertex containing adjacent edges

    public Graph() {
    }

    /**
     * Creates adjacent lists for all vertices
     * @param V Number of vertices parsed in OSMHandler
     */
    public void initialize(int V) {
        this.V = V;
        this.E = 0;
        adj = (ArrayList<Edge>[]) new ArrayList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<>();
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Constructs the graph using a list of highways
     * @param highways highways to be used in the graph
     */
    public void addEdges(Collection<Highway> highways) {
        for (Highway highway : highways) {
            addEdge(highway);
        }
    }

    private void addEdge(Highway way) {
        for (Edge e : way.edges()) {
            int v = e.either();
            int w = e.other(v);
            validateVertex(v);
            validateVertex(w);
            if (e.isOneWay()) { //Edge in one way direction
                adj[v].add(e);
                E++;
            } else if (e.isOneWayReverse()) { //Edge in reverse one way direction
                adj[w].add(e);
                E++;
            } else { //Undirected edge
                adj[v].add(e);
                adj[w].add(e);
                E++;
            }
        }
    }
    


    /**
     * Returns the edges incident from vertex v.
     * @return the edges incident from vertex v as an Iterable
     * @param v the vertex
     */
    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the degree of vertex v.
     * @return the degree of vertex v
     * @param v the vertex
     */
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns all edges in the edge-weighted graph.
     * To iterate over the edges in the edge-weighted graph
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public Iterable<Edge> edges() {
        ArrayList<Edge> list = new ArrayList<>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }

    /**
     * Returns a string representation of the edge-weighted digraph.
     * This method takes time proportional to E + V.
     * @return the number of vertices V, followed by the number of edges E,
     *   followed by the V adjacency lists of edges
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (Edge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}