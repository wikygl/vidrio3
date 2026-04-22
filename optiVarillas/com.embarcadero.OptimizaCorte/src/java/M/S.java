package M;

import android.view.View;
import android.view.ViewParent;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class S {
    public static boolean a(ViewParent viewParent, View view, float f, float f4, boolean z4) {
        return viewParent.onNestedFling(view, f, f4, z4);
    }

    public static boolean b(ViewParent viewParent, View view, float f, float f4) {
        return viewParent.onNestedPreFling(view, f, f4);
    }

    public static void c(ViewParent viewParent, View view, int i4, int i5, int[] iArr) {
        viewParent.onNestedPreScroll(view, i4, i5, iArr);
    }

    public static void d(ViewParent viewParent, View view, int i4, int i5, int i6, int i7) {
        viewParent.onNestedScroll(view, i4, i5, i6, i7);
    }

    public static void e(ViewParent viewParent, View view, View view2, int i4) {
        viewParent.onNestedScrollAccepted(view, view2, i4);
    }

    public static boolean f(ViewParent viewParent, View view, View view2, int i4) {
        return viewParent.onStartNestedScroll(view, view2, i4);
    }

    public static void g(ViewParent viewParent, View view) {
        viewParent.onStopNestedScroll(view);
    }
}
