package u0;

import android.os.Build;
import android.view.ViewGroup;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class n {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f6002a = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static int a(ViewGroup viewGroup, int i4) {
            return viewGroup.getChildDrawingOrder(i4);
        }

        public static void b(ViewGroup viewGroup, boolean z4) {
            viewGroup.suppressLayout(z4);
        }
    }

    public static void a(ViewGroup viewGroup, boolean z4) {
        if (Build.VERSION.SDK_INT >= 29) {
            a.b(viewGroup, z4);
        } else if (f6002a) {
            try {
                a.b(viewGroup, z4);
            } catch (NoSuchMethodError unused) {
                f6002a = false;
            }
        }
    }
}
