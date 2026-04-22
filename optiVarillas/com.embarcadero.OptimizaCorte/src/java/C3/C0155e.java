package C3;

import C3.Y;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* renamed from: C3.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0155e<T> extends J<T> implements n3.d, p3.d {

    /* renamed from: o  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f467o = AtomicIntegerFieldUpdater.newUpdater(C0155e.class, "_decisionAndIndex");

    /* renamed from: p  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f468p = AtomicReferenceFieldUpdater.newUpdater(C0155e.class, Object.class, "_state");

    /* renamed from: q  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f469q = AtomicReferenceFieldUpdater.newUpdater(C0155e.class, Object.class, "_parentHandle");
    private volatile int _decisionAndIndex;
    private volatile Object _parentHandle;
    private volatile Object _state;

    /* renamed from: m  reason: collision with root package name */
    public final n3.d<T> f470m;

    /* renamed from: n  reason: collision with root package name */
    public final n3.f f471n;

    public C0155e(n3.d dVar) {
        super(1);
        this.f470m = dVar;
        this.f471n = dVar.getContext();
        this._decisionAndIndex = 536870911;
        this._state = C0152b.f453j;
    }

    @Override // C3.J
    public final void a(Object obj, CancellationException cancellationException) {
        boolean z4;
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f468p;
            Object obj2 = atomicReferenceFieldUpdater.get(this);
            if (!(obj2 instanceof j0)) {
                if (obj2 instanceof C0162l) {
                    return;
                }
                if (obj2 instanceof C0161k) {
                    C0161k c0161k = (C0161k) obj2;
                    if (c0161k.f488e != null) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (!z4) {
                        C0161k c0161k2 = new C0161k(c0161k.f484a, c0161k.f485b, c0161k.f486c, c0161k.f487d, cancellationException);
                        while (!atomicReferenceFieldUpdater.compareAndSet(this, obj2, c0161k2)) {
                            if (atomicReferenceFieldUpdater.get(this) != obj2) {
                                break;
                            }
                        }
                        AbstractC0154d abstractC0154d = c0161k.f485b;
                        if (abstractC0154d != null) {
                            g(abstractC0154d, cancellationException);
                        }
                        u3.l<Throwable, l3.g> lVar = c0161k.f486c;
                        if (lVar != null) {
                            try {
                                lVar.g(cancellationException);
                                return;
                            } catch (Throwable th) {
                                C0173x.a(this.f471n, new RuntimeException("Exception in resume onCancellation handler for " + this, th));
                                return;
                            }
                        }
                        return;
                    }
                    throw new IllegalStateException("Must be called at most once".toString());
                }
                C0161k c0161k3 = new C0161k(obj2, (AbstractC0154d) null, (u3.l) null, cancellationException, 14);
                while (!atomicReferenceFieldUpdater.compareAndSet(this, obj2, c0161k3)) {
                    if (atomicReferenceFieldUpdater.get(this) != obj2) {
                        break;
                    }
                }
                return;
            }
            throw new IllegalStateException("Not completed".toString());
        }
    }

    @Override // C3.J
    public final n3.d<T> b() {
        return this.f470m;
    }

    @Override // C3.J
    public final Throwable c(Object obj) {
        Throwable c4 = super.c(obj);
        if (c4 == null) {
            return null;
        }
        return c4;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // C3.J
    public final <T> T d(Object obj) {
        if (obj instanceof C0161k) {
            return (T) ((C0161k) obj).f484a;
        }
        return obj;
    }

    @Override // C3.J
    public final Object f() {
        return f468p.get(this);
    }

    public final void g(AbstractC0154d abstractC0154d, Throwable th) {
        try {
            abstractC0154d.a(th);
        } catch (Throwable th2) {
            C0173x.a(this.f471n, new RuntimeException("Exception in invokeOnCancellation handler for " + this, th2));
        }
    }

    @Override // n3.d
    public final n3.f getContext() {
        return this.f471n;
    }

    @Override // p3.d
    public final p3.d h() {
        n3.d<T> dVar = this.f470m;
        if (dVar instanceof p3.d) {
            return (p3.d) dVar;
        }
        return null;
    }

    public final void i(Throwable th) {
        boolean z4;
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f468p;
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (!(obj instanceof j0)) {
                return;
            }
            if (!(obj instanceof AbstractC0154d) && !(obj instanceof F3.v)) {
                z4 = false;
            } else {
                z4 = true;
            }
            C0156f c0156f = new C0156f(this, th, z4);
            while (!atomicReferenceFieldUpdater.compareAndSet(this, obj, c0156f)) {
                if (atomicReferenceFieldUpdater.get(this) != obj) {
                    break;
                }
            }
            j0 j0Var = (j0) obj;
            if (j0Var instanceof AbstractC0154d) {
                g((AbstractC0154d) obj, th);
            } else if (j0Var instanceof F3.v) {
                F3.v vVar = (F3.v) obj;
                if ((f467o.get(this) & 536870911) != 536870911) {
                    try {
                        vVar.a();
                    } catch (Throwable th2) {
                        C0173x.a(this.f471n, new RuntimeException("Exception in invokeOnCancellation handler for " + this, th2));
                    }
                } else {
                    throw new IllegalStateException("The index for Segment.onCancellation(..) is broken".toString());
                }
            }
            if (!o()) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = f469q;
                L l2 = (L) atomicReferenceFieldUpdater2.get(this);
                if (l2 != null) {
                    l2.d();
                    atomicReferenceFieldUpdater2.set(this, i0.f483j);
                }
            }
            k(this.f430l);
            return;
        }
    }

    @Override // n3.d
    public final void j(Object obj) {
        Object obj2;
        AbstractC0154d abstractC0154d;
        Throwable a4 = l3.c.a(obj);
        if (a4 != null) {
            obj = new C0162l(a4, false);
        }
        int i4 = this.f430l;
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f468p;
            Object obj3 = atomicReferenceFieldUpdater.get(this);
            if (obj3 instanceof j0) {
                j0 j0Var = (j0) obj3;
                if ((obj instanceof C0162l) || !B2.a.g(i4) || !(j0Var instanceof AbstractC0154d)) {
                    obj2 = obj;
                } else {
                    if (j0Var instanceof AbstractC0154d) {
                        abstractC0154d = (AbstractC0154d) j0Var;
                    } else {
                        abstractC0154d = null;
                    }
                    obj2 = new C0161k(obj, abstractC0154d, (u3.l) null, (CancellationException) null, 16);
                }
                while (!atomicReferenceFieldUpdater.compareAndSet(this, obj3, obj2)) {
                    if (atomicReferenceFieldUpdater.get(this) != obj3) {
                        break;
                    }
                }
                if (!o()) {
                    AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = f469q;
                    L l2 = (L) atomicReferenceFieldUpdater2.get(this);
                    if (l2 != null) {
                        l2.d();
                        atomicReferenceFieldUpdater2.set(this, i0.f483j);
                    }
                }
                k(i4);
                return;
            }
            if (obj3 instanceof C0156f) {
                C0156f c0156f = (C0156f) obj3;
                c0156f.getClass();
                if (C0156f.f474c.compareAndSet(c0156f, 0, 1)) {
                    return;
                }
            }
            throw new IllegalStateException(("Already resumed, but proposed with update " + obj).toString());
        }
    }

    public final void k(int i4) {
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater;
        int i5;
        boolean z4;
        do {
            atomicIntegerFieldUpdater = f467o;
            i5 = atomicIntegerFieldUpdater.get(this);
            int i6 = i5 >> 29;
            if (i6 != 0) {
                if (i6 == 1) {
                    if (i4 == 4) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    n3.d<T> dVar = this.f470m;
                    if (!z4 && (dVar instanceof F3.h) && B2.a.g(i4) == B2.a.g(this.f430l)) {
                        AbstractC0171v abstractC0171v = ((F3.h) dVar).f916m;
                        n3.f context = ((F3.h) dVar).f917n.getContext();
                        if (abstractC0171v.G()) {
                            abstractC0171v.F(context, this);
                            return;
                        }
                        N a4 = m0.a();
                        if (a4.f434l >= 4294967296L) {
                            m3.a<J<?>> aVar = a4.f436n;
                            if (aVar == null) {
                                aVar = new m3.a<>();
                                a4.f436n = aVar;
                            }
                            aVar.j(this);
                            return;
                        }
                        a4.I(true);
                        try {
                            B2.a.l(this, dVar, true);
                            do {
                            } while (a4.J());
                        } finally {
                            try {
                                return;
                            } finally {
                            }
                        }
                        return;
                    }
                    B2.a.l(this, dVar, z4);
                    return;
                }
                throw new IllegalStateException("Already resumed".toString());
            }
        } while (!atomicIntegerFieldUpdater.compareAndSet(this, i5, 1073741824 + (536870911 & i5)));
    }

    public final Object l() {
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater;
        int i4;
        boolean o4 = o();
        do {
            atomicIntegerFieldUpdater = f467o;
            i4 = atomicIntegerFieldUpdater.get(this);
            int i5 = i4 >> 29;
            if (i5 != 0) {
                if (i5 == 2) {
                    if (o4) {
                        p();
                    }
                    Object obj = f468p.get(this);
                    if (!(obj instanceof C0162l)) {
                        if (B2.a.g(this.f430l)) {
                            Y y4 = (Y) this.f471n.E(Y.b.f450j);
                            if (y4 != null && !y4.a()) {
                                CancellationException t3 = y4.t();
                                a(obj, t3);
                                throw t3;
                            }
                        }
                        return d(obj);
                    }
                    throw ((C0162l) obj).f490a;
                }
                throw new IllegalStateException("Already suspended".toString());
            }
        } while (!atomicIntegerFieldUpdater.compareAndSet(this, i4, 536870912 + (536870911 & i4)));
        if (((L) f469q.get(this)) == null) {
            n();
        }
        if (o4) {
            p();
        }
        return o3.a.f5500j;
    }

    public final void m() {
        L n4 = n();
        if (n4 != null && (!(f468p.get(this) instanceof j0))) {
            n4.d();
            f469q.set(this, i0.f483j);
        }
    }

    public final L n() {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        Y y4 = (Y) this.f471n.E(Y.b.f450j);
        if (y4 == null) {
            return null;
        }
        L a4 = Y.a.a(y4, true, new C0157g(this), 2);
        do {
            atomicReferenceFieldUpdater = f469q;
            if (atomicReferenceFieldUpdater.compareAndSet(this, null, a4)) {
                break;
            }
        } while (atomicReferenceFieldUpdater.get(this) == null);
        return a4;
    }

    public final boolean o() {
        if (this.f430l == 2) {
            n3.d<T> dVar = this.f470m;
            v3.h.c(dVar, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<*>");
            if (F3.h.f915q.get((F3.h) dVar) != null) {
                return true;
            }
        }
        return false;
    }

    public final void p() {
        F3.h hVar;
        n3.d<T> dVar = this.f470m;
        Throwable th = null;
        if (dVar instanceof F3.h) {
            hVar = (F3.h) dVar;
        } else {
            hVar = null;
        }
        if (hVar != null) {
            loop0: while (true) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = F3.h.f915q;
                Object obj = atomicReferenceFieldUpdater.get(hVar);
                C1.A a4 = F3.i.f921b;
                if (obj == a4) {
                    while (!atomicReferenceFieldUpdater.compareAndSet(hVar, a4, this)) {
                        if (atomicReferenceFieldUpdater.get(hVar) != a4) {
                            break;
                        }
                    }
                    break loop0;
                } else if (obj instanceof Throwable) {
                    while (!atomicReferenceFieldUpdater.compareAndSet(hVar, obj, null)) {
                        if (atomicReferenceFieldUpdater.get(hVar) != obj) {
                            throw new IllegalArgumentException("Failed requirement.".toString());
                        }
                    }
                    th = (Throwable) obj;
                } else {
                    throw new IllegalStateException(("Inconsistent state " + obj).toString());
                }
            }
            if (th != null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = f469q;
                L l2 = (L) atomicReferenceFieldUpdater2.get(this);
                if (l2 != null) {
                    l2.d();
                    atomicReferenceFieldUpdater2.set(this, i0.f483j);
                }
                i(th);
            }
        }
    }

    public final String toString() {
        String str;
        StringBuilder sb = new StringBuilder("CancellableContinuation(");
        sb.append(C.e(this.f470m));
        sb.append("){");
        Object obj = f468p.get(this);
        if (obj instanceof j0) {
            str = "Active";
        } else if (obj instanceof C0156f) {
            str = "Cancelled";
        } else {
            str = "Completed";
        }
        sb.append(str);
        sb.append("}@");
        sb.append(C.c(this));
        return sb.toString();
    }
}
