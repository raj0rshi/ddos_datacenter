package ddos_datacenter;

import static ddos_datacenter.Constants.DATARATES;
import static ddos_datacenter.Constants.PM_NUMBERS;
import static ddos_datacenter.Constants.PM_PROB;
import static ddos_datacenter.Constants.VM_NUMBERS;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author rajor
 */
public class Graph {

    final static int INF = 9999;

    HashMap<Integer, Node> Nodes;
    HashMap<Integer, Edge> Edges;

    HashMap<Integer, Node> VMS;
    HashMap<Integer, Node> FreeVMS;
    HashMap<Integer, Node> PMS;
    HashMap<Integer, Node> SDNS;
    HashMap<Integer, Flow> Flows;
    int[][] dist;
    int[][] cost;
    double[][] cost2;

    int VM_CAP = 1;

    int eID = 0;

    Graph(int topnum) {

        int topNum = topnum;
        Constants.TOPOLOGY_FILE_EDGES = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\edges_" + topNum + ".txt";
        Constants.TOPOLOGY_FILE_NODES = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\nodes_" + topNum + ".txt";
        Constants.TOPOLOGY_FILE_FLOWS = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\flows_" + topNum + ".txt";
        init();
    }

    private void init() {
        Nodes = new HashMap<>();
        Edges = new HashMap<>();
        VMS = new HashMap<>();
        FreeVMS = new HashMap<>();
        PMS = new HashMap<>();
        SDNS = new HashMap<>();
        Flows = new HashMap<>();
    }

    Graph() {
        init();
    }

    void ReadFile() throws FileNotFoundException {
        Scanner scn = new Scanner(new File(Constants.TOPOLOGY_FILE_EDGES));

        while (scn.hasNext()) {
            String line = scn.nextLine();
            //      System.out.println(line);
            StringTokenizer strtok = new StringTokenizer(line, " ");
            int v1 = Integer.parseInt(strtok.nextToken().trim());
            int v2 = Integer.parseInt(strtok.nextToken().trim());
            //     System.out.println(v1+"-"+v2);
            //  if(v1>133){System.out.println("wrong v1");}
            //if(v2>133){System.out.println("wrong v2");}

            Node n1 = null;
            if (!Nodes.containsKey(v1)) {
                n1 = new Node(v1);
                Nodes.put(v1, n1);
            } else {
                n1 = Nodes.get(v1);

            }
            Node n2 = null;
            if (!Nodes.containsKey(v2)) {
                n2 = new Node(v2);
                Nodes.put(v2, n2);
            } else {
                n2 = Nodes.get(v2);
            }

            // System.out.println(v1+"-"+v2);
            n1.Neighbors.put(v2, n2);
            n2.Neighbors.put(v1, n1);
            Edge e = new Edge(n1, n2, eID);
            Edges.put(eID++, e);
            n1.Edges.put(e.ID, e);
            n2.Edges.put(e.ID, e);
        }

        scn = new Scanner(new File(Constants.TOPOLOGY_FILE_NODES));
        // System.out.println(Nodes.keySet());
        while (scn.hasNext()) {
            String line = scn.nextLine();
            StringTokenizer strtok = new StringTokenizer(line);
            int ID = Integer.parseInt(strtok.nextToken());
            int x = Integer.parseInt(strtok.nextToken());
            int y = Integer.parseInt(strtok.nextToken());
            String Type = strtok.nextToken();

            // System.out.println("ID:" + ID);
            Nodes.get(ID).x = x;
            Nodes.get(ID).y = y;
            Nodes.get(ID).Type = Type;
            if (Type.equals("R")) {
                Nodes.get(ID).color = Color.red;
                SDNS.put(ID, Nodes.get(ID));
            }
            if (Type.equals("VM")) {
                Nodes.get(ID).color = Color.green;
                VMS.put(ID, Nodes.get(ID));
            }
            if (Type.equals("PM")) {
                Nodes.get(ID).color = Color.BLUE;
                PMS.put(ID, Nodes.get(ID));
            }
        }
        scn = new Scanner(new File(Constants.TOPOLOGY_FILE_FLOWS));
        // System.out.println(Nodes.keySet());

        FreeVMS.putAll(VMS);
        int fID = 0;
        while (scn.hasNext()) {
            String line = scn.nextLine();
            //  System.out.println(line);
            StringTokenizer strtok = new StringTokenizer(line, " ");
            int A = Integer.parseInt(strtok.nextToken());
            int B = Integer.parseInt(strtok.nextToken());
            int DR = Integer.parseInt(strtok.nextToken());

            if (FreeVMS.containsKey(A)) {
                FreeVMS.remove(A);
            }
            if (FreeVMS.containsKey(B)) {
                FreeVMS.remove(B);
            }

            Flow flw = new Flow(fID, A, B, DR);
            Flows.put(fID++, flw);
        }
    }

