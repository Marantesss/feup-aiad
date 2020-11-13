import io.ConfigReader;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false");

        ConfigReader reader = null;
        try {
            reader = new ConfigReader("src/main/resources/config.test.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContainerController cc = rt.createMainContainer(profile);

        // create developer agents
        assert reader != null;
        int developerCount = 0;
        for (var aoe : reader.getDevelopersExpertise()) {
            Object[] devArgs = { ++developerCount, aoe };
            AgentController dev = cc.createNewAgent("developer" + developerCount, "agents.DeveloperAgent", devArgs);
            dev.start();
        }
        // wait for devs to be ready
        Thread.sleep(500);
        // create scrum master agent
        Object[] scrumMasterArgs = { reader.getStrategy(), reader.getTasks(), developerCount };
        AgentController sm = cc.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", scrumMasterArgs);
        sm.start();

        //Example Test task generation
        /*
        RandomTaskGenerator  generator = new RandomTaskGenerator(10,0,20);
        ArrayList<Task> tasks = generator.generateTaskList(200);

        tasks.forEach(System.out::println);
         */
    }
}
