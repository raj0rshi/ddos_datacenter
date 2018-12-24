/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_datacenter;

import java.util.HashMap;

/**
 *
 * @author rajor
 */
public class Flow {

    int ID;
    int TO;
    int FROM;
    int Datarate = 0;

    public Flow(int TO, int FROM, int Datarate) {
        ID = 0;
        this.TO = TO;
        this.FROM = FROM;
        this.Datarate = Datarate;

    }

    public Flow(int ID, int TO, int FROM, int Datarate) {
        this.ID = ID;
        this.TO = TO;
        this.FROM = FROM;
        this.Datarate = Datarate;

    }

    @Override
    public String toString() {
        return ID + "("+FROM+"=>"+TO+")";
    }

}