    public void Draw() {
        ArrayList<Edge> x = new ArrayList<>(Edges.values());
        //   System.out.println(VMS);
        for (Flow f : Flows.values()) {
            //       System.out.println("Flow:" + f.ID + "\t FROM:" + f.FROM + "\t TO:" + f.TO);
            Edge e = new Edge(VMS.get(f.FROM), VMS.get(f.TO), eID++);
            //   System.out.println(e.A);
            //   System.out.println(e.B);
            e.color = Color.GREEN;
            x.add(e);
        }
        GraphDraw frame = new GraphDraw(new ArrayList<>(Nodes.values()), x);
        frame.setSize(1000, 1000);
        //frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    void removeEdge(Edge e) {
        if (e == null) {
            return;
        }
        Edges.remove(e.ID);
//        System.out.println("removing edge:" + e);
//        System.out.println("neighbor:" + e.A + ":" + e.A.Neighbors.values());
//        System.out.println("neighbor:" + e.B + ":" + e.B.Neighbors.values());
        e.A.Neighbors.remove(e.B.ID);
        e.B.Neighbors.remove(e.A.ID);
        e.A.Edges.remove(e.ID);
        e.B.Edges.remove(e.ID);

//        System.out.println("neighbor:" + e.A + ":" + e.A.Neighbors.values());
//        System.out.println("neighbor:" + e.B + ":" + e.B.Neighbors.values());
    }

    void addEdge(Edge e) {
        if (e == null) {
            return;
        }
        Edges.put(e.ID, e);
        e.A.Neighbors.put(e.B.ID, e.B);
        e.B.Neighbors.put(e.A.ID, e.A);
        e.A.Edges.put(e.ID, e);
        e.B.Edges.put(e.ID, e);
    }

    void addEdgeCheckDuplicate(Edge e) {

        for (Edge ee : Edges.values()) {
            if ((ee.A.ID == e.A.ID) && (ee.B.ID == e.B.ID)) {
                return;
            }
            if ((ee.A.ID == e.B.ID) && (ee.B.ID == e.A.ID)) {
                return;
            }

        }
        Edges.put(e.ID, e);
        e.A.Neighbors.put(e.B.ID, e.B);
        e.B.Neighbors.put(e.A.ID, e.A);
        e.A.Edges.put(e.ID, e);
        e.B.Edges.put(e.ID, e);
    }

    void RemoveNode(Node n) {
        for (Node nn : n.Neighbors.values()) {
            Edge e = nn.getLink(n);
            nn.Edges.remove(e.ID);
            Edges.remove(e.ID);
            nn.Neighbors.remove(n.ID);
            Nodes.remove(n.ID);
        }
    }

    void AddNode(Node n) {
        for (Node nn : n.Neighbors.values()) {
            Edge e = n.getLink(nn);
            nn.Edges.put(e.ID, e);
            Edges.put(e.ID, e);
            nn.Neighbors.put(n.ID, n);
            Nodes.put(n.ID, n);
        }
    }
    int Users_count = 0;
    int Affected_users = 0;

