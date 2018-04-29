package team.sjexpress.frientris2;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class TitleActivity extends AppCompatActivity {
  public static final int REQUEST_IMAGE_CAPTURE = 532;

  private String imageFilePath;
  private Uri photoUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_title);

    Button buttonStart = (Button)findViewById(R.id.button_start);
    Button buttonSettings = (Button)findViewById(R.id.button_settings);
    Button buttonManual = (Button)findViewById(R.id.button_manual);
    Button buttonGallery = (Button)findViewById(R.id.button_gallery);

    buttonStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendTakePhotoIntent();
      }
    });

    buttonSettings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
        // 액티비티 전환 코드
        Intent intent = new Intent(getApplicationContext(), Setting.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
      }
    });

    buttonManual.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "Manual", Toast.LENGTH_SHORT).show();
        // 액티비티 전환 코드
        Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
      }
    });

    buttonGallery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "Gallery", Toast.LENGTH_SHORT).show();
        // 액티비티 전환 코드
        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
      }
    });
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Toast.makeText(getApplicationContext(), "Image...", Toast.LENGTH_SHORT).show();

      Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
      ExifInterface exif = null;

      try {
        exif = new ExifInterface(imageFilePath);
      } catch (IOException e) {
        e.printStackTrace();
      }

      int exifOrientation;
      int exifDegree;

      if (exif != null) {
        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        exifDegree = exifOrientationToDegrees(exifOrientation);
      } else {
        exifDegree = 0;
      }

      Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_LONG).show();

      SparseArray<Face> mFaces = null;
      Bitmap mBitmap = rotate(bitmap, exifDegree);
      Bitmap rBitmap = mBitmap;
      FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

      if (!detector.isOperational()) {
      } else {
        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        mFaces = detector.detect(frame);
        detector.release();
        if(mFaces.size() > 0) {
          int x = (int) mFaces.valueAt(0).getPosition().x;
          int y = (int) mFaces.valueAt(0).getPosition().y;
          int w = (int) mFaces.valueAt(0).getWidth();
          int h = (int) mFaces.valueAt(0).getHeight();

          if(w > h) {
            x += (w - h) / 2;
            w = h;
          } else if(h > w) {
              y += (h - w) / 2;
              h = w;
          }
          rBitmap = Bitmap.createBitmap(mBitmap, x, y, w, h);
        }
        else {
          Toast.makeText(getApplicationContext(), "Cannot find any face; RETRY!!", Toast.LENGTH_LONG).show();
          return;
        }
      }

      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      ContextWrapper cw = new ContextWrapper(getApplicationContext());
      File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);

      File path = new File(dir, timeStamp + "_face.png");
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(path);
        rBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
      } catch(IOException e) {
        e.printStackTrace();
      } finally {
        try {
          fos.close();
        } catch(Exception e) {
          e.printStackTrace();
        }
      }

      File[] files = dir.listFiles();
      Arrays.sort(files);

      ArrayList<String> paths = new ArrayList<>();

      if(files.length < 4) {
        for(File f : files) {
          paths.add(f.toString());
        }
      } else {
        paths.add(dir.toString() + "/" + timeStamp + "_face.png");
        Random r = new Random();
        while(paths.size() < 4) {
          int n = r.nextInt(files.length - 1);
          String name = files[n].toString();
          if(paths.indexOf(name) <= 0) {
            paths.add(name);
          }
        }
      }

      Intent intent = new Intent(TitleActivity.this, GameActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      intent.putExtra("paths", paths);
      startActivity(intent);
    }
  }

  private int exifOrientationToDegrees(int exifOrientation) {
    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
      return 90;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
      return 180;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
      return 270;
    }
    return 0;
  }

  private Bitmap rotate(Bitmap bitmap, float degree) {
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  }


  private void sendTakePhotoIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
      }

      if (photoFile != null) {
        photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
        imageFileName,      /* prefix */
        ".jpg",         /* suffix */
        storageDir          /* directory */
    );
    imageFilePath = image.getAbsolutePath();
    return image;
  }
}
