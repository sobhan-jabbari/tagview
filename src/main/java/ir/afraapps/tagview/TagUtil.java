package ir.afraapps.tagview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;

public class TagUtil {

  @ColorInt
  public static int changeLightness(@ColorInt int color, float value) {
    float[] hsl = new float[3];
    ColorUtils.colorToHSL(color, hsl);
    hsl[2] += value;
    hsl[2] = Math.min(Math.max(hsl[2], 0.0f), 1.0f);
    return ColorUtils.HSLToColor(hsl);
  }

  @ColorInt
  public static int setLightness(@ColorInt int color, float value) {
    float[] hsl = new float[3];
    ColorUtils.colorToHSL(color, hsl);
    hsl[2] = value;
    hsl[2] = Math.min(Math.max(hsl[2], 0.0f), 1.0f);
    return ColorUtils.HSLToColor(hsl);
  }

  public static Typeface getFromAsset(Context context, @Nullable String fontName) {
    if (TextUtils.isEmpty(fontName)) return Typeface.DEFAULT;
    return Typeface.createFromAsset(context.getAssets(), fontName);
  }
}
