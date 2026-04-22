package W1;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Scope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/* renamed from: W1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class AbstractC0314b<T extends IInterface> {

    /* renamed from: x  reason: collision with root package name */
    public static final T1.d[] f2682x = new T1.d[0];

    /* renamed from: a  reason: collision with root package name */
    public volatile String f2683a;

    /* renamed from: b  reason: collision with root package name */
    public a0 f2684b;

    /* renamed from: c  reason: collision with root package name */
    public final Context f2685c;

    /* renamed from: d  reason: collision with root package name */
    public final AbstractC0319g f2686d;

    /* renamed from: e  reason: collision with root package name */
    public final T1.f f2687e;
    public final K f;

    /* renamed from: g  reason: collision with root package name */
    public final Object f2688g;

    /* renamed from: h  reason: collision with root package name */
    public final Object f2689h;

    /* renamed from: i  reason: collision with root package name */
    public InterfaceC0321i f2690i;

    /* renamed from: j  reason: collision with root package name */
    public c f2691j;

    /* renamed from: k  reason: collision with root package name */
    public IInterface f2692k;

    /* renamed from: l  reason: collision with root package name */
    public final ArrayList f2693l;

    /* renamed from: m  reason: collision with root package name */
    public N f2694m;

    /* renamed from: n  reason: collision with root package name */
    public int f2695n;

    /* renamed from: o  reason: collision with root package name */
    public final a f2696o;

    /* renamed from: p  reason: collision with root package name */
    public final InterfaceC0032b f2697p;

    /* renamed from: q  reason: collision with root package name */
    public final int f2698q;

    /* renamed from: r  reason: collision with root package name */
    public final String f2699r;

    /* renamed from: s  reason: collision with root package name */
    public volatile String f2700s;

    /* renamed from: t  reason: collision with root package name */
    public T1.b f2701t;

    /* renamed from: u  reason: collision with root package name */
    public boolean f2702u;

    /* renamed from: v  reason: collision with root package name */
    public volatile Q f2703v;

    /* renamed from: w  reason: collision with root package name */
    public final AtomicInteger f2704w;

    /* renamed from: W1.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface a {
        void B(int i4);

        void Z();
    }

    /* renamed from: W1.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface InterfaceC0032b {
        void p0(T1.b bVar);
    }

    /* renamed from: W1.b$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface c {
        void a(T1.b bVar);
    }

    /* renamed from: W1.b$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class d implements c {
        public d() {
        }

        @Override // W1.AbstractC0314b.c
        public final void a(T1.b bVar) {
            boolean z4;
            if (bVar.f2342k == 0) {
                z4 = true;
            } else {
                z4 = false;
            }
            AbstractC0314b abstractC0314b = AbstractC0314b.this;
            if (z4) {
                abstractC0314b.d(null, abstractC0314b.v());
                return;
            }
            InterfaceC0032b interfaceC0032b = abstractC0314b.f2697p;
            if (interfaceC0032b != null) {
                interfaceC0032b.p0(bVar);
            }
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AbstractC0314b(int r10, W1.AbstractC0314b.a r11, W1.AbstractC0314b.InterfaceC0032b r12, android.content.Context r13, android.os.Looper r14) {
        /*
            r9 = this;
            W1.Y r3 = W1.AbstractC0319g.a(r13)
            T1.f r4 = T1.f.f2354b
            W1.C0324l.d(r11)
            W1.C0324l.d(r12)
            r8 = 0
            r0 = r9
            r1 = r13
            r2 = r14
            r5 = r10
            r6 = r11
            r7 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: W1.AbstractC0314b.<init>(int, W1.b$a, W1.b$b, android.content.Context, android.os.Looper):void");
    }

    public static /* bridge */ /* synthetic */ void A(AbstractC0314b abstractC0314b) {
        int i4;
        int i5;
        synchronized (abstractC0314b.f2688g) {
            i4 = abstractC0314b.f2695n;
        }
        if (i4 == 3) {
            abstractC0314b.f2702u = true;
            i5 = 5;
        } else {
            i5 = 4;
        }
        K k4 = abstractC0314b.f;
        k4.sendMessage(k4.obtainMessage(i5, abstractC0314b.f2704w.get(), 16));
    }

    public static /* bridge */ /* synthetic */ boolean B(AbstractC0314b abstractC0314b, int i4, int i5, IInterface iInterface) {
        synchronized (abstractC0314b.f2688g) {
            try {
                if (abstractC0314b.f2695n != i4) {
                    return false;
                }
                abstractC0314b.C(i5, iInterface);
                return true;
            } finally {
            }
        }
    }

    public final void C(int i4, IInterface iInterface) {
        boolean z4;
        a0 a0Var;
        boolean z5 = false;
        if (i4 != 4) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (iInterface != null) {
            z5 = true;
        }
        if (z4 == z5) {
            synchronized (this.f2688g) {
                try {
                    this.f2695n = i4;
                    this.f2692k = iInterface;
                    if (i4 != 1) {
                        if (i4 != 2 && i4 != 3) {
                            if (i4 == 4) {
                                C0324l.d(iInterface);
                                System.currentTimeMillis();
                            }
                        } else {
                            N n4 = this.f2694m;
                            if (n4 != null && (a0Var = this.f2684b) != null) {
                                Log.e("GmsClient", "Calling connect() while still connected, missing disconnect() for " + ((String) a0Var.f2681b) + " on com.google.android.gms");
                                AbstractC0319g abstractC0319g = this.f2686d;
                                String str = (String) this.f2684b.f2681b;
                                C0324l.d(str);
                                this.f2684b.getClass();
                                if (this.f2699r == null) {
                                    this.f2685c.getClass();
                                }
                                abstractC0319g.c(str, n4, this.f2684b.f2680a);
                                this.f2704w.incrementAndGet();
                            }
                            N n5 = new N(this, this.f2704w.get());
                            this.f2694m = n5;
                            String y4 = y();
                            boolean z6 = z();
                            this.f2684b = new a0(y4, z6);
                            if (z6 && f() < 17895000) {
                                throw new IllegalStateException("Internal Error, the minimum apk version of this BaseGmsClient is too low to support dynamic lookup. Start service action: ".concat(String.valueOf((String) this.f2684b.f2681b)));
                            }
                            AbstractC0319g abstractC0319g2 = this.f2686d;
                            String str2 = (String) this.f2684b.f2681b;
                            C0324l.d(str2);
                            this.f2684b.getClass();
                            String str3 = this.f2699r;
                            if (str3 == null) {
                                str3 = this.f2685c.getClass().getName();
                            }
                            if (!abstractC0319g2.d(new V(str2, this.f2684b.f2680a), n5, str3, null)) {
                                Log.w("GmsClient", "unable to connect to service: " + ((String) this.f2684b.f2681b) + " on com.google.android.gms");
                                int i5 = this.f2704w.get();
                                P p4 = new P(this, 16);
                                K k4 = this.f;
                                k4.sendMessage(k4.obtainMessage(7, i5, -1, p4));
                            }
                        }
                    } else {
                        N n6 = this.f2694m;
                        if (n6 != null) {
                            AbstractC0319g abstractC0319g3 = this.f2686d;
                            String str4 = (String) this.f2684b.f2681b;
                            C0324l.d(str4);
                            this.f2684b.getClass();
                            if (this.f2699r == null) {
                                this.f2685c.getClass();
                            }
                            abstractC0319g3.c(str4, n6, this.f2684b.f2680a);
                            this.f2694m = null;
                        }
                    }
                } finally {
                }
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    public final boolean a() {
        boolean z4;
        synchronized (this.f2688g) {
            if (this.f2695n == 4) {
                z4 = true;
            } else {
                z4 = false;
            }
        }
        return z4;
    }

    public final void c(String str) {
        this.f2683a = str;
        n();
    }

    public final void d(InterfaceC0320h interfaceC0320h, Set<Scope> set) {
        Bundle u4 = u();
        String str = this.f2700s;
        int i4 = T1.f.f2353a;
        Scope[] scopeArr = C0317e.f2723x;
        Bundle bundle = new Bundle();
        int i5 = this.f2698q;
        T1.d[] dVarArr = C0317e.f2724y;
        C0317e c0317e = new C0317e(6, i5, i4, null, null, scopeArr, bundle, null, dVarArr, dVarArr, true, 0, false, str);
        c0317e.f2728m = this.f2685c.getPackageName();
        c0317e.f2731p = u4;
        if (set != null) {
            c0317e.f2730o = (Scope[]) set.toArray(new Scope[0]);
        }
        if (o()) {
            Account s4 = s();
            if (s4 == null) {
                s4 = new Account("<<default account>>", "com.google");
            }
            c0317e.f2732q = s4;
            if (interfaceC0320h != null) {
                c0317e.f2729n = interfaceC0320h.asBinder();
            }
        }
        c0317e.f2733r = f2682x;
        c0317e.f2734s = t();
        if (this instanceof f2.c) {
            c0317e.f2737v = true;
        }
        try {
            synchronized (this.f2689h) {
                try {
                    InterfaceC0321i interfaceC0321i = this.f2690i;
                    if (interfaceC0321i != null) {
                        interfaceC0321i.l3(new M(this, this.f2704w.get()), c0317e);
                    } else {
                        Log.w("GmsClient", "mServiceBroker is null, client disconnected");
                    }
                } finally {
                }
            }
        } catch (DeadObjectException e4) {
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e4);
            int i6 = this.f2704w.get();
            K k4 = this.f;
            k4.sendMessage(k4.obtainMessage(6, i6, 3));
        } catch (RemoteException e5) {
            e = e5;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e);
            int i7 = this.f2704w.get();
            O o4 = new O(this, 8, null, null);
            K k5 = this.f;
            k5.sendMessage(k5.obtainMessage(1, i7, -1, o4));
        } catch (SecurityException e6) {
            throw e6;
        } catch (RuntimeException e7) {
            e = e7;
            Log.w("GmsClient", "IGmsServiceBroker.getService failed", e);
            int i72 = this.f2704w.get();
            O o42 = new O(this, 8, null, null);
            K k52 = this.f;
            k52.sendMessage(k52.obtainMessage(1, i72, -1, o42));
        }
    }

    public final boolean e() {
        return true;
    }

    public int f() {
        return T1.f.f2353a;
    }

    public final boolean g() {
        boolean z4;
        synchronized (this.f2688g) {
            int i4 = this.f2695n;
            z4 = true;
            if (i4 != 2 && i4 != 3) {
                z4 = false;
            }
        }
        return z4;
    }

    public final T1.d[] h() {
        Q q4 = this.f2703v;
        if (q4 == null) {
            return null;
        }
        return q4.f2659k;
    }

    public final String i() {
        if (a() && this.f2684b != null) {
            return "com.google.android.gms";
        }
        throw new RuntimeException("Failed to connect when checking package");
    }

    public final void k(c cVar) {
        this.f2691j = cVar;
        C(2, null);
    }

    public final String l() {
        return this.f2683a;
    }

    public final void m(D1.E e4) {
        ((V1.u) e4.f633j).f2616v.f2587v.post(new V1.t(e4));
    }

    public final void n() {
        this.f2704w.incrementAndGet();
        synchronized (this.f2693l) {
            try {
                int size = this.f2693l.size();
                for (int i4 = 0; i4 < size; i4++) {
                    ((L) this.f2693l.get(i4)).b();
                }
                this.f2693l.clear();
            } catch (Throwable th) {
                throw th;
            }
        }
        synchronized (this.f2689h) {
            this.f2690i = null;
        }
        C(1, null);
    }

    public boolean o() {
        return false;
    }

    public final void q() {
        int c4 = this.f2687e.c(this.f2685c, f());
        if (c4 != 0) {
            C(1, null);
            this.f2691j = new d();
            int i4 = this.f2704w.get();
            K k4 = this.f;
            k4.sendMessage(k4.obtainMessage(3, i4, c4, null));
            return;
        }
        k(new d());
    }

    public abstract T r(IBinder iBinder);

    public Account s() {
        return null;
    }

    public T1.d[] t() {
        return f2682x;
    }

    public Bundle u() {
        return new Bundle();
    }

    public Set<Scope> v() {
        return Collections.emptySet();
    }

    public final T w() {
        T t3;
        synchronized (this.f2688g) {
            try {
                if (this.f2695n != 5) {
                    if (a()) {
                        t3 = (T) this.f2692k;
                        C0324l.e(t3, "Client is connected but service is null");
                    } else {
                        throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
                    }
                } else {
                    throw new DeadObjectException();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return t3;
    }

    public abstract String x();

    public abstract String y();

    public boolean z() {
        if (f() >= 211700000) {
            return true;
        }
        return false;
    }

    public AbstractC0314b(Context context, Looper looper, Y y4, T1.f fVar, int i4, a aVar, InterfaceC0032b interfaceC0032b, String str) {
        this.f2683a = null;
        this.f2688g = new Object();
        this.f2689h = new Object();
        this.f2693l = new ArrayList();
        this.f2695n = 1;
        this.f2701t = null;
        this.f2702u = false;
        this.f2703v = null;
        this.f2704w = new AtomicInteger(0);
        C0324l.e(context, "Context must not be null");
        this.f2685c = context;
        C0324l.e(looper, "Looper must not be null");
        C0324l.e(y4, "Supervisor must not be null");
        this.f2686d = y4;
        C0324l.e(fVar, "API availability must not be null");
        this.f2687e = fVar;
        this.f = new K(this, looper);
        this.f2698q = i4;
        this.f2696o = aVar;
        this.f2697p = interfaceC0032b;
        this.f2699r = str;
    }
}
