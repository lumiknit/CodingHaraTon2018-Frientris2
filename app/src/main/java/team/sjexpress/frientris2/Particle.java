package team.sjexpress.frientris2;

public class Particle {
  public Game game;
  public long life;
  public int lifeCnt;
  public float x, y;
  public float vx, vy;
  private float ay;
  public float size;
  public float aSpd;
  public float angle;
  public float r, g, b, a;
  public int index;

  public Particle(Game game, float x, float y, float size, float r, float g, float b, int index) {
    this.game = game;
    this.life = 0;
    this.lifeCnt = 0;
    this.x = x;
    this.y = y;
    this.size = size;
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = 1.f;
    this.angle = 0;

    if(game.gameOverFlag >= 0) {
      this.vx = ((float) Math.random() * 2 - 1.f) * 0.001f;
      this.vy = ((float) Math.random() * 2 - 1.f) * 0.0005f;
      this.life = -240 + (int)(Math.random() * 180);
    } else {
      this.vx = ((float) Math.random() * 2 - 1.f) * 0.07f;
      this.vy = ((float) Math.random() * 2 - 1.f) * 0.04f;
    }
    this.aSpd = (this.vx * this. vx + this.vy * this.vy) * 18000;
    this.ay = -0.0007f;
    this.index = index;
  }

  public void step() {
    if(game.gameOverFlag >= 0) {
      if(System.currentTimeMillis() - game.gameOverFlag < 2000) {
        vx = vx * 0.96f;
        vy = vy * 0.96f;
      }
      size = size * 0.98f;
      a = a * 0.99f;
      float z = y / 2;
      float r2 = x * x + z * z;
      float r3 = r2 * (float)Math.sqrt(r2);
      vx -= x / r3 * 0.0001f;
      vy -= y / r3 * 0.0004f;
      x += vx;
      y += vy;
      angle += aSpd * 0.25;
      life += 1;
    } else {
      x += vx;
      y += vy;
      vy += ay;
      angle += aSpd;
      if (ay != 0.f) {
        if (x < -1.f) {
          x = -1.f;
          vx = 0.f;
          vy = -0.004f;
          ay = 0.f;
          aSpd = 0.f;
          lifeCnt = 1;
        } else if (x > 1.f) {
          x = 1.f;
          vx = 0.f;
          vy = -0.004f;
          ay = 0.f;
          aSpd = 0.f;
          lifeCnt = 1;
        } else if (y < -1.f) {
          y = -1.f;
          vx = 0.f;
          vy = 0.f;
          ay = 0.f;
          aSpd = 0.f;
          lifeCnt = 1;
        }
      }
      life += lifeCnt;
      if (life > 20) a *= 0.9f;
    }
  }
}
