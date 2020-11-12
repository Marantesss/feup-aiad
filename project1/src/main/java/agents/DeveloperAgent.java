package agents;

import jade.lang.acl.UnreadableException;
import proposals.Proposal;
import tasks.Task;
import tasks.TaskType;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.io.IOException;
import java.util.HashMap;

public class DeveloperAgent extends Agent {
    private int id;
    private TaskType aoe;
    private HashMap<Task,Integer> tasks; //key -> task; Value: -> instant a task is finished
    private Task latestTask; //The latest task this developer is working on

    protected void setup() {
        // devArgs = { ++devCount, aoe };
        Object[] args = this.getArguments();
        this.id = (int) args[0];
        this.aoe = (TaskType) args[1];
        this.tasks = new HashMap<>();

        addBehaviour(new FIPAContractNetResp(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public String toString() {
        return "DeveloperAgent{" +
                "id=" + id +
                ", tasks=" + tasks + //TODO: print the hashmap and not the pointer
                ", aoe=" + aoe +
                '}';
    }

    /**
     * Returns the minimum instant a task can be allocated to
     * @param task The task to allocate
     * @return The minimum instant this developer can start the task
     */
    private int allocateTask(Task task) {
        if (latestTask == null) {
            return 0;
        }

        //Get the earliest theoretical instant the task can be finished
        int earlyInstant = tasks.get(latestTask) + latestTask.getDuration();

        return Math.max(earlyInstant, task.getStartingInstant());
    }


    /**
     * Adds a new task to the be done by this developer
     * @param task The task to be added
     * @return The instant the task is expected to finish
     */
    private int addTask(Task task) {
        //Get instant the task can be started
        int instant = allocateTask(task);

        tasks.put(task,instant);

        return instant + task.getDuration();
    }

    class FIPAContractNetResp extends ContractNetResponder {
        FIPAContractNetResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

            Task latestReceivedTask = null;

            try {
                latestReceivedTask = (Task) cfp.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace(); //TODO: reply error on exception
            }

            // if TaskType and aoe match, cut duration in half, rounding up
            if (latestReceivedTask.getType() == aoe)
                latestReceivedTask.setDuration((int) Math.ceil(latestReceivedTask.getDuration()/2.0));

            //Get the task allocation
            int minStartingInstant = allocateTask(latestReceivedTask);

            // Responds with the timestamp when it could complete the task
            Proposal proposal = new Proposal(minStartingInstant);
            try {
                reply.setContentObject(proposal);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reply;
        }

        @Override
        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject...");
        }

        // TODO: Após receber um accept do SM adicionar a task à lista
        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
            System.out.println(myAgent.getLocalName() + " got an accept!");

            ACLMessage result = accept.createReply();
            // Verify between all the informs
            result.setPerformative(ACLMessage.INFORM);
            result.setContent("this is the result");

            return result;
        }
    }

}
