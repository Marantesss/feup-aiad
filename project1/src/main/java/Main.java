import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        ContainerController cc = rt.createMainContainer(profile);

        AgentController dev1 = cc.createNewAgent("developer1", "Agents.DeveloperAgent", null);
        AgentController dev2 = cc.createNewAgent("developer2", "Agents.DeveloperAgent", null);
        AgentController sm = cc.createNewAgent("ScrumMaster", "Agents.ScrumMasterAgent", null);

        dev1.start();
        dev2.start();
        sm.start();
    }
}
