package team.sjexpress.frientris2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class RenderUtil {
  public static ShortBuffer createShortBuffer(short[] array) {
    ShortBuffer buffer =  ByteBuffer.allocateDirect(2 * array.length)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer();
    buffer.put(array);
    buffer.position(0);
    return buffer;
  }

  public static IntBuffer createIntBuffer(int[] array) {
    IntBuffer buffer = ByteBuffer.allocateDirect(4 * array.length)
        .order(ByteOrder.nativeOrder())
        .asIntBuffer();
    buffer.put(array);
    buffer.position(0);
    return buffer;
  }

  public static FloatBuffer createFloatBuffer(float[] array) {
    FloatBuffer buffer = ByteBuffer.allocateDirect(4 * array.length)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    buffer.put(array);
    buffer.position(0);
    return buffer;
  }


  /* Texture */

  /* Draw */


  public final static FloatBuffer squareVertex = createFloatBuffer(new float[]{
      -1.0f, -1.0f, 0.0f,
      1.0f, -1.0f, 0.0f,
      1.0f, 1.0f, 0.0f,
      -1.0f, 1.0f, 0.0f,
  });

  public final static ShortBuffer squareIndex = createShortBuffer(new short[]{
      0, 1, 2,
      0, 2, 3,
  });

  public static void drawSquare(GL10 gl) {
    gl.glVertexPointer(3, gl.GL_FLOAT, 0, squareVertex);
    gl.glDrawElements(gl.GL_TRIANGLES, 6, gl.GL_UNSIGNED_SHORT, squareIndex);
  }

  public final static FloatBuffer diamondVertex = createFloatBuffer(new float[]{
      -1.0f, 0.0f, 0.0f,
      0.0f, -1.0f, 0.0f,
      1.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
  });

  public static void drawDiamond(GL10 gl) {
    gl.glVertexPointer(3, gl.GL_FLOAT, 0, diamondVertex);
    gl.glDrawElements(gl.GL_TRIANGLES, 6, gl.GL_UNSIGNED_SHORT, squareIndex);
  }

  public final static FloatBuffer lineVertex = createFloatBuffer(new float[] {
      0.0f, 0.0f, 0.0f,
      1.0f, 0.0f, 0.0f,
  });

  public static void drawLine(GL10 gl) {
    gl.glVertexPointer(3, gl.GL_FLOAT, 0, lineVertex);
    gl.glDrawArrays(gl.GL_LINES, 0, 2);
  }



  public final static FloatBuffer shadowSquareColor = createFloatBuffer(new float[]{
      0.0f, 0.0f, 0.0f, 0.3f,
      0.0f, 0.0f, 0.0f, 0.3f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
  });
}
