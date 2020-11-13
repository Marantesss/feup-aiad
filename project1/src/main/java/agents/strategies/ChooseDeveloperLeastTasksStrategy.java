package agents.strategies;

import proposals.Proposal;

import java.util.List;

/**
 * This strategy chooses the best proposal based on the number of tasks it is doing
 */
public class ChooseDeveloperLeastTasksStrategy implements ChooseDeveloperStrategy {
    @Override
    public Proposal execute(List<Proposal> proposals) {
        int min = proposals.get(0).getNumberOfTasks();
        Proposal ret = proposals.get(0);

        for (Proposal p : proposals) {
            if(p.getNumberOfTasks() < min)
                ret = p;
        }

        return ret;
    }
}
