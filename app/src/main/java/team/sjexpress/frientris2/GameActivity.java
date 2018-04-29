package team.sjexpress.frientris2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class GameActivity extends AppCompatActivity {
  public SurfaceView surfaceView;
  public Game game;

  public MediaPlayer bgm, se, splat;

  public Bitmap[] faces;

  public Vibrator vibrator;

  public boolean optGore;
  public boolean optPart;
  public boolean optHos;
  public boolean optVib;
  public boolean optSta;
  public boolean optGhost;
  public boolean optBgm;
  public boolean optSe;
  public int optWidth;

  public long startTime = 0;
  public long duration = 0;

  public Handler mHandeler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
        case 1:
          startGame();
          break;
        case 2:
          stopGame();
          break;
        case 3:
          duration = System.currentTimeMillis() - startTime;
          break;
        case 10:
          Toast.makeText(getApplicationContext(), "Level " + game.level, Toast.LENGTH_LONG);
          break;
        case 20:
          if(optSe) {
            se.start();
            splat.start();
          }
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    bgm = MediaPlayer.create(getApplicationContext(), R.raw.brandisky);
    bgm.setLooping(true);
    bgm.setVolume(0.9f, 0.9f);

    se = MediaPlayer.create(getApplicationContext(), R.raw.se);
    se.setVolume(1.f, 1.f);
    se.setLooping(false);

    splat = MediaPlayer.create(getApplicationContext(), R.raw.splat);
    splat.setVolume(1.f, 1.f);
    splat.setLooping(false);

    SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
    optGore = pref.getBoolean("gore", true);
    optPart = pref.getBoolean("particle", true);
    optHos = pref.getBoolean("hos", true);
    optVib = pref.getBoolean("vibration", true);
    optSta = pref.getBoolean("sta", true);
    optGhost = pref.getBoolean("ghost", true);
    optBgm = pref.getBoolean("bgm", true);
    optSe = pref.getBoolean("se", true);
    optWidth = pref.getInt("width", 7);

    Intent intent = getIntent();
    ArrayList<String> paths = intent.getStringArrayListExtra("paths");
    if(paths != null) {
      faces = new Bitmap[paths.size()];
      for(int i=0;i<paths.size();i++) {
        File file = new File(paths.get(i));
        faces[i] = readBitmap(file);
      }
    } else {
      File file;
      String fileName = intent.getStringExtra("face");
      if(fileName == null) {
        fileName = intent.getStringExtra("path");
        file = new File(fileName);
      } else {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        file = new File(dir, fileName);
      }
      faces = new Bitmap[1];
      faces[0] = readBitmap(file);
    }

    initializeOpenGL();

    game = new Game(this);
    surfaceView = new SurfaceView(this, new Renderer(game, mHandeler));
    game.setSurfaceView(surfaceView);

    setContentView(surfaceView);

    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
    Log.d("Game", "onCreate");
  }

  private Bitmap readBitmap(File file) {
    try {
      Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
      int expectedSize = 1024 / optWidth;
      int p = 8;
      while(p * 2 < expectedSize) p *= 2;
      return Bitmap.createScaledBitmap(b, p, p, true);
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopGame();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
  }

  @Override
  public boolean onTouchEvent(MotionEvent m) {
    if(m.getAction() == MotionEvent.ACTION_DOWN &&
        game.gameOverFlag >= 0 &&
        System.currentTimeMillis() - game.gameOverFlag > 2000) {
      Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
      intent.putExtra("level", game.level);
      intent.putExtra("score", game.score);
      intent.putExtra("lines", game.lines);
      intent.putExtra("duration", duration);
      stopGame();
      startActivity(intent);
    }
    return false;
  }

  public boolean initializeOpenGL() {
    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    ConfigurationInfo info = am.getDeviceConfigurationInfo();

    return true;
  }

  public SurfaceView getSurfaceView() {
    return surfaceView;
  }

  private class GameThread extends Thread {
    @Override
    public void run() {
      game.loop();
    }
  }
  GameThread thread;

  public void startGame() {
    if(thread != null) {
      stopGame();
    }
    thread = new GameThread();
    game.startRunning();
    thread.start();
    Log.d("Game", "Start");
    startTime = System.currentTimeMillis();
    if(optBgm) bgm.start();
  }

  public void stopGame() {
    if(thread != null) {
      game.stopRunning();
      game.finalize();
    }
    Log.d("Game", "Stop");
    if(optBgm) bgm.stop();
  }
}
