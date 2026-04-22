package t1;

import A1.C0124p;
import A1.C1;
import A1.InterfaceC0084a;
import A1.L;
import A1.M0;
import A1.O0;
import A1.Q0;
import A1.m1;
import W1.C0324l;
import android.content.Context;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.pc;
import u1.InterfaceC0823c;

/* renamed from: t1.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0806h extends ViewGroup {

    /* renamed from: j  reason: collision with root package name */
    public final O0 f5800j;

    public AbstractC0806h(Context context) {
        super(context);
        this.f5800j = new O0(this);
    }

    public final void a(C0802d c0802d) {
        C0324l.b("#008 Must be called on the main UI thread.");
        Gb.a(getContext());
        if (((Boolean) pc.f.e()).booleanValue()) {
            if (((Boolean) A1.r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                E1.c.f852b.execute(new Q0(this, 4, c0802d));
                return;
            }
        }
        this.f5800j.b(c0802d.f5787a);
    }

    public AbstractC0800b getAdListener() {
        return this.f5800j.f;
    }

    public C0803e getAdSize() {
        C1 h4;
        O0 o02 = this.f5800j;
        o02.getClass();
        try {
            L l2 = o02.f74i;
            if (l2 != null && (h4 = l2.h()) != null) {
                return new C0803e(h4.f13n, h4.f10k, h4.f9j);
            }
        } catch (RemoteException e4) {
            E1.m.i("#007 Could not call remote method.", e4);
        }
        C0803e[] c0803eArr = o02.f72g;
        if (c0803eArr != null) {
            return c0803eArr[0];
        }
        return null;
    }

    public String getAdUnitId() {
        L l2;
        O0 o02 = this.f5800j;
        if (o02.f76k == null && (l2 = o02.f74i) != null) {
            try {
                o02.f76k = l2.t();
            } catch (RemoteException e4) {
                E1.m.i("#007 Could not call remote method.", e4);
            }
        }
        return o02.f76k;
    }

    public InterfaceC0809k getOnPaidEventListener() {
        this.f5800j.getClass();
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x001b  */
    /* JADX WARN: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public t1.C0811m getResponseInfo() {
        /*
            r3 = this;
            A1.O0 r0 = r3.f5800j
            r0.getClass()
            r1 = 0
            A1.L r0 = r0.f74i     // Catch: android.os.RemoteException -> Lf
            if (r0 == 0) goto L11
            A1.A0 r0 = r0.k()     // Catch: android.os.RemoteException -> Lf
            goto L19
        Lf:
            r0 = move-exception
            goto L13
        L11:
            r0 = r1
            goto L19
        L13:
            java.lang.String r2 = "#007 Could not call remote method."
            E1.m.i(r2, r0)
            goto L11
        L19:
            if (r0 == 0) goto L20
            t1.m r1 = new t1.m
            r1.<init>(r0)
        L20:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: t1.AbstractC0806h.getResponseInfo():t1.m");
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        View childAt = getChildAt(0);
        if (childAt != null && childAt.getVisibility() != 8) {
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            int i8 = ((i6 - i4) - measuredWidth) / 2;
            int i9 = ((i7 - i5) - measuredHeight) / 2;
            childAt.layout(i8, i9, measuredWidth + i8, measuredHeight + i9);
        }
    }

    @Override // android.view.View
    public final void onMeasure(int i4, int i5) {
        C0803e c0803e;
        int i6;
        int i7;
        int i8 = 0;
        View childAt = getChildAt(0);
        if (childAt != null && childAt.getVisibility() != 8) {
            measureChild(childAt, i4, i5);
            i8 = childAt.getMeasuredWidth();
            i6 = childAt.getMeasuredHeight();
        } else {
            try {
                c0803e = getAdSize();
            } catch (NullPointerException e4) {
                E1.m.e("Unable to retrieve ad size.", e4);
                c0803e = null;
            }
            if (c0803e != null) {
                Context context = getContext();
                int i9 = c0803e.f5791a;
                if (i9 != -3) {
                    if (i9 != -1) {
                        E1.f fVar = C0124p.f.f161a;
                        i7 = E1.f.m(context, i9);
                    } else {
                        i7 = context.getResources().getDisplayMetrics().widthPixels;
                    }
                } else {
                    i7 = -1;
                }
                i6 = c0803e.b(context);
                i8 = i7;
            } else {
                i6 = 0;
            }
        }
        setMeasuredDimension(View.resolveSize(Math.max(i8, getSuggestedMinimumWidth()), i4), View.resolveSize(Math.max(i6, getSuggestedMinimumHeight()), i5));
    }

    public void setAdListener(AbstractC0800b abstractC0800b) {
        O0 o02 = this.f5800j;
        o02.f = abstractC0800b;
        M0 m02 = o02.f70d;
        synchronized (m02.f59j) {
            m02.f60k = abstractC0800b;
        }
        if (abstractC0800b == null) {
            o02.c(null);
            return;
        }
        if (abstractC0800b instanceof InterfaceC0084a) {
            o02.c((InterfaceC0084a) abstractC0800b);
        }
        if (abstractC0800b instanceof InterfaceC0823c) {
            o02.e((InterfaceC0823c) abstractC0800b);
        }
    }

    public void setAdSize(C0803e c0803e) {
        C0803e[] c0803eArr = {c0803e};
        O0 o02 = this.f5800j;
        if (o02.f72g == null) {
            o02.d(c0803eArr);
            return;
        }
        throw new IllegalStateException("The ad size can only be set once on AdView.");
    }

    public void setAdUnitId(String str) {
        O0 o02 = this.f5800j;
        if (o02.f76k == null) {
            o02.f76k = str;
            return;
        }
        throw new IllegalStateException("The ad unit ID can only be set once on AdView.");
    }

    public void setOnPaidEventListener(InterfaceC0809k interfaceC0809k) {
        O0 o02 = this.f5800j;
        o02.getClass();
        try {
            L l2 = o02.f74i;
            if (l2 != null) {
                l2.X1(new m1());
            }
        } catch (RemoteException e4) {
            E1.m.i("#007 Could not call remote method.", e4);
        }
    }
}
