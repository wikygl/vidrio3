package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class A2 extends AbstractC0526c {

    /* renamed from: j  reason: collision with root package name */
    private final AbstractC0521b f4248j;

    /* renamed from: k  reason: collision with root package name */
    private final IntFunction f4249k;

    /* renamed from: l  reason: collision with root package name */
    private final long f4250l;

    /* renamed from: m  reason: collision with root package name */
    private final long f4251m;

    /* renamed from: n  reason: collision with root package name */
    private long f4252n;

    /* renamed from: o  reason: collision with root package name */
    private volatile boolean f4253o;

    A2(A2 a22, Spliterator spliterator) {
        super(a22, spliterator);
        this.f4248j = a22.f4248j;
        this.f4249k = a22.f4249k;
        this.f4250l = a22.f4250l;
        this.f4251m = a22.f4251m;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A2(AbstractC0521b abstractC0521b, AbstractC0521b abstractC0521b2, Spliterator spliterator, IntFunction intFunction, long j4, long j5) {
        super(abstractC0521b2, spliterator);
        this.f4248j = abstractC0521b;
        this.f4249k = intFunction;
        this.f4250l = j4;
        this.f4251m = j5;
    }

    private long k(long j4) {
        if (this.f4253o) {
            return this.f4252n;
        }
        A2 a22 = (A2) this.f4479d;
        A2 a23 = (A2) this.f4480e;
        if (a22 == null || a23 == null) {
            return this.f4252n;
        }
        long k4 = a22.k(j4);
        return k4 >= j4 ? k4 : k4 + a23.k(j4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        if (d()) {
            D0 J3 = this.f4248j.J(EnumC0540e3.SIZED.u(this.f4248j.f4450c) ? this.f4248j.C(this.f4477b) : -1L, this.f4249k);
            InterfaceC0599q2 N3 = this.f4248j.N(this.f4476a.G(), J3);
            AbstractC0521b abstractC0521b = this.f4476a;
            abstractC0521b.x(this.f4477b, abstractC0521b.S(N3));
            return J3.a();
        }
        D0 J4 = this.f4248j.J(-1L, this.f4249k);
        if (this.f4250l == 0) {
            InterfaceC0599q2 N4 = this.f4248j.N(this.f4476a.G(), J4);
            AbstractC0521b abstractC0521b2 = this.f4476a;
            abstractC0521b2.x(this.f4477b, abstractC0521b2.S(N4));
        } else {
            this.f4476a.R(this.f4477b, J4);
        }
        L0 a4 = J4.a();
        this.f4252n = a4.count();
        this.f4253o = true;
        this.f4477b = null;
        return a4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new A2(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final void h() {
        this.f4462i = true;
        if (this.f4253o) {
            f(AbstractC0637z0.L(this.f4248j.E()));
        }
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final Object j() {
        return AbstractC0637z0.L(this.f4248j.E());
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x00df, code lost:
        if (r2 >= r0) goto L51;
     */
    /* JADX WARN: Removed duplicated region for block: B:22:0x006c  */
    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onCompletion(java.util.concurrent.CountedCompleter r14) {
        /*
            Method dump skipped, instructions count: 232
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.A2.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
