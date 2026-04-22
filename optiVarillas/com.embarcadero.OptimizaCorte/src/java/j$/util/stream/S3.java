package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class S3 extends AbstractC0526c {

    /* renamed from: j  reason: collision with root package name */
    private final AbstractC0521b f4393j;

    /* renamed from: k  reason: collision with root package name */
    private final IntFunction f4394k;

    /* renamed from: l  reason: collision with root package name */
    private final boolean f4395l;

    /* renamed from: m  reason: collision with root package name */
    private long f4396m;

    /* renamed from: n  reason: collision with root package name */
    private boolean f4397n;

    /* renamed from: o  reason: collision with root package name */
    private volatile boolean f4398o;

    S3(S3 s32, Spliterator spliterator) {
        super(s32, spliterator);
        this.f4393j = s32.f4393j;
        this.f4394k = s32.f4394k;
        this.f4395l = s32.f4395l;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public S3(AbstractC0521b abstractC0521b, AbstractC0521b abstractC0521b2, Spliterator spliterator, IntFunction intFunction) {
        super(abstractC0521b2, spliterator);
        this.f4393j = abstractC0521b;
        this.f4394k = intFunction;
        this.f4395l = EnumC0540e3.ORDERED.r(abstractC0521b2.G());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        D0 J3 = this.f4476a.J(-1L, this.f4394k);
        InterfaceC0599q2 N3 = this.f4393j.N(this.f4476a.G(), J3);
        AbstractC0521b abstractC0521b = this.f4476a;
        boolean x4 = abstractC0521b.x(this.f4477b, abstractC0521b.S(N3));
        this.f4397n = x4;
        if (x4) {
            i();
        }
        L0 a4 = J3.a();
        this.f4396m = a4.count();
        return a4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new S3(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final void h() {
        this.f4462i = true;
        if (this.f4395l && this.f4398o) {
            f(AbstractC0637z0.L(this.f4393j.E()));
        }
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final Object j() {
        return AbstractC0637z0.L(this.f4393j.E());
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        Object I2;
        Object c4;
        AbstractC0536e abstractC0536e = this.f4479d;
        if (abstractC0536e != null) {
            this.f4397n = ((S3) abstractC0536e).f4397n | ((S3) this.f4480e).f4397n;
            if (this.f4395l && this.f4462i) {
                this.f4396m = 0L;
                I2 = AbstractC0637z0.L(this.f4393j.E());
            } else {
                if (this.f4395l) {
                    S3 s32 = (S3) this.f4479d;
                    if (s32.f4397n) {
                        this.f4396m = s32.f4396m;
                        I2 = (L0) s32.c();
                    }
                }
                S3 s33 = (S3) this.f4479d;
                long j4 = s33.f4396m;
                S3 s34 = (S3) this.f4480e;
                this.f4396m = j4 + s34.f4396m;
                if (s33.f4396m == 0) {
                    c4 = s34.c();
                } else if (s34.f4396m == 0) {
                    c4 = s33.c();
                } else {
                    I2 = AbstractC0637z0.I(this.f4393j.E(), (L0) ((S3) this.f4479d).c(), (L0) ((S3) this.f4480e).c());
                }
                I2 = (L0) c4;
            }
            f(I2);
        }
        this.f4398o = true;
        super.onCompletion(countedCompleter);
    }
}
