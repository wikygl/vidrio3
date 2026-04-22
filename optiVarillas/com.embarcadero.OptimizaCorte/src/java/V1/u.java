package V1;

import A1.RunnableC0089b1;
import D1.RunnableC0186f;
import U1.a;
import U1.d;
import W1.AbstractC0314b;
import W1.C0315c;
import W1.C0323k;
import W1.C0324l;
import W1.C0336y;
import android.app.PendingIntent;
import android.content.Context;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import com.google.android.gms.common.api.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class u implements d.a, d.b {

    /* renamed from: k  reason: collision with root package name */
    public final a.e f2605k;

    /* renamed from: l  reason: collision with root package name */
    public final C0295a f2606l;

    /* renamed from: m  reason: collision with root package name */
    public final C0307m f2607m;

    /* renamed from: p  reason: collision with root package name */
    public final int f2610p;

    /* renamed from: q  reason: collision with root package name */
    public final G f2611q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f2612r;

    /* renamed from: v  reason: collision with root package name */
    public final /* synthetic */ C0298d f2616v;

    /* renamed from: j  reason: collision with root package name */
    public final LinkedList f2604j = new LinkedList();

    /* renamed from: n  reason: collision with root package name */
    public final HashSet f2608n = new HashSet();

    /* renamed from: o  reason: collision with root package name */
    public final HashMap f2609o = new HashMap();

    /* renamed from: s  reason: collision with root package name */
    public final ArrayList f2613s = new ArrayList();

    /* renamed from: t  reason: collision with root package name */
    public T1.b f2614t = null;

    /* renamed from: u  reason: collision with root package name */
    public int f2615u = 0;

    public u(C0298d c0298d, U1.c cVar) {
        this.f2616v = c0298d;
        Looper looper = c0298d.f2587v.getLooper();
        C0315c.a b4 = cVar.b();
        C0315c c0315c = new C0315c(b4.f2713a, b4.f2714b, b4.f2715c, b4.f2716d);
        a.AbstractC0026a abstractC0026a = cVar.f2380c.f2374a;
        C0324l.d(abstractC0026a);
        a.e a4 = abstractC0026a.a(cVar.f2378a, looper, c0315c, cVar.f2381d, this, this);
        String str = cVar.f2379b;
        if (str != null && (a4 instanceof AbstractC0314b)) {
            ((AbstractC0314b) a4).f2700s = str;
        }
        if (str != null && (a4 instanceof ServiceConnectionC0302h)) {
            ((ServiceConnectionC0302h) a4).getClass();
        }
        this.f2605k = a4;
        this.f2606l = cVar.f2382e;
        this.f2607m = new C0307m();
        this.f2610p = cVar.f;
        if (a4.o()) {
            Context context = c0298d.f2579n;
            g2.g gVar = c0298d.f2587v;
            C0315c.a b5 = cVar.b();
            this.f2611q = new G(context, gVar, new C0315c(b5.f2713a, b5.f2714b, b5.f2715c, b5.f2716d));
            return;
        }
        this.f2611q = null;
    }

    @Override // V1.InterfaceC0297c
    public final void B(int i4) {
        Looper myLooper = Looper.myLooper();
        C0298d c0298d = this.f2616v;
        if (myLooper == c0298d.f2587v.getLooper()) {
            f(i4);
        } else {
            c0298d.f2587v.post(new RunnableC0312s(i4, 0, this));
        }
    }

    @Override // V1.InterfaceC0297c
    public final void Z() {
        Looper myLooper = Looper.myLooper();
        C0298d c0298d = this.f2616v;
        if (myLooper == c0298d.f2587v.getLooper()) {
            e();
        } else {
            c0298d.f2587v.post(new RunnableC0089b1(2, this));
        }
    }

    public final void a(T1.b bVar) {
        HashSet hashSet = this.f2608n;
        Iterator it = hashSet.iterator();
        if (it.hasNext()) {
            M m4 = (M) it.next();
            if (C0323k.a(bVar, T1.b.f2340n)) {
                this.f2605k.i();
            }
            m4.getClass();
            throw null;
        }
        hashSet.clear();
    }

    public final void b(Status status) {
        C0324l.a(this.f2616v.f2587v);
        c(status, null, false);
    }

    public final void c(Status status, RuntimeException runtimeException, boolean z4) {
        boolean z5;
        C0324l.a(this.f2616v.f2587v);
        boolean z6 = true;
        if (status != null) {
            z5 = false;
        } else {
            z5 = true;
        }
        if (runtimeException != null) {
            z6 = false;
        }
        if (z5 != z6) {
            Iterator it = this.f2604j.iterator();
            while (it.hasNext()) {
                L l2 = (L) it.next();
                if (!z4 || l2.f2556a == 2) {
                    if (status != null) {
                        l2.a(status);
                    } else {
                        l2.b(runtimeException);
                    }
                    it.remove();
                }
            }
            return;
        }
        throw new IllegalArgumentException("Status XOR exception should be null");
    }

    public final void d() {
        LinkedList linkedList = this.f2604j;
        ArrayList arrayList = new ArrayList(linkedList);
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            L l2 = (L) arrayList.get(i4);
            if (this.f2605k.a()) {
                if (h(l2)) {
                    linkedList.remove(l2);
                }
            } else {
                return;
            }
        }
    }

    public final void e() {
        C0298d c0298d = this.f2616v;
        C0324l.a(c0298d.f2587v);
        this.f2614t = null;
        a(T1.b.f2340n);
        if (this.f2612r) {
            g2.g gVar = c0298d.f2587v;
            C0295a c0295a = this.f2606l;
            gVar.removeMessages(11, c0295a);
            c0298d.f2587v.removeMessages(9, c0295a);
            this.f2612r = false;
        }
        Iterator it = this.f2609o.values().iterator();
        if (!it.hasNext()) {
            d();
            g();
            return;
        }
        ((E) it.next()).getClass();
        throw null;
    }

    public final void f(int i4) {
        C0298d c0298d = this.f2616v;
        C0324l.a(c0298d.f2587v);
        this.f2614t = null;
        this.f2612r = true;
        String l2 = this.f2605k.l();
        C0307m c0307m = this.f2607m;
        c0307m.getClass();
        StringBuilder sb = new StringBuilder("The connection to Google Play services was lost");
        if (i4 == 1) {
            sb.append(" due to service disconnection.");
        } else if (i4 == 3) {
            sb.append(" due to dead object exception.");
        }
        if (l2 != null) {
            sb.append(" Last reason for disconnect: ");
            sb.append(l2);
        }
        c0307m.a(true, new Status(20, sb.toString(), (PendingIntent) null, (T1.b) null));
        g2.g gVar = c0298d.f2587v;
        C0295a c0295a = this.f2606l;
        gVar.sendMessageDelayed(Message.obtain(gVar, 9, c0295a), 5000L);
        g2.g gVar2 = c0298d.f2587v;
        gVar2.sendMessageDelayed(Message.obtain(gVar2, 11, c0295a), 120000L);
        c0298d.f2581p.f2776a.clear();
        Iterator it = this.f2609o.values().iterator();
        if (!it.hasNext()) {
            return;
        }
        ((E) it.next()).getClass();
        throw null;
    }

    public final void g() {
        C0298d c0298d = this.f2616v;
        g2.g gVar = c0298d.f2587v;
        C0295a c0295a = this.f2606l;
        gVar.removeMessages(12, c0295a);
        g2.g gVar2 = c0298d.f2587v;
        gVar2.sendMessageDelayed(gVar2.obtainMessage(12, c0295a), c0298d.f2575j);
    }

    public final boolean h(L l2) {
        T1.d dVar;
        if (!(l2 instanceof A)) {
            a.e eVar = this.f2605k;
            l2.d(this.f2607m, eVar.o());
            try {
                l2.c(this);
            } catch (DeadObjectException unused) {
                B(1);
                eVar.c("DeadObjectException thrown while running ApiCallRunner.");
            }
            return true;
        }
        A a4 = (A) l2;
        T1.d[] g4 = a4.g(this);
        if (g4 != null && g4.length != 0) {
            T1.d[] h4 = this.f2605k.h();
            if (h4 == null) {
                h4 = new T1.d[0];
            }
            r.j jVar = new r.j(h4.length);
            for (T1.d dVar2 : h4) {
                jVar.put(dVar2.f2348j, Long.valueOf(dVar2.h()));
            }
            int length = g4.length;
            for (int i4 = 0; i4 < length; i4++) {
                dVar = g4[i4];
                Long l4 = (Long) jVar.getOrDefault(dVar.f2348j, null);
                if (l4 == null || l4.longValue() < dVar.h()) {
                    break;
                }
            }
        }
        dVar = null;
        if (dVar == null) {
            a.e eVar2 = this.f2605k;
            l2.d(this.f2607m, eVar2.o());
            try {
                l2.c(this);
            } catch (DeadObjectException unused2) {
                B(1);
                eVar2.c("DeadObjectException thrown while running ApiCallRunner.");
            }
            return true;
        }
        Log.w("GoogleApiManager", this.f2605k.getClass().getName() + " could not execute call because it requires feature (" + dVar.f2348j + ", " + dVar.h() + ").");
        if (this.f2616v.f2588w && a4.f(this)) {
            v vVar = new v(this.f2606l, dVar);
            int indexOf = this.f2613s.indexOf(vVar);
            if (indexOf >= 0) {
                v vVar2 = (v) this.f2613s.get(indexOf);
                this.f2616v.f2587v.removeMessages(15, vVar2);
                g2.g gVar = this.f2616v.f2587v;
                gVar.sendMessageDelayed(Message.obtain(gVar, 15, vVar2), 5000L);
            } else {
                this.f2613s.add(vVar);
                g2.g gVar2 = this.f2616v.f2587v;
                gVar2.sendMessageDelayed(Message.obtain(gVar2, 15, vVar), 5000L);
                g2.g gVar3 = this.f2616v.f2587v;
                gVar3.sendMessageDelayed(Message.obtain(gVar3, 16, vVar), 120000L);
                T1.b bVar = new T1.b(2, null);
                if (!i(bVar)) {
                    this.f2616v.b(bVar, this.f2610p);
                }
            }
            return false;
        }
        a4.b(new U1.j(dVar));
        return true;
    }

    public final boolean i(T1.b bVar) {
        synchronized (C0298d.f2574z) {
            this.f2616v.getClass();
        }
        return false;
    }

    public final boolean j(boolean z4) {
        C0324l.a(this.f2616v.f2587v);
        a.e eVar = this.f2605k;
        if (eVar.a() && this.f2609o.isEmpty()) {
            C0307m c0307m = this.f2607m;
            if (c0307m.f2596a.isEmpty() && c0307m.f2597b.isEmpty()) {
                eVar.c("Timing out service connection.");
                return true;
            } else if (z4) {
                g();
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r4v6, types: [U1.a$e, n2.f] */
    public final void k() {
        C0298d c0298d = this.f2616v;
        C0324l.a(c0298d.f2587v);
        a.e eVar = this.f2605k;
        if (!eVar.a() && !eVar.g()) {
            try {
                C0336y c0336y = c0298d.f2581p;
                Context context = c0298d.f2579n;
                c0336y.getClass();
                C0324l.d(context);
                int i4 = 0;
                if (eVar.e()) {
                    int f = eVar.f();
                    SparseIntArray sparseIntArray = c0336y.f2776a;
                    int i5 = sparseIntArray.get(f, -1);
                    if (i5 != -1) {
                        i4 = i5;
                    } else {
                        int i6 = 0;
                        while (true) {
                            if (i6 < sparseIntArray.size()) {
                                int keyAt = sparseIntArray.keyAt(i6);
                                if (keyAt > f && sparseIntArray.get(keyAt) == 0) {
                                    break;
                                }
                                i6++;
                            } else {
                                i4 = -1;
                                break;
                            }
                        }
                        if (i4 == -1) {
                            i4 = c0336y.f2777b.c(context, f);
                        }
                        sparseIntArray.put(f, i4);
                    }
                }
                if (i4 != 0) {
                    T1.b bVar = new T1.b(i4, null);
                    String name = eVar.getClass().getName();
                    String bVar2 = bVar.toString();
                    Log.w("GoogleApiManager", "The service for " + name + " is not available: " + bVar2);
                    m(bVar, null);
                    return;
                }
                x xVar = new x(c0298d, eVar, this.f2606l);
                if (eVar.o()) {
                    G g4 = this.f2611q;
                    C0324l.d(g4);
                    n2.f fVar = g4.f2548p;
                    if (fVar != null) {
                        fVar.n();
                    }
                    Integer valueOf = Integer.valueOf(System.identityHashCode(g4));
                    C0315c c0315c = g4.f2547o;
                    c0315c.f2712h = valueOf;
                    Handler handler = g4.f2544l;
                    Looper looper = handler.getLooper();
                    g4.f2548p = g4.f2545m.a(g4.f2543k, looper, c0315c, c0315c.f2711g, g4, g4);
                    g4.f2549q = xVar;
                    Set set = g4.f2546n;
                    if (set != null && !set.isEmpty()) {
                        g4.f2548p.p();
                    } else {
                        handler.post(new RunnableC0186f(2, g4));
                    }
                }
                try {
                    eVar.k(xVar);
                } catch (SecurityException e4) {
                    m(new T1.b(10), e4);
                }
            } catch (IllegalStateException e5) {
                m(new T1.b(10), e5);
            }
        }
    }

    public final void l(A a4) {
        C0324l.a(this.f2616v.f2587v);
        boolean a5 = this.f2605k.a();
        LinkedList linkedList = this.f2604j;
        if (a5) {
            if (h(a4)) {
                g();
                return;
            } else {
                linkedList.add(a4);
                return;
            }
        }
        linkedList.add(a4);
        T1.b bVar = this.f2614t;
        if (bVar != null && bVar.f2342k != 0 && bVar.f2343l != null) {
            m(bVar, null);
        } else {
            k();
        }
    }

    public final void m(T1.b bVar, RuntimeException runtimeException) {
        n2.f fVar;
        C0324l.a(this.f2616v.f2587v);
        G g4 = this.f2611q;
        if (g4 != null && (fVar = g4.f2548p) != null) {
            fVar.n();
        }
        C0324l.a(this.f2616v.f2587v);
        this.f2614t = null;
        this.f2616v.f2581p.f2776a.clear();
        a(bVar);
        if ((this.f2605k instanceof Y1.d) && bVar.f2342k != 24) {
            C0298d c0298d = this.f2616v;
            c0298d.f2576k = true;
            g2.g gVar = c0298d.f2587v;
            gVar.sendMessageDelayed(gVar.obtainMessage(19), 300000L);
        }
        if (bVar.f2342k == 4) {
            b(C0298d.f2573y);
        } else if (this.f2604j.isEmpty()) {
            this.f2614t = bVar;
        } else if (runtimeException != null) {
            C0324l.a(this.f2616v.f2587v);
            c(null, runtimeException, false);
        } else if (this.f2616v.f2588w) {
            c(C0298d.c(this.f2606l, bVar), null, true);
            if (!this.f2604j.isEmpty() && !i(bVar) && !this.f2616v.b(bVar, this.f2610p)) {
                if (bVar.f2342k == 18) {
                    this.f2612r = true;
                }
                if (this.f2612r) {
                    C0298d c0298d2 = this.f2616v;
                    C0295a c0295a = this.f2606l;
                    g2.g gVar2 = c0298d2.f2587v;
                    gVar2.sendMessageDelayed(Message.obtain(gVar2, 9, c0295a), 5000L);
                    return;
                }
                b(C0298d.c(this.f2606l, bVar));
            }
        } else {
            b(C0298d.c(this.f2606l, bVar));
        }
    }

    public final void n(T1.b bVar) {
        C0324l.a(this.f2616v.f2587v);
        a.e eVar = this.f2605k;
        String name = eVar.getClass().getName();
        String valueOf = String.valueOf(bVar);
        eVar.c("onSignInFailed for " + name + " with " + valueOf);
        m(bVar, null);
    }

    public final void o() {
        C0324l.a(this.f2616v.f2587v);
        Status status = C0298d.f2572x;
        b(status);
        C0307m c0307m = this.f2607m;
        c0307m.getClass();
        c0307m.a(false, status);
        for (C0301g c0301g : (C0301g[]) this.f2609o.keySet().toArray(new C0301g[0])) {
            l(new K(c0301g, new C0758g()));
        }
        a(new T1.b(4));
        a.e eVar = this.f2605k;
        if (eVar.a()) {
            eVar.m(new D1.E(this));
        }
    }

    @Override // V1.InterfaceC0303i
    public final void p0(T1.b bVar) {
        m(bVar, null);
    }
}
