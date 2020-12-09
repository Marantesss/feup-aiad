package draw;

import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;

import java.awt.*;

public class Edge extends DefaultEdge implements DrawableEdge {
    private Color color = Color.BLACK;
    private static final float DEFAULT_STRENGTH = 1;

    public Edge(Node from, Node to) {
        super (from, to, "", DEFAULT_STRENGTH);
    }

    @Override
    public void draw(SimGraphics simGraphics, int fromX, int toX, int fromY, int toY) {
        simGraphics.drawLink(color, fromX, toX, fromY, toY);
    }
}
