package F3;

import C3.C;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class m {

    /* renamed from: j  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f931j = AtomicReferenceFieldUpdater.newUpdater(m.class, Object.class, "_next");

    /* renamed from: k  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f932k = AtomicReferenceFieldUpdater.newUpdater(m.class, Object.class, "_prev");

    /* renamed from: l  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f933l = AtomicReferenceFieldUpdater.newUpdater(m.class, Object.class, "_removedRef");
    private volatile Object _next = this;
    private volatile Object _prev = this;
    private volatile Object _removedRef;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static abstract class a extends F3.b<m> {

        /* renamed from: b  reason: collision with root package name */
        public final m f934b;

        /* renamed from: c  reason: collision with root package name */
        public m f935c;

        public a(m mVar) {
            this.f934b = mVar;
        }

        @Override // F3.b
        public final void b(m mVar, Object obj) {
            boolean z4;
            m mVar2;
            m mVar3 = mVar;
            if (obj == null) {
                z4 = true;
            } else {
                z4 = false;
            }
            m mVar4 = this.f934b;
            if (z4) {
                mVar2 = mVar4;
            } else {
                mVar2 = this.f935c;
            }
            if (mVar2 != null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = m.f931j;
                while (!atomicReferenceFieldUpdater.compareAndSet(mVar3, this, mVar2)) {
                    if (atomicReferenceFieldUpdater.get(mVar3) != this) {
                        return;
                    }
                }
                if (z4) {
                    m mVar5 = this.f935c;
                    v3.h.b(mVar5);
                    mVar4.k(mVar5);
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x003e, code lost:
        r6 = ((F3.s) r6).f945a;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0046, code lost:
        if (r5.compareAndSet(r4, r3, r6) == false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x004e, code lost:
        if (r5.get(r4) == r3) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final F3.m i() {
        /*
            r9 = this;
        L0:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r0 = F3.m.f932k
            java.lang.Object r1 = r0.get(r9)
            F3.m r1 = (F3.m) r1
            r2 = 0
            r3 = r1
        La:
            r4 = r2
        Lb:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r5 = F3.m.f931j
            java.lang.Object r6 = r5.get(r3)
            if (r6 != r9) goto L24
            if (r1 != r3) goto L16
            return r3
        L16:
            boolean r2 = r0.compareAndSet(r9, r1, r3)
            if (r2 == 0) goto L1d
            return r3
        L1d:
            java.lang.Object r2 = r0.get(r9)
            if (r2 == r1) goto L16
            goto L0
        L24:
            boolean r7 = r9.n()
            if (r7 == 0) goto L2b
            return r2
        L2b:
            if (r6 != 0) goto L2e
            return r3
        L2e:
            boolean r7 = r6 instanceof F3.r
            if (r7 == 0) goto L38
            F3.r r6 = (F3.r) r6
            r6.a(r3)
            goto L0
        L38:
            boolean r7 = r6 instanceof F3.s
            if (r7 == 0) goto L58
            if (r4 == 0) goto L51
            F3.s r6 = (F3.s) r6
            F3.m r6 = r6.f945a
        L42:
            boolean r7 = r5.compareAndSet(r4, r3, r6)
            if (r7 == 0) goto L4a
            r3 = r4
            goto La
        L4a:
            java.lang.Object r7 = r5.get(r4)
            if (r7 == r3) goto L42
            goto L0
        L51:
            java.lang.Object r3 = r0.get(r3)
            F3.m r3 = (F3.m) r3
            goto Lb
        L58:
            java.lang.String r4 = "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }"
            v3.h.c(r6, r4)
            r4 = r6
            F3.m r4 = (F3.m) r4
            r8 = r4
            r4 = r3
            r3 = r8
            goto Lb
        */
        throw new UnsupportedOperationException("Method not decompiled: F3.m.i():F3.m");
    }

    public final void k(m mVar) {
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f932k;
            m mVar2 = (m) atomicReferenceFieldUpdater.get(mVar);
            if (l() != mVar) {
                return;
            }
            while (!atomicReferenceFieldUpdater.compareAndSet(mVar, mVar2, this)) {
                if (atomicReferenceFieldUpdater.get(mVar) != mVar2) {
                    break;
                }
            }
            if (n()) {
                mVar.i();
                return;
            }
            return;
        }
    }

    public final Object l() {
        while (true) {
            Object obj = f931j.get(this);
            if (!(obj instanceof r)) {
                return obj;
            }
            ((r) obj).a(this);
        }
    }

    public final m m() {
        s sVar;
        m mVar;
        Object l2 = l();
        if (l2 instanceof s) {
            sVar = (s) l2;
        } else {
            sVar = null;
        }
        if (sVar == null || (mVar = sVar.f945a) == null) {
            v3.h.c(l2, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
            return (m) l2;
        }
        return mVar;
    }

    public boolean n() {
        return l() instanceof s;
    }

    public String toString() {
        return new v3.l(this) + '@' + C.c(this);
    }
}
