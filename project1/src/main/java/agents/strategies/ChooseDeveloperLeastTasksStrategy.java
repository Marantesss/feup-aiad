package agents.strategies;

import proposals.Proposal;
import proposals.comparators.ProposalNumberOfTasksComparator;
import proposals.comparators.ProposalTimeComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This strategy chooses the best proposal based on the number of tasks it is doing
 */
public class ChooseDeveloperLeastTasksStrategy implements ChooseDeveloperStrategy {

    /**
     * This comparator will sort the proposals based on their time to complete
     */
    private final Comparator<Proposal> comparator = new ProposalNumberOfTasksComparator();

    @Override
    public Proposal execute(List<Proposal> proposals) {
        // hard copy proposal list
        List<Proposal> proposalsCopy = new ArrayList<>(proposals);
        // sort the proposals
        proposalsCopy.sort(comparator);
        // return the first choice
        return proposalsCopy.get(0);
    }
}
