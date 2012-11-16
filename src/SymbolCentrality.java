//implement the following API: 

import java.util.*;

public class SymbolCentrality {

    private SymbolGraph sg;
    private Graph g;
    private BreadthFirstPaths bfp[];
    private int degrees[];
    private int ecc[];
    private int effEcc[];
    private double closeness[];

    private int effCenter;
    private int closest;
    private int center;
    private int popularVertex;

    // Constructor
    public SymbolCentrality(SymbolGraph G) {
        sg = G;
        g = G.G();
        bfp = new BreadthFirstPaths[g.V()];
        degrees = new int[g.V()];
        ecc = new int[g.V()];
        closeness = new double[g.V()];
        effEcc = new int[g.V()];

        Arrays.fill(effEcc, -1);
        Arrays.fill(closeness, -1);
        Arrays.fill(ecc, -1);
        Arrays.fill(degrees, -1);
        popularVertex = -1;
        center = -1;
        closest = -1;
        effCenter = -1;
    }

    /*
    * The first four methods compute the centrality of a given node v, for each of the centrality definitions 
    * (except betweenness).  The last four methods return the index of the vertex with optimal centrality     
    * value, for each of the four centrality definitions. Ties should be broken in favor of vertices of smallest 
    * index.
    */

    public int degree(String key) {
        return degree(sg.index(key));
    }

    public int degree(int v) {
        if (degrees[v] == -1) {
            int count = 0;
            for (int i : g.adj(v)) count++;
            degrees[v] = count;
        }
        return degrees[v];

    }

    public int popularVertex() {
        int popular = -1;
        if (popularVertex == -1) {
            for (int i = 0; i < g.V(); i++) {
                if (degrees[i] == -1) degree(i);
                if (degrees[i] > popular) {
                    popular = degrees[i];
                    popularVertex = i;
                }
            }
        }
        return popularVertex;
    }

    public int ecc(String key) {
        return ecc(sg.index(key));
    }

    public int ecc(int v) {
        if (ecc[v] == -1) {
            if (bfp[v] == null) bfp[v] = new BreadthFirstPaths(g, v);
            for (int i = 0; i < g.V(); i++) {
                if (bfp[v].distTo(i) > ecc[v])
                    ecc[v] = bfp[v].distTo(i);
            }
        }
        return ecc[v];
    }

    public int center() {
        int lowest = Integer.MAX_VALUE;
        if (center == -1) {
            for (int i = 0; i < g.V(); i++) {
                if (ecc[i] == -1) ecc(i);
                if (ecc[i] < lowest) {
                    lowest = ecc[i];
                    center = i;
                }
            }
        }
        return center;
    }

    public double closeness(String key) {
        return closeness(sg.index(key));
    }

    public double closeness(int v) {
        if (closeness[v] == -1) {
            if (bfp[v] == null) bfp[v] = new BreadthFirstPaths(g, v);
            double hollywood = 0;
            for (int i = 0; i < g.V(); i++)
                if (i != v) hollywood += bfp[v].distTo(i);
            closeness[v] = 1.0 / (hollywood / (g.V() - 1));
        }
        return closeness[v];
    }

    public int closest() {
        if (closest == -1) {
            double high = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < g.V(); i++) {
                if (closeness[i] == -1) closeness(i);
                if (closeness[i] > high) {
                    high = closeness[i];
                    closest = i;
                }
            }
        }
        return closest;
    }

    public double effEcc(String key) {
        return closeness(sg.index(key));
    }

    public int effEcc(int v) {
        if (effEcc[v] == -1) {
            ArrayList<Integer> effs = new ArrayList<Integer>();
            if (bfp[v] == null) bfp[v] = new BreadthFirstPaths(g, v);

            for (int i = 0; i < g.V(); i++) {
                if (i != v) {
                    effs.add(bfp[v].distTo(i));
                }
            }
            Collections.sort(effs);
            ecc[v] = effs.get(effs.size() - 1);
            effEcc[v] = effs.get((int) Math.floor((g.V() - 1) * 0.9));
        }
        return effEcc[v];
    }

    public int effCenter() {
        if (effCenter == -1) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < g.V(); i++) {
                if (effEcc[i] == -1) effEcc(i);
                if (effEcc[i] < min) {
                    min = effEcc[i];
                    effCenter = i;
                }
            }
        }
        return effCenter;
    }

    public void dislpay() {
        StdOut.println("          Node  Deg  Ecc  Eff   Clo");
        System.out.printf("Popular:  %3d  %3d  %3d  %3d   %5.3f\n", popularVertex(), degree(popularVertex()), ecc(popularVertex()), effEcc(popularVertex()), closeness(popularVertex()));
        System.out.printf("Center:   %3d  %3d  %3d  %3d   %5.3f\n", center(), degree(center()), ecc(center()), effEcc(center()), closeness(center()));
        System.out.printf("Eff.ctr:  %3d  %3d  %3d  %3d   %5.3f\n", effCenter(), degree(effCenter()), ecc(effCenter()), effEcc(effCenter()), closeness(effCenter()));
        System.out.printf("Closest : %3d  %3d  %3d  %3d   %5.3f\n", closest(), degree(closest()), ecc(closest()), effEcc(closest()), closeness(closest()));
    }

    public void dislpay(String key) {
        StdOut.println("          Node  Deg  Ecc  Eff   Clo");
        System.out.printf("Popular:  %3d  %3d  %3d  %3d   %5.3f\n", sg.index(key), degree(sg.index(key)), ecc(sg.index(key)), effEcc(sg.index(key)), closeness(sg.index(key)));
    }

    public static void main(String[] args) {
        /*
        * Example output output tiny.out produces on the sample input tiny.txt. 
        *   
        *             Node  Deg  Ecc   Eff  Clo 
        * Popular:    2     4    3     3    0.667
        * Center:     3     3    2     2    0.667
        * Eff.ctr:    3     3    2     2    0.667
        * Closest:    2     4    3     3    0.667
        *
        * The format should match this sample output.  (Specifically, use printf() with formatting
        * fields “%5.3f“ and “%3d“).
        */
        SymbolGraph SG = new SymbolGraph(args[0], "/");
        SymbolCentrality cen = new SymbolCentrality(SG);
        cen.dislpay("Kevin Bacon");
        cen.dislpay("Clint Eastwood");
    }

}
