package O;

import android.graphics.Path;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {
    public static Interpolator a(float f, float f4) {
        return new PathInterpolator(f, f4);
    }

    public static Interpolator b(float f, float f4, float f5, float f6) {
        return new PathInterpolator(f, f4, f5, f6);
    }

    public static Interpolator c(Path path) {
        return new PathInterpolator(path);
    }
}
