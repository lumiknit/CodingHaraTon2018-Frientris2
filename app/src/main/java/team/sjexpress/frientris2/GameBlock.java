package team.sjexpress.frientris2;

public class GameBlock {
  public static final int TYPE_EMPTY = 0;
  public static final int TYPE_FIXED = 1;
  public static final int TYPE_TEMP = 2;
  public static final int TYPE_GHOST = -1;

  public int type;
  public int angle;
  public int face;

  public GameBlock() {
    this(TYPE_EMPTY, 0, 0);
  }

  public GameBlock(int type, int angle, int face) {
    set(type, angle, face);
  }

  public void set(int type, int angle, int face) {
    this.type = type;
    set(angle, face);
  }

  public void set(int angle, int face) {
    this.angle = angle;
    this.face = face;
  }

  public void clear() {
    set(TYPE_EMPTY, 0, 0);
  }

  public void setFixed(int angle, int face) {
    set(TYPE_FIXED, angle, face);
  }

  public void setTemp(int angle, int face) {
    set(TYPE_TEMP, angle, face);
  }

  public void setGhost() {
    set(TYPE_GHOST, 0, 0);
  }

  public boolean isEmpty() {
    return type == TYPE_EMPTY;
  }

  public boolean isFixed() {
    return type == TYPE_FIXED;
  }

  public boolean isTemp() {
    return type == TYPE_TEMP;
  }

  public boolean isGhost() {
    return type == TYPE_GHOST;
  }

  public boolean isBlock() {
    return type == TYPE_FIXED || type == TYPE_TEMP;
  }

  public int rotate(int a) {
    return angle = (angle + a) % 4;
  }
}
