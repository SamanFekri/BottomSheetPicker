package skings.bottomsheetpicker.model;

import android.net.Uri;

/**
 * Created by SKings (samanf74@gmail.com) on 7/18/2017.
 */

public class SelectableMedia extends Media {
    private boolean select;
    public SelectableMedia(String type, Uri uri) {
        super(type, uri);
        this.select = false;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
