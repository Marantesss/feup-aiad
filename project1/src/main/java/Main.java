import Tasks.RandomTaskGenerator;
import Tasks.Task;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws StaleProxyException {

        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        //ConfigReader reader = new ConfigReader("project1/src/main/resources/config.test.json");

        ContainerController cc = rt.createMainContainer(profile);

        /*
        //AgentController dev1 = cc.createNewAgent("developer1", "agents.DeveloperAgent", null);
        //AgentController dev2 = cc.createNewAgent("developer2", "agents.DeveloperAgent", null);

        String[] ar = {"cenas"};
        AgentController sm = cc.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", ar);

        //dev1.start();
        //dev2.start();
        sm.start();
        */

        //Example Test task generation
        /*
        RandomTaskGenerator  generator = new RandomTaskGenerator(10,0,20);
        ArrayList<Task> tasks = generator.generateTaskList(200);

        tasks.forEach(System.out::println);
         */
    }
}
