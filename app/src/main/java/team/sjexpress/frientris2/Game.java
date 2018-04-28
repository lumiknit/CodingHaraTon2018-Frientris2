package team.sjexpress.frientris2;

import android.os.Message;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Game {
  /* Maybe This is Fxxxing Large Class */
  /* Don't try to read this */

  /* Constant */
  public int WIDTH;
  public int HEIGHT;
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

  public int touchType = -1;
  public int touchId = -1;
  public long firstDown = -1;
  public long secondDown = -1;

  public ArrayList<Integer> delLines;

  public ArrayList<Particle> particles;
  public Semaphore semaParticle;

  /* Current State */
  public int[][] board;
  public int level, l = 60;
  public int score;
  public int lines;
  public int combo = 0;
  /* Current Block */
  public int type;
  public int x, y;
  public int angle, rAngle;

  public Random typeRandom;


  public Game(GameActivity activity) {
    this.activity = activity;
    WIDTH = activity.optWidth;
    HEIGHT = WIDTH * 2;
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
        xaShake = xaShake * 0.85f;
        yaShake = yaShake * 0.85f;

        if (delLineFlag >= 0) {
          delLineFlag--;
          if (delLineFlag < 0) {
            xaShake += 0.1f + xaShake;
            yaShake += 0.1f + xaShake;
            if(activity.optVib) activity.vibrator.vibrate(250 + 50 * delLines.size());
            deleteLines();
          }
        } else {
          dropTick++;
          if (dropTick > l ||
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

      try {
        semaParticle.acquire();
        for (int i = 0; i < particles.size(); i++) {
          Particle p = particles.get(i);
          p.step();
          if (p.life >= fpsLimit * 2 / 3) {
            particles.remove(i--);
          }
        }
        semaParticle.release();
      } catch(Exception e) {}


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
    if(gameOverFlag >= 0) return false;
    int action = event.getAction() & MotionEvent.ACTION_MASK;
    float x = event.getX();
    float y = event.getY();
    if(action == MotionEvent.ACTION_DOWN) {
      int w = activity.getWindow().getDecorView().getWidth();
      int h = activity.getWindow().getDecorView().getHeight();
      float xp = x / w;
      float yp = y / h;
      Log.d("Game", "" + touchType);
      if(yp >= 0.8f) { /* Drop */
        if(touchType <= 0) {
          touchType = 3;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      } else if(xp < 0.35f) { /* Left */
        if(touchType <= 0) {
          touchType = 1;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      } else if(xp > 0.65f) { /* Right */
        if(touchType <= 0) {
          touchType = 2;
          firstDown = tick;
          touchId = event.getPointerId(0);
        }
      } else {
        rotate();
      }
    } else if(action == MotionEvent.ACTION_UP && touchId == event.getPointerId(0)) {
      touchType = 0;
      firstDown = -1;
      secondDown = -1;
      touchId = 0;
    } else if(action == MotionEvent.ACTION_POINTER_DOWN) {
      int w = activity.getWindow().getDecorView().getWidth();
      int h = activity.getWindow().getDecorView().getHeight();
      float xp = x / w;
      float yp = y / h;
      Log.d("Game", "PTDOWN " + yp);
      if(yp >= 0.75f && touchType == 3) {
        hardDrop();
        touchType = 0;
        firstDown = -1;
        secondDown = -1;
      }
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
    lines = 0;
    type = 0;
    x = 0;
    y = 0;
    angle = 0;
    rAngle = 0;
    genBlock();
    particles = new ArrayList<>();
    semaParticle = new Semaphore(1);
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
    y = -3;
    angle = 0;
    rAngle = typeRandom.nextInt(4);
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

  private void hardDrop() {
    int i;
    for(i=1;i<=HEIGHT+3;i++) {
      if(isCollided(type, x, y + i, angle)) break;
    }
    if(i < HEIGHT) {
      y += i - 1;
      yShake = -0.5f;
      yaShake = 1.0f;
      xaShake = 0.3f;
      fixBlock();
      genBlock();
      updateBoard();
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
    if(delLines.size() > 0) combo++;
    else combo = 0;
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

    if(activity.optGhost) {
      int k;
      for (k = 1; k <= HEIGHT + 3; k++) {
        if (isCollided(type, x, y + k, angle)) break;
      }
      k--;
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          if (inBoard(x + j, y + k + i) && b[i * 4 + j] == 1 && board[y + k + i][x + j] == 0) {
            board[y + k + i][x + j] = -2;
          }
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
            if(activity.optGore)
              particles.add(new Particle(
                  this,
                  -1.f + k / 3.f / WIDTH * 2,
                  1.f - (i + l / 3.f) / HEIGHT * 2,
                  0.3f + 0.9f * random.nextFloat(),
                  0.9f - random.nextFloat() * 0.2f, 0.3f * random.nextFloat(), 0.2f * random.nextFloat(),
                  -1));
            if(activity.optPart)
              particles.add(new Particle(
                  this,
                  -1.f + k / 3.f / WIDTH * 2,
                  1.f - (i + l / 3.f) / HEIGHT * 2,
                  0.3f + 0.9f * random.nextFloat(),
                  1.f - random.nextFloat() * 0.1f,
                  1.f - random.nextFloat() * 0.3f,
                  1.f - random.nextFloat() * 0.3f,
                  (k % 3) * 3 + l));
          }
        }
      } else board[i + off] = board[i];
    }
    lines += off;
    switch(off) {
      case 1: score += 1; break;
      case 2: score += 2; break;
      case 3: score += 4; break;
      case 4: score += 10; break;
    }
    if(combo >= 30) score += 5;
    else if(combo >= 20) score += 4;
    else if(combo >= 10) score += 3;
    else if(combo >= 5) score += 2;
    else if(combo >= 2) score += 1;
    while(score / 4 > level) {
      level++;
      switch(level) {
        case 1: l = 55; break;
        case 3: l = 50; break;
        case 5: l = 45; break;
        case 7: l = 40; break;
        case 10: l = 35; break;
        case 12: l = 33; break;
        case 14: l = 31; break;
        case 16: l = 29; break;
        case 18: l = 27; break;
        case 20: l = 25; break;
        case 23: l = 23; break;
        case 27: l = 21; break;
        case 30: l = 18; break;
        case 31: l = 17; break;
        case 32: l = 16; break;
        case 33: l = 15; break;
        case 34: l = 14; break;
        case 35: l = 13; break;
        case 36: l = 12; break;
        case 37: l = 11; break;
        case 38: l = 10; break;
        case 39: l = 9; break;
        case 40: l = 8; break;
        case 50: l = 7; break;
        case 60: l = 6; break;
        case 70: l = 5; break;
        case 80: l = 4; break;
        case 90: l = 3; break;
        case 100: l = 2; break;
        case 120: l = 1; break;
      }
      Message msg = activity.mHandeler.obtainMessage(10);
      activity.mHandeler.sendMessage(msg);
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
    Message msg = activity.mHandeler.obtainMessage(3);
    activity.mHandeler.sendMessage(msg);
    for(int i=0;i<HEIGHT;i++) {
      for (int j = 0; j < WIDTH; j++) {
        if(board[i][j] != 0) {
          for(int k=0;k<3;k++) {
            for(int l=0;l<3;l++) {
              if(activity.optGore)
                particles.add(new Particle(
                    this,
                    -1.f + (j + k / 3.f) / WIDTH * 2,
                    1.f - (i + l / 3.f) / HEIGHT * 2,
                    0.4f + 0.8f * random.nextFloat(),
                    1.f - random.nextFloat() * 0.3f,
                    0.3f * random.nextFloat(),
                    0.2f * random.nextFloat(),
                    -1));
              if(activity.optPart)
                particles.add(new Particle(
                    this,
                    -1.f + (j + k / 3.f) / WIDTH * 2,
                    1.f - (i + l / 3.f) / HEIGHT * 2,
                    0.8f + 0.3f * random.nextFloat(),
                    1.f - random.nextFloat() * 0.1f,
                    1.f - random.nextFloat() * 0.2f,
                    1.f - random.nextFloat() * 0.2f,
                    k * 3 + l));
            }
          }
          board[i][j] = 0;
        }
      }
    }
  }
}