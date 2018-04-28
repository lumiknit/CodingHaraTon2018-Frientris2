package team.sjexpress.frientris2;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {

    public String basePath = null;
    public GridView mGridView;
    public CustomImageAdapter mCustomImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File mediaStorageDir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        //File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
        basePath = mediaStorageDir.getPath();

        mGridView = (GridView)findViewById(R.id.gridview); // .xml의 GridView와 연결
        mCustomImageAdapter = new CustomImageAdapter(this, basePath); // 앞에서 정의한 Custom Image Adapter와 연결
        mGridView.setAdapter(mCustomImageAdapter); // GridView가 Custom Image Adapter에서 받은 값을 뿌릴 수 있도록 연결
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), mCustomImageAdapter.getItemPath(position), Toast.LENGTH_LONG).show();
            }
        });
    }
}
