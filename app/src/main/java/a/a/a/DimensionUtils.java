package a.a.a;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

public class DimensionUtils {

    /**
     * Converts a dp value to pixels.
     *
     * @param context the Context in which to do the conversion
     * @param value   the value in dp to convert
     * @return the value in pixels
     */
    public static float dpToPx(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * Inflates a layout resource into a new View.
     *
     * @param context   the Context in which to inflate the layout
     * @param layoutRes the layout resource ID to inflate
     * @return the inflated View
     */
    public static View inflate(Context context, @LayoutRes int layoutRes) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutRes, null);
    }

    /**
     * Inflates a layout resource into a ViewGroup.
     *
     * @param context   the Context in which to inflate the layout
     * @param parent    the parent ViewGroup to attach the inflated layout to
     * @param layoutRes the layout resource ID to inflate
     * @return the inflated View
     */
    public static View inflate(Context context, ViewGroup parent, @LayoutRes int layoutRes) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutRes, parent, true);
    }
}
