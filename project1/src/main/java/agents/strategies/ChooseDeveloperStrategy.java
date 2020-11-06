package agents.strategies;

import communication.Proposal;

import java.util.List;

/**
 * Given a task to complete and a set of developers, the scrum master
 * will ask all developers when will this task be completed if it
 * were allocated to them. Then it will choose the best developer.
 *
 * To choose the best developer, the scrum master will receive
 * information about the developer (identification) and the
 * task (time until the task is completed)
 *
 * This time until the task is completed is calculated
 * by the time it takes to complete all allocated tasks to
 * that developer plus this newly added task.
 *
 * Given this information from all developers, the scrum master will
 * use a given strategy to choose which developer will get
 * the task.
 */
public interface ChooseDeveloperStrategy {

    default Proposal execute(List<Proposal> proposals) {
        return proposals.get(0);
    }
}
