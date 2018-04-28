package team.sjexpress.frientris2;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {
  private Context context;
  private Renderer renderer;
  private Game game;

  public SurfaceView(GameActivity context, Renderer renderer) {
    super(context);
    this.context = context;
    this.renderer = renderer;
    this.game = context.game;

    setEGLConfigChooser(8, 8, 8, 8, 0, 0);
    getHolder().setFormat(PixelFormat.RGBA_8888);

    setRenderer(renderer);
    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return game.onTouchEvent(event);
  }

  public Renderer getRenderer() {
    return renderer;
  }
}
