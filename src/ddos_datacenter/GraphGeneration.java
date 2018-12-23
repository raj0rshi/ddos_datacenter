/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author rajor
 */
public class GraphGeneration {

    /**
     * @param args the command line arguments
     */
    public static final double FACT = 1;
    public static ArrayList<Node> nodes;
    public static ArrayList<Edge> edges;
    public static final int W_H = (int) (500.0 / FACT);
    public static final int W_W = (int) (500.0 / FACT);
    public static final int BLOCK_SIZE = 99;
    public static int ID = 0;
    public static final int NODES_PER_BLOCK = 1;
    public static final int NEIGHBOR_RAD = 170;

    public static int MAX_X = 0;
    public static int MAX_Y = 0;

    static int N = 0;
    static int EID = 0;

    static int topNum = 1;
    static int FlowNum = 10;
    static int FreeVMNum = 3;

    public static final double PM_PROB = .5;

    public static final int[] PM_NUMBERS = {1, 2,};
    public static final int[] VM_NUMBERS = {3,};
    public static final int[] DATARATES = {1, 2, 3, 4, 5, 6};

    public static void generate(int topnum) throws InterruptedException, IOException {

        topNum = topnum;
        main(null);
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {
        // TODO code application logic here

        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();

        File f = new File("topologies\\nodes_" + topNum + ".txt");
        File f2 = new File("topologies\\edges_" + topNum + ".txt");
        f.delete();
        if (!f.exists()) {
            f.createNewFile();
            f2.delete();
            f2.createNewFile();

            for (int x = 60; x < W_H - BLOCK_SIZE - 60; x += BLOCK_SIZE) {
                for (int y = 70; y < +W_W - BLOCK_SIZE - 60; y += BLOCK_SIZE) {
                    for (int i = 0; i < NODES_PER_BLOCK; i++) {

                        int xx = (int) (x + Math.random() * (BLOCK_SIZE));
                        int yy = (int) (y + Math.random() * BLOCK_SIZE);

                        if (nearest_node_distance(xx, yy) > 15) {
                            nodes.add(new Node(N++, xx, yy));
                        }

                    }
                }
            }
            cal_neighbor();
            pm_generation();
            flow_generation();
            System.out.println("edges size:" + edges.size());

            //add victim
            PrintWriter pw = new PrintWriter(f);
            for (Node n : nodes) {
                String NODE = n.ID + " " + n.x + " " + n.y + " " + n.Type;
                pw.println(NODE);

            }
            pw.close();

        }

        System.out.println("edges size:" + edges.size());

        for (Edge e : edges) {
            System.out.println(e.A.ID + "-" + e.B.ID);
        }
        PrintWriter pw2 = new PrintWriter(f2);
        for (Edge e : edges) {
            String NODE = e.A.ID + " " + e.B.ID;
            pw2.println(NODE);
        }
        pw2.close();

        System.out.print("#Edges:\t" + edges.size() + "\t");
        GraphDraw frame = new GraphDraw(nodes, edges);
        //  frame.setSize(W_H, W_W);
        //  frame.setAlwaysOnTop(true);
        //  frame.setVisible(true);

    }

    static void pm_generation() {
        ArrayList<Node> Nodes = new ArrayList<>();
        for (Node n : nodes) {
            if (Math.random() < PM_PROB) {

                int numPms = getRandValue(PM_NUMBERS);
                for (int i = 0; i < numPms; i++) {
                    Node PM = new Node(N++, n.x + i * 15 - numPms * 15 / 2, n.y + 20+3*i*i);
                    PM.Type = "PM";
                    n.addNeighbor(PM);
                    Edge e = new Edge(PM, n, EID++);
                    edges.add(e);
                    Nodes.add(PM);
                    int numVms = getRandValue(VM_NUMBERS);
                    for (int j = 0; j < numVms; j++) {
                        Node VM = new Node(N++, PM.x + j * 15 - numVms * 15 / 2, PM.y + 20+3*j*j);
                        VM.Type = "VM";
                        PM.addNeighbor(VM);
                        Nodes.add(VM);

                        Edge ee = new Edge(PM, VM, EID++);
                        edges.add(ee);
                    }
                }
            }
        }
        nodes.addAll(Nodes);
    }

    static void flow_generation() throws FileNotFoundException {
        ArrayList<Node> VMS = new ArrayList<>();

        for (Node n : nodes) {
            if (n.Type.equals("VM")) {
                VMS.add(n);
                //   System.out.println(n.Type);
            }
        }

        ArrayList<Node> SelectedVMS = new ArrayList<>();

        for (int i = 0; i < VMS.size() - FreeVMNum; i++) {
            int index = (int) (Math.random() * Integer.MAX_VALUE) % VMS.size();
            Node n = VMS.remove(index);
            SelectedVMS.add(n);
        }
        System.out.println(SelectedVMS);

        HashMap<String, Flow> flows = new HashMap<String, Flow>();
        while (flows.size() < FlowNum) {
            int A = (int) (Math.random() * Integer.MAX_VALUE) % SelectedVMS.size();
            int B = (int) (Math.random() * Integer.MAX_VALUE) % SelectedVMS.size();
            Flow f = new Flow(SelectedVMS.get(A).ID, SelectedVMS.get(B).ID, getRandValue(DATARATES));

            if ((A == B)||(flows.containsKey(A+"-"+B))||(flows.containsKey(B+"-"+A))) {
                continue;
            }

            String key=SelectedVMS.get(A).ID+"_"+SelectedVMS.get(B).ID;
            flows.put(key, f);
            System.out.println("flow added: " +key);
        }

        File f = new File("topologies\\flows_" + topNum + ".txt");
        if (f.exists()) {
            f.delete();
        }
        PrintWriter pw = new PrintWriter(f);
        for (Flow flw : flows.values()) {
            pw.println(flw.FROM + " " + flw.TO + " " + flw.Datarate);

        }
        pw.close();

    }

    static int getRandValue(int[] a) {
        int x = (int) (Math.random() * Integer.MAX_VALUE);
        x = x % a.length;
        return a[x];
    }

    static void cal_neighbor() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node I = nodes.get(i);
                Node J = nodes.get(j);
                //    System.out.println(" cal neigh:" + i + "=" + I.ID + " " + j + "=" + J.ID);
                if (dist(I, J) <= NEIGHBOR_RAD) {

                    Edge e = new Edge(I, J, EID++);
                    //     System.out.println(" adding edge:" + e.V1.ID + " " + e.V2.ID);
                    edges.add(e);
                    if (!I.Neighbors.containsKey(J)) {
                        I.Neighbors.put(J.ID, J);
                    }
                    if (!J.Neighbors.containsKey(I)) {
                        J.Neighbors.put(I.ID, J);
                    }

                }
            }

        }
    }

    static double dist(Node x, Node y) {
        return Math.sqrt((x.x - y.x) * (x.x - y.x) + (x.y - y.y) * (x.y - y.y));
    }

    static double nearest_node_distance(int x1, int y1) {
        double min = Double.MAX_VALUE;
        int nn = -1;

        for (int j = 0; j < nodes.size(); j++) {
            Node x = nodes.get(j);
            double dist = Math.sqrt((x.x - x1) * (x.x - x1) + (x.y - y1) * (x.y - y1));
            if (dist <= min) {

                min = dist;
                nn = j;
            }
        }
        return min;
    }

    static Node nearest_node(int x1, int y1) {
        double min = Double.MAX_VALUE;
        Node nn = null;

        for (int j = 0; j < nodes.size(); j++) {
            Node x = nodes.get(j);
            double dist = Math.sqrt((x.x - x1) * (x.x - x1) + (x.y - y1) * (x.y - y1));
            if (dist <= min) {

                min = dist;
                nn = x;
            }
        }
        return nn;
    }

    static Node find(int x, int y) {
        for (Node n : nodes) {

            if (x == n.x && y == n.y) {
                return n;
            }
        }
        System.out.println("Null Node:(" + x + "," + y + ")");
        return null;
    }

}
