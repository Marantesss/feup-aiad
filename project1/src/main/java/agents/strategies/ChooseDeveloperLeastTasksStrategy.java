package agents.strategies;

import proposals.Proposal;
import proposals.comparators.ProposalTaskQuantityComparator;

import java.util.Comparator;
import java.util.List;

/**
 * This strategy chooses the best proposal based on the number of tasks it is doing
 */
public class ChooseDeveloperLeastTasksStrategy implements ChooseDeveloperStrategy {

    /**
     * This comparator will sort the proposals based on the number of tasks
     */
    private final Comparator<Proposal> comparator = new ProposalTaskQuantityComparator();

    @Override
    public Proposal execute(List<Proposal> proposals) {
        proposals.sort(comparator);

        return proposals.get(0);
    }
}
