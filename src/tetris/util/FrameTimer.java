package tetris.util;

public class FrameTimer {
    private long length;
    private long startTime;
    private boolean isDisabled;

    public FrameTimer(double length) {
        startTime = System.nanoTime();
        this.length = (long)(length*1e9);
        isDisabled = false;
    }

    public boolean isDone() {
        return !isDisabled && System.nanoTime() - startTime > length;
    }

    public void reset() {
        isDisabled = false;
        startTime = System.nanoTime();
    }

    public void setLength(double length) {
        this.length = (long)(length*1e9);
    }

    public void disable() {
        isDisabled = true;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public double getProgress() {
        return (double)(System.nanoTime() - startTime) / (double)length;
    }
}

