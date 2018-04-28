package team.sjexpress.frientris2;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {
  private Game game;

  private Image hos, sj, face;

  private int width, height;
  private float screenRatio;

  public Renderer(Game game) {
    this.game = game;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    hos = new Image(gl, game.activity.getApplicationContext(), R.drawable.hos);
    sj = new Image(gl, game.activity.getApplicationContext(), R.drawable.sj);
    face = new Image(gl, game.activity.face, 1);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    this.width = width;
    this.height = height;
    screenRatio = (float) height / width;
    gl.glViewport(0, 0, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    gl.glClearColor(1.f, 1.f, 1.f, 1.f);
    gl.glClear(gl.GL_DEPTH_BUFFER_BIT | gl.GL_COLOR_BUFFER_BIT);

    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadIdentity();
    if(screenRatio > 2.f) {
      gl.glOrthof(-1.f, 1.f, -screenRatio, screenRatio, -1.f, 1.f);
    } else {
      gl.glOrthof(-2.f / screenRatio, 2.f / screenRatio, -2.f, 2.f, -1.f, 1.f);
    }

    gl.glMatrixMode(gl.GL_MODELVIEW);
    gl.glLoadIdentity();

    gl.glEnable(gl.GL_BLEND);
    gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnableClientState(gl.GL_VERTEX_ARRAY);

    long d = System.currentTimeMillis() - game.gameOverFlag;

    if(game.gameOverFlag >= 0) {
      gl.glPushMatrix();
      gl.glScalef(1.4f, 1.4f, 1.f);
      gl.glColor4f(1.f, 1.f, 1.f, d * 0.0002f);
      gl.glRotatef(180.f * (100000.f / d), 0.f, 0.f, 1.f);
      hos.draw(gl);
      gl.glPopMatrix();
    }

    gl.glTranslatef(game.xShake, game.yShake, 0.f);

    gl.glColor4f(0.f, 0.f, 0.f, 1.f);

    gl.glScalef(1.0f, 2.0f, 1.0f);

    gl.glPushMatrix();
    gl.glColor4f(0.0f, 0.0f, 0.0f, 1.f);
    if(game.activity.optHos && game.gameOverFlag >= 0) {
      gl.glRotatef(d / 300.f, 0.f, 0.f, 1.f);
      gl.glScalef(accF(d), accF(d), 1.f);
    }
    RenderUtil.drawSquare(gl);
    gl.glPopMatrix();

    gl.glPushMatrix();
    if(game.activity.optHos && game.gameOverFlag >= 0) {
      gl.glRotatef(d / 400.f, 0.f, 0.f, -1.f);
      gl.glScalef(accF(d), accF(d), 1.f);
    }
    gl.glScalef(0.94f, 0.97f, 1.0f);
    gl.glColor4f(1.f, 1.f, 1.f, 1.f);
    RenderUtil.drawSquare(gl);
    gl.glPopMatrix();

    gl.glPushMatrix();
    if(game.activity.optHos && game.gameOverFlag >= 0) {
      gl.glRotatef(d / 200.f, 0.f, 0.f, -1.f);
      gl.glScalef(accF(d), accF(d), 1.f);
    }
    gl.glScalef(0.92f, 0.96f, 1.0f);
    gl.glTranslatef(-1.f, -1.f, 0.f);
    gl.glScalef(1.0f / game.WIDTH, 1.0f / game.HEIGHT, 1.0f);
    gl.glTranslatef(-1.f, 1.f, 0.f);
    for(int i=game.HEIGHT - 1;i >= 0;i--) {
      gl.glPushMatrix();
      for(int j=0;j<game.WIDTH;j++) {
        gl.glTranslatef(2.f, 0.f, 0.f);
        if(game.board[i][j] != 0) {
          gl.glPushMatrix();
          gl.glTranslatef(1.f, 1.f, 0.f);
          gl.glScalef(0.95f, 0.95f, 1.0f);
          gl.glTranslatef(-1.f, -1.f, 0.f);
          if(game.board[i][j] > 0)
            gl.glRotatef((game.board[i][j] - 1) * 90.f, 0.f, 0.f, -1.f);
          else
            gl.glRotatef(game.rAngle * 90.f, 0.f, 0.f, -1.f);
          gl.glColor4f(0.f, 0.f, 0.f, 1.f);
          RenderUtil.drawSquare(gl);
          gl.glScalef(0.95f, 0.95f, 1.0f);
          if(game.delLineFlag >= 0 && game.delLines.indexOf(i) >= 0) {
            gl.glColor4f(1.f,
                1.f * game.delLineFlag / game.DELL_F_MAX,
                1.f * game.delLineFlag / game.DELL_F_MAX, 1.f);
          } else {
            gl.glColor4f(1.f, 1.f, 1.f, 1.f);
          }
          face.draw(gl);
          gl.glPopMatrix();
        }
      }
      gl.glPopMatrix();
      gl.glTranslatef(0.f, 2.f, 0.f);
    }
    gl.glPopMatrix();

    gl.glPushMatrix();
    gl.glScalef(0.92f, 0.96f, 1.0f);
    if(game.activity.optHos && game.gameOverFlag >= 0) {
      gl.glRotatef(d / 300.f, 0.f, 0.f, -1.f);
      gl.glScalef(accF(d), accF(d), 1.f);
    }
    for(int i = 0; i < game.particles.size(); i++) {
      Particle p = game.particles.get(i);
      gl.glColor4f(p.r, p.g, p.b, p.a);
      gl.glPushMatrix();
      gl.glTranslatef(p.x, p.y, 0.f);
      gl.glScalef(0.3f / game.WIDTH * p.size, 0.3f / game.HEIGHT * p.size, 1.f);
      gl.glRotatef(p.angle, 0.f, 0.f, 1.f);
      if(p.index < 0)  RenderUtil.drawSquare(gl);
      else face.draw(gl, p.index);
      gl.glPopMatrix();
    }
    gl.glPopMatrix();

    gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
  }

  public float accF(long d) {
    return 1000.f / (1000.f  + d * d / 1000.f);
  }
}
