package draw;

import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;

import java.awt.*;

public class Edge extends DefaultEdge implements DrawableEdge {
    private Color color;
    private static final float DEFAULT_STRENGTH = 1;

    public Edge() { }

    public Edge(Node from, Node to) {
        super (from, to, "", DEFAULT_STRENGTH);
    }

    public Edge(Node from, Node to, float strength) {
        super(from, to, "", strength);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void draw(SimGraphics simGraphics, int fromX, int toX, int fromY, int toY) {
        simGraphics.drawDirectedLink(color, fromX, toX, fromY, toY);
    }
}
