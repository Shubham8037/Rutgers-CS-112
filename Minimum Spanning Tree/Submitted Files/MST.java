package apps;

import structures.Graph;
import structures.MinHeap;
import structures.Vertex;

import java.util.ArrayList;

public class MST {

    /**
     * Initializes the algorithm by building single-vertex partial trees
     *
     * @param graph Graph for which the MST is to be found
     * @return The initial partial tree list
     */
    public static PartialTreeList initialize(Graph graph) {

        // 1. Create an empty list L of partial trees.
        PartialTreeList L = new PartialTreeList();

        // 2. for each vertex v in the graph
        for (Vertex v : graph.vertices) {

            // 2.1 Create a partial tree T containing only v.
            PartialTree T = new PartialTree(v); // 2.2 Mark v as belonging to T.

            // 2.3 Create a priority queue (heap) P and associate it with T.
            MinHeap<PartialTree.Arc> P = T.getArcs();

            Vertex.Neighbor neighbor = v.neighbors;

            do {
                // 2.4.1 all of the arcs (edges) connected to v
                PartialTree.Arc arc = new PartialTree.Arc(v, neighbor.vertex, neighbor.weight);

                // 2.4.2 Insert into P.
                P.insert(arc);

                neighbor = neighbor.next;

            } while (neighbor != null);

            // 2.5 Add the partial tree T to the list L.
            L.append(T);

        }

        return L;
    }

    /**
     * Executes the algorithm on a graph, starting with the initial partial tree list
     *
     * @param L Initial partial tree list
     * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
     */
    public static ArrayList<PartialTree.Arc> execute(PartialTreeList L) {

        ArrayList<PartialTree.Arc> arcsInMST = new ArrayList<>();

        do {
            // 3.1 Remove the first partial tree PTX from L.
            PartialTree PTX = L.remove();

            // 3.2 Let PQX be PTX's priority queue.
            MinHeap<PartialTree.Arc> PQX = PTX.getArcs();

            PartialTree.Arc a;
            Vertex v2;

            do {
                // 4.1 Remove the highest-priority arc from PQX. Say this arc is a.;
                a = PQX.deleteMin();

                // 4.2 Let v1 and v2 be the two vertices connected by a, where v1 belongs to PTX.
                // v1 = a.v1;
                v2 = a.v2;
            }
            // 5. If v2 also belongs to PTX, go back to Step 4 and pick the next highest priority arc,
            // otherwise continue to the next step.
            while (PTX.getRoot().getRoot().equals(v2.getRoot()));

            // 6. Report a - this is a component of the minimum spanning tree.
            arcsInMST.add(a);

            // 7.1 Find the partial tree PTY to which v2 belongs.
            // Remove PTY from the partial tree list L
            PartialTree PTY = L.removeTreeContaining(v2);

            // 8.1 Combine PTX and PTY.
            // This includes merging the priority queues PQX and PQY into a single priority queue.
            PTX.merge(PTY);

            // 8.2 Append the resulting tree to the end of L.
            L.append(PTX);

        }
        // 9. If there is more than one tree in L, go to Step 3.
        while (L.size() > 1);

        return arcsInMST;
    }

}
