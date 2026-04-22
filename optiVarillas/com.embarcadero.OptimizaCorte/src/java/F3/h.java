package F3;

import C3.AbstractC0171v;
import C3.C;
import C3.C0162l;
import C3.C0163m;
import C3.J;
import C3.N;
import C3.m0;
import F3.x;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class h<T> extends J<T> implements p3.d, n3.d<T> {

    /* renamed from: q  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f915q = AtomicReferenceFieldUpdater.newUpdater(h.class, Object.class, "_reusableCancellableContinuation");
    private volatile Object _reusableCancellableContinuation;

    /* renamed from: m  reason: collision with root package name */
    public final AbstractC0171v f916m;

    /* renamed from: n  reason: collision with root package name */
    public final n3.d<T> f917n;

    /* renamed from: o  reason: collision with root package name */
    public Object f918o;

    /* renamed from: p  reason: collision with root package name */
    public final Object f919p;

    public h(AbstractC0171v abstractC0171v, p3.c cVar) {
        super(-1);
        this.f916m = abstractC0171v;
        this.f917n = cVar;
        this.f918o = i.f920a;
        n3.f fVar = cVar.f5579k;
        v3.h.b(fVar);
        Object B4 = fVar.B(0, x.a.f948k);
        v3.h.b(B4);
        this.f919p = B4;
    }

    @Override // C3.J
    public final void a(Object obj, CancellationException cancellationException) {
        if (obj instanceof C0163m) {
            ((C0163m) obj).f492b.g(cancellationException);
        }
    }

    @Override // C3.J
    public final Object f() {
        Object obj = this.f918o;
        this.f918o = i.f920a;
        return obj;
    }

    @Override // n3.d
    public final n3.f getContext() {
        return this.f917n.getContext();
    }

    @Override // p3.d
    public final p3.d h() {
        n3.d<T> dVar = this.f917n;
        if (dVar instanceof p3.d) {
            return (p3.d) dVar;
        }
        return null;
    }

    @Override // n3.d
    public final void j(Object obj) {
        Object c0162l;
        n3.d<T> dVar = this.f917n;
        n3.f context = dVar.getContext();
        Throwable a4 = l3.c.a(obj);
        if (a4 == null) {
            c0162l = obj;
        } else {
            c0162l = new C0162l(a4, false);
        }
        AbstractC0171v abstractC0171v = this.f916m;
        if (abstractC0171v.G()) {
            this.f918o = c0162l;
            this.f430l = 0;
            abstractC0171v.F(context, this);
            return;
        }
        N a5 = m0.a();
        if (a5.f434l >= 4294967296L) {
            this.f918o = c0162l;
            this.f430l = 0;
            m3.a<J<?>> aVar = a5.f436n;
            if (aVar == null) {
                aVar = new m3.a<>();
                a5.f436n = aVar;
            }
            aVar.j(this);
            return;
        }
        a5.I(true);
        try {
            n3.f context2 = dVar.getContext();
            Object b4 = x.b(context2, this.f919p);
            dVar.j(obj);
            x.a(context2, b4);
            do {
            } while (a5.J());
        } finally {
            try {
            } finally {
            }
        }
    }

    public final String toString() {
        return "DispatchedContinuation[" + this.f916m + ", " + C.e(this.f917n) + ']';
    }

    @Override // C3.J
    public final n3.d<T> b() {
        return this;
    }
}
