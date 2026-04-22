package M;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;

/* renamed from: M.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0237t {

    /* renamed from: a  reason: collision with root package name */
    public ViewParent f1652a;

    /* renamed from: b  reason: collision with root package name */
    public ViewParent f1653b;

    /* renamed from: c  reason: collision with root package name */
    public final View f1654c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f1655d;

    /* renamed from: e  reason: collision with root package name */
    public int[] f1656e;

    public C0237t(View view) {
        this.f1654c = view;
    }

    public final boolean a(float f, float f4, boolean z4) {
        ViewParent f5;
        if (!this.f1655d || (f5 = f(0)) == null) {
            return false;
        }
        try {
            return S.a(f5, this.f1654c, f, f4, z4);
        } catch (AbstractMethodError e4) {
            Log.e("ViewParentCompat", "ViewParent " + f5 + " does not implement interface method onNestedFling", e4);
            return false;
        }
    }

    public final boolean b(float f, float f4) {
        ViewParent f5;
        if (!this.f1655d || (f5 = f(0)) == null) {
            return false;
        }
        try {
            return S.b(f5, this.f1654c, f, f4);
        } catch (AbstractMethodError e4) {
            Log.e("ViewParentCompat", "ViewParent " + f5 + " does not implement interface method onNestedPreFling", e4);
            return false;
        }
    }

    public final boolean c(int i4, int i5, int i6, int[] iArr, int[] iArr2) {
        ViewParent f;
        int i7;
        int i8;
        int[] iArr3;
        if (!this.f1655d || (f = f(i6)) == null) {
            return false;
        }
        if (i4 == 0 && i5 == 0) {
            if (iArr2 == null) {
                return false;
            }
            iArr2[0] = 0;
            iArr2[1] = 0;
            return false;
        }
        View view = this.f1654c;
        if (iArr2 != null) {
            view.getLocationInWindow(iArr2);
            i7 = iArr2[0];
            i8 = iArr2[1];
        } else {
            i7 = 0;
            i8 = 0;
        }
        if (iArr == null) {
            if (this.f1656e == null) {
                this.f1656e = new int[2];
            }
            iArr3 = this.f1656e;
        } else {
            iArr3 = iArr;
        }
        iArr3[0] = 0;
        iArr3[1] = 0;
        boolean z4 = f instanceof InterfaceC0238u;
        View view2 = this.f1654c;
        if (z4) {
            ((InterfaceC0238u) f).j(view2, i4, i5, iArr3, i6);
        } else if (i6 == 0) {
            try {
                S.c(f, view2, i4, i5, iArr3);
            } catch (AbstractMethodError e4) {
                Log.e("ViewParentCompat", "ViewParent " + f + " does not implement interface method onNestedPreScroll", e4);
            }
        }
        if (iArr2 != null) {
            view.getLocationInWindow(iArr2);
            iArr2[0] = iArr2[0] - i7;
            iArr2[1] = iArr2[1] - i8;
        }
        if (iArr3[0] == 0 && iArr3[1] == 0) {
            return false;
        }
        return true;
    }

    public final void d(int i4, int i5, int i6, int[] iArr) {
        e(0, i4, 0, i5, null, i6, iArr);
    }

    public final boolean e(int i4, int i5, int i6, int i7, int[] iArr, int i8, int[] iArr2) {
        ViewParent f;
        int i9;
        int i10;
        int[] iArr3;
        if (!this.f1655d || (f = f(i8)) == null) {
            return false;
        }
        if (i4 == 0 && i5 == 0 && i6 == 0 && i7 == 0) {
            if (iArr != null) {
                iArr[0] = 0;
                iArr[1] = 0;
            }
            return false;
        }
        View view = this.f1654c;
        if (iArr != null) {
            view.getLocationInWindow(iArr);
            i9 = iArr[0];
            i10 = iArr[1];
        } else {
            i9 = 0;
            i10 = 0;
        }
        if (iArr2 == null) {
            if (this.f1656e == null) {
                this.f1656e = new int[2];
            }
            int[] iArr4 = this.f1656e;
            iArr4[0] = 0;
            iArr4[1] = 0;
            iArr3 = iArr4;
        } else {
            iArr3 = iArr2;
        }
        boolean z4 = f instanceof InterfaceC0239v;
        View view2 = this.f1654c;
        if (z4) {
            ((InterfaceC0239v) f).m(view2, i4, i5, i6, i7, i8, iArr3);
        } else {
            iArr3[0] = iArr3[0] + i6;
            iArr3[1] = iArr3[1] + i7;
            if (f instanceof InterfaceC0238u) {
                ((InterfaceC0238u) f).n(view2, i4, i5, i6, i7, i8);
            } else if (i8 == 0) {
                try {
                    S.d(f, view2, i4, i5, i6, i7);
                } catch (AbstractMethodError e4) {
                    Log.e("ViewParentCompat", "ViewParent " + f + " does not implement interface method onNestedScroll", e4);
                }
            }
        }
        if (iArr != null) {
            view.getLocationInWindow(iArr);
            iArr[0] = iArr[0] - i9;
            iArr[1] = iArr[1] - i10;
        }
        return true;
    }

    public final ViewParent f(int i4) {
        if (i4 != 0) {
            if (i4 != 1) {
                return null;
            }
            return this.f1653b;
        }
        return this.f1652a;
    }

    public final boolean g(int i4) {
        if (f(i4) != null) {
            return true;
        }
        return false;
    }

    public final boolean h(int i4, int i5) {
        boolean f;
        if (g(i5)) {
            return true;
        }
        if (this.f1655d) {
            View view = this.f1654c;
            View view2 = view;
            for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
                boolean z4 = parent instanceof InterfaceC0238u;
                if (z4) {
                    f = ((InterfaceC0238u) parent).o(view2, view, i4, i5);
                } else {
                    if (i5 == 0) {
                        try {
                            f = S.f(parent, view2, view, i4);
                        } catch (AbstractMethodError e4) {
                            Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onStartNestedScroll", e4);
                        }
                    }
                    f = false;
                }
                if (f) {
                    if (i5 != 0) {
                        if (i5 == 1) {
                            this.f1653b = parent;
                        }
                    } else {
                        this.f1652a = parent;
                    }
                    if (z4) {
                        ((InterfaceC0238u) parent).h(view2, view, i4, i5);
                    } else if (i5 == 0) {
                        try {
                            S.e(parent, view2, view, i4);
                        } catch (AbstractMethodError e5) {
                            Log.e("ViewParentCompat", "ViewParent " + parent + " does not implement interface method onNestedScrollAccepted", e5);
                        }
                    }
                    return true;
                }
                if (parent instanceof View) {
                    view2 = (View) parent;
                }
            }
        }
        return false;
    }

    public final void i(int i4) {
        ViewParent f = f(i4);
        if (f != null) {
            boolean z4 = f instanceof InterfaceC0238u;
            View view = this.f1654c;
            if (z4) {
                ((InterfaceC0238u) f).i(view, i4);
            } else if (i4 == 0) {
                try {
                    S.g(f, view);
                } catch (AbstractMethodError e4) {
                    Log.e("ViewParentCompat", "ViewParent " + f + " does not implement interface method onStopNestedScroll", e4);
                }
            }
            if (i4 != 0) {
                if (i4 == 1) {
                    this.f1653b = null;
                    return;
                }
                return;
            }
            this.f1652a = null;
        }
    }
}
