package agents;

import io.OutputWriter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import proposals.Proposal;
import tasks.Task;

import java.io.IOException;
import agents.strategies.ChooseDeveloperStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class ScrumMasterAgent extends Agent {
    private ChooseDeveloperStrategy strategy;
    private LinkedList<Task> bufferedTasks;
    private List<String> developerNames;
    private SequentialBehaviour behaviour;

    private OutputWriter writer;

    @Override
    protected void setup() {
        // scrumMasterArgs = { reader.getStrategy(), reader.getTasks(), developerCount }
        Object[] args = this.getArguments();
        this.strategy = (ChooseDeveloperStrategy) args[0];
        this.bufferedTasks = (LinkedList<Task>) args[1];
        this.developerNames = generateDeveloperNames((int) args[2]);
        this.writer = new OutputWriter("results.test.json");

        behaviour = new SequentialBehaviour();

        this.sendNextMessage();
        addBehaviour(behaviour);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public String toString() {
        return "ScrumMasterAgent{" +
                "strategy=" + strategy +
                ", bufferedTasks=" + bufferedTasks +
                ", developerNames=" + developerNames +
                '}';
    }

    private List<String> generateDeveloperNames(int developerCount) {
        List<String> developerNames = new ArrayList<>();

        for (int i = 1; i <= developerCount; i++) {
            developerNames.add("developer"+i);
        }

        return developerNames;
    }

    private void sendNextMessage() {
        behaviour.addSubBehaviour(new FIPAContractNetInit(this, new ACLMessage(ACLMessage.CFP)));
    }

    class FIPAContractNetInit extends ContractNetInitiator {
        FIPAContractNetInit(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            for (String name : developerNames) {
                cfp.addReceiver(new AID(name, false));
            }

            Task task = bufferedTasks.pop();
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
            List<Proposal> proposals = new ArrayList<>();

            for (Object response : responses) {
                ACLMessage msg = (ACLMessage) response;
                Proposal prop = null;
                try {
                    prop = (Proposal) msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                proposals.add(prop);
            }

            Proposal best = strategy.execute(proposals);

            for (int i = 0; i < responses.size(); i++) {
                ACLMessage response = (ACLMessage) responses.get(i);
                ACLMessage reply = response.createReply();

                // TODO: If this misbehaves, then it is because we don't override the equals() method for Proposal class
                if (proposals.get(i).equals(best)) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                }
                else
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

                try {
                    reply.setContentObject(proposals.get(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                acceptances.add(reply);
            }
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            if (!bufferedTasks.isEmpty()) {
                sendNextMessage();
            }
        }
    }
}
