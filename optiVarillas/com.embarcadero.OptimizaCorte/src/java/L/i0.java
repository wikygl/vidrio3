package l;

import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class i0 {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f5158a;

    /* renamed from: b  reason: collision with root package name */
    public static Method f5159b;

    /* renamed from: c  reason: collision with root package name */
    public static final boolean f5160c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static void a(View view, Rect rect, Rect rect2) {
            Insets systemWindowInsets = view.computeSystemWindowInsets(new WindowInsets.Builder().setSystemWindowInsets(Insets.of(rect)).build(), rect2).getSystemWindowInsets();
            rect.set(systemWindowInsets.left, systemWindowInsets.top, systemWindowInsets.right, systemWindowInsets.bottom);
        }
    }

    static {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 27) {
            z4 = true;
        } else {
            z4 = false;
        }
        f5160c = z4;
    }
}
