package proposals;

public class Proposal {

    final int id;

    final int timeUntilCompleted;

    public Proposal(int id, int timeUntilCompleted) {
        this.id = id;
        this.timeUntilCompleted = timeUntilCompleted;
    }

    public int getId() {
        return id;
    }

    public int getTimeUntilCompleted() {
        return timeUntilCompleted;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "id=" + id +
                ", timeUntilCompleted=" + timeUntilCompleted +
                '}';
    }
}
