/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author rajor
 */
public class StatHelper {

    static double avg(ArrayList<Double> A) {
        double sum = 0;
        for (double d : A) {
            sum += d;
        }
        return sum / A.size();
    }

    static double max(ArrayList<Double> A) {
        double max = Double.MIN_VALUE;
        for (double d : A) {
            if (max < d) {
                max = d;
            }
        }
        return max;
    }

    static double min(ArrayList<Double> A) {
        double min = Double.MAX_VALUE;
        for (double d : A) {
            if (min > d) {
                min = d;
            }
        }
        return min;
    }

    static double std(ArrayList<Double> A) {

        double avg = avg(A);
        double sum = 0;
        for (double d : A) {
            sum += (avg - d) * (avg - d);
        }
        return Math.sqrt(sum / A.size());
    }

    static Map<String, Double> sort(Map<String, Double> unsortedMap) {
        //convert map to a List
        List<Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());

        //sorting the list with a comparator
        Collections.sort(list, new Comparator<Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        //convert sortedMap back to Map
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
