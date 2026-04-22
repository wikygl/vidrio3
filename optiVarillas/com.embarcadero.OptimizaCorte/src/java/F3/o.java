package F3;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class o<E> {

    /* renamed from: e  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f937e = AtomicReferenceFieldUpdater.newUpdater(o.class, Object.class, "_next");
    public static final AtomicLongFieldUpdater f = AtomicLongFieldUpdater.newUpdater(o.class, "_state");

    /* renamed from: g  reason: collision with root package name */
    public static final C1.A f938g = new C1.A(1, "REMOVE_FROZEN");
    private volatile Object _next;
    private volatile long _state;

    /* renamed from: a  reason: collision with root package name */
    public final int f939a;

    /* renamed from: b  reason: collision with root package name */
    public final boolean f940b;

    /* renamed from: c  reason: collision with root package name */
    public final int f941c;

    /* renamed from: d  reason: collision with root package name */
    public final AtomicReferenceArray f942d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final int f943a;

        public a(int i4) {
            this.f943a = i4;
        }
    }

    public o(int i4, boolean z4) {
        this.f939a = i4;
        this.f940b = z4;
        int i5 = i4 - 1;
        this.f941c = i5;
        this.f942d = new AtomicReferenceArray(i4);
        if (i5 <= 1073741823) {
            if ((i4 & i5) == 0) {
                return;
            }
            throw new IllegalStateException("Check failed.".toString());
        }
        throw new IllegalStateException("Check failed.".toString());
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0053, code lost:
        return 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final int a(E r16) {
        /*
            r15 = this;
            r6 = r15
            r7 = r16
        L3:
            java.util.concurrent.atomic.AtomicLongFieldUpdater r8 = F3.o.f
            long r2 = r8.get(r15)
            r0 = 3458764513820540928(0x3000000000000000, double:1.727233711018889E-77)
            long r0 = r0 & r2
            r9 = 0
            r4 = 1
            int r5 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r5 == 0) goto L1c
            r0 = 2305843009213693952(0x2000000000000000, double:1.4916681462400413E-154)
            long r0 = r0 & r2
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 == 0) goto L1b
            r4 = 2
        L1b:
            return r4
        L1c:
            r0 = 1073741823(0x3fffffff, double:5.304989472E-315)
            long r0 = r0 & r2
            int r1 = (int) r0
            r11 = 1152921503533105152(0xfffffffc0000000, double:1.2882296003504729E-231)
            long r11 = r11 & r2
            r0 = 30
            long r11 = r11 >> r0
            int r12 = (int) r11
            int r5 = r12 + 2
            int r11 = r6.f941c
            r5 = r5 & r11
            r13 = r1 & r11
            if (r5 != r13) goto L35
            return r4
        L35:
            boolean r5 = r6.f940b
            r13 = 1073741823(0x3fffffff, float:1.9999999)
            java.util.concurrent.atomic.AtomicReferenceArray r14 = r6.f942d
            if (r5 != 0) goto L54
            r5 = r12 & r11
            java.lang.Object r5 = r14.get(r5)
            if (r5 == 0) goto L54
            r0 = 1024(0x400, float:1.435E-42)
            int r2 = r6.f939a
            if (r2 < r0) goto L53
            int r12 = r12 - r1
            r0 = r12 & r13
            int r1 = r2 >> 1
            if (r0 <= r1) goto L3
        L53:
            return r4
        L54:
            int r1 = r12 + 1
            r1 = r1 & r13
            r4 = -1152921503533105153(0xf00000003fffffff, double:-3.1050369248997324E231)
            long r4 = r4 & r2
            long r9 = (long) r1
            long r0 = r9 << r0
            long r4 = r4 | r0
            java.util.concurrent.atomic.AtomicLongFieldUpdater r0 = F3.o.f
            r1 = r15
            boolean r0 = r0.compareAndSet(r1, r2, r4)
            if (r0 == 0) goto L3
            r0 = r12 & r11
            r14.set(r0, r7)
            r0 = r6
        L70:
            long r1 = r8.get(r0)
            r3 = 1152921504606846976(0x1000000000000000, double:1.2882297539194267E-231)
            long r1 = r1 & r3
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L9b
            F3.o r0 = r0.c()
            java.util.concurrent.atomic.AtomicReferenceArray r1 = r0.f942d
            int r2 = r0.f941c
            r2 = r2 & r12
            java.lang.Object r5 = r1.get(r2)
            boolean r9 = r5 instanceof F3.o.a
            if (r9 == 0) goto L98
            F3.o$a r5 = (F3.o.a) r5
            int r5 = r5.f943a
            if (r5 != r12) goto L98
            r1.set(r2, r7)
            goto L99
        L98:
            r0 = 0
        L99:
            if (r0 != 0) goto L70
        L9b:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: F3.o.a(java.lang.Object):int");
    }

    public final boolean b() {
        AtomicLongFieldUpdater atomicLongFieldUpdater;
        long j4;
        do {
            atomicLongFieldUpdater = f;
            j4 = atomicLongFieldUpdater.get(this);
            if ((j4 & 2305843009213693952L) != 0) {
                return true;
            }
            if ((1152921504606846976L & j4) != 0) {
                return false;
            }
        } while (!atomicLongFieldUpdater.compareAndSet(this, j4, 2305843009213693952L | j4));
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final o<E> c() {
        AtomicLongFieldUpdater atomicLongFieldUpdater;
        long j4;
        while (true) {
            atomicLongFieldUpdater = f;
            j4 = atomicLongFieldUpdater.get(this);
            if ((j4 & 1152921504606846976L) == 0) {
                long j5 = j4 | 1152921504606846976L;
                if (atomicLongFieldUpdater.compareAndSet(this, j4, j5)) {
                    j4 = j5;
                    break;
                }
            } else {
                break;
            }
        }
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f937e;
            o<E> oVar = (o) atomicReferenceFieldUpdater.get(this);
            if (oVar != null) {
                return oVar;
            }
            o oVar2 = new o(this.f939a * 2, this.f940b);
            int i4 = (int) (1073741823 & j4);
            int i5 = (int) ((1152921503533105152L & j4) >> 30);
            while (true) {
                int i6 = this.f941c;
                int i7 = i4 & i6;
                if (i7 == (i6 & i5)) {
                    break;
                }
                Object obj = this.f942d.get(i7);
                if (obj == null) {
                    obj = new a(i4);
                }
                oVar2.f942d.set(oVar2.f941c & i4, obj);
                i4++;
            }
            atomicLongFieldUpdater.set(oVar2, (-1152921504606846977L) & j4);
            while (!atomicReferenceFieldUpdater.compareAndSet(this, null, oVar2) && atomicReferenceFieldUpdater.get(this) == null) {
            }
        }
    }

    public final Object d() {
        while (true) {
            AtomicLongFieldUpdater atomicLongFieldUpdater = f;
            long j4 = atomicLongFieldUpdater.get(this);
            if ((j4 & 1152921504606846976L) != 0) {
                return f938g;
            }
            int i4 = (int) (j4 & 1073741823);
            int i5 = (int) ((1152921503533105152L & j4) >> 30);
            int i6 = this.f941c;
            int i7 = i4 & i6;
            if ((i5 & i6) == i7) {
                return null;
            }
            AtomicReferenceArray atomicReferenceArray = this.f942d;
            Object obj = atomicReferenceArray.get(i7);
            boolean z4 = this.f940b;
            if (obj == null) {
                if (z4) {
                    return null;
                }
            } else if (obj instanceof a) {
                return null;
            } else {
                long j5 = (i4 + 1) & 1073741823;
                if (atomicLongFieldUpdater.compareAndSet(this, j4, (j4 & (-1073741824)) | j5)) {
                    atomicReferenceArray.set(i7, null);
                    return obj;
                } else if (z4) {
                    o<E> oVar = this;
                    while (true) {
                        AtomicLongFieldUpdater atomicLongFieldUpdater2 = f;
                        long j6 = atomicLongFieldUpdater2.get(oVar);
                        int i8 = (int) (j6 & 1073741823);
                        if ((j6 & 1152921504606846976L) != 0) {
                            oVar = oVar.c();
                        } else {
                            if (atomicLongFieldUpdater2.compareAndSet(oVar, j6, (j6 & (-1073741824)) | j5)) {
                                oVar.f942d.set(oVar.f941c & i8, null);
                                oVar = null;
                            } else {
                                continue;
                            }
                        }
                        if (oVar == null) {
                            return obj;
                        }
                    }
                }
            }
        }
    }
}
