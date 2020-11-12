package agents;

import jade.lang.acl.UnreadableException;
import tasks.Task;
import tasks.TaskType;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class DeveloperAgent extends Agent {
    private int id;
    private Task task;
    private TaskType aoe;

    protected void setup() {
        // devArgs = { ++devCount, aoe };
        Object[] args = this.getArguments();
        this.id = (int) args[0];
        this.aoe = (TaskType)args[1];
        System.out.println(this);
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
                ", task=" + task +
                ", aoe=" + aoe +
                '}';
    }

    class FIPAContractNetResp extends ContractNetResponder {
        FIPAContractNetResp(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

            try {
                Task task = (Task) cfp.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            // Responds with the timestamp when it could complete the task
            reply.setContent(String.valueOf(id));       // Now sending ids for debugging

            return reply;
        }

        @Override
        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject...");
        }

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
