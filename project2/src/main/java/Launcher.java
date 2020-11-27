import io.ConfigReader;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import uchicago.src.sim.engine.SimInit;

import java.io.IOException;

public class Launcher extends Repast3Launcher {
    private ContainerController mainController;

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();

        profile.setParameter(Profile.GUI, "true");
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
            reader = new ConfigReader("json/small.test.json");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // create developer agents
        int developerCount = 0;

        for (var aoe : reader.getDevelopersExpertise()) {
            Object[] devArgs = { ++developerCount, aoe };
            AgentController dev = mainController.createNewAgent("developer" + developerCount, "agents.DeveloperAgent", devArgs);
            dev.start();
        }

        // create scrum master agent
        Object[] scrumMasterArgs = { reader.getStrategy(), reader.getTasks(), reader.generateResultsFilePath() };
        AgentController sm = mainController.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", scrumMasterArgs);
        sm.start();
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void begin() {
        super.begin();
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
