/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

/**
 *
 * @author rajor
 */
public class Constants {

    public static int topNum = 6;
    static String TOPOLOGY_FILE_EDGES = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\edges_" + topNum + ".txt";
    static String TOPOLOGY_FILE_NODES = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\nodes_" + topNum + ".txt";
    static String TOPOLOGY_FILE_FLOWS = "F:\\OneDrive - Temple University\\NetBeansProjects\\DDOS_DATACENTER\\topologies\\flows_" + topNum + ".txt";

    public static final double PM_PROB = .5;

    public static final int[] PM_NUMBERS = {1,2,3,};
    public static final int[] VM_NUMBERS = {1,2,3, 4,5,6,7};
    public static final int[] DATARATES = {1, 2, 3, 4, 5, 6};

    public static int FlowNum = 15;
    public static int FreeVMNum = 5;
    public static int VM_CAP = 10;

}
