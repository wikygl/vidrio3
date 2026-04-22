package Q2;

import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.sidesheet.SideSheetBehavior;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a extends d {

    /* renamed from: a  reason: collision with root package name */
    public final SideSheetBehavior<? extends View> f2021a;

    public a(SideSheetBehavior<? extends View> sideSheetBehavior) {
        this.f2021a = sideSheetBehavior;
    }

    @Override // Q2.d
    public final int a(ViewGroup.MarginLayoutParams marginLayoutParams) {
        return marginLayoutParams.leftMargin;
    }

    @Override // Q2.d
    public final float b(int i4) {
        float d4 = d();
        return (i4 - d4) / (c() - d4);
    }

    @Override // Q2.d
    public final int c() {
        SideSheetBehavior<? extends View> sideSheetBehavior = this.f2021a;
        return Math.max(0, sideSheetBehavior.n + sideSheetBehavior.o);
    }

    @Override // Q2.d
    public final int d() {
        SideSheetBehavior<? extends View> sideSheetBehavior = this.f2021a;
        return (-sideSheetBehavior.l) - sideSheetBehavior.o;
    }

    @Override // Q2.d
    public final int e() {
        return this.f2021a.o;
    }

    @Override // Q2.d
    public final int f() {
        return -this.f2021a.l;
    }

    @Override // Q2.d
    public final <V extends View> int g(V v4) {
        return v4.getRight() + this.f2021a.o;
    }

    @Override // Q2.d
    public final int h(CoordinatorLayout coordinatorLayout) {
        return coordinatorLayout.getLeft();
    }

    @Override // Q2.d
    public final int i() {
        return 1;
    }

    @Override // Q2.d
    public final boolean j(float f) {
        if (f > 0.0f) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final boolean k(View view) {
        if (view.getRight() < (c() - d()) / 2) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final boolean l(float f, float f4) {
        if (Math.abs(f) > Math.abs(f4)) {
            float abs = Math.abs(f);
            this.f2021a.getClass();
            if (abs > 500) {
                return true;
            }
        }
        return false;
    }

    @Override // Q2.d
    public final boolean m(View view, float f) {
        if (Math.abs((f * this.f2021a.k) + view.getLeft()) > 0.5f) {
            return true;
        }
        return false;
    }

    @Override // Q2.d
    public final void n(ViewGroup.MarginLayoutParams marginLayoutParams, int i4, int i5) {
        if (i4 <= this.f2021a.m) {
            marginLayoutParams.leftMargin = i5;
        }
    }
}
