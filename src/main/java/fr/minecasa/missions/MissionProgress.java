package fr.minecasa.missions;

public class MissionProgress {

    private final String missionId;
    private int current;
    private int target;
    private boolean completed;

    public MissionProgress(String missionId, int target) {
        this.missionId = missionId;
        this.target = target;
        this.current = 0;
        this.completed = false;
    }

    public String getMissionId() {
        return missionId;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        checkCompletion();
    }

    public void increment() {
        this.current++;
        checkCompletion();
    }

    public void increment(int amount) {
        this.current += amount;
        checkCompletion();
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public boolean isCompleted() {
        return completed;
    }

    private void checkCompletion() {
        if (current >= target) {
            completed = true;
        }
    }

    public int getPercentage() {
        if (target == 0) return 0;
        return (int) ((current * 100.0) / target);
    }

    public String getProgressString() {
        return current + "/" + target;
    }
}
