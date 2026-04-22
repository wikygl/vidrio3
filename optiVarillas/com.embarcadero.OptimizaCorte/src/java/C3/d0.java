package C3;

import C3.Y;
import e0.C0406b;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import n3.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class d0 implements Y, InterfaceC0160j, k0 {

    /* renamed from: j  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f457j = AtomicReferenceFieldUpdater.newUpdater(d0.class, Object.class, "_state");

    /* renamed from: k  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f458k = AtomicReferenceFieldUpdater.newUpdater(d0.class, Object.class, "_parentHandle");
    private volatile Object _parentHandle;
    private volatile Object _state;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a extends c0 {

        /* renamed from: n  reason: collision with root package name */
        public final d0 f459n;

        /* renamed from: o  reason: collision with root package name */
        public final b f460o;

        /* renamed from: p  reason: collision with root package name */
        public final C0159i f461p;

        /* renamed from: q  reason: collision with root package name */
        public final Object f462q;

        public a(d0 d0Var, b bVar, C0159i c0159i, Object obj) {
            this.f459n = d0Var;
            this.f460o = bVar;
            this.f461p = c0159i;
            this.f462q = obj;
        }

        @Override // u3.l
        public final /* bridge */ /* synthetic */ l3.g g(Throwable th) {
            o(th);
            return l3.g.f5271a;
        }

        @Override // C3.AbstractC0164n
        public final void o(Throwable th) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = d0.f457j;
            d0 d0Var = this.f459n;
            d0Var.getClass();
            C0159i M3 = d0.M(this.f461p);
            b bVar = this.f460o;
            Object obj = this.f462q;
            if (M3 != null) {
                while (Y.a.a(M3.f482n, false, new a(d0Var, bVar, M3, obj), 1) == i0.f483j) {
                    M3 = d0.M(M3);
                    if (M3 == null) {
                        d0Var.e(d0Var.C(bVar, obj));
                    }
                }
                return;
            }
            d0Var.e(d0Var.C(bVar, obj));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class b implements U {

        /* renamed from: k  reason: collision with root package name */
        public static final AtomicIntegerFieldUpdater f463k = AtomicIntegerFieldUpdater.newUpdater(b.class, "_isCompleting");

        /* renamed from: l  reason: collision with root package name */
        public static final AtomicReferenceFieldUpdater f464l = AtomicReferenceFieldUpdater.newUpdater(b.class, Object.class, "_rootCause");

        /* renamed from: m  reason: collision with root package name */
        public static final AtomicReferenceFieldUpdater f465m = AtomicReferenceFieldUpdater.newUpdater(b.class, Object.class, "_exceptionsHolder");
        private volatile Object _exceptionsHolder;
        private volatile int _isCompleting = 0;
        private volatile Object _rootCause;

        /* renamed from: j  reason: collision with root package name */
        public final h0 f466j;

        public b(h0 h0Var, Throwable th) {
            this.f466j = h0Var;
            this._rootCause = th;
        }

        @Override // C3.U
        public final boolean a() {
            if (c() == null) {
                return true;
            }
            return false;
        }

        public final void b(Throwable th) {
            Throwable c4 = c();
            if (c4 == null) {
                f464l.set(this, th);
            } else if (th == c4) {
            } else {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f465m;
                Object obj = atomicReferenceFieldUpdater.get(this);
                if (obj == null) {
                    atomicReferenceFieldUpdater.set(this, th);
                } else if (obj instanceof Throwable) {
                    if (th == obj) {
                        return;
                    }
                    ArrayList arrayList = new ArrayList(4);
                    arrayList.add(obj);
                    arrayList.add(th);
                    atomicReferenceFieldUpdater.set(this, arrayList);
                } else if (obj instanceof ArrayList) {
                    ((ArrayList) obj).add(th);
                } else {
                    throw new IllegalStateException(("State is " + obj).toString());
                }
            }
        }

        public final Throwable c() {
            return (Throwable) f464l.get(this);
        }

        public final boolean d() {
            if (c() != null) {
                return true;
            }
            return false;
        }

        public final boolean e() {
            if (f463k.get(this) != 0) {
                return true;
            }
            return false;
        }

        public final ArrayList f(Throwable th) {
            ArrayList arrayList;
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f465m;
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (obj == null) {
                arrayList = new ArrayList(4);
            } else if (obj instanceof Throwable) {
                ArrayList arrayList2 = new ArrayList(4);
                arrayList2.add(obj);
                arrayList = arrayList2;
            } else if (obj instanceof ArrayList) {
                arrayList = (ArrayList) obj;
            } else {
                throw new IllegalStateException(("State is " + obj).toString());
            }
            Throwable c4 = c();
            if (c4 != null) {
                arrayList.add(0, c4);
            }
            if (th != null && !th.equals(c4)) {
                arrayList.add(th);
            }
            atomicReferenceFieldUpdater.set(this, f0.f479e);
            return arrayList;
        }

        @Override // C3.U
        public final h0 h() {
            return this.f466j;
        }

        public final String toString() {
            return "Finishing[cancelling=" + d() + ", completing=" + e() + ", rootCause=" + c() + ", exceptions=" + f465m.get(this) + ", list=" + this.f466j + ']';
        }
    }

    public d0(boolean z4) {
        M m4;
        if (z4) {
            m4 = f0.f480g;
        } else {
            m4 = f0.f;
        }
        this._state = m4;
    }

    public static C0159i M(F3.m mVar) {
        while (mVar.n()) {
            F3.m i4 = mVar.i();
            if (i4 == null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = F3.m.f932k;
                Object obj = atomicReferenceFieldUpdater.get(mVar);
                while (true) {
                    mVar = (F3.m) obj;
                    if (!mVar.n()) {
                        break;
                    }
                    obj = atomicReferenceFieldUpdater.get(mVar);
                }
            } else {
                mVar = i4;
            }
        }
        while (true) {
            mVar = mVar.m();
            if (!mVar.n()) {
                if (mVar instanceof C0159i) {
                    return (C0159i) mVar;
                }
                if (mVar instanceof h0) {
                    return null;
                }
            }
        }
    }

    public static String R(Object obj) {
        if (obj instanceof b) {
            b bVar = (b) obj;
            if (bVar.d()) {
                return "Cancelling";
            }
            if (!bVar.e()) {
                return "Active";
            }
            return "Completing";
        } else if (obj instanceof U) {
            if (((U) obj).a()) {
                return "Active";
            }
            return "New";
        } else if (obj instanceof C0162l) {
            return "Cancelled";
        } else {
            return "Completed";
        }
    }

    public final Throwable A(Object obj) {
        boolean z4;
        if (obj == null) {
            z4 = true;
        } else {
            z4 = obj instanceof Throwable;
        }
        if (z4) {
            Throwable th = (Throwable) obj;
            if (th == null) {
                return new Z(x(), null, this);
            }
            return th;
        }
        v3.h.c(obj, "null cannot be cast to non-null type kotlinx.coroutines.ParentJob");
        return ((k0) obj).s();
    }

    @Override // n3.f
    public final <R> R B(R r4, u3.p<? super R, ? super f.b, ? extends R> pVar) {
        return pVar.f(r4, this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Object C(b bVar, Object obj) {
        C0162l c0162l;
        Throwable th;
        Object obj2;
        Throwable th2 = null;
        if (obj instanceof C0162l) {
            c0162l = (C0162l) obj;
        } else {
            c0162l = null;
        }
        if (c0162l != null) {
            th = c0162l.f490a;
        } else {
            th = null;
        }
        synchronized (bVar) {
            bVar.d();
            ArrayList<Throwable> f = bVar.f(th);
            if (f.isEmpty()) {
                if (bVar.d()) {
                    th2 = new Z(x(), null, this);
                }
            } else {
                Iterator it = f.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object next = it.next();
                    if (!(((Throwable) next) instanceof CancellationException)) {
                        th2 = next;
                        break;
                    }
                }
                th2 = th2;
                if (th2 == null) {
                    th2 = (Throwable) f.get(0);
                }
            }
            if (th2 != null && f.size() > 1) {
                Set newSetFromMap = Collections.newSetFromMap(new IdentityHashMap(f.size()));
                for (Throwable th3 : f) {
                    if (th3 != th2 && th3 != th2 && !(th3 instanceof CancellationException) && newSetFromMap.add(th3)) {
                        A3.d.a(th2, th3);
                    }
                }
            }
        }
        if (th2 != null && th2 != th) {
            obj = new C0162l(th2, false);
        }
        if (th2 != null && o(th2)) {
            v3.h.c(obj, "null cannot be cast to non-null type kotlinx.coroutines.CompletedExceptionally");
            C0162l.f489b.compareAndSet((C0162l) obj, 0, 1);
        }
        O(obj);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f457j;
        if (obj instanceof U) {
            obj2 = new V((U) obj);
        } else {
            obj2 = obj;
        }
        while (!atomicReferenceFieldUpdater.compareAndSet(this, bVar, obj2) && atomicReferenceFieldUpdater.get(this) == bVar) {
        }
        y(bVar, obj);
        return obj;
    }

    public boolean D() {
        return true;
    }

    @Override // n3.f
    public final <E extends f.b> E E(f.c<E> cVar) {
        return (E) f.b.a.a(this, cVar);
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [F3.k, C3.h0] */
    public final h0 F(U u4) {
        h0 h4 = u4.h();
        if (h4 == null) {
            if (u4 instanceof M) {
                return new F3.k();
            }
            if (u4 instanceof c0) {
                Q((c0) u4);
                return null;
            }
            throw new IllegalStateException(("State should have list: " + u4).toString());
        }
        return h4;
    }

    public final Object G() {
        while (true) {
            Object obj = f457j.get(this);
            if (!(obj instanceof F3.r)) {
                return obj;
            }
            ((F3.r) obj).a(this);
        }
    }

    public final void I(Y y4) {
        i0 i0Var = i0.f483j;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f458k;
        if (y4 == null) {
            atomicReferenceFieldUpdater.set(this, i0Var);
            return;
        }
        y4.start();
        InterfaceC0158h d4 = y4.d(this);
        atomicReferenceFieldUpdater.set(this, d4);
        if (!(G() instanceof U)) {
            d4.d();
            atomicReferenceFieldUpdater.set(this, i0Var);
        }
    }

    public final L J(C0406b c0406b) {
        return r(false, true, c0406b);
    }

    public boolean K() {
        return false;
    }

    public String L() {
        return getClass().getSimpleName();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.lang.Throwable, C3.o] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [java.lang.RuntimeException] */
    /* JADX WARN: Type inference failed for: r1v5 */
    public final void N(h0 h0Var, Throwable th) {
        Object l2 = h0Var.l();
        v3.h.c(l2, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
        F3.m mVar = (F3.m) l2;
        C0165o c0165o = 0;
        while (!mVar.equals(h0Var)) {
            if (mVar instanceof a0) {
                c0 c0Var = (c0) mVar;
                try {
                    c0Var.o(th);
                } catch (Throwable th2) {
                    if (c0165o != 0) {
                        A3.d.a(c0165o, th2);
                    } else {
                        c0165o = new RuntimeException("Exception in completion handler " + c0Var + " for " + this, th2);
                    }
                }
            }
            mVar = mVar.m();
            c0165o = c0165o;
        }
        if (c0165o != 0) {
            H(c0165o);
        }
        o(th);
    }

    public final void Q(c0 c0Var) {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        F3.k kVar = new F3.k();
        c0Var.getClass();
        F3.m.f932k.lazySet(kVar, c0Var);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = F3.m.f931j;
        atomicReferenceFieldUpdater2.lazySet(kVar, c0Var);
        loop0: while (true) {
            if (c0Var.l() == c0Var) {
                while (!atomicReferenceFieldUpdater2.compareAndSet(c0Var, c0Var, kVar)) {
                    if (atomicReferenceFieldUpdater2.get(c0Var) != c0Var) {
                        break;
                    }
                }
                kVar.k(c0Var);
                break loop0;
            }
            break;
        }
        F3.m m4 = c0Var.m();
        do {
            atomicReferenceFieldUpdater = f457j;
            if (atomicReferenceFieldUpdater.compareAndSet(this, c0Var, m4)) {
                return;
            }
        } while (atomicReferenceFieldUpdater.get(this) == c0Var);
    }

    public final Object S(Object obj, Object obj2) {
        V v4;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        b bVar;
        C0162l c0162l;
        C0159i c0159i;
        if (!(obj instanceof U)) {
            return f0.f475a;
        }
        if (((obj instanceof M) || (obj instanceof c0)) && !(obj instanceof C0159i) && !(obj2 instanceof C0162l)) {
            U u4 = (U) obj;
            if (obj2 instanceof U) {
                v4 = new V((U) obj2);
            } else {
                v4 = obj2;
            }
            do {
                atomicReferenceFieldUpdater = f457j;
                if (atomicReferenceFieldUpdater.compareAndSet(this, u4, v4)) {
                    O(obj2);
                    y(u4, obj2);
                    return obj2;
                }
            } while (atomicReferenceFieldUpdater.get(this) == u4);
            return f0.f477c;
        }
        U u5 = (U) obj;
        h0 F4 = F(u5);
        if (F4 == null) {
            return f0.f477c;
        }
        C0159i c0159i2 = null;
        if (u5 instanceof b) {
            bVar = (b) u5;
        } else {
            bVar = null;
        }
        if (bVar == null) {
            bVar = new b(F4, null);
        }
        synchronized (bVar) {
            if (bVar.e()) {
                return f0.f475a;
            }
            b.f463k.set(bVar, 1);
            if (bVar != u5) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = f457j;
                while (!atomicReferenceFieldUpdater2.compareAndSet(this, u5, bVar)) {
                    if (atomicReferenceFieldUpdater2.get(this) != u5) {
                        return f0.f477c;
                    }
                }
            }
            boolean d4 = bVar.d();
            if (obj2 instanceof C0162l) {
                c0162l = (C0162l) obj2;
            } else {
                c0162l = null;
            }
            if (c0162l != null) {
                bVar.b(c0162l.f490a);
            }
            Throwable c4 = bVar.c();
            if (!(!d4)) {
                c4 = null;
            }
            if (c4 != null) {
                N(F4, c4);
            }
            if (u5 instanceof C0159i) {
                c0159i = (C0159i) u5;
            } else {
                c0159i = null;
            }
            if (c0159i == null) {
                h0 h4 = u5.h();
                if (h4 != null) {
                    c0159i2 = M(h4);
                }
            } else {
                c0159i2 = c0159i;
            }
            if (c0159i2 != null) {
                while (Y.a.a(c0159i2.f482n, false, new a(this, bVar, c0159i2, obj2), 1) == i0.f483j) {
                    c0159i2 = M(c0159i2);
                    if (c0159i2 == null) {
                        return C(bVar, obj2);
                    }
                }
                return f0.f476b;
            }
            return C(bVar, obj2);
        }
    }

    @Override // C3.Y
    public boolean a() {
        Object G4 = G();
        if ((G4 instanceof U) && ((U) G4).a()) {
            return true;
        }
        return false;
    }

    public final boolean c(U u4, h0 h0Var, c0 c0Var) {
        char c4;
        e0 e0Var = new e0(c0Var, this, u4);
        do {
            F3.m i4 = h0Var.i();
            if (i4 == null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = F3.m.f932k;
                Object obj = atomicReferenceFieldUpdater.get(h0Var);
                while (true) {
                    i4 = (F3.m) obj;
                    if (!i4.n()) {
                        break;
                    }
                    obj = atomicReferenceFieldUpdater.get(i4);
                }
            }
            F3.m.f932k.lazySet(c0Var, i4);
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = F3.m.f931j;
            atomicReferenceFieldUpdater2.lazySet(c0Var, h0Var);
            e0Var.f935c = h0Var;
            while (true) {
                if (atomicReferenceFieldUpdater2.compareAndSet(i4, h0Var, e0Var)) {
                    if (e0Var.a(i4) == null) {
                        c4 = 1;
                    } else {
                        c4 = 2;
                    }
                } else if (atomicReferenceFieldUpdater2.get(i4) != h0Var) {
                    c4 = 0;
                    break;
                }
            }
            if (c4 == 1) {
                return true;
            }
        } while (c4 != 2);
        return false;
    }

    @Override // C3.Y
    public final InterfaceC0158h d(d0 d0Var) {
        return (InterfaceC0158h) Y.a.a(this, true, new C0159i(d0Var), 2);
    }

    @Override // C3.InterfaceC0160j
    public final void g(d0 d0Var) {
        n(d0Var);
    }

    @Override // n3.f.b
    public final f.c<?> getKey() {
        return Y.b.f450j;
    }

    @Override // n3.f
    public final n3.f k(n3.f fVar) {
        return f.b.a.c(this, fVar);
    }

    public void m(Object obj) {
        e(obj);
    }

    public final boolean n(Object obj) {
        C1.A a4;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        C1.A a5 = f0.f475a;
        if (this instanceof b0) {
            do {
                Object G4 = G();
                if ((G4 instanceof U) && (!(G4 instanceof b) || !((b) G4).e())) {
                    a5 = S(G4, new C0162l(A(obj), false));
                } else {
                    a5 = f0.f475a;
                    break;
                }
            } while (a5 == f0.f477c);
            if (a5 == f0.f476b) {
                return true;
            }
        }
        if (a5 == f0.f475a) {
            Throwable th = null;
            Throwable th2 = null;
            loop1: while (true) {
                Object G5 = G();
                if (G5 instanceof b) {
                    synchronized (G5) {
                        try {
                            b bVar = (b) G5;
                            bVar.getClass();
                            if (b.f465m.get(bVar) == f0.f479e) {
                                a4 = f0.f478d;
                            } else {
                                boolean d4 = ((b) G5).d();
                                if (obj != null || !d4) {
                                    if (th2 == null) {
                                        th2 = A(obj);
                                    }
                                    ((b) G5).b(th2);
                                }
                                Throwable c4 = ((b) G5).c();
                                if (!d4) {
                                    th = c4;
                                }
                                if (th != null) {
                                    N(((b) G5).f466j, th);
                                }
                                a4 = f0.f475a;
                            }
                        } catch (Throwable th3) {
                            throw th3;
                        }
                    }
                } else if (G5 instanceof U) {
                    if (th2 == null) {
                        th2 = A(obj);
                    }
                    U u4 = (U) G5;
                    if (u4.a()) {
                        h0 F4 = F(u4);
                        if (F4 == null) {
                            continue;
                        } else {
                            b bVar2 = new b(F4, th2);
                            do {
                                atomicReferenceFieldUpdater = f457j;
                                if (atomicReferenceFieldUpdater.compareAndSet(this, u4, bVar2)) {
                                    N(F4, th2);
                                    a4 = f0.f475a;
                                    break loop1;
                                }
                            } while (atomicReferenceFieldUpdater.get(this) == u4);
                        }
                    } else {
                        Object S3 = S(G5, new C0162l(th2, false));
                        if (S3 != f0.f475a) {
                            if (S3 != f0.f477c) {
                                a5 = S3;
                                break;
                            }
                        } else {
                            throw new IllegalStateException(("Cannot happen in " + G5).toString());
                        }
                    }
                } else {
                    a4 = f0.f478d;
                    break;
                }
            }
            a5 = a4;
        }
        if (a5 != f0.f475a && a5 != f0.f476b) {
            if (a5 == f0.f478d) {
                return false;
            }
            e(a5);
        }
        return true;
    }

    public final boolean o(Throwable th) {
        if (K()) {
            return true;
        }
        boolean z4 = th instanceof CancellationException;
        InterfaceC0158h interfaceC0158h = (InterfaceC0158h) f458k.get(this);
        if (interfaceC0158h != null && interfaceC0158h != i0.f483j) {
            if (interfaceC0158h.j(th) || z4) {
                return true;
            }
            return false;
        }
        return z4;
    }

    @Override // n3.f
    public final n3.f q(f.c<?> cVar) {
        return f.b.a.b(this, cVar);
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [F3.k, C3.h0] */
    @Override // C3.Y
    public final L r(boolean z4, boolean z5, u3.l<? super Throwable, l3.g> lVar) {
        c0 c0Var;
        T t3;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        C0162l c0162l;
        Throwable th;
        Throwable th2 = null;
        if (z4) {
            if (lVar instanceof a0) {
                c0Var = (a0) lVar;
            } else {
                c0Var = null;
            }
            if (c0Var == null) {
                c0Var = new W(lVar);
            }
        } else {
            if (lVar instanceof c0) {
                c0Var = (c0) lVar;
            } else {
                c0Var = null;
            }
            if (c0Var == null) {
                c0Var = new X(lVar);
            }
        }
        c0Var.f456m = this;
        while (true) {
            Object G4 = G();
            if (G4 instanceof M) {
                M m4 = (M) G4;
                if (m4.f433j) {
                    AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = f457j;
                    while (!atomicReferenceFieldUpdater2.compareAndSet(this, G4, c0Var)) {
                        if (atomicReferenceFieldUpdater2.get(this) != G4) {
                            break;
                        }
                    }
                    return c0Var;
                }
                ?? kVar = new F3.k();
                if (m4.f433j) {
                    t3 = kVar;
                } else {
                    t3 = new T(kVar);
                }
                do {
                    atomicReferenceFieldUpdater = f457j;
                    if (atomicReferenceFieldUpdater.compareAndSet(this, m4, t3)) {
                        break;
                    }
                } while (atomicReferenceFieldUpdater.get(this) == m4);
            } else if (G4 instanceof U) {
                h0 h4 = ((U) G4).h();
                if (h4 == null) {
                    v3.h.c(G4, "null cannot be cast to non-null type kotlinx.coroutines.JobNode");
                    Q((c0) G4);
                } else {
                    L l2 = i0.f483j;
                    if (z4 && (G4 instanceof b)) {
                        synchronized (G4) {
                            try {
                                th = ((b) G4).c();
                                if (th != null) {
                                    if ((lVar instanceof C0159i) && !((b) G4).e()) {
                                    }
                                }
                                if (c((U) G4, h4, c0Var)) {
                                    if (th == null) {
                                        return c0Var;
                                    }
                                    l2 = c0Var;
                                }
                            } catch (Throwable th3) {
                                throw th3;
                            }
                        }
                    } else {
                        th = null;
                    }
                    if (th != null) {
                        if (z5) {
                            lVar.g(th);
                        }
                        return l2;
                    } else if (c((U) G4, h4, c0Var)) {
                        return c0Var;
                    }
                }
            } else {
                if (z5) {
                    if (G4 instanceof C0162l) {
                        c0162l = (C0162l) G4;
                    } else {
                        c0162l = null;
                    }
                    if (c0162l != null) {
                        th2 = c0162l.f490a;
                    }
                    lVar.g(th2);
                }
                return i0.f483j;
            }
        }
    }

    @Override // C3.k0
    public final CancellationException s() {
        Throwable th;
        Object G4 = G();
        CancellationException cancellationException = null;
        if (G4 instanceof b) {
            th = ((b) G4).c();
        } else if (G4 instanceof C0162l) {
            th = ((C0162l) G4).f490a;
        } else if (!(G4 instanceof U)) {
            th = null;
        } else {
            throw new IllegalStateException(("Cannot be cancelling child in this state: " + G4).toString());
        }
        if (th instanceof CancellationException) {
            cancellationException = th;
        }
        if (cancellationException == null) {
            return new Z("Parent job is ".concat(R(G4)), th, this);
        }
        return cancellationException;
    }

    @Override // C3.Y
    public final boolean start() {
        char c4;
        do {
            Object G4 = G();
            boolean z4 = G4 instanceof M;
            c4 = 65535;
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f457j;
            if (z4) {
                if (!((M) G4).f433j) {
                    M m4 = f0.f480g;
                    while (!atomicReferenceFieldUpdater.compareAndSet(this, G4, m4)) {
                        if (atomicReferenceFieldUpdater.get(this) != G4) {
                            break;
                        }
                    }
                    c4 = 1;
                }
                c4 = 0;
            } else {
                if (G4 instanceof T) {
                    h0 h0Var = ((T) G4).f445j;
                    while (!atomicReferenceFieldUpdater.compareAndSet(this, G4, h0Var)) {
                        if (atomicReferenceFieldUpdater.get(this) != G4) {
                            break;
                        }
                    }
                    c4 = 1;
                }
                c4 = 0;
            }
            if (c4 == 0) {
                return false;
            }
        } while (c4 != 1);
        return true;
    }

    @Override // C3.Y
    public final CancellationException t() {
        Object G4 = G();
        CancellationException cancellationException = null;
        if (G4 instanceof b) {
            Throwable c4 = ((b) G4).c();
            if (c4 != null) {
                String concat = getClass().getSimpleName().concat(" is cancelling");
                if (c4 instanceof CancellationException) {
                    cancellationException = (CancellationException) c4;
                }
                if (cancellationException == null) {
                    if (concat == null) {
                        concat = x();
                    }
                    return new Z(concat, c4, this);
                }
                return cancellationException;
            }
            throw new IllegalStateException(("Job is still new or active: " + this).toString());
        } else if (!(G4 instanceof U)) {
            if (G4 instanceof C0162l) {
                Throwable th = ((C0162l) G4).f490a;
                if (th instanceof CancellationException) {
                    cancellationException = (CancellationException) th;
                }
                if (cancellationException == null) {
                    return new Z(x(), th, this);
                }
                return cancellationException;
            }
            return new Z(getClass().getSimpleName().concat(" has completed normally"), null, this);
        } else {
            throw new IllegalStateException(("Job is still new or active: " + this).toString());
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(L() + '{' + R(G()) + '}');
        sb.append('@');
        sb.append(C.c(this));
        return sb.toString();
    }

    @Override // C3.Y
    public final void w(CancellationException cancellationException) {
        if (cancellationException == null) {
            cancellationException = new Z(x(), null, this);
        }
        n(cancellationException);
    }

    public String x() {
        return "Job was cancelled";
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.RuntimeException, C3.o] */
    /* JADX WARN: Type inference failed for: r1v4, types: [java.lang.Throwable, C3.o] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6, types: [java.lang.RuntimeException] */
    /* JADX WARN: Type inference failed for: r1v8 */
    public final void y(U u4, Object obj) {
        C0162l c0162l;
        Throwable th;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f458k;
        InterfaceC0158h interfaceC0158h = (InterfaceC0158h) atomicReferenceFieldUpdater.get(this);
        if (interfaceC0158h != null) {
            interfaceC0158h.d();
            atomicReferenceFieldUpdater.set(this, i0.f483j);
        }
        C0165o c0165o = 0;
        if (obj instanceof C0162l) {
            c0162l = (C0162l) obj;
        } else {
            c0162l = null;
        }
        if (c0162l != null) {
            th = c0162l.f490a;
        } else {
            th = null;
        }
        if (u4 instanceof c0) {
            try {
                ((c0) u4).o(th);
                return;
            } catch (Throwable th2) {
                H(new RuntimeException("Exception in completion handler " + u4 + " for " + this, th2));
                return;
            }
        }
        h0 h4 = u4.h();
        if (h4 != null) {
            Object l2 = h4.l();
            v3.h.c(l2, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
            F3.m mVar = (F3.m) l2;
            while (!mVar.equals(h4)) {
                if (mVar instanceof c0) {
                    c0 c0Var = (c0) mVar;
                    try {
                        c0Var.o(th);
                    } catch (Throwable th3) {
                        if (c0165o != 0) {
                            A3.d.a(c0165o, th3);
                        } else {
                            c0165o = new RuntimeException("Exception in completion handler " + c0Var + " for " + this, th3);
                        }
                    }
                }
                mVar = mVar.m();
                c0165o = c0165o;
            }
            if (c0165o != 0) {
                H(c0165o);
            }
        }
    }

    public void P() {
    }

    public void H(C0165o c0165o) {
        throw c0165o;
    }

    public void O(Object obj) {
    }

    public void e(Object obj) {
    }
}
