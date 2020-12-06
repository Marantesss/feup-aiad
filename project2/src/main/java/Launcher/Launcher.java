package Launcher;

import agents.DeveloperAgent;
import agents.ScrumMasterAgent;
import io.ConfigReader;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Launcher extends Repast3Launcher {
    private ContainerController mainController;
    private static List<DefaultDrawableNode> nodes;

    private static List<DeveloperAgent> developerAgents;
    private ScrumMasterAgent scrumMasterAgent;

    private final int WIDTH = 400;
    private final int HEIGHT = 400;
    private final int RADIUS = 100;

    private DisplaySurface dsurf;
    private OpenSequenceGraph plot;

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();

        mainController = rt.createMainContainer(profile);

        try {
            launchAgents();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private void launchAgents() throws StaleProxyException {
        ConfigReader reader;
        try {
            System.out.println("Reading config file...");
            reader = new ConfigReader("json/small.test.json");  // TODO: Attention to this
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create developer agents
        int developerCount = 0;
        Random random = new Random(System.currentTimeMillis());
        nodes = new ArrayList<>();
        developerAgents =  new ArrayList<>();

        for (var aoe : reader.getDevelopersExpertise()) {
            DeveloperAgent developerAgent =  new DeveloperAgent(++developerCount, aoe, reader.getNumberOfExpertise());
            DefaultDrawableNode node = generateNode(Color.RED, developerAgent.getX(RADIUS, WIDTH), developerAgent.getY(RADIUS, HEIGHT));

            developerAgents.add(developerAgent);
            developerAgent.setNode(node);
            nodes.add(node);

            mainController.acceptNewAgent("Developer" + developerCount, developerAgent).start();
        }

        // Create scrum master agent
        scrumMasterAgent = new ScrumMasterAgent(reader.getStrategy(), reader.generateResultsFilePath(), reader.getTasks());
        DefaultDrawableNode node = generateNode(Color.BLUE, WIDTH/2, HEIGHT/2);

        scrumMasterAgent.setNode(node);
        nodes.add(node);

        mainController.acceptNewAgent("ScrumMaster", scrumMasterAgent).start();
    }

    private DefaultDrawableNode generateNode(Color color, int x , int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(30);
        oval.setWidth(30);

        DefaultDrawableNode node = new DefaultDrawableNode("", oval);
        node.setColor(color);

        return node;
    }

    public static DefaultDrawableNode getNode(AID aid) {
        for (DeveloperAgent agent : developerAgents) {
            if(aid.getLocalName().equals(agent.getAID().getLocalName()))
                return agent.getNode();
        }

        return null;
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void begin() {
        super.begin();
        buildAndScheduleDisplay();
    }

    private void buildAndScheduleDisplay() {
        // Display
        if (dsurf != null) dsurf.dispose();

        dsurf = new DisplaySurface(this, "Task Allocation");
        registerDisplaySurface("Task Allocation", dsurf);

        Network2DDisplay display = new Network2DDisplay(nodes, WIDTH, HEIGHT);
        dsurf.addDisplayableProbeable(display, "Network Display");
        dsurf.addZoomable(display);
        addSimEventListener(dsurf);
        dsurf.setBackground(Color.WHITE);
        dsurf.display();

        // Graph
        if (plot != null) plot.dispose();
        plot = new OpenSequenceGraph("Service performance", this);
        plot.setAxisTitles("time", "% successful service executions");

        plot.addSequence("Developers", new Sequence() {
            public double getSValue() {
                double v = 0.0;
                for (DeveloperAgent developerAgent : developerAgents) {
                    v += developerAgent.getMovingAverage(10);
                }
                return v / developerAgents.size();
            }
        });

        plot.display();

        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST);
    }

    @Override
    public String[] getInitParam() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Scrum Agile Development -- SAJaS Repast3";
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Need filename as argument");
            return;
        }

        SimInit init = new SimInit();
        init.setNumRuns(1);
        init.loadModel(new Launcher(), null, true);
    }
}
