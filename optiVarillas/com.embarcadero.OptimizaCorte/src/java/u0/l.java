package u0;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class l {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f5998a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static Bitmap a(Picture picture) {
            return Bitmap.createBitmap(picture);
        }
    }

    static {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 28) {
            z4 = true;
        } else {
            z4 = false;
        }
        f5998a = z4;
    }
}
