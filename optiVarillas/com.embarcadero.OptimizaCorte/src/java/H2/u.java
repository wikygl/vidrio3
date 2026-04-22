package H2;

import M.O;
import M.V;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.View;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class u {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface a {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public int f1124a;

        /* renamed from: b  reason: collision with root package name */
        public int f1125b;

        /* renamed from: c  reason: collision with root package name */
        public int f1126c;
    }

    public static float a(Context context, int i4) {
        return TypedValue.applyDimension(1, i4, context.getResources().getDisplayMetrics());
    }

    public static boolean b(View view) {
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        if (view.getLayoutDirection() == 1) {
            return true;
        }
        return false;
    }

    public static PorterDuff.Mode c(int i4, PorterDuff.Mode mode) {
        if (i4 != 3) {
            if (i4 != 5) {
                if (i4 != 9) {
                    switch (i4) {
                        case 14:
                            return PorterDuff.Mode.MULTIPLY;
                        case 15:
                            return PorterDuff.Mode.SCREEN;
                        case 16:
                            return PorterDuff.Mode.ADD;
                        default:
                            return mode;
                    }
                }
                return PorterDuff.Mode.SRC_ATOP;
            }
            return PorterDuff.Mode.SRC_IN;
        }
        return PorterDuff.Mode.SRC_OVER;
    }
}
