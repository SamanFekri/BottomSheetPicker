package skings.bottomsheetpicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.cameraview.CameraView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import skings.bottomsheetpicker.model.SelectableMedia;

/**
 * Created by SKings (samanf74@gmail.com) on 7/17/2017.
 */

public class PickerRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SelectableMedia> medias;
    private List<Uri> selectedImgUris;
    public Context context;
    private int width, height;
    private int maxLimitSelection;
    private Boolean cameraEnable;
    private Activity activity;

    private CameraViewHolder cameraViewHolder;
    private View cameraView;
//    private Camera camera;

    private ImagePreviewDialog imagePreviewDialog;

    public PickerRVAdapter(List<SelectableMedia> medias, Context context, Activity activity, Boolean cameraEnable, int maxLimitSelection) {
        this.medias = medias;
        imgUrisEmprty();
        this.context = context;

        // default size of items is 350
        this.height = 350;
        this.width = 350;

        // set limit in selection
        this.maxLimitSelection = maxLimitSelection;

        // set show camera or no
        this.cameraEnable = cameraEnable;

        // adding preview of camera if you choos it
        if (this.cameraEnable) {
//            camera = Camera.open();
            this.medias.add(new SelectableMedia("camera", null));
        }

        // set activity
        this.activity = activity;

        // create image preview
        imagePreviewDialog = new ImagePreviewDialog(context);
        imagePreviewDialog.show();
        imagePreviewDialog.dismiss();
        imagePreviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_recycler_view, parent, false);
        // set which type of view holder must shown in this place
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_recycler_view, parent, false);
                return (new ImageViewHolder(v));
            case 1:
                if (cameraViewHolder == null) {
                    cameraView = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_item_recycler_view, parent, false);
                    cameraViewHolder = new CameraViewHolder(cameraView/*, context, width, height, camera*/);
                }
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_item_recycler_view, parent, false);
//                return (new CameraViewHolder(v/*, context, width, height, camera*/));
                return cameraViewHolder;
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_in_recycler_view, parent, false);
                return (new VideoViewHolder(v));
        }
        return (new ImageViewHolder(v));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        // bind views
        switch (holder.getItemViewType()) {
            case 0:
                bindImageViewHolder((PickerRVAdapter.ImageViewHolder) holder, position);
                break;
            case 1:
                bindCameraViewHolder((PickerRVAdapter.CameraViewHolder) holder, position);
                break;
            case 2:
                bindVideoViewHolder((PickerRVAdapter.VideoViewHolder) holder, position);
                break;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getItemViewType() == 1) {
            ((CameraViewHolder) holder).cv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getItemViewType() == 1) {
            ((CameraViewHolder) holder).cv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    @Override
    public int getItemViewType(int position) {
        // return type depends on position
        int retVal = 0;
        switch (medias.get(position).getType()) {
            case "image":
                retVal = 0;
                break;
            case "camera":
                retVal = 1;
                break;
            case "video":
                retVal = 2;
                break;
        }
        return retVal;
    }

    // Binding View Methods
    @SuppressLint("ClickableViewAccessibility")
    public void bindImageViewHolder(final PickerRVAdapter.ImageViewHolder holder, final int position) {
        // get media
        final SelectableMedia media = medias.get(position);

        // get uri of that media and set on simple drawee and set size of image
        final Uri uri = medias.get(position).getUri();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.iv.getController())
                .setImageRequest(request)
                .build();

        holder.iv.setController(controller);
        holder.iv.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageOnClickListener(view, position);
            }
        });
        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                imagePreviewDialog.setMainImage(uri);
                imagePreviewDialog.show();
                return false;
            }
        });

        holder.iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (imagePreviewDialog.isShowing()) {
                        imagePreviewDialog.dismiss();
                    }
                }
                return false;
            }
        });

        holder.cb.setChecked(media.isSelect());
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = !media.isSelect();
//                Log.d("SKings", selectedImgUris.size() + " " + maxLimitSelection);
                if (isChecked) {
                    if (maxLimitSelection > selectedImgUris.size()) {
                        media.setSelect(isChecked);
                        holder.cb.setChecked(isChecked);
                        if (isChecked) {
                            selectedImgUris.add(uri);
                        } else {
                            selectedImgUris.remove(uri);
                        }
                    } else {
                        holder.cb.setChecked(!isChecked);
                        Toast.makeText(context, "you can just select " + maxLimitSelection, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    media.setSelect(isChecked);
                    holder.cb.setChecked(isChecked);
                    if (isChecked) {
                        selectedImgUris.add(uri);
                    } else {
                        selectedImgUris.remove(uri);
                    }
                }
                imageCheckBoxOnClickListener(view, position);
            }
        });
    }

    public void bindCameraViewHolder(final PickerRVAdapter.CameraViewHolder holder, final int position) {
        holder.iv.setImageResource(android.R.drawable.ic_menu_camera);
//        holder.width = this.width;
//        holder.height = this.height;
//        holder.cv.start();

        holder.setIsRecyclable(true);
        holder.cv.setLayoutParams(new RelativeLayout.LayoutParams(this.width, this.height));
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraOnClickListener(view, position);
            }
        });

    }

    public void bindVideoViewHolder(final PickerRVAdapter.VideoViewHolder holder, final int position) {
        // get media
        final SelectableMedia media = medias.get(position);

        // get uri of that media and set on simple drawee and set size of image
        final Uri uri = medias.get(position).getUri();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.iv.getController())
                .setImageRequest(request)
                .build();

        holder.iv.setController(controller);
        holder.iv.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        // set image on click listener
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoOnClickListener(view, position);
            }
        });

        // set time duration of video to textview of it
        MediaPlayer mp = MediaPlayer.create(context, uri);
        int duration = mp.getDuration();
        mp.release();
        String tmp = String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        holder.tv.setText(tmp);

        // set size of bottom nav of video
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, holder.rl.getLayoutParams().height);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.video_item_recycler_view_preview_image);
        holder.rl.setLayoutParams(params);

        holder.cb.setChecked(media.isSelect());
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = !media.isSelect();
//                Log.d("SKings", selectedImgUris.size() + " " + maxLimitSelection);

                if (isChecked) {
                    if (maxLimitSelection > selectedImgUris.size()) {
                        media.setSelect(isChecked);
                        holder.cb.setChecked(isChecked);
                        if (isChecked) {
                            selectedImgUris.add(uri);
                        } else {
                            selectedImgUris.remove(uri);
                        }
                    } else {
                        holder.cb.setChecked(!isChecked);
                        Toast.makeText(context, "you can just select " + maxLimitSelection, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    media.setSelect(isChecked);
                    holder.cb.setChecked(isChecked);
                    if (isChecked) {
                        selectedImgUris.add(uri);
                    } else {
                        selectedImgUris.remove(uri);
                    }
                }
                imageCheckBoxOnClickListener(view, position);
            }
        });
    }

    // View Holders class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView iv;
        CheckBox cb;

        ImageViewHolder(View viewItem) {
            super(viewItem);
            iv = viewItem.findViewById(R.id.image_item_recycler_view_main_image);
            cb = viewItem.findViewById(R.id.checkbox_recycler_picker);
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView iv;
        CheckBox cb;
        RelativeLayout rl;
        TextView tv;

        VideoViewHolder(View viewItem) {
            super(viewItem);
            iv = viewItem.findViewById(R.id.video_item_recycler_view_preview_image);
            cb = viewItem.findViewById(R.id.checkbox_recycler_picker_video);
            rl = viewItem.findViewById(R.id.bottom_line_video_item_recycler_view);
            tv = viewItem.findViewById(R.id.textview_video_duration_item);
        }
    }

    public static class CameraViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView iv;
        //        TextureView tv;
        CameraView cv;
