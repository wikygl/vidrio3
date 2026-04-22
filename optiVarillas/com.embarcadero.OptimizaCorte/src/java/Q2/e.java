package Q2;

import M.O;
import M.V;
import N.p;
import android.view.View;
import android.view.ViewParent;
import com.google.android.material.sidesheet.SideSheetBehavior;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class e implements p {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ SideSheetBehavior f2023a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f2024b;

    public /* synthetic */ e(SideSheetBehavior sideSheetBehavior, int i4) {
        this.f2023a = sideSheetBehavior;
        this.f2024b = i4;
    }

    @Override // N.p
    public final boolean a(View view) {
        String str;
        final SideSheetBehavior sideSheetBehavior = this.f2023a;
        sideSheetBehavior.getClass();
        final int i4 = this.f2024b;
        if (i4 != 1 && i4 != 2) {
            WeakReference weakReference = sideSheetBehavior.p;
            if (weakReference != null && weakReference.get() != null) {
                View view2 = (View) sideSheetBehavior.p.get();
                Runnable runnable = new Runnable() { // from class: Q2.f
                    @Override // java.lang.Runnable
                    public final void run() {
                        SideSheetBehavior sideSheetBehavior2 = sideSheetBehavior;
                        View view3 = (View) sideSheetBehavior2.p.get();
                        if (view3 != null) {
                            sideSheetBehavior2.u(view3, i4, false);
                        }
                    }
                };
                ViewParent parent = view2.getParent();
                if (parent != null && parent.isLayoutRequested()) {
                    WeakHashMap<View, V> weakHashMap = O.f1526a;
                    if (view2.isAttachedToWindow()) {
                        view2.post(runnable);
                    }
                }
                runnable.run();
            } else {
                sideSheetBehavior.s(i4);
            }
            return true;
        }
        StringBuilder sb = new StringBuilder("STATE_");
        if (i4 == 1) {
            str = "DRAGGING";
        } else {
            str = "SETTLING";
        }
        throw new IllegalArgumentException(C.b.c(sb, str, " should not be set externally."));
    }
}
