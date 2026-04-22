package A1;

import android.content.Context;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.L8;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.pc;
import java.util.concurrent.atomic.AtomicBoolean;
import t1.AbstractC0800b;
import t1.C0803e;
import t1.C0813o;
import t1.C0814p;
import u1.InterfaceC0823c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class O0 {

    /* renamed from: a  reason: collision with root package name */
    public final bg f67a;

    /* renamed from: b  reason: collision with root package name */
    public final B1 f68b;

    /* renamed from: c  reason: collision with root package name */
    public final C0813o f69c;

    /* renamed from: d  reason: collision with root package name */
    public final M0 f70d;

    /* renamed from: e  reason: collision with root package name */
    public InterfaceC0084a f71e;
    public AbstractC0800b f;

    /* renamed from: g  reason: collision with root package name */
    public C0803e[] f72g;

    /* renamed from: h  reason: collision with root package name */
    public InterfaceC0823c f73h;

    /* renamed from: i  reason: collision with root package name */
    public L f74i;

    /* renamed from: j  reason: collision with root package name */
    public C0814p f75j;

    /* renamed from: k  reason: collision with root package name */
    public String f76k;

    /* renamed from: l  reason: collision with root package name */
    public final ViewGroup f77l;

    /* renamed from: m  reason: collision with root package name */
    public final int f78m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f79n;

    public O0(ViewGroup viewGroup) {
        B1 b12 = B1.f8a;
        this.f67a = new bg();
        this.f69c = new C0813o();
        this.f70d = new M0(this);
        this.f77l = viewGroup;
        this.f68b = b12;
        this.f74i = null;
        new AtomicBoolean(false);
        this.f78m = 0;
    }

    public static C1 a(Context context, C0803e[] c0803eArr, int i4) {
        boolean z4 = false;
        for (C0803e c0803e : c0803eArr) {
            if (c0803e.equals(C0803e.f5790k)) {
                return new C1("invalid", 0, 0, false, 0, 0, null, false, false, false, true, false, false, false, false);
            }
        }
        C1 c12 = new C1(context, c0803eArr);
        if (i4 == 1) {
            z4 = true;
        }
        c12.f18s = z4;
        return c12;
    }

    public final void b(K0 k02) {
        L l2;
        try {
            L l4 = this.f74i;
            ViewGroup viewGroup = this.f77l;
            if (l4 == null) {
                if (this.f72g != null && this.f76k != null) {
                    Context context = viewGroup.getContext();
                    C1 a4 = a(context, this.f72g, this.f78m);
                    if ("search_v2".equals(a4.f9j)) {
                        l2 = (L) new C0105h(C0124p.f.f162b, context, a4, this.f76k).d(context, false);
                    } else {
                        l2 = (L) new C0099f(C0124p.f.f162b, context, a4, this.f76k, this.f67a).d(context, false);
                    }
                    this.f74i = l2;
                    l2.p1(new u1(this.f70d));
                    InterfaceC0084a interfaceC0084a = this.f71e;
                    if (interfaceC0084a != null) {
                        this.f74i.B1(new C0126q(interfaceC0084a));
                    }
                    InterfaceC0823c interfaceC0823c = this.f73h;
                    if (interfaceC0823c != null) {
                        this.f74i.N3(new L8(interfaceC0823c));
                    }
                    C0814p c0814p = this.f75j;
                    if (c0814p != null) {
                        this.f74i.v0(new s1(c0814p));
                    }
                    this.f74i.X1(new m1());
                    this.f74i.p4(this.f79n);
                    L l5 = this.f74i;
                    if (l5 != null) {
                        try {
                            InterfaceC0374a l6 = l5.l();
                            if (l6 != null) {
                                if (((Boolean) pc.f.e()).booleanValue()) {
                                    if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                                        E1.f.f854b.post(new L0(this, 0, l6));
                                    }
                                }
                                viewGroup.addView((View) c2.b.p0(l6));
                            }
                        } catch (RemoteException e4) {
                            E1.m.i("#007 Could not call remote method.", e4);
                        }
                    }
                } else {
                    throw new IllegalStateException("The ad size and ad unit ID must be set before loadAd is called.");
                }
            }
            L l7 = this.f74i;
            l7.getClass();
            B1 b12 = this.f68b;
            Context context2 = viewGroup.getContext();
            b12.getClass();
            l7.V1(B1.a(context2, k02));
        } catch (RemoteException e5) {
            E1.m.i("#007 Could not call remote method.", e5);
        }
    }

    public final void c(InterfaceC0084a interfaceC0084a) {
        C0126q c0126q;
        try {
            this.f71e = interfaceC0084a;
            L l2 = this.f74i;
            if (l2 != null) {
                if (interfaceC0084a != null) {
                    c0126q = new C0126q(interfaceC0084a);
                } else {
                    c0126q = null;
                }
                l2.B1(c0126q);
            }
        } catch (RemoteException e4) {
            E1.m.i("#007 Could not call remote method.", e4);
        }
    }

    public final void d(C0803e... c0803eArr) {
        ViewGroup viewGroup = this.f77l;
        this.f72g = c0803eArr;
        try {
            L l2 = this.f74i;
            if (l2 != null) {
                l2.M0(a(viewGroup.getContext(), this.f72g, this.f78m));
            }
        } catch (RemoteException e4) {
            E1.m.i("#007 Could not call remote method.", e4);
        }
        viewGroup.requestLayout();
    }

    public final void e(InterfaceC0823c interfaceC0823c) {
        L8 l8;
        try {
            this.f73h = interfaceC0823c;
            L l2 = this.f74i;
            if (l2 != null) {
                if (interfaceC0823c != null) {
                    l8 = new L8(interfaceC0823c);
                } else {
                    l8 = null;
                }
                l2.N3(l8);
            }
        } catch (RemoteException e4) {
            E1.m.i("#007 Could not call remote method.", e4);
        }
    }
}
