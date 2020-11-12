package agents.strategies;

import proposals.Proposal;
import proposals.comparators.ProposalTimeComparator;

import java.util.Comparator;
import java.util.List;

/**
 * This strategy chooses the best proposal based on time until the task is completed
 */
public class ChooseDeveloperLowestTimeStrategy implements ChooseDeveloperStrategy {

    /**
     * This comparator will sort the proposals based on their time to complete
     */
    private final Comparator<Proposal> comparator = new ProposalTimeComparator();

    @Override
    public Proposal execute(List<Proposal> proposals) {
        // sort the proposals
        proposals.sort(comparator);
        // return the first choice
        return proposals.get(0);
    }
}