    public int[][] CalculateAllPairShortestPath() {

        int V = Nodes.size();
        dist = new int[V][V];
        int i, j, k;
        for (i = 0; i < V; i++) {
            for (j = 0; j < V; j++) {
                dist[i][j] = isAdj(i, j);
            }
        }

        for (k = 0; k < V; k++) {
            // Pick all vertices as source one by one 
            for (i = 0; i < V; i++) {
                // Pick all vertices as destination for the 
                // above picked source 
                for (j = 0; j < V; j++) {
                    // If vertex k is on the shortest path from 
                    // i to j, then update the value of dist[i][j] 
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

    void printDist() {
        System.out.println("The following matrix shows the shortest distances between every pair of vertices");
        int V = Nodes.size();
        for (int i = 0; i < V; ++i) {
            for (int j = 0; j < V; ++j) {
                if (dist[i][j] == INF) {
                    System.out.print("INF ");
                } else {
                    System.out.print(dist[i][j] + "   ");
                }
            }
            System.out.println();
        }
    }

    int isAdj(int i, int j) {

        Node I = Nodes.get(i);
        Node J = Nodes.get(j);

        if (i == j) {
            return 0;
        }
        if (I.Neighbors.containsKey(j)) {
            return 1;
        }
        if (J.Neighbors.containsKey(i)) {
            return 1;
        }
        return INF;
    }

    public ArrayList<Integer> getPath(int i, int j) {
        ArrayList<Integer> path = new ArrayList<>();

        path.add(i);
        Node I = Nodes.get(i);
        Node J = Nodes.get(j);

        Node Next = I;
        int d = dist[i][j] - 1;

        while (Next != J && d > 0) {
            boolean flag = false;
            //System.out.println("*********From " + Next + "**********");
            for (Node n : Next.Neighbors.values()) {
//                System.out.println("d:" + d);
//                System.out.println("neighbor:" + n);
//                System.out.println("dist: " + dist[n.ID][j]);

                if (dist[n.ID][j] == d) {
                    Next = n;
                    //    System.out.println("Next: " + Next);

                    d = d - 1;
                    path.add(n.ID);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                break;
            }
        }

        path.add(j);
        return path;
    }

    public int[][] costMatrix() {

        cost = new int[Flows.size()][Nodes.size()];
        //  System.out.println("Flows"+ Flows);
        for (int i = 0; i < Flows.size(); i++) {

            Flow F = Flows.get(i);

            //   System.out.println("Getting path of  "+ F);
            ArrayList<Integer> path = getPath(F.FROM, F.TO);
            //    System.out.println(i + "(" + F.Datarate + "): " + path);
            for (Node VM : FreeVMS.values()) {
                int min_d = Integer.MAX_VALUE;
                int min_SDN = 0;
                for (int SDN : path) {
                    if (dist[SDN][VM.ID] < min_d) {
                        min_d = dist[SDN][VM.ID];
                        min_SDN = SDN;
                    }
                }

                Node CPF = Nodes.get(min_SDN);
                if (CPF.Type.equals("R") || CPF.Type.equals("PM")) {
                    min_d = min_d - 1;
                } else {
                    min_d = min_d - 2;
                }
                cost[i][VM.ID] = (min_d) * F.Datarate;
            }
        }

        cost2 = new double[cost.length][cost[0].length];
        for (int i = 0; i < cost.length; i++) {
            for (int j = 0; j < cost[0].length; j++) {
                cost2[i][j] = cost[i][j];
            }
        }
        return cost;
    }

    void printCostMatrix() {
        System.out.println("The following matrix shows the cost matrix");

        for (int i = 0; i < Flows.size(); ++i) {

            for (int j = 0; j < Nodes.size(); ++j) {

                if (FreeVMS.containsKey(j)) {
                    if (cost[i][j] == INF) {
                        System.out.print("INF ");
                    } else {
                        String x = "(" + i + "," + j + ")" + cost[i][j];
                        System.out.print(x + "          ".substring(0, 10 - x.length()));
                    }
                }
            }
            System.out.println();
        }
    }

    Map<String, Map<String, Double>> getCostMap() {
        Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();

        for (Flow F : Flows.values()) {
            Map<String, Double> m = new HashMap<String, Double>();
            map.put("F" + F.ID, m);
        }
        for (Node n : FreeVMS.values()) {
            Map<String, Double> m = new HashMap<String, Double>();
            map.put("VM" + n.ID, m);
        }

        for (int i = 0; i < Flows.size(); ++i) {
            Map<String, Double> Fm = map.get("F" + i);
            Flow F = Flows.get(i);
            for (int j = 0; j < Nodes.size(); ++j) {
                if (FreeVMS.containsKey(j)) {
                    Map<String, Double> VMm = map.get("VM" + j);
                    Node VM = Nodes.get(j);
                    Fm.put("VM" + VM.ID, (double) cost[i][j]);
                    VMm.put("F" + F.ID, (double) cost[i][j]);

                }
            }
            // System.out.println();

        }

        return map;
    }

    Map<String, Map<String, Double>> getCostMap(int X) {
        Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();

        for (Flow F : Flows.values()) {
            Map<String, Double> m = new HashMap<String, Double>();
            map.put("F" + F.ID, m);
        }
        for (Node n : FreeVMS.values()) {
            Map<String, Double> m = new HashMap<String, Double>();
            for (int k = 0; k < X; k++) {
                //  map.put("VM" + n.ID + "_" + k, m);
            }
        }

        for (int i = 0; i < Flows.size(); ++i) {
            Map<String, Double> Fm = map.get("F" + i);
            Flow F = Flows.get(i);
            for (int j = 0; j < Nodes.size(); ++j) {
                if (FreeVMS.containsKey(j)) {

                    Node VM = Nodes.get(j);
                    for (int k = 0; k < X; k++) {
                        //  Map<String, Double> VMm = map.get("VM" + j + "_" + k);
                        // VMm.put("F" + F.ID, (double) cost[i][j]);
                        Fm.put("VM" + VM.ID + "_" + k, (double) cost[i][j]);

                    }

                }
            }
            // System.out.println();

        }

        return map;
    }

    void flow_generation(int FlowNum, int FreeVMNum) throws FileNotFoundException {
        ArrayList<Node> VMS = new ArrayList<>();

        for (Node n : Nodes.values()) {
            if (n.Type.equals("VM")) {
                VMS.add(n);
                //   System.out.println(n.Type);
            }
        }

        ArrayList<Node> SelectedVMS = new ArrayList<>();

        //  System.out.println("VMS:"+VMS.size());
        while (VMS.size() > FreeVMNum) {
            int index = ((int) (Math.random() * Integer.MAX_VALUE)) % VMS.size();
            Node n = VMS.remove(index);
            SelectedVMS.add(n);
            // System.out.println("hii");
        }
        //  System.out.println(SelectedVMS.size());

        ArrayList<Pair> Pairs = new ArrayList<>();
        for (int i = 0; i < SelectedVMS.size(); i++) {
            for (int j = i + 1; j < SelectedVMS.size(); j++) {
                Pair p = new Pair();
                p.A = i;
                p.B = j;
                Pairs.add(p);
                //   System.out.println("pair: added "+ i+ "-"+j);
            }
        }

        int fID = 0;
        HashMap<String, Flow> flows = new HashMap<String, Flow>();
        while (flows.size() < FlowNum) {

            if (Pairs.size() == 0) {
                break;
            }
            int index = (int) (Math.random() * Integer.MAX_VALUE) % Pairs.size();

            Pair p = Pairs.get(index);
            Pairs.remove(index);
            Flow f = new Flow(fID++, SelectedVMS.get(p.A).ID, SelectedVMS.get(p.B).ID, getRandValue(DATARATES));

            //  System.out.println("flow: added "+ f);
            flows.put(f.ID + "", f);

        }

        FreeVMS.putAll(this.VMS);
        Flows.clear();
        for (Flow f : flows.values()) {
            Flows.put(f.ID, f);
            if (FreeVMS.containsKey(f.TO)) {
                FreeVMS.remove(f.TO);
            }
            if (FreeVMS.containsKey(f.FROM)) {
                FreeVMS.remove(f.FROM);
            }
        }
        //System.out.println("After generation Flows: " + flows);
        //System.out.println("After generation Flows: " + Flows);

    }

    static int getRandValue(int[] a) {
        int x = (int) (Math.random() * Integer.MAX_VALUE);
        x = x % a.length;
        return a[x];
    }

    private class Pair {

        int A;
        int B;
    }

    public void generateTree(int N, int degree) throws IOException {
        ArrayList<Node> nodes = new ArrayList< Node>();
        ArrayList<Node> nodes2 = new ArrayList< Node>();
        HashMap<Integer, Node> leafs = new HashMap<>();
        for (int i = 0; i < N; i++) {
            Node I = new Node(i);
            I.Type = "R";
            nodes.add(I);
            nodes2.add(I);
            leafs.put(i, I);

            this.Nodes.put(i, I);
        }

        //  System.out.println("This nodes:" + Nodes);
        ArrayList<Node> Branches = new ArrayList< Node>();
        Branches.add(nodes.get(0));
        Node root = nodes.remove(0);
        root.x = 200;
        root.y = 50;
        int eID = 0;
        while (!nodes.isEmpty()) {
            Node n = nodes.remove(0);
            //System.out.println(Branches.size());
            int rand_parent = (int) (Math.random() * Branches.size() * 100) % Branches.size();
            //System.out.println(rand_parent);
            Node p = Branches.get(rand_parent);

            leafs.remove(p.ID);
            n.x = p.x + p.Neighbors.size() * p.Neighbors.size() * 20;
            n.y = p.y + 30;

            if (p.Neighbors.size() < degree) {
                p.addNeighbor(n);
                Branches.add(n);
                Edge e = new Edge(p, n, eID);
                this.Edges.put(eID++, e);
            } else {
                Branches.remove(rand_parent);
                nodes.add(n);
            }

        }

        //  System.out.println("This nodes:" + Nodes);
        for (Node n : leafs.values()) {
            if (Math.random() < PM_PROB) {
                int numPms = getRandValue(PM_NUMBERS);
                for (int i = 0; i < numPms; i++) {
                    Node PM = new Node(N++, n.x + i * 18 - numPms * 15 / 2, n.y + 20 + 15 * i * i);
                    PM.Type = "PM";
                    PM.color = Color.BLUE;
                    n.addNeighbor(PM);
                    Edge e = new Edge(PM, n, eID++);
                    this.Edges.put(e.ID, e);
                    this.Nodes.put(PM.ID, PM);
                    this.PMS.put(PM.ID, PM);

                    // System.out.println("PM+" + PM);
                    int numVms = getRandValue(VM_NUMBERS);
                    for (int j = 0; j < numVms; j++) {
                        Node VM = new Node(N++, PM.x + j * 15 - numVms * 15 / 2, PM.y + 20 + 4 * j * j);
                        VM.Type = "VM";
                        VM.color = Color.GREEN;
                        PM.addNeighbor(VM);
                        this.Nodes.put(VM.ID, VM);
                        this.VMS.put(VM.ID, VM);

                        //    System.out.println("VM+" + VM);
                        Edge ee = new Edge(PM, VM, eID++);
                        this.Edges.put(ee.ID, ee);
                    }
                }
            }
        }

        //  System.out.println("This nodes:" + Nodes);
        flow_generation(Constants.FlowNum, Constants.FreeVMNum);

    }

    int totalBW() {
        int sum = 0;
        for (Flow f : Flows.values()) {
            sum += f.Datarate;
        }
        return sum;
    }

    int totalFlows() {

        return Flows.size();
    }

    static Map<String, Map<String, Double>> costMapFilter(HashMap<Integer, Node> SelectedVMS, Map<String, Map<String, Double>> MAP) {
        Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();
        for (String key : MAP.keySet()) {
            Map<String, Double> m = MAP.get(key);
            map.put(key, new HashMap<String, Double>(m));
        }

        for (Map<String, Double> m : map.values()) {
            ArrayList<String> COLS = new ArrayList<>(m.keySet());
            for (String VM_K : COLS) {
                String VM = VM_K.substring(0, VM_K.indexOf("_"));

                VM = VM.replace("VM", "");
                //  System.out.println("VM_ID: "+VM);
                if (!SelectedVMS.containsKey(Integer.parseInt(VM))) {
                    m.remove(VM_K);
                }
            }
        }

        return map;
    }
}
