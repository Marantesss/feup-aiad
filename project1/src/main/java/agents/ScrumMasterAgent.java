package agents;

import io.ResultsWriter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import proposals.Proposal;
import tasks.Task;

import java.io.IOException;
import agents.strategies.ChooseDeveloperStrategy;
import tasks.TaskPriorityComparator;

import java.util.*;

public class ScrumMasterAgent extends Agent {
    private ChooseDeveloperStrategy strategy;
    private PriorityQueue<Task> bufferedTasks;
    private AID[] developers;
    private SequentialBehaviour behaviour;

    private ResultsWriter writer;

    @Override
    protected void setup() {
        // scrumMasterArgs = { reader.getStrategy(), reader.getTasks(), developerCount }
        Object[] args = this.getArguments();
        this.strategy = (ChooseDeveloperStrategy) args[0];
        String outputFilePath = (String) args[2];
        this.writer = new ResultsWriter(outputFilePath);
        this.bufferedTasks = new PriorityQueue<>(new TaskPriorityComparator());

        LinkedList<Task> tasks = (LinkedList<Task>) args[1];
        bufferedTasks.addAll(tasks);

        GetDevelopersBehaviour g1 = new GetDevelopersBehaviour(this, 2000);

        this.behaviour = new SequentialBehaviour();
        this.behaviour.addSubBehaviour(g1);

        this.sendNextMessage();

        this.addBehaviour(behaviour);
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
                ", developers=" + Arrays.toString(developers) +
                ", behaviour=" + behaviour +
                ", writer=" + writer +
                '}';
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

            for (AID aid : developers)
                cfp.addReceiver(aid);

            Task task = bufferedTasks.poll(); //removes the head of the queue
            System.out.println("CFP\tProposing task: " + task);

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
                    writer.addTask(response.getSender().getLocalName(), best.getTask());
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }

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
            } else {
                writer.writeOutput();
            }
        }
    }

    private class GetDevelopersBehaviour extends TickerBehaviour {
        private int attempts = 0;

        GetDevelopersBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            System.out.println("DF\tSearching for developers...");

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Developer");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                developers = new AID[result.length];

                for (int i = 0; i < result.length; i++)
                    developers[i] = result[i].getName();

                if (result.length <= 1 && attempts < 3)
                    attempts++;
                else if (result.length <= 1 && attempts == 3)
                    behaviour.reset();
                else
                    this.stop();
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
    }
}
