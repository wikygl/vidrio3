package E;

import android.graphics.Paint;
import android.graphics.Rect;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class c {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<L.b<Rect, Rect>> f807a = new ThreadLocal<>();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static boolean a(Paint paint, String str) {
            return paint.hasGlyph(str);
        }
    }
}
