package M;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.PointerIcon;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class B {

    /* renamed from: a  reason: collision with root package name */
    public final PointerIcon f1513a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {
        public static PointerIcon a(Bitmap bitmap, float f, float f4) {
            return PointerIcon.create(bitmap, f, f4);
        }

        public static PointerIcon b(Context context, int i4) {
            return PointerIcon.getSystemIcon(context, i4);
        }

        public static PointerIcon c(Resources resources, int i4) {
            return PointerIcon.load(resources, i4);
        }
    }

    public B(PointerIcon pointerIcon) {
        this.f1513a = pointerIcon;
    }
}
