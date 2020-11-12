package agents.strategies;

import proposals.Proposal;

import java.util.Collections;
import java.util.List;

/**
 * This strategy chooses a random proposal from the list of proposal
 * recovered from the developers
 */
public class ChooseDeveloperRandomStrategy implements ChooseDeveloperStrategy {

    @Override
    public Proposal execute(List<Proposal> proposals) {
        // randomize proposals array
        Collections.shuffle(proposals);
        // return the first proposal
        return proposals.get(0);
    }
}
