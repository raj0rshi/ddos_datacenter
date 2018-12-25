package ddos_datacenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, Exception {

        int budget = 2;
        int topNum = 5;
        if (args.length > 0) {
            topNum = Integer.parseInt(args[0]);
        }
        Graph g = new Graph(topNum);
        g.ReadFile();
        g.CalculateAllPairShortestPath();
        g.costMatrix();

        int x = (int) Math.ceil(g.totalFlows() / (double) budget);
        Map<String, Map<String, Double>> CM = g.getCostMap(x);

        HashMap<Integer, Node> SelectedVMS = new HashMap<>();
        for (int i = 0; i < budget; i++) {
            Node VM = (Node) g.FreeVMS.values().toArray()[i];
            SelectedVMS.put(VM.ID, VM);

        }
        // System.out.println("Selected VM: " + SelectedVMS);

        Map<String, Map<String, Double>> CMF = g.costMapFilter(SelectedVMS, CM);
        System.out.println("CM: " + CM);
        // System.out.println("CMF: " + CMF);
        Result result = new Main().apply(CMF);
        System.out.println(result.assignment);
        System.out.println(result.weight);

        Result result_op = Combination.OptimalCost(CM, g.FreeVMS, budget);
        System.out.println("OP: " + result_op.assignment);
        System.out.println("OP: " + result_op.weight);

        Result result_kmnn = KMNNCost(g, budget);
        System.out.println("KMNN: " + result_kmnn.assignment);
        System.out.println("KMNN: " + result_kmnn.weight);
    }

    public static void main2(String[] args) throws Exception {
        int topNum = 4;
        if (args.length > 0) {
            topNum = Integer.parseInt(args[0]);
        }
        File f = new File("output_TREE_DEGREE_" + topNum + ".txt");
        if (f.exists()) {
            f.delete();
        }
        PrintWriter pw = new PrintWriter(f);

        boolean flag = true;
        for (int i = 10; i <= 100; i += 10) {
            ArrayList<Double> Cost = new ArrayList<>();
            ArrayList<Double> BW = new ArrayList<>();
            ArrayList<Double> FL_COUNT = new ArrayList<>();
            for (int r = 0; r < 100; r++) {

                System.out.print("*");
                Map<String, Map<String, Double>> CM;

                Graph g = new Graph(topNum);
                g.generateTree(i, topNum);
                BW.add((double) g.totalBW());
                FL_COUNT.add((double) g.Flows.size());

//                System.out.println("Nodes:" + g.Nodes);
//                System.out.println("Edes:" + g.Edges);
//
//                System.out.println("VMS:" + g.VMS);
//                System.out.println("FreeVMS:" + g.FreeVMS);
//                System.out.println("Flows:" + g.Flows);
                if (flag) {
                    //   GraphGeneration.generate(topNum);

                    //    g.Draw();
                    flag = false;

                }
                g.flow_generation(Constants.FlowNum, Constants.FreeVMNum);
                g.CalculateAllPairShortestPath();
                //g.printDist();
                //System.out.println("Path: " + g.getPath(10, 36));
                g.costMatrix();
                // g.printCostMatrix();
                CM = g.getCostMap((int) Math.ceil(((double) g.Flows.size()) / g.FreeVMS.size()));
                // System.out.println(CM);
                //  System.out.println("VMS:" + g.VMS.size() + "-" + g.VMS);
                // System.out.println("FreeVMS:" + g.FreeVMS.size() + "-" + g.FreeVMS);
                Result result = new Main().apply(CM);
                // System.out.println(result.assignment);
                //  System.out.println(result.weight);
                Cost.add(result.weight);

            }

            double avgC = StatHelper.avg(Cost);
            double stdC = StatHelper.std(Cost);
            double avgBW = StatHelper.avg(BW);
            double avgFlows = StatHelper.avg(FL_COUNT);
            System.out.println("\n" + i + " " + avgC);

            pw.println(i + "\t" + avgC + "\t" + stdC + "\t" + avgBW + "\t" + avgFlows);
            pw.flush();
        }
        pw.close();

    }

    public Result apply(Map<String, Map<String, Double>> input) throws Exception {
        // A bipartite graph can be split into 2 node sets
        Set<String> lhsNodes = new HashSet<>(input.keySet());
        Set<String> rhsNodes = new HashSet<>();

        // Put each node in a set
        fillNodeSets(input, lhsNodes, rhsNodes);

        // Get lists for indexing
        List<String> lhsNodesList = new ArrayList<>(lhsNodes);
        List<String> rhsNodesList = new ArrayList<>(rhsNodes);

        //   System.out.println("LHS: " + lhsNodes);
        //   System.out.println("RHS: " + rhsNodes);
        // Calculate the weights matrix and run the Hungarian Algorithm
        double[][] weights = calculateWeights(input, lhsNodesList, rhsNodesList);
        int[] output = new HungarianAlgorithm(weights).execute();
//        for (int o : output) {
//            System.out.print(o + " ");
//        }
//        System.out.println("");

        // Parse the output
        Map<String, String> matches = getMatchPairs(output, lhsNodesList, rhsNodesList);
        double totalWeight = calculateTotalWeight(output, weights);

        // Return the result
        return new Result(matches, totalWeight);
    }

    private void fillNodeSets(Map<String, Map<String, Double>> input, Set<String> lhsNodes, Set<String> rhsNodes) {
        input.forEach((node, neighbours) -> neighbours.forEach((neighbour, weight) -> {
            if (lhsNodes.contains(node)) {
                lhsNodes.remove(neighbour);
                rhsNodes.add(neighbour);
            }
        }));
    }

    private double[][] calculateWeights(Map<String, Map<String, Double>> input, List<String> lhsNodesList, List<String> rhsNodesList) {
        int m = lhsNodesList.size();
        int n = rhsNodesList.size();
        double[][] weights = new double[m][n];

        for (int i = 0; i < m; i++) {
            String node1 = lhsNodesList.get(i);
            Map<String, Double> edges = input.get(node1);

            for (Map.Entry<String, Double> entry : edges.entrySet()) {
                String node2 = entry.getKey();
                double weight = entry.getValue();
                int j = rhsNodesList.indexOf(node2);
                weights[i][j] = weight;
            }
        }

        return weights;
    }

    private double calculateTotalWeight(int[] output, double[][] weights) {
        double weight = 0;

        for (int i = 0; i < output.length; i++) {
            weight += weights[i][output[i]];
        }

        return weight;
    }

    private Map<String, String> getMatchPairs(int[] output, List<String> lhsNodesList, List<String> rhsNodesList) {
        Map<String, String> matches = new HashMap<>();
        int n = lhsNodesList.size();
        for (int i = 0; i < n; i++) {
            // System.out.println(i);
            //System.out.println(output[i]);
            matches.put(lhsNodesList.get(i), rhsNodesList.get(output[i]));
        }

        return matches;
    }

    public static class Result {

        public final Map<String, String> assignment;
        public final double weight;

        public Result(Map<String, String> assignment, double weight) {
            this.assignment = assignment;
            this.weight = weight;
        }

    }

    private static Result KMNNCost(Graph g, int budget) throws Exception {

        int k = (int) Math.ceil(g.Flows.size() / (double) budget);
        Map<String, Map<String, Double>> CM = g.getCostMap(1);
        HashMap<String, ArrayList<Double>> CMT = new HashMap<>();
        for (Map<String, Double> M : CM.values()) {
            for (String key : M.keySet()) {
                double val = M.get(key);
                if (CMT.containsKey(key)) {
                    ArrayList<Double> AL = CMT.get(key);
                    AL.add(val);
                } else {
                    ArrayList<Double> AL = new ArrayList<>();
                    AL.add(val);
                    CMT.put(key, AL);

                }
            }

        }
        // System.out.println("CMT:" + CMT);

        HashMap<String, Double> MKCOST = new HashMap<>();

        for (String key : CMT.keySet()) {
            ArrayList<Double> AL = CMT.get(key);

            double sum = 0;
            for (int i = 0; i < k; i++) {
                double min = Collections.min(AL);
                int min_i = AL.indexOf(min);
                AL.remove(min_i);
                sum += min;
            }
            MKCOST.put(key, sum);
        }

        Map<String, Double> MKCOST_SORTED = StatHelper.sort(MKCOST);
        // System.out.println("SORTED_ MKCOST: " + MKCOST_SORTED);

        HashMap<Integer, Node> SelectedVMs = new HashMap<>();
        for (String key : MKCOST_SORTED.keySet()) {
            // System.out.println(key + MKCOST_SORTED.get(key));
            String ID = key.substring(0, key.indexOf("_"));
            ID = ID.replace("VM", "");
            int id = Integer.parseInt(ID);
            SelectedVMs.put(id, g.FreeVMS.get(id));
            if (SelectedVMs.size() == budget) {
                break;
            }

        }

        Map<String, Map<String, Double>> CMF = Graph.costMapFilter(SelectedVMs, g.getCostMap(k));
        //System.out.println("SELECTED VMS: "+ CMF);
        return new Main().apply(CMF);
    }

    private static Result GreedyCost(Graph g, int budget) {
        return null;
    }

}
