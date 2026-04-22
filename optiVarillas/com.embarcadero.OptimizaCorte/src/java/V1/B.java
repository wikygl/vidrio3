package V1;

import U1.a;
import W1.AbstractC0314b;
import W1.C0316d;
import W1.C0322j;
import W1.C0325m;
import W1.C0326n;
import android.os.SystemClock;
import com.google.android.gms.common.api.Status;
import p2.AbstractC0757f;
import p2.InterfaceC0754c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class B implements InterfaceC0754c {

    /* renamed from: j  reason: collision with root package name */
    public final C0298d f2530j;

    /* renamed from: k  reason: collision with root package name */
    public final int f2531k;

    /* renamed from: l  reason: collision with root package name */
    public final C0295a f2532l;

    /* renamed from: m  reason: collision with root package name */
    public final long f2533m;

    /* renamed from: n  reason: collision with root package name */
    public final long f2534n;

    public B(C0298d c0298d, int i4, C0295a c0295a, long j4, long j5) {
        this.f2530j = c0298d;
        this.f2531k = i4;
        this.f2532l = c0295a;
        this.f2533m = j4;
        this.f2534n = j5;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0031 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0032 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static W1.C0316d a(V1.u r4, W1.AbstractC0314b r5, int r6) {
        /*
            W1.Q r5 = r5.f2703v
            r0 = 0
            if (r5 != 0) goto L7
            r5 = r0
            goto L9
        L7:
            W1.d r5 = r5.f2661m
        L9:
            if (r5 == 0) goto L36
            boolean r1 = r5.f2718k
            if (r1 == 0) goto L36
            int[] r1 = r5.f2720m
            r2 = 0
            if (r1 != 0) goto L24
            int[] r1 = r5.f2722o
            if (r1 != 0) goto L19
            goto L2b
        L19:
            int r3 = r1.length
            if (r2 >= r3) goto L2b
            r3 = r1[r2]
            if (r3 != r6) goto L21
            goto L36
        L21:
            int r2 = r2 + 1
            goto L19
        L24:
            int r3 = r1.length
            if (r2 >= r3) goto L36
            r3 = r1[r2]
            if (r3 != r6) goto L33
        L2b:
            int r4 = r4.f2615u
            int r6 = r5.f2721n
            if (r4 >= r6) goto L32
            return r5
        L32:
            return r0
        L33:
            int r2 = r2 + 1
            goto L24
        L36:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: V1.B.a(V1.u, W1.b, int):W1.d");
    }

    @Override // p2.InterfaceC0754c
    public final void c(AbstractC0757f abstractC0757f) {
        boolean z4;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        long j4;
        long j5;
        int i9;
        boolean z5;
        boolean z6;
        C0298d c0298d = this.f2530j;
        if (c0298d.a()) {
            C0326n c0326n = C0325m.a().f2758a;
            if (c0326n == null || c0326n.f2760k) {
                u uVar = (u) c0298d.f2584s.get(this.f2532l);
                if (uVar != null) {
                    a.e eVar = uVar.f2605k;
                    if (eVar instanceof AbstractC0314b) {
                        AbstractC0314b abstractC0314b = (AbstractC0314b) eVar;
                        long j6 = this.f2533m;
                        int i10 = (j6 > 0L ? 1 : (j6 == 0L ? 0 : -1));
                        if (i10 > 0) {
                            z4 = true;
                        } else {
                            z4 = false;
                        }
                        int i11 = abstractC0314b.f2698q;
                        if (c0326n != null) {
                            z4 &= c0326n.f2761l;
                            if (abstractC0314b.f2703v != null) {
                                z5 = true;
                            } else {
                                z5 = false;
                            }
                            i4 = c0326n.f2762m;
                            i6 = c0326n.f2759j;
                            if (z5 && !abstractC0314b.g()) {
                                C0316d a4 = a(uVar, abstractC0314b, this.f2531k);
                                if (a4 != null) {
                                    if (a4.f2719l && i10 > 0) {
                                        z6 = true;
                                    } else {
                                        z6 = false;
                                    }
                                    i5 = a4.f2721n;
                                    z4 = z6;
                                } else {
                                    return;
                                }
                            } else {
                                i5 = c0326n.f2763n;
                            }
                        } else {
                            i4 = 5000;
                            i5 = 100;
                            i6 = 0;
                        }
                        if (abstractC0757f.k()) {
                            i7 = 0;
                            i8 = 0;
                        } else {
                            if (abstractC0757f.i()) {
                                i7 = 100;
                            } else {
                                Exception g4 = abstractC0757f.g();
                                if (g4 instanceof U1.b) {
                                    Status status = ((U1.b) g4).f2377j;
                                    i7 = status.j;
                                    T1.b bVar = status.m;
                                    if (bVar != null) {
                                        i8 = bVar.f2342k;
                                    }
                                } else {
                                    i7 = 101;
                                }
                            }
                            i8 = -1;
                        }
                        if (z4) {
                            long currentTimeMillis = System.currentTimeMillis();
                            i9 = (int) (SystemClock.elapsedRealtime() - this.f2534n);
                            j4 = j6;
                            j5 = currentTimeMillis;
                        } else {
                            j4 = 0;
                            j5 = 0;
                            i9 = -1;
                        }
                        C c4 = new C(new C0322j(this.f2531k, i7, i8, j4, j5, null, null, i11, i9), i6, i4, i5);
                        g2.g gVar = c0298d.f2587v;
                        gVar.sendMessage(gVar.obtainMessage(18, c4));
                    }
                }
            }
        }
    }
}
