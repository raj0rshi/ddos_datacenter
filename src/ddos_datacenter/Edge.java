/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

import java.awt.Color;

/**
 *
 * @author rajor
 */
public class Edge {

    Node A;
    Node B;
    int ID;

    Color color = Color.black;
    boolean congested = false;

    Edge(Node a, Node b, int ID) {
        A = a;
        B = b;
        this.ID = ID;
    }

    @Override
    public String toString() {
        return ID + "";
    }

    void setDirection(Node N) {

        if (B.ID != N.ID) {
            Node temp = B;
            B = A;
            A = temp;
        }

    }
}
