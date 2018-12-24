package ddos_datacenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.PrintWriter;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        int topNum = 4;
        if (args.length > 0) {
            topNum = Integer.parseInt(args[0]);
        }
        File f = new File("output_TOP_"+topNum+".txt");
        if (f.exists()) {
            f.delete();
        }
        PrintWriter pw = new PrintWriter(f);

        boolean flag = true;
        for (int i = 10; i < 500; i++) {
            ArrayList<Double> Cost = new ArrayList<>();
            for (int r = 0; r < 100; r++) {

                Constants.FlowNum = i;
                System.out.println("*************round:" + r + "***************");
                Map<String, Map<String, Double>> CM;

                Graph g = new Graph(topNum);
                g.ReadFile();
                if (flag) {
                    //   GraphGeneration.generate(topNum);

                    // g.Draw();
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
                //System.out.println(result.assignment);
                System.out.println(result.weight);
                Cost.add(result.weight);
            }

            double avg = StatHelper.avg(Cost);
            double std = StatHelper.std(Cost);

            pw.println(avg + "\t" + std);
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

}
