package ddos_datacenter;

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class GraphDraw extends JFrame {

    int width;
    int height;

    public static ArrayList<Node> nodes;
    public static ArrayList<Edge> edges;
    private double amp = 2;

    public GraphDraw(ArrayList nodes, ArrayList edges) { //Construct with label
        this.setTitle("Graph Draw");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        this.nodes = nodes;
        this.edges = edges;
        width = (10);
        height = (10);

    }

    synchronized public void paint(Graphics g) { // draw the nodes and edges
        // System.out.println("drawing");
        FontMetrics f = g.getFontMetrics();
        int nodeHeight = amplify(height);
        int nodeWidth = amplify(width);
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        for (int pp = 0; pp < edges.size(); pp++) {
            Edge e = edges.get(pp);
            g.setColor(e.color);
            Node xx = e.A;
            Node yy = e.B;
          //  System.out.println("xx "+ xx);
          //  System.out.println("yy "+ yy);

            g2.setStroke(new BasicStroke(2));
            if (e.color.getRGB() != Color.white.getRGB()) {
                g.drawLine(amplify(xx.x), amplify(xx.y), amplify(yy.x), amplify(yy.y));
            }
            g2.setStroke(new BasicStroke(1));

            // g.drawString(e.ID + "", (amplify(e.A.x) + amplify(e.B.x)) / 2, (amplify(e.A.y) + amplify(e.B.y)) / 2);
        }
        g.setColor(Color.black);
        for (Node n : nodes) {

            g.setColor(n.color);
            if (n.color == Color.GRAY) {
                g.setColor(Color.white);
            }

            g.fillOval(amplify(n.x) - nodeWidth / 2, amplify(n.y) - nodeHeight / 2, nodeWidth, nodeHeight);
            if (n.color.getRGB() != Color.black.getRGB()) {
                g.setColor(Color.black);
                g2.setStroke(new BasicStroke(3));
                g.drawOval(amplify(n.x) - nodeWidth / 2 - 2, amplify(n.y) - nodeHeight / 2 - 2, nodeWidth + 4, nodeHeight + 4);
            }

            //  g.drawOval(amplify(n.x) - nodeWidth / 2, amplify(n.y) - nodeHeight / 2, nodeWidth, nodeHeight);
            g.setColor(Color.white);
            if (n.color == Color.GRAY) {
                g.setColor(Color.black);
            }
            g.drawString(n.ID + "", amplify(n.x) - f.stringWidth(n.ID + "") / 2, amplify(n.y) + f.getHeight() / 2);

        }
    }

    public int amplify(int x) {
        return (int) (x * amp);
    }

    public double amplify(double x) {
        return (x * amp);
    }

    synchronized public void EdgeColor(Edge e, Color c) {

        if (e == null) {
            return;
        }
        e.color = c;
        //this.repaint();

    }

}
