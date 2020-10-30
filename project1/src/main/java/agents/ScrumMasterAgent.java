package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class ScrumMasterAgent extends Agent {

    @Override
    protected void setup() {
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
            // cfp.addReceiver(new AID("a1", false));

            // Send the Task object in the content
            // cfp.setContentObject(task);

            v.add(cfp);

            return v;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            System.out.println("got " + responses.size() + " responses!");

            // Go through all the responses and check their end time
            // Choose the earliest
            // In case of a draw choose at random
            for (Object respons : responses) {
                ACLMessage msg = ((ACLMessage) respons).createReply();
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                acceptances.add(msg);
            }
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }
    }
}