//        Camera camera;
//        Context context;
//        int width, height;

        CameraViewHolder(View viewItem/*, final Context context, int width, int height*/) {
            super(viewItem);
//            this.context = context;

            iv = viewItem.findViewById(R.id.image_item_recycler_view_camera_image);
//            tv = viewItem.findViewById(R.id.textureview_camera_item_recycler_view);
            cv = viewItem.findViewById(R.id.camera_view_item_recycler_view);
            cv.start();
//            Log.d("SKings","here");
//            camera = Camera.open();
//            this.camera = camera;
//
//            tv.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
//            tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//
//                @Override
//                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//
//                    try {
//                        camera.setPreviewTexture(surfaceTexture);
//                        new AsyncTask<Void, Void, Boolean>() {
//                            protected Boolean doInBackground(Void... params) {
//                                try {
//                                    Thread.sleep(10);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                camera.startPreview();
//                                return null;
//                            }
//                        }.execute();
//
//                        Camera.Parameters parameters = camera.getParameters();
//                        float best_ratio = 0;
//                        int best_height = 0, best_width = 0;
//                        for (Camera.Size previewSize : camera.getParameters().getSupportedPreviewSizes()) {
//                            float temp = ((float) previewSize.width / (float) previewSize.height);
//                            if (temp == 1.0) {
//                                best_ratio = temp;
//                                best_height = previewSize.height;
//                                best_width = previewSize.width;
//                                break;
//                            } else if (Math.abs(temp - 1) < Math.abs(best_ratio - 1)) {
//                                best_ratio = temp;
//                                best_height = previewSize.height;
//                                best_width = previewSize.width;
//                            }
//
//                        }
//                        parameters.setPreviewSize(best_width, best_height);
//                        camera.setParameters(parameters);
//
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
//
//                }
//
//                @Override
//                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
//                    new AsyncTask<Void, Void, Boolean>() {
//                        protected Boolean doInBackground(Void... params) {
//                            try {
//                                Thread.sleep(10);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            camera.stopPreview();
//                            return null;
//                        }
//                    }.execute();
//
//                    return false;
//                }
//
//                @Override
//                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//                    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                    int rotation = display.getRotation();
//                    switch (rotation) {
//                        case Surface.ROTATION_0:
//                            camera.setDisplayOrientation(90);
//                            break;
//                        case Surface.ROTATION_90:
//                            camera.setDisplayOrientation(0);
//                            break;
//                        case Surface.ROTATION_180:
//                            camera.setDisplayOrientation(270);
//                            break;
//                        case Surface.ROTATION_270:
//                            camera.setDisplayOrientation(180);
//                            break;
//                    }
//                }
//            });

//            SurfaceHolder surfaceHolder = sv.getHolder();
//
//            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                    camera = Camera.open();
//                    try {
//                        camera.setPreviewDisplay(surfaceHolder);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Camera.Parameters parameters = camera.getParameters();
//
//                    float best_ratio = 0;
//                    int best_height = 0, best_width = 0;
//                    for (Camera.Size previewSize : camera.getParameters().getSupportedPreviewSizes()) {
//                        float temp = ((float) previewSize.width / (float) previewSize.height);
//                        if (temp == 1.0) {
//                            best_ratio = temp;
//                            best_height = previewSize.height;
//                            best_width = previewSize.width;
//                            break;
//                        } else if (Math.abs(temp - 1) < Math.abs(best_ratio - 1)) {
//                            best_ratio = temp;
//                            best_height = previewSize.height;
//                            best_width = previewSize.width;
//                        }
//
//                    }
//
//                    parameters.setPreviewSize(best_width, best_height);
//                    camera.setParameters(parameters);
//                    camera.startPreview();
//
//                }
//
//                @Override
//                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//                    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                    int rotation = display.getRotation();
//                    switch (rotation){
//                        case Surface.ROTATION_0:
//                            camera.setDisplayOrientation(90);
//                            break;
//                        case Surface.ROTATION_90:
//                            camera.setDisplayOrientation(0);
//                            break;
//                        case Surface.ROTATION_180:
//                            camera.setDisplayOrientation(270);
//                            break;
//                        case Surface.ROTATION_270:
//                            camera.setDisplayOrientation(180);
//                            break;
//                    }
//
//                }
//
//                @Override
//                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
////                    camera.stopPreview();
////                    camera.release();
//                }
//            });
        }
    }

    // change medias with new array list
    public void imgUrisEmprty() {
        this.selectedImgUris = new ArrayList<>();
    }

    // change size of items
    public void setItemSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // when clicked listens
    public void cameraOnClickListener(View view, int position) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        activity.startActivityForResult(intent, 0);
    }

    public void imageOnClickListener(View view, int position) {

    }

    public void imageCheckBoxOnClickListener(View view, int position) {

    }

    public void videoOnClickListener(View view, int position) {

    }

    // get uris what medias selected in picker
    public List<Uri> getSelectedMediaUris() {
        return selectedImgUris;
    }

    public void setSelectedImgUris(List<Uri> selectedImgUris) {
        this.selectedImgUris = selectedImgUris;
    }

}
