//
// SHORTESTPATHS.JAVA
// Compute shortest paths in a weighted, directed graph.
//

package spath;

import java.util.LinkedList;
import java.util.HashMap;

// heap-related structures from Lab 3
import heaps.Decreaser;
import heaps.MinHeap;

// directed graph structure
import spath.graphs.DirectedGraph;
import spath.graphs.Edge;
import spath.graphs.Vertex;

// vertex/dist pair for priority queue
import spath.VertexAndDist;

import timing.Ticker;


public class ShortestPaths {

    // "infinity" value for path lengths
    private final static Integer inf = Integer.MAX_VALUE;
    
    // a directed graph, and a weighting function on its edges
    private final DirectedGraph g;
    private HashMap<Edge, Integer> weights; 
    
    // starting vertex for shortest path computation
    private Vertex startVertex;
    
    // map from vertices to their handles into the priority queue
    private HashMap<Vertex, Decreaser<VertexAndDist>> handles;
    
    // map from vertices to their parent edges in the shortest-path tree
    private HashMap<Vertex, Edge> parentEdges;
    
    
    //
    // constructor
    //
    public ShortestPaths(DirectedGraph g, HashMap<Edge,Integer> weights, 
             Vertex startVertex) {
        this.g           = g;
        this.weights     = weights;

        this.startVertex = startVertex; 
    
        this.handles     = new HashMap<Vertex, Decreaser<VertexAndDist>>();
        this.parentEdges = new HashMap<Vertex, Edge>();
    }

    
    //
    // run() 
    //
    // Given a weighted digraph stored in g/weights, compute a
    // shortest-path tree of parent edges back to a given starting
    // vertex.
    //
    public void run() {
        Ticker ticker = new Ticker(); // heap requires a ticker
    
        MinHeap<VertexAndDist> pq = 
                new MinHeap<VertexAndDist>(g.getNumVertices(), ticker);
    
        //
        // Put all vertices into the heap, infinitely far from start.
        // Record handle to each inserted vertex, and initialize
        // parent edge of each to null (since we have as yet found 
        // no path to it.)
        //
        for (Vertex v : g.vertices()) {
            Decreaser<VertexAndDist> d = pq.insert(new VertexAndDist(v, inf));
            handles.put(v, d);
            parentEdges.put(v, null);
        }
    
        //
        // Relax the starting vertex's distance to 0.
        //   - get the handle to the vertex from the heap
        //   - extract the vertex + distance object from the handle
        //   - create a *new* vertex + distance object with a reduced 
        //      distance
        //   - update the heap through the vertex's handle
        //
        Decreaser<VertexAndDist> startHandle = handles.get(startVertex);
        VertexAndDist vd = startHandle.getValue();
        startHandle.decrease(new VertexAndDist(vd.vertex, 0));
    
        //
        // OK, now it's up to you!
        // Implement the main loop of Dijkstra's shortest-path algorithm,
        // recording the parent edges of each vertex in parentEdges.
        // FIXME
        //
        while (!pq.isEmpty()) {
            VertexAndDist v = pq.extractMin();
            
            for(Edge e: v.vertex.edgesFrom()) {
                
                Vertex nextVertex = e.to;
                Decreaser<VertexAndDist> nextDec = handles.get(nextVertex);
                
                int original=nextDec.getValue().distance;
                int potential = weights.get(e)+ v.distance;
                        
                if(potential <original ) {
                    parentEdges.put(nextDec.getValue().vertex, e);
                    VertexAndDist nvd = new VertexAndDist (nextDec.getValue().vertex, weights.get(e)+ v.distance);
                    nextDec.decrease(nvd);
                    handles.put(nextVertex, nextDec);
                }
                
            }
        }
    }
    
    
    //
    // returnPath()
    //
    // Given an ending vertex v, compute a linked list containing every
    // edge on a shortest path from the starting vertex (stored) to v.
    // The edges should be ordered starting from the start vertex.
    //
    public LinkedList<Edge> returnPath(Vertex endVertex) {
        LinkedList<Edge> path = new LinkedList<Edge>();
    
        //
        // FIXME: implement this using the parent edges computed in run()
        //
        Edge next = parentEdges.get(endVertex);
        while(next!=null) {
            path.addFirst(next);
            next = parentEdges.get(next.from);
        }   
        return path;
        }
    
    ////////////////////////////////////////////////////////////////
    
    //
    // returnLength()
    // Compute the total weight of a putative shortest path
    // from the start vertex to the specified end vertex.
    // No user-serviceable parts inside.
    //
    public int returnLength(Vertex endVertex) {
        LinkedList<Edge> path = returnPath(endVertex);
        Vertex currentvertex = endVertex;
        int length = 0;
            for(Edge e : path) {        
                if(endVertex!=null && endVertex!=startVertex) {
                    length += weights.get(e);
                }
            }
        return length;  
    }

    //
    // returnLengthDirect()
    // Expose the current-best distance estimate stored at a vertex.
    // Useful for comparing to ground-truth shortest-path distance
    //   in the absence of parent pointers.
    public int returnLengthDirect(Vertex endVertex) {
        Decreaser<VertexAndDist> endhandle = handles.get(endVertex);
        return endhandle.getValue().distance;
    }
}
