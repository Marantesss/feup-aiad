import io.ConfigReader;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws StaleProxyException {
        if (args.length != 1) {
            System.out.println("Need filename as argument");
            return;
        }

        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false");

        ConfigReader reader;
        try {
            System.out.println("Reading config file...");
            reader = new ConfigReader(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ContainerController cc = rt.createMainContainer(profile);

        // create developer agents
        int developerCount = 0;

        for (var aoe : reader.getDevelopersExpertise()) {
            Object[] devArgs = { ++developerCount, aoe };
            AgentController dev = cc.createNewAgent("developer" + developerCount, "agents.DeveloperAgent", devArgs);
            dev.start();
        }

        // create scrum master agent
        Object[] scrumMasterArgs = { reader.getStrategy(), reader.getTasks(), reader.generateResultsFilePath() };
        AgentController sm = cc.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", scrumMasterArgs);
        sm.start();

        cc.kill();
    }
}
