package Q2;

import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.sidesheet.SideSheetBehavior;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends d {

    /* renamed from: a  reason: collision with root package name */
    public final SideSheetBehavior<? extends View> f2022a;

    public b(SideSheetBehavior<? extends View> sideSheetBehavior) {
        this.f2022a = sideSheetBehavior;
    }

    @Override // Q2.d
    public final int a(ViewGroup.MarginLayoutParams marginLayoutParams) {
        return marginLayoutParams.rightMargin;
    }

    @Override // Q2.d
    public final float b(int i4) {
        float f = this.f2022a.m;
        return (f - i4) / (f - c());
    }

    @Override // Q2.d
    public final int c() {
        SideSheetBehavior<? extends View> sideSheetBehavior = this.f2022a;
        return Math.max(0, (sideSheetBehavior.m - sideSheetBehavior.l) - sideSheetBehavior.o);
    }

    @Override // Q2.d
    public final int d() {
        return this.f2022a.m;
    }

    @Override // Q2.d
    public final int e() {
        return this.f2022a.m;
    }

    @Override // Q2.d
    public final int f() {
        return c();
    }

    @Override // Q2.d
    public final <V extends View> int g(V v4) {
        return v4.getLeft() - this.f2022a.o;
    }

    @Override // Q2.d
    public final int h(CoordinatorLayout coordinatorLayout) {
        return coordinatorLayout.getRight();
    }

    @Override // Q2.d
    public final int i() {
        return 0;
    }

    @Override // Q2.d
    public final boolean j(float f) {
        if (f < 0.0f) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final boolean k(View view) {
        if (view.getLeft() > (c() + this.f2022a.m) / 2) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final boolean l(float f, float f4) {
        if (Math.abs(f) > Math.abs(f4)) {
            float abs = Math.abs(f);
            this.f2022a.getClass();
            if (abs > 500) {
                return true;
            }
        }
        return false;
    }

    @Override // Q2.d
    public final boolean m(View view, float f) {
        if (Math.abs((f * this.f2022a.k) + view.getRight()) > 0.5f) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final void n(ViewGroup.MarginLayoutParams marginLayoutParams, int i4, int i5) {
        int i6 = this.f2022a.m;
        if (i4 <= i6) {
            marginLayoutParams.rightMargin = i6 - i4;
        }
    }
}
