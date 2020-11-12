package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import tasks.Task;
import tasks.TaskPriority;
import tasks.TaskType;

import java.io.IOException;
import agents.strategies.ChooseDeveloperLowestTimeStrategy;
import agents.strategies.ChooseDeveloperRandomStrategy;
import agents.strategies.ChooseDeveloperStrategy;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class ScrumMasterAgent extends Agent {

    private ChooseDeveloperStrategy strategy;

    public ChooseDeveloperStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ChooseDeveloperStrategy strategy) {
        this.strategy = strategy;
    }

    public void pickStrategy(String strategyName) {
        switch (strategyName.toLowerCase()) {
            case "random":
                this.setStrategy(new ChooseDeveloperRandomStrategy());
                break;
            case "lowesttime":
                this.setStrategy(new ChooseDeveloperLowestTimeStrategy());
                break;
            default:
                break;
        }
    }

    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        if (args.length != 0) {
            this.pickStrategy(args[0].toString());
        }
        addBehaviour(new FIPAContractNetInit(this, new ACLMessage(ACLMessage.CFP)));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    static class FIPAContractNetInit extends ContractNetInitiator {
        FIPAContractNetInit(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            // Add all the developers
            cfp.addReceiver(new AID("developer1", false));
            cfp.addReceiver(new AID("developer2", false));

            // Send the Task object in the content
            Task task = new Task(5, TaskPriority.HIGH, TaskType.API);
            try {
                cfp.setContentObject(task);
            } catch (IOException e) {
                e.printStackTrace();
            }

            v.add(cfp);

            return v;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            ACLMessage earliestDev = (ACLMessage) responses.get(0);
            responses.remove(0);

            for (Object res : responses) {
                ACLMessage response = (ACLMessage) res;
                int timestamp = Integer.parseInt(response.getContent());

                if(timestamp < Integer.parseInt(earliestDev.getContent())) {
                    ACLMessage msg = earliestDev.createReply();
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(msg);

                    earliestDev = response;
                } else {
                    ACLMessage msg = response.createReply();
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(msg);
                }
            }

            ACLMessage msg = earliestDev.createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            acceptances.add(msg);
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
