package proposals.comparators;

import proposals.Proposal;

import java.util.Comparator;

public class ProposalTaskQuantityComparator implements Comparator<Proposal> {
    @Override
    public int compare(Proposal p1, Proposal p2) {
        return Integer.compare(p1.getNumberOfTasks(), p2.getNumberOfTasks());
    }
}
