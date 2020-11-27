package proposals.comparators;

import proposals.Proposal;

import java.util.Comparator;

/**
 * This comparator choose the Proposal with the lowest task completion time.
 */
public class ProposalTimeComparator implements Comparator<Proposal> {
    @Override
    public int compare(Proposal p1, Proposal p2) {
        return Integer.compare(p1.getTimeUntilCompleted(), p2.getTimeUntilCompleted());
    }
}
