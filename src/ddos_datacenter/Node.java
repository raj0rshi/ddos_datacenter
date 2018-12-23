/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rajor
 */
public class Node {

    int ID;
    int x;
    int y;
    String Type = "R";
    Color color = Color.GRAY;
    HashMap<Integer, Node> Neighbors;
    HashMap<Integer, Edge> Edges;

    int N_PATH = 0;
    int NC_PATH = 0;

    int N_PATH_D = 0;
    int NC_PATH_D = 0;
    ArrayList<Integer> NH_D;
    ArrayList<Integer> NH;
    int D = -1;
    int D_D = -1;

    ArrayList<Integer> MQ = new ArrayList<>();
    int MQ_SIZE = 10000;

    void Enqueue(int m) {
        if (MQ.size() < MQ_SIZE) {
            MQ.add(m);
        } else {
            System.out.println("packet dropped!!");
        }
    }

    public Node(int ID) {
        this.ID = ID;
        Neighbors = new HashMap<Integer, Node>();
        Edges = new HashMap<Integer, Edge>();
        NH_D = new ArrayList<Integer>();
        NH = new ArrayList<Integer>();
    }

    public Node(int ID, int x, int y) {
        this.ID = ID;
        Neighbors = new HashMap<Integer, Node>();
        Edges = new HashMap<Integer, Edge>();
        NH_D = new ArrayList<Integer>();
        NH = new ArrayList<Integer>();

        this.x = x;
        this.y = y;
    }

    public void addNeighbor(Node n) {
        if (!n.Neighbors.containsKey(n.ID)) {
            Neighbors.put(n.ID, n);
            n.Neighbors.put(ID, this);
        } else {
            System.out.println("node " + n.ID + " already exits as neighbor of " + ID);
        }
    }

    Edge getLink(int ID) {
        Edge er = null;

        for (Edge e : Edges.values()) {
            if ((e.A.ID == this.ID) && (e.B.ID == ID)) {
                er = e;
            }
            if ((e.B.ID == this.ID) && (e.A.ID == ID)) {
                er = e;
            }
        }
        return er;
    }

    Edge getLink(Node n) {
        int ID = n.ID;
        Edge er = null;

        for (Edge e : Edges.values()) {
            if ((e.A.ID == this.ID) && (e.B.ID == ID)) {
                er = e;
            }
            if ((e.B.ID == this.ID) && (e.A.ID == ID)) {
                er = e;
            }
        }
        return er;
    }

    @Override
    public String toString() {
        return ID + "";
    }
}
