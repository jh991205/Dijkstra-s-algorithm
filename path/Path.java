
package path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import graph.Edge;
import graph.Node;

/** This class contains the shortest-path algorithm and other methods<br>
 * for an undirected graph. */
public class Path {

    /** Return the shortest path from node v to node end <br>
     * ---or the empty list if a path does not exist. <br>
     * Note: The empty list is a list with 0 elements ---it is not "null". */
    public static List<Node> shortest(Node v, Node end) {

        // Heap is described in the abstract version of the algorithm in the A5 handout.
        // The priority of a node will be the shortest path from v to the node.
        Heap<Node> F= new Heap<>(false);  // As described in the A5 handout.
        F.add(v, 0);
        HashMap<Node, NodeInfo> SandF= new HashMap<>();
        // The HashMap contains object Node and NodeInfo for nodes in the
        // settled and frontier set.
        NodeInfo first= new NodeInfo(0, null);
        SandF.put(v, first);
        // Invariant:
        // P1: For a settled node s, at least one shortest v-s path contains
        // only settled nodes and d[s] is its distance.
        // P2: For f in the frontier, at least one v-f path contains only
        // settled nodes, except for f, and d[f] is the minimum distance from
        // v to f over all paths from v to f that contain only settled nodes
        // except for the last one, f.
        // P3: Edges leaving the settled set end in the frontier.
        // Theorem: For f in the frontier with minimum d value (over nodes in
        // the frontier), d[f] is the shortest-path distance from v to f.
        while (F.size() > 0) {
            Node f= F.poll();
            if (f == end) { return getPath(SandF, f); }
            for (Edge i : f.getExits()) {
                Node w= i.getOther(f);
                int dw= SandF.get(f).dist + i.length;
                if (!SandF.containsKey(w)) {
                    NodeInfo wInfo= new NodeInfo(dw, f);
                    SandF.put(w, wInfo);
                    F.add(w, dw);
                } else if (dw < SandF.get(w).dist) {
                    SandF.get(w).dist= dw;
                    SandF.get(w).bkptr= f;
                    F.updatePriority(w, dw);
                }
            }
        }
        return new LinkedList<>();
    }

    /** An instance contains information about a node: <br>
     * the Distance of this node from the start node and <br>
     * its Backpointer: the previous node on a shortest path <br>
     * from the start node to this node (null for the start node). */
    private static class NodeInfo {
        /** shortest known distance from the start node to this one. */
        private int dist;
        /** backpointer on path (with shortest known distance) from start node to this one */
        private Node bkptr;

        /** Constructor: an instance with dist d from the start node and<br>
         * backpointer p. */
        private NodeInfo(int d, Node p) {
            dist= d;     // Distance from start node to this one.
            bkptr= p;    // Backpointer on the path (null if start node)
        }

        /** return a representation of this instance. */
        @Override
        public String toString() {
            return "dist " + dist + ", bckptr " + bkptr;
        }
    }

    /** Return the path from the start node to node end.<br>
     * Precondition: SandF contains all the necessary information about<br>
     * ............. the path. */
    public static List<Node> getPath(HashMap<Node, NodeInfo> SandF, Node end) {
        List<Node> path= new LinkedList<>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        // path, in reverse order.
        while (p != null) {
            path.add(0, p);
            p= SandF.get(p).bkptr;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path pa. <br>
     * Precondition: pa contains at least 1 node. <br>
     * If 1 node, it's a path of length 0, i.e. with no edges. */
    public static int pathSum(List<Node> pa) {
        synchronized (pa) {
            Node v= null;
            int sum= 0;
            // invariant: if v is null, n is the first node of the path.<br>
            // .......... if v is not null, v is the predecessor of n on the path.
            // .......... sum = sum of weights on edges from first node to v
            for (Node n : pa) {
                if (v != null) sum= sum + v.getEdge(n).length;
                v= n;
            }
            return sum;
        }
    }

}
