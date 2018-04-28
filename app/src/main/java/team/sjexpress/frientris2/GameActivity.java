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
import java.util.concurrent.Semaphore;

public class GameActivity extends AppCompatActivity {
  public SurfaceView surfaceView;
  public Game game;

  public Bitmap face;

  public Vibrator vibrator;

  public boolean optGore;
  public boolean optPart;
  public boolean optHos;
  public boolean optVib;
  public boolean optSta;
  public int optWidth;

  private Handler mHandeler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
        case 1:
          startGame();
          break;
        case 2:
          stopGame();
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN);

    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

    SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
    optGore = pref.getBoolean("gore", true);
    optPart = pref.getBoolean("particle", true);
    optHos = pref.getBoolean("hos", true);
    optVib = pref.getBoolean("vibration", true);
    optSta = pref.getBoolean("sta", true);
    optWidth = pref.getInt("width", 7);

    Intent intent = getIntent();
    String fileName = intent.getStringExtra("face");
    ContextWrapper cw = new ContextWrapper(getApplicationContext());
    File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
    File file = new File(dir, fileName);
    try {
      face = BitmapFactory.decodeStream(new FileInputStream(file));
    } catch(Exception e) {
      e.printStackTrace();
    }

    initializeOpenGL();

    game = new Game(this);
    surfaceView = new SurfaceView(this, new Renderer(game, mHandeler));
    game.setSurfaceView(surfaceView);

    setContentView(surfaceView);

    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
    Log.d("Game", "onCreate");
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
  }

  @Override
  public boolean onTouchEvent(MotionEvent m) {
    if(m.getAction() == MotionEvent.ACTION_DOWN &&
        game.gameOverFlag >= 0 &&
        System.currentTimeMillis() - game.gameOverFlag > 8000) {
      Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
      intent.putExtra("score", game.score);
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
  }

  public void stopGame() {
    if(thread != null) {
      game.stopRunning();
      game.finalize();
    }
    Log.d("Game", "Stop");
  }
}
