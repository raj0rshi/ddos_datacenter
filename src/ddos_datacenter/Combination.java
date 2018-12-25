package ddos_datacenter;

// Java program to print all combination of size r in an array of size n 
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Combination {

    static HashMap<Integer, Node> FreeVMS;
    static double min_cost = Double.MAX_VALUE;
    static Map<String, Map<String, Double>> CM;
    static Main.Result min_result;

    static void combinationUtil(int arr[], int data[], int start,
            int end, int index, int r) throws Exception {
        if (index == r) {
            HashMap<Integer, Node> SelectedVMS = new HashMap<>();
            for (int j = 0; j < data.length; j++) {
                // System.out.print(data[j] + " ");
                Node VM = (Node) FreeVMS.get(data[j]);
                SelectedVMS.put(VM.ID, VM);
            }
            Map<String, Map<String, Double>> CMF = Graph.costMapFilter(SelectedVMS, CM);

            //System.out.println("CMF: "+CMF);
            Main.Result result = new Main().apply(CMF);
            //   System.out.println("result: " + result.weight);

            if (min_cost > result.weight) {
                min_cost = result.weight;
                min_result = result;
                //     System.out.println("result updated: " + min_result.weight);
            }
            return;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r);
        }
    }

    static void printCombination(int arr[], int n, int r) throws Exception {

        int data[] = new int[r];

        combinationUtil(arr, data, 0, n - 1, 0, r);
    }

    /*Driver function to check for above function*/
    public static void main(String[] args) throws Exception {
        int arr[] = {1, 2, 3, 4, 5};
        int r = 3;
        int n = arr.length;
        printCombination(arr, n, r);
    }

    static Main.Result OptimalCost(Map<String, Map<String, Double>> CM, HashMap<Integer, Node> FreeVMS, int budget) throws Exception {

        Combination.CM = CM;
        Combination.FreeVMS = FreeVMS;
        int arr[] = new int[FreeVMS.size()];
        int index = 0;
        for (int i : FreeVMS.keySet()) {
            arr[index++] = i;
        }
        int r = budget;
        int n = arr.length;
        printCombination(arr, n, r);
        return min_result;
    }
}

/* This code is contributed by Devesh Agrawal */
