package skings.bottomsheetpicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import skings.bottomsheetpicker.model.Media;
import skings.bottomsheetpicker.model.SelectableMedia;

/**
 * Created by SKings (samanf74@gmail.com) on 7/17/2017.
 */

public class BottomSheetPickerFragment extends BottomSheetDialogFragment {

    private List<SelectableMedia> medias;
    private int MAXShownPics = 25;
    protected View contentView = null;
    private boolean showImage = true, showVideo = true;
    private  PickerRVAdapter adapter;
    private boolean cameraEnable = true;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        // set picker dialog to its main content view
        contentView = View.inflate(getContext(), R.layout.picker_dialog, null);

        // set recycler view configs
        RecyclerView rv = contentView.findViewById(R.id.rv);
        medias = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(llm);

        // set adapter configs
        adapter = new PickerRVAdapter(medias, getContext(), getActivity(), cameraEnable, 3);
        adapter.setItemSize(300, 300);
        rv.setAdapter(adapter);

        // get media uri
        ArrayList<Media> paths = findAllImageVideoPath();
        for (int i = 0; i < paths.size() && i < MAXShownPics; i++) {
            medias.add(new SelectableMedia(paths.get(i).getType(), paths.get(i).getUri()));
        }
        dialog.setContentView(contentView);
    }

    public ArrayList<Media> findAllImageVideoPath() {
        ArrayList<Media> resultPath = new ArrayList<>();
        // Get relevant columns for use later.
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        // Return only video and image metadata.
        String selection = "";
        if (showImage) {
            selection += MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        }
        if (showVideo) {
            if (!"".equals(selection)) {
                selection += " OR ";
            }
            selection += MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }

//        Log.d("SKings", selection);
        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
        cursor.moveToFirst();
        do {

            String tmp = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)).toLowerCase();
            if (tmp.contains("image")) {
                resultPath.add(new Media("image", Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))))));
            } else if (tmp.contains("video")) {
                resultPath.add(new Media("video", Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))))));
            }
        } while (cursor.moveToNext());
        return resultPath;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dismiss();
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public boolean isShowVideo() {
        return showVideo;
    }

    public void setShowVideo(boolean showVideo) {
        this.showVideo = showVideo;
    }

    public int getMAXShownPics() {
        return MAXShownPics;
    }

    public void setMAXShownPics(int numOfShownPics) {
        this.MAXShownPics = numOfShownPics;
    }

    public PickerRVAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PickerRVAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean isCameraEnable() {
        return cameraEnable;
    }

    public void setCameraEnable(boolean cameraEnable) {
        this.cameraEnable = cameraEnable;
    }
}
