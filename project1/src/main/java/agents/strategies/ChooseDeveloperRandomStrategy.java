package agents.strategies;

import proposals.Proposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This strategy chooses a random proposal from the list of proposal
 * recovered from the developers
 */
public class ChooseDeveloperRandomStrategy implements ChooseDeveloperStrategy {

    @Override
    public Proposal execute(List<Proposal> proposals) {
        // hard copy proposal list
        List<Proposal> proposalsCopy = new ArrayList<>(proposals);
        // randomize proposals array
        Collections.shuffle(proposalsCopy);
        // return the first proposal
        return proposalsCopy.get(0);
    }
}
