package skings.bottomsheetpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by SKings (samanf74@gmail.com) on 7/29/2017.
 */

public class ImagePreviewDialog extends Dialog {

    private SimpleDraweeView mainImage;
    private Point screen;
    private Context context;

    public ImagePreviewDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        getScreenSize();
    }

    public ImagePreviewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        getScreenSize();
    }

    protected ImagePreviewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        getScreenSize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_preview);

        this.mainImage = findViewById(R.id.simpledrawee_image_preview_dialog);
    }

    public SimpleDraweeView getMainImage() {
        return mainImage;
    }

    public void setMainImage(Uri imageUri) {
//        Pair pair = getDropboxIMGSize(imageUri);
//
        int degree = getExifOrientation(new File(imageUri.getPath()).getAbsolutePath());
        Point dim = getDropboxIMGSize(imageUri);

        if(degree == 90 || degree == 270){
            int tmp = dim.x;
            dim.x = dim.y;
            dim.y = tmp;
        }

        float ratio = (float) dim.x / (float) dim.y;

        switch (degree){
            case 0:
            case 180:
                if(dim.x > screen.x){
                    dim.x = screen.x;
                    dim.y = (int) (screen.x / ratio);
                    Log.d("SKings1",dim.x +"x" + dim.y);
                }
                break;

            case 90:
            case 270:
                if(dim.y > screen.y){
                    dim.y = screen.y;
                    dim.x = (int) (screen.y * ratio);
                    Log.d("SKings2",dim.x +"x" + dim.y);
                }
                break;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
                .setResizeOptions(new ResizeOptions(dim.x, dim.y))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(this.getMainImage().getController())
                .setImageRequest(request)
                .build();

        this.mainImage.setController(controller);
        this.mainImage.setLayoutParams(new RelativeLayout.LayoutParams(dim.x, dim.y));
    }

    private Point getDropboxIMGSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        Log.d("SKings", imageWidth + "x" + imageHeight);
        return new Point(imageWidth, imageHeight);
    }

    public static int getExifOrientation(String filepath) {// YOUR MEDIA PATH AS STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    private void getScreenSize() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen = new Point();
        display.getSize(screen);
        screen.x *= 0.9;
        screen.y *= 0.6;

    }
}
