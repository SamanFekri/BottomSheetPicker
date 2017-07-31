package skings.bottomsheetpicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by SKings (samanf74@gmail.com) on 7/24/2017.
 */

public class MenuBottomSheetPicker extends BottomSheetPickerFragment {
    private View menuView;
    public MenuBottomSheetPicker() {
    }

    @SuppressLint("ValidFragment")
    public MenuBottomSheetPicker(View menuView) {
        super();
        this.menuView = menuView;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        // set custom menu view on picker
        try {
            CoordinatorLayout coordinatorLayout = super.contentView.findViewById(R.id.coordinat_main_menu);
            coordinatorLayout.addView(this.menuView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
