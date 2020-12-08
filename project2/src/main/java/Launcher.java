import agents.RandomExpertiseGenerator;
import agents.DeveloperAgent;
import agents.ScrumMasterAgent;
import io.ConfigReader;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import uchicago.src.reflector.ListPropertyDescriptor;
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
import java.util.*;
import java.util.List;

public class Launcher extends Repast3Launcher {
    private ContainerController mainController;
    private static List<DefaultDrawableNode> nodes;

    private List<DeveloperAgent> developerAgents;
    private ScrumMasterAgent scrumMasterAgent;

    private final int WIDTH = 200, HEIGHT = 200;
    private DisplaySurface dsurf;
    private OpenSequenceGraph plot;

    // ---
    // GUI Variables
    private static final boolean BATCH_MODE = true;
    private int NUMBER_OF_TASKS = 100;
    private int NUMBER_OF_DEVELOPERS = 6;
    private String STRATEGY = "random";

    public Launcher() {
        // Create strategy dropdown
        Hashtable h1 = new Hashtable();
        h1.put("random", "Random");
        h1.put("lowesttime", "Lowest Time");
        h1.put("numberoftasks", "Number of Tasks");
        ListPropertyDescriptor pd = new ListPropertyDescriptor("STRATEGY", h1);
        descriptors.put("STRATEGY", pd);
    }

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

    public String getSTRATEGY() {
        return STRATEGY;
    }

    public void setSTRATEGY(String STRATEGY) {
        this.STRATEGY = STRATEGY;
    }

    public int getNUMBER_OF_TASKS() {
        return NUMBER_OF_TASKS;
    }

    public void setNUMBER_OF_TASKS(int NUMBER_OF_TASKS) {
        this.NUMBER_OF_TASKS = NUMBER_OF_TASKS;
    }

    public int getNUMBER_OF_DEVELOPERS() {
        return NUMBER_OF_DEVELOPERS;
    }

    public void setNUMBER_OF_DEVELOPERS(int NUMBER_OF_DEVELOPERS) {
        this.NUMBER_OF_DEVELOPERS = NUMBER_OF_DEVELOPERS;
    }

    private void launchAgents() throws StaleProxyException {
        ConfigReader reader;
        try {
            System.out.println("Reading config file...");
            reader = new ConfigReader("json/small.test.json");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // create developer agents
        int developerCount = 0;
        Random random = new Random(System.currentTimeMillis());
        nodes = new ArrayList<>();
        developerAgents =  new ArrayList<>();

        //Use RandomExpertiseGenerator.getRandomExpertise(<numDevs>) to generate random agents
        for (var aoe : reader.getDevelopersExpertise()) {
            DeveloperAgent developerAgent =  new DeveloperAgent(++developerCount, aoe);
            DefaultDrawableNode node = generateNode("developer" + developerCount, Color.RED, random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));

            developerAgents.add(developerAgent);
            developerAgent.setNode(node);
            nodes.add(node);

            mainController.acceptNewAgent("developer" + developerCount, developerAgent).start();
        }

        // create scrum master agent
        scrumMasterAgent = new ScrumMasterAgent(reader.pickStrategy(STRATEGY), reader.generateResultsFilePath(), reader.getTasks());
        DefaultDrawableNode node = generateNode("ScrumMaster", Color.WHITE, random.nextInt(WIDTH/2),random.nextInt(HEIGHT/2));

        scrumMasterAgent.setNode(node);
        nodes.add(node);

        mainController.acceptNewAgent("ScrumMaster", scrumMasterAgent).start();
    }

    private DefaultDrawableNode generateNode(String label, Color color, int x , int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(5);
        oval.setWidth(5);

        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        node.setColor(color);

        return node;
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
        // display surface
        if (dsurf != null) dsurf.dispose();
        dsurf = new DisplaySurface(this, "Service Consumer/Provider Display");
        registerDisplaySurface("Service Consumer/Provider Display", dsurf);
        Network2DDisplay display = new Network2DDisplay(nodes,WIDTH,HEIGHT);
        dsurf.addDisplayableProbeable(display, "Network Display");
        dsurf.addZoomable(display);
        addSimEventListener(dsurf);
        dsurf.display();

        // graph
        if (plot != null) plot.dispose();
        plot = new OpenSequenceGraph("Service performance", this);
        plot.setAxisTitles("time", "% successful service executions");

        plot.addSequence("Developers", new Sequence() {
            public double getSValue() {
                // iterate through consumers
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
        return new String[] {"NUMBER_OF_TASKS", "NUMBER_OF_DEVELOPERS", "STRATEGY"};
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
        init.loadModel(new Launcher(), null, !BATCH_MODE);
    }
}
