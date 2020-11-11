import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import tasks.TaskType;

public class Main {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false");

        ContainerController cc = rt.createMainContainer(profile);

        AgentController sm = cc.createNewAgent("ScrumMaster", "agents.ScrumMasterAgent", null);
        AgentController dev1 = cc.createNewAgent("developer1", "agents.DeveloperAgent", new TaskType[]{TaskType.API});
        AgentController dev2 = cc.createNewAgent("developer2", "agents.DeveloperAgent", new TaskType[]{TaskType.TESTING});

        dev1.start();
        dev2.start();
        Thread.sleep(500);
        sm.start();
    }
}
