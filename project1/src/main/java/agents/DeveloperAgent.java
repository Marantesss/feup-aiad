package agents;

import jade.lang.acl.UnreadableException;
import proposals.Proposal;
import tasks.Task;
import tasks.TaskType;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeveloperAgent extends Agent {
    private int id;
    private TaskType aoe;
    private LinkedHashMap<Task,Integer> tasks; // key -> task; Value: -> instant a task is finished
    private Task latestTask; // The latest task this developer is working on

    protected void setup() {
        // devArgs = { ++devCount, aoe };
        Object[] args = this.getArguments();
        this.id = (int) args[0];
        this.aoe = (TaskType) args[1];
        this.tasks = new LinkedHashMap<>();

        addBehaviour(new FIPAContractNetResp(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

    public Task getLatestTask() {
        return latestTask;
    }

    public Integer getTaskStartInstant(Task task) {
        return this.tasks.get(task);
    }

    public Integer getTaskCompletionInstant(Task task) {
        return this.tasks.get(task) + task.getDuration();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    @Override
    public String toString() {
        return "DeveloperAgent{" +
                "tasks=" + tasks.keySet() +
                '}';
    }

    /**
     * Returns the minimum instant a task can be allocated to
     * @param task The task to allocate
     * @return The minimum instant this developer can start the task
     */
    private int allocateTask(Task task) {
        int earlyInstant;

        if (latestTask == null) {
            earlyInstant = 0;
        }
        else {
            //Get the earliest theoretical instant the task can be finished
            earlyInstant = tasks.get(latestTask) + latestTask.getDuration();
        }

        return Math.max(earlyInstant, task.getStartingInstant());
    }

    /**
     * Adds a new task to the be done by this developer so that the key
     * of the map is the task and the value is the instant it is going to be started
     * @param task The task to be added
     */
    private void addTask(Task task) {
        //Get instant the task can be started
        int instant = allocateTask(task);

        tasks.put(task,instant);

        this.latestTask = task; //Sets the latest accepted task
    }

    /**
     * Returns a string with all the relevant task information
     * @return
     */
    private String getTaskString() {
        StringBuilder buffer = new StringBuilder("{");

        for (Map.Entry<Task,Integer> entry : tasks.entrySet()) {
            Task task = entry.getKey();
            buffer.append("Task").append(task.getId()).append(": start ->").append(entry.getValue()).append("; finish -> ")
                    .append(entry.getValue() + task.getDuration()).append("|");
        }

        buffer.append("}");

        return buffer.toString();
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
            if (latestReceivedTask.getType().equals(aoe))
                latestReceivedTask.setDuration((int) Math.ceil(latestReceivedTask.getDuration()/2.0));

            //Get the task allocation
            int minStartingInstant = allocateTask(latestReceivedTask);

            // Responds with the timestamp when it could complete the task
            Proposal proposal = new Proposal(minStartingInstant + latestReceivedTask.getDuration(), tasks.size(), latestReceivedTask);
            try {
                reply.setContentObject(proposal);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reply;
        }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            try {
                Proposal proposal = (Proposal) accept.getContentObject();
                addTask(proposal.getTask());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            ACLMessage result = accept.createReply();
            // Verify between all the informs
            result.setPerformative(ACLMessage.INFORM);
            result.setContent("this is the result");

            System.out.println("Developer " + id + ": " + getTaskString());

            return result;
        }
    }

}
