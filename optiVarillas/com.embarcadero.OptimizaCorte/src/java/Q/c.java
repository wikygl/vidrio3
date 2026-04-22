package q;

import android.graphics.drawable.Drawable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    public static final double f5597a = Math.cos(Math.toRadians(45.0d));

    public static float a(float f, float f4, boolean z4) {
        if (z4) {
            return (float) (((1.0d - f5597a) * f4) + f);
        }
        return f;
    }

    public static float b(float f, float f4, boolean z4) {
        if (z4) {
            return (float) (((1.0d - f5597a) * f4) + (f * 1.5f));
        }
        return f * 1.5f;
    }
}
