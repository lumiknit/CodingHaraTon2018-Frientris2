package team.sjexpress.frientris2;

import android.os.VibrationEffect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Game {
  /* Maybe This is Fxxxing Large Class */
  /* Don't try to read this */

  /* Constant */
  public static final int WIDTH = 7;
  public static final int HEIGHT = 14;
  public static final int T_O = 0;
  public static final int T_I = 1;
  public static final int T_S = 2;
  public static final int T_Z = 3;
  public static final int T_T = 4;
  public static final int T_J = 5;
  public static final int T_L = 6;

  public static final int[][] B = {
      {0, 0, 0, 0,
          0, 1, 1, 0,
          0, 1, 1, 0,
          0, 0, 0, 0},
      {0, 1, 0, 0,
          0, 1, 0, 0,
          0, 1, 0, 0,
          0, 1, 0, 0},
      {0, 0, 1, 0,
          0, 1, 1, 0,
          0, 1, 0, 0,
          0, 0, 0, 0},
      {0, 1, 0, 0,
          0, 1, 1, 0,
          0, 0, 1, 0,
          0, 0, 0, 0},
      {0, 1, 0, 0,
          0, 1, 1, 0,
          0, 1, 0, 0,
          0, 0, 0, 0},
      {0, 1, 0, 0,
          0, 1, 0, 0,
          1, 1, 0, 0,
          0, 0, 0, 0},
      {0, 1, 0, 0,
          0, 1, 0, 0,
          0, 1, 1, 0,
          0, 0, 0, 0},
  };

  public static final int DELL_F_MAX = 5;

  public Random random = new Random();

  public GameActivity activity;
  public SurfaceView surfaceView;
  public boolean running;

  public float xShake = 0.f, yShake = 0.f;
  public float xaShake = 0.f, yaShake = 0.f;

  public long tick = 0;
  public long dropTick = 0;
  public long delLineFlag = -1;
  public long gameOverFlag = -1;

  public int touchType = 0;
  public int touchId = -1;
  public long firstDown = -1;
  public long secondDown = -1;

  public ArrayList<Integer> delLines;

  public ArrayList<Particle> particles;

  /* Current State */
  public int[][] board;
  public int level;
  public int score;
  /* Current Block */
  public int type;
  public int x, y;
  public int angle, rAngle;

  public Random typeRandom;


  public Game(GameActivity activity) {
    this.activity = activity;
    typeRandom = new Random();
    initialize();
  }

  /* Interactive */

  public void startRunning() {
    running = true;
  }

  public void stopRunning() {
    running = false;
  }

  public void loop() {
    Log.d("Game", "Loop");
    Timer fpsLimitTimer = new Timer();
    int fpsLimit = 60;
    long iFpsLimit = 1000 / fpsLimit;

    while(running) {
      tick++;
      long t = System.currentTimeMillis();
      fpsLimitTimer.start();

      if(gameOverFlag >= 0) {
        xShake = -0.05f + random.nextFloat() * 0.1f;
        yShake = -0.05f + random.nextFloat() * 0.1f;
      } else {
        xShake = xShake * 0.8f + xaShake * random.nextFloat() - xaShake * 0.5f;
        yShake = yShake * 0.8f + yaShake * random.nextFloat() - yaShake * 0.5f;
        xaShake = xaShake * 0.8f;
        yaShake = yaShake * 0.8f;

        if (delLineFlag >= 0) {
          delLineFlag--;
          if (delLineFlag < 0) {
            xaShake += 0.18f;
            yaShake += 0.18f;
            if(activity.optVib) activity.vibrator.vibrate(250 + 50 * delLines.size());
            deleteLines();
          }
        } else {
          dropTick++;
          if (dropTick > fpsLimit ||
              (dropTick > 10 && touchType == 3) ||
              (secondDown >= 0 && dropTick > 3 && touchType == 3)) {
            stepBlock();
            Log.d("Game", "Step");
            dropTick = 0;
            secondDown = tick;
          }
        }
        if(firstDown >= 0 || (secondDown >= 0 && tick - secondDown > 4)) {
          if(touchType == 1) {
            moveLeft();
            if(activity.optVib) activity.vibrator.vibrate(20);
            xShake = -0.05f;
          } else if(touchType == 2) {
            moveRight();
            if(activity.optVib) activity.vibrator.vibrate(20);
            xShake = 0.05f;
          }
          if(firstDown >= 0) {
            firstDown = -1;
            secondDown = tick + 12;
          } else {
            secondDown = tick;
          }
        }
      }

      for (int i = 0; i < particles.size(); i++) {
        Particle p = particles.get(i);
        p.step();
        if (p.life >= fpsLimit * 2 / 3) {
          particles.remove(i--);
        }
      }

      surfaceView.requestRender();

      fpsLimitTimer.stop();
      if(iFpsLimit > fpsLimitTimer.getV()) {
        try {
          Thread.sleep(iFpsLimit - fpsLimitTimer.getV());
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    float x = event.getX();
    float y = event.getY();
    if(action == MotionEvent.ACTION_DOWN) {
      int w = activity.getWindow().getDecorView().getWidth();
      int h = activity.getWindow().getDecorView().getHeight();
      float xp = x / w;
      float yp = y / h;
      if(yp >= 0.8f) { /* Drop */
        if(touchType == 0) {
          touchType = 3;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      } else if(yp < 0.4f) { /* Rotate */
        rotate();
      } else if(xp < 0.5f) { /* Left */
        if(touchType == 0) {
          touchType = 1;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      } else { /* Right */
        if(touchType == 0) {
          touchType = 2;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      }
    } else if(action == MotionEvent.ACTION_UP && touchId == event.getPointerId(0)) {
      touchType = 0;
      firstDown = -1;
      secondDown = -1;
      touchId = 0;
    }
    return true;
  }

  /* Getter / Setter */

  public void setSurfaceView(SurfaceView view) {
    this.surfaceView = view;
  }

  /* Game Internal */
  /* = Tetris */

  private void initialize() {
    clearBoard();
    level = 0;
    score = 0;
    type = 0;
    x = 0;
    y = 0;
    angle = 0;
    rAngle = 0;
    genBlock();
    particles = new ArrayList<>();
  }

  public void finalize() {

  }

  private void clearBoard() {
    board = new int[HEIGHT][];
    for(int i=0;i<HEIGHT;i++) {
      board[i] = new int[WIDTH];
      for(int j=0;j<WIDTH;j++) {
        board[i][j] = 0;
      }
    }
  }

  private void genBlock() {
    type = typeRandom.nextInt(7);
    x = WIDTH / 2 - 1;
    y = -4;
    angle = 0;
  }

  private int[] getBlockVector(int type, int angle) {
    int[] b = new int[16];
    int sw = 0, flip = 0;
    switch(angle) {
      case 0: sw = 0; flip = 0; break;
      case 1: sw = 1; flip = 1; break;
      case 2: sw = 0; flip = 1; break;
      case 3: sw = 1; flip = 0; break;
    }
    int isw = 1 - sw;
    for(int i=0;i<4;i++) {
      for(int j=0;j<4;j++) {
        int z = (i * isw + j * sw) * 4 + (j * isw + (3 - i) * sw);
        if(flip == 1) z = 15 - z;
        b[i * 4 + j] = B[type][z];
      }
    }
    return b;
  }

  /* Move Block Left (by User Input) */
  private void moveLeft() {
    if(!isCollided(type, x - 1, y, angle)) {
      x--;
    }
    updateBoard();
  }

  /* Move Block Right (by User Input) */
  private void moveRight() {
    if(!isCollided(type, x + 1, y, angle)) {
      x++;
    }
    updateBoard();
  }

  /* Rotate Block (by User Input) */
  private void rotate() {
    int t = 4, xo = 0, yo = 0;
    if(type == T_O) {
      t = 1;
    } else if(type < T_T) {
      t = 2;
      if(angle == 0) { xo = -1; yo = 0; }
      else { xo = 1; yo = 0; }
    } else {
      switch(angle) {
        case 0: xo = -1; yo = 0; break;
        case 1: xo = 0; yo = -1; break;
        case 2: xo = 1; yo = 0; break;
        case 3: xo = 0; yo = 1; break;
      }
    }
    if(!isCollided(type, x + xo, y + yo, (angle + 1) % t)) {
      x = x + xo;
      y = y + yo;
      angle = (angle + 1) % t;
      rAngle = (rAngle + 1) % 4;
    } else if(!isCollided(type, x + xo - 1, y + yo, (angle + 1) % t)) {
      x = x + xo - 1;
      y = y + yo;
      angle = (angle + 1) % t;
      rAngle = (rAngle + 1) % 4;
    } else if(!isCollided(type, x + xo + 1, y + yo, (angle + 1) % t)) {
      x = x + xo + 1;
      y = y + yo;
      angle = (angle + 1) % t;
      rAngle = (rAngle + 1) % 4;
    }
    updateBoard();
  }

  /* Move Block Down (by Each Step) */
  private void stepBlock() {
    boolean b = isCollided(type, x, y + 1, angle);
    if(!b) {
      y++;
      updateBoard();
    } else {
      fixBlock();
      genBlock();
    }
  }

  /* Check Current Block is Collided with Fixed Blocks (by User Input) */
  private boolean isCollided(int type, int x, int y, int angle) {
    int[] b = getBlockVector(type, angle);
    for(int i=0;i<4;i++) {
      for(int j=0;j<4;j++) {
        if(b[i * 4 + j] == 1) {
          if(y + i >= 0 &&
              (!inBoard(x + j, y + i) || board[y + i][x + j] > 0))
            return true;
          else if(x + j < 0 || x + j >= WIDTH) return true;
        }
      }
    }
    return false;
  }

  /* Fix Current Block */
  private void fixBlock() {
    boolean go = false;
    delLines = new ArrayList<>();
    int[] b = getBlockVector(type, angle);
    for(int i=0;i<4;i++) {
      for(int j=0;j<4;j++) {
        if(b[i * 4 + j] > 0) {
          if(y + i >= 0) {
            board[y + i][x + j] = 1 + rAngle;
          } else {
            go = true;
          }
        }
      }
    }
    if(go) gameOver();
    else {
      for (int i = 0; i < HEIGHT; i++) {
        boolean f = true;
        for (int j = 0; j < WIDTH; j++) {
          if (board[i][j] == 0) f = false;
        }
        if (f) {
          delLines.add(i);
          delLineFlag = DELL_F_MAX;
        }
      }
    }
  }

  private boolean inBoard(int x, int y) {
    return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
  }

  private void updateBoard() {
    for(int i=0;i<HEIGHT;i++) {
      for(int j=0;j<WIDTH;j++) {
        if(board[i][j] < 0) board[i][j] = 0;
      }
    }
    int[] b = getBlockVector(type, angle);
    for(int i=0;i<4;i++) {
      for(int j=0;j<4;j++) {
        if(inBoard(x + j, y + i) && b[i * 4 + j] == 1) {
          board[y + i][x + j] = -1;
        }
      }
    }
  }

  private void deleteLines() {
    int off = 0;
    for(int i=HEIGHT - 1;i>=0;i--) {
      if(delLines.indexOf(i) >= 0) {
        off++;
        for(int k=0;k<3 * WIDTH;k++) {
          for(int l=0;l<3;l++) {
            if(activity.optPart)
              particles.add(new Particle(
                  -1.f + k / 3.f / WIDTH * 2,
                  1.f - (i + l / 2.f) / HEIGHT * 2,
                  0.3f + 0.7f * random.nextFloat(),
                  // 0.9f, 0.02f, 0.0f,
                  1.f - random.nextFloat() * 0.1f,
                  1.f - random.nextFloat() * 0.4f,
                  1.f - random.nextFloat() * 0.5f,
                  (k % 3) * 3 + l));
            if(activity.optGore)
              particles.add(new Particle(
                  -1.f + k / 3.f / WIDTH * 2,
                  1.f - (i + l / 2.f) / HEIGHT * 2,
                  0.3f + 0.7f * random.nextFloat(),
                  // 0.9f, 0.02f, 0.0f,
                  0.9f - random.nextFloat() * 0.2f, 0.3f * random.nextFloat(), 0.2f * random.nextFloat(),
                  -1));
          }
        }
      } else board[i + off] = board[i];
    }
    for(int i=0;i<off;i++) {
      board[i] = new int[WIDTH];
      for(int j=0;j<WIDTH;j++) {
        board[i][j] = 0;
      }
    }
  }

  public void gameOver() {
    gameOverFlag = System.currentTimeMillis();
    for(int i=0;i<HEIGHT;i++) {
      for (int j = 0; j < WIDTH; j++) {
        if(board[i][j] != 0) {
          for(int k=0;k<3;k++) {
            for(int l=0;l<3;l++) {
              if(activity.optPart)
                particles.add(new Particle(
                    -1.f + (j + k / 3.f) / WIDTH * 2,
                    1.f - (i + l / 2.f) / HEIGHT * 2,
                    0.3f + 0.7f * random.nextFloat(),
                    // 0.9f, 0.02f, 0.0f,
                    1.f - random.nextFloat() * 0.1f,
                    1.f - random.nextFloat() * 0.4f,
                    1.f - random.nextFloat() * 0.5f,
                    k * 3 + l));
              if(activity.optGore)
                particles.add(new Particle(
                    -1.f + (j + k / 3.f) / WIDTH * 2,
                    1.f - (i + l / 2.f) / HEIGHT * 2,
                    0.3f + 0.7f * random.nextFloat(),
                    // 0.9f, 0.02f, 0.0f,
                    0.9f - random.nextFloat() * 0.2f, 0.3f * random.nextFloat(), 0.2f * random.nextFloat(),
                    -1));
            }
          }
          board[i][j] = 0;
        }
      }
    }
  }
}