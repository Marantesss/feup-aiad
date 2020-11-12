import io.ConfigReader;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import tasks.TaskType;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false");

        try {
            ConfigReader reader = new ConfigReader("project1/src/main/resources/config.test.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContainerController cc = rt.createMainContainer(profile);

        AgentController sm = cc.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", null);
        AgentController dev1 = cc.createNewAgent("developer1", "agents.DeveloperAgent", new TaskType[]{TaskType.API});
        AgentController dev2 = cc.createNewAgent("developer2", "agents.DeveloperAgent", new TaskType[]{TaskType.TESTING});

        dev1.start();
        dev2.start();
        Thread.sleep(500);
        sm.start();

        //Example Test task generation
        /*
        RandomTaskGenerator  generator = new RandomTaskGenerator(10,0,20);
        ArrayList<Task> tasks = generator.generateTaskList(200);

        tasks.forEach(System.out::println);
         */
    }
}
