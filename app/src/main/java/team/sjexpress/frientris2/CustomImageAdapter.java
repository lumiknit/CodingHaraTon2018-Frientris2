package team.sjexpress.frientris2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

import static android.content.ContentValues.TAG;

public class CustomImageAdapter extends BaseAdapter {
    private Context mContext;
    String[] mImgs;
    Bitmap bm;
    String mBasePath = null;

    public CustomImageAdapter(Context context, String basepath) {
        this.mContext = context;
        this.mBasePath = basepath;

        File file = new File(mBasePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }
        }
        mImgs = file.list();
    }

    public int getCount() {
        return mImgs.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public String getItemPath(int position){
        String path = mBasePath + File.separator + mImgs[position];
        return path;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgs[position]);
        Bitmap mThumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
        imageView.setPadding(8, 8, 8, 8);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(mThumbnail);
        return imageView;
    }


}
