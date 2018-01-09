package gauri.com.paintapp.component;

import android.graphics.Path;

/**
 * Created by Gauri Gadkari on 1/7/18.
 */

class FingerPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
