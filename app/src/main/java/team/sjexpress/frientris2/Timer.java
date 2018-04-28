package team.sjexpress.frientris2;

public class Timer {
  private long time;
  private long v;

  public Timer() {
    start();
  }

  public void start() {
    time = System.currentTimeMillis();
    v = 0;
  }

  public long stop() {
    v = getCurrent();
    return v;
  }

  public long getV() {
    return v;
  }

  public long getCurrent() {
    return System.currentTimeMillis() - time;
  }
}
