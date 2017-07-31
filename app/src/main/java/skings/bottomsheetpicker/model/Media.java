package skings.bottomsheetpicker.model;

import android.net.Uri;

/**
 * Created by SKings (samanf74@gmail.com) on 7/17/2017.
 */

public class Media {
    private String type;
    private Uri uri;

    public Media(String type, Uri uri) {
        this.type = type;
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
