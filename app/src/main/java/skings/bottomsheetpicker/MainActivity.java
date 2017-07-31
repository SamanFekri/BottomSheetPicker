package skings.bottomsheetpicker;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.cameraview.CameraView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(MainActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            showBottomSheet(true);
                        }else{
                            List<PermissionDeniedResponse> deniedPermissions = report.getDeniedPermissionResponses();
                            boolean cameraPermission = true;
                            boolean storagePermission = true;

                            for(PermissionDeniedResponse permission : deniedPermissions){
                                if(Manifest.permission.CAMERA.equals(permission.getPermissionName())){
                                    cameraPermission = false;
                                }else if(Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission.getPermissionName())){
                                    storagePermission = false;
                                }
                            }

                            if(storagePermission){
                                if(cameraPermission){
                                    showBottomSheet(true);
                                }else{
                                    showBottomSheet(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

//                final MenuBottomSheetPicker menuBottomSheetPicker = new MenuBottomSheetPicker(menuPicker);
//                menuBottomSheetPicker.setCameraEnable(false);
//                menuBottomSheetPicker.show(getSupportFragmentManager(), "BTDDDD");
//                menuBottomSheetPicker.setMAXShownPics(15);
//                menuPicker.findViewById(R.id.alakitv).setOnClickListener(new View.OnClickListener() {
//                    @Override
//
//                    public void onClick(View view) {
//                        Log.d("Skings", menuBottomSheetPicker.getAdapter().getSelectedMediaUris() + "");
//                        menuBottomSheetPicker.dismiss();
//                    }
//                });

            }
        });

    }

    private void showBottomSheet(boolean cameraEnable){
        final View menuPicker = LayoutInflater.from(getApplicationContext()).inflate(R.layout.picker_menu_layout, null);
        Bundle bundle = new Bundle();
        bundle.putInt("coordinator_id", R.layout.picker_menu_layout);

        final MenuBottomSheetPicker menuBottomSheetPicker = new MenuBottomSheetPicker(menuPicker);
        menuBottomSheetPicker.setShowVideo(false);
        menuBottomSheetPicker.setCameraEnable(cameraEnable);
        menuBottomSheetPicker.show(getSupportFragmentManager(), "BTDDDD");
        menuBottomSheetPicker.setMAXShownPics(25);
        menuPicker.findViewById(R.id.alakitv).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Log.d("Skings", menuBottomSheetPicker.getAdapter().getSelectedMediaUris() + "");
                menuBottomSheetPicker.dismiss();
            }
        });

    }

}
