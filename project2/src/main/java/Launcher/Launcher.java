package Launcher;

import agents.RandomExpertiseGenerator;
import agents.DeveloperAgent;
import agents.ScrumMasterAgent;
import agents.strategies.ChooseDeveloperLeastTasksStrategy;
import agents.strategies.ChooseDeveloperLowestTimeStrategy;
import agents.strategies.ChooseDeveloperRandomStrategy;
import agents.strategies.ChooseDeveloperStrategy;
import io.ConfigReader;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import tasks.RandomTaskGenerator;
import tasks.Task;
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

    private static List<DeveloperAgent> developerAgents;
    private ScrumMasterAgent scrumMasterAgent;

    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private final int RADIUS = 200;

    private DisplaySurface dsurf;
    private OpenSequenceGraph plotTasks;
    private OpenSequenceGraph plotProject;

    private static final boolean BATCH_MODE = true;
    // GUI Variables
    // For some reason this had to be in caps
    // DROPDOWN does not work if camelCase... no clue
    private String CONFIG_FILE_PATH = "json/small.test.json";
    private boolean CUSTOM_OPTIONS = false;
    private int NUMBER_OF_TASKS = 100;
    private int NUMBER_OF_DEVELOPERS = 6;
    private ChooseDeveloperStrategy STRATEGY = new ChooseDeveloperRandomStrategy();

    public Launcher(boolean runBatchMode) {
        if (runBatchMode) {
            this.CONFIG_FILE_PATH = "json/small.test.json";
        } else {
            // Create strategy dropdown
            Hashtable strategyDropdown = new Hashtable();
            strategyDropdown.put(new ChooseDeveloperRandomStrategy(), "Random");
            strategyDropdown.put(new ChooseDeveloperLowestTimeStrategy(), "Lowest Time");
            strategyDropdown.put(new ChooseDeveloperLeastTasksStrategy(), "Number of Tasks");
            ListPropertyDescriptor pd = new ListPropertyDescriptor("STRATEGY", strategyDropdown);
            descriptors.put("STRATEGY", pd);
        }
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

    @Override
    public String[] getInitParam() {
        return new String[] {"CONFIG_FILE_PATH", "CUSTOM_OPTIONS", "NUMBER_OF_TASKS", "NUMBER_OF_DEVELOPERS", "STRATEGY"};
    }

    public String getCONFIG_FILE_PATH() {
        return CONFIG_FILE_PATH;
    }

    public void setCONFIG_FILE_PATH(String CONFIG_FILE_PATH) {
        this.CONFIG_FILE_PATH = CONFIG_FILE_PATH;
    }

    public boolean getCUSTOM_OPTIONS() {
        return CUSTOM_OPTIONS;
    }

    public void setCUSTOM_OPTIONS(boolean CUSTOM_OPTIONS) {
        this.CUSTOM_OPTIONS = CUSTOM_OPTIONS;
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

    public ChooseDeveloperStrategy getSTRATEGY() {
        return STRATEGY;
    }

    public void setSTRATEGY(ChooseDeveloperStrategy strategy) {
        this.STRATEGY = strategy;
    }

    private void lunchDevelopersFromFile() throws StaleProxyException {
        ConfigReader reader;
        try {
            System.out.println("Reading config file...");
            reader = new ConfigReader(CONFIG_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create developer agents
        int developerCount = 0;
        Random random = new Random(System.currentTimeMillis());
        nodes = new ArrayList<>();
        developerAgents =  new ArrayList<>();

        // Use RandomExpertiseGenerator.getRandomExpertise(<numDevs>) to generate random agents
        for (var aoe : reader.getDevelopersExpertise()) {
            DeveloperAgent developerAgent =  new DeveloperAgent(++developerCount, aoe, reader.getNumberOfExpertise());
            DefaultDrawableNode node = generateNode("Dev "+developerCount, Color.RED, developerAgent.getX(RADIUS, WIDTH), developerAgent.getY(RADIUS, HEIGHT));

            developerAgents.add(developerAgent);
            developerAgent.setNode(node);
            nodes.add(node);

            mainController.acceptNewAgent("Developer" + developerCount, developerAgent).start();
        }

        // Create scrum master agent
        scrumMasterAgent = new ScrumMasterAgent(reader.getStrategy(), reader.generateResultsFilePath(), reader.getTasks());
        DefaultDrawableNode node = generateNode("SM", Color.BLUE, WIDTH/2, HEIGHT/2);

        scrumMasterAgent.setNode(node);
        nodes.add(node);

        mainController.acceptNewAgent("ScrumMaster", scrumMasterAgent).start();
    }

    private void lunchDevelopersFromConfig() throws StaleProxyException {
        Random random = new Random(System.currentTimeMillis());
        // generate random tasks
        // TODO maxTask and minTaskTime
        var taskList = new RandomTaskGenerator(10, 0, 0).generateTaskList(NUMBER_OF_TASKS);

        // generate developer agents
        // create developer agents
        int developerCount = 0;
        nodes = new ArrayList<>();
        developerAgents =  new ArrayList<>();

        // Use RandomExpertiseGenerator.getRandomExpertise(<numDevs>) to generate random agents
        for (var aoe : RandomExpertiseGenerator.getRandomExpertise(NUMBER_OF_DEVELOPERS)) {
            DeveloperAgent developerAgent =  new DeveloperAgent(++developerCount, aoe, NUMBER_OF_DEVELOPERS);
            DefaultDrawableNode node = generateNode("Dev "+developerCount, Color.RED, developerAgent.getX(RADIUS, WIDTH), developerAgent.getY(RADIUS, HEIGHT));

            developerAgents.add(developerAgent);
            developerAgent.setNode(node);
            nodes.add(node);

            mainController.acceptNewAgent("Developer" + developerCount, developerAgent).start();
        }

        // create scrum master agent
        var outputFilePath = "json/" + this.hashCode() + ".results.json";
        scrumMasterAgent = new ScrumMasterAgent(STRATEGY, outputFilePath, taskList);
        DefaultDrawableNode node = generateNode("SM", Color.BLUE, WIDTH/2, HEIGHT/2);

        scrumMasterAgent.setNode(node);
        nodes.add(node);

        mainController.acceptNewAgent("ScrumMaster", scrumMasterAgent).start();
    }

    private void launchAgents() throws StaleProxyException {
       if (CUSTOM_OPTIONS) {
           lunchDevelopersFromConfig();
       } else {
           lunchDevelopersFromFile();
       }
    }

    private DefaultDrawableNode generateNode(String label, Color color, int x , int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(30);
        oval.setWidth(30);

        DefaultDrawableNode node = new DefaultDrawableNode(label, oval);
        node.setLabelColor(Color.WHITE);
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
        dsurf.setBackground(Color.LIGHT_GRAY);
        dsurf.display();

        // Graph
        if (plotTasks != null) plotTasks.dispose();
        plotTasks = new OpenSequenceGraph("SCRUM Task Allocation - Tasks", this);
        plotTasks.setAxisTitles("Time", "Tasks");

        // Average task per developer
        plotTasks.addSequence("AVG no. of Tasks per developer", new Sequence() {
            public double getSValue() {
                double v = 0.0;
                for (DeveloperAgent developerAgent : developerAgents) {
                    v += developerAgent.getTasks().size();
                }
                return v / developerAgents.size();
            }
        });

        // Max task per developer
        plotTasks.addSequence("MAX no. of Tasks per developer", new Sequence() {
            public double getSValue() {
                double v = 0.0;
                for (DeveloperAgent developerAgent : developerAgents) {
                    v = Math.max(v, developerAgent.getTasks().size());
                }
                return v;
            }
        });

        // Min task per developer
        plotTasks.addSequence("MIN no. of Tasks per developer", new Sequence() {
            public double getSValue() {
                double v = Double.MAX_VALUE;
                for (DeveloperAgent developerAgent : developerAgents) {
                    v = Math.min(v, developerAgent.getTasks().size());
                }
                return v;
            }
        });
        plotTasks.display();

        if (plotProject != null) plotProject.dispose();
        plotProject = new OpenSequenceGraph("SCRUM Task Allocation - Project Time", this);
        plotProject.setAxisTitles("Time", "Time");

        // Time wasted in project
        plotProject.addSequence("Time to project completion", new Sequence() {
            public double getSValue() {
                double v = 0.0;
                for (DeveloperAgent developerAgent : developerAgents) {
                    Task latestTask = developerAgent.getLatestTask();
                    if (latestTask != null) {
                        v = Math.max(v, latestTask.getStartingInstant() + latestTask.getDuration());
                    }
                }
                return v;
            }
        });
        plotProject.display();

        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plotTasks, "step", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plotProject, "step", Schedule.LAST);
    }

    @Override
    public String getName() {
        return "Scrum Agile Development -- SAJaS Repast3";
    }

    public static void main(String[] args) {
        boolean runBatchMode = !BATCH_MODE;
        SimInit init = new SimInit();
        init.setNumRuns(1);
        init.loadModel(new Launcher(runBatchMode), null, runBatchMode);
    }
}
