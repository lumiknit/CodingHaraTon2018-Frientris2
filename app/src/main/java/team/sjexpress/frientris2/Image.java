package team.sjexpress.frientris2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Image {
  public static FloatBuffer[] arr;
  static{
    arr = new FloatBuffer[10];
    arr[9] = RenderUtil.createFloatBuffer(new float[]{0.f, 1.f, 1.f, 1.f, 1.f, 0.f, 0.f, 0.f});
    for(float i=0;i<3;i++) {
      for(float j=0;j<3;j++) {
        arr[(int)i * 3 + (int)j] = RenderUtil.createFloatBuffer(new float[]{
            i / 3, (j + 1) / 3, (i + 1) / 3, (j + 1) / 3, (i + 1) / 3, j / 3, i / 3, j / 3});
      }
    }
  }

  public int w, h;
  public int id;

  public Image(GL10 gl, Context context, int resID) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;

    Bitmap img = BitmapFactory.decodeResource(context.getResources(), resID, options);
    initialize(gl, img, 0);
  }

  public Image(GL10 gl, Bitmap img) {
    initialize(gl, img, 0);
  }

  public Image(GL10 gl, Bitmap img, int option) {
    initialize(gl, img, option);
  }

  public void initialize(GL10 gl, Bitmap img, int option) {
    int[] id = new int[1];
    id[0] = 0;

    gl.glGenTextures(1, id, 0);
    gl.glBindTexture(gl.GL_TEXTURE_2D, id[0]);

    gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
    gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);

    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

    int width = img.getWidth();
    int height = img.getHeight();
    ByteBuffer imageBuffer = ByteBuffer.allocateDirect(height * width * 4);
    imageBuffer.order(ByteOrder.nativeOrder());
    byte[] buf = new byte[4];

    if(option == 0) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int argb = img.getPixel(j, i);
          buf[0] = (byte) ((argb & (0x00ff0000)) >> 16);
          buf[1] = (byte) ((argb & (0x0000ff00)) >> 8);
          buf[2] = (byte) ((argb & (0x000000ff)) >> 0);
          buf[3] = (byte) ((argb & (0xff000000)) >> 24);
          imageBuffer.put(buf);
        }
      }
    } else {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int argb = img.getPixel(j, i);
          buf[0] = (byte) ((argb & (0x00ff0000)) >> 16);
          buf[1] = (byte) ((argb & (0x0000ff00)) >> 8);
          buf[2] = (byte) ((argb & (0x000000ff)) >> 0);
          buf[3] = (byte) (((argb & (0xff000000)) >> 24));
          imageBuffer.put(buf);
        }
      }
    }

    imageBuffer.position(0);

    gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height,
        0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, imageBuffer);
    // GLUtils.texImage2D(gl.GL_TEXTURE_2D, 0, img, 0);

    img.recycle();

    this.id = id[0];

    /* Get Size */
    w = img.getWidth();
    h = img.getHeight();
  }

  public int getId() {
    return id;
  }

  public int getW() {
    return w;
  }

  public int getH() {
    return h;
  }

  public void draw(GL10 gl) {
    this.draw(gl, 9);
  }

  public void draw(GL10 gl, int index) {
    gl.glPushMatrix(); {
      gl.glEnable(gl.GL_TEXTURE);
      gl.glEnable(gl.GL_TEXTURE_2D);
      gl.glBindTexture(gl.GL_TEXTURE_2D, id);

      gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, arr[index]);
      gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY);

      RenderUtil.drawSquare(gl);

      gl.glDisableClientState(gl.GL_TEXTURE_COORD_ARRAY);
      gl.glDisable(gl.GL_TEXTURE);
      gl.glDisable(gl.GL_TEXTURE_2D);
    } gl.glPopMatrix();
  }
}
