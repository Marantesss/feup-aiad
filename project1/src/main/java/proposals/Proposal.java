package proposals;

import java.io.Serializable;

public class Proposal implements Serializable {
    private final int timeUntilCompleted;

    public Proposal(int timeUntilCompleted) {
        this.timeUntilCompleted = timeUntilCompleted;
    }

    public int getTimeUntilCompleted() {
        return timeUntilCompleted;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "timeUntilCompleted=" + timeUntilCompleted +
                '}';
    }
}
