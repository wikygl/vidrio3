package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class R3 extends AbstractC0536e {

    /* renamed from: h  reason: collision with root package name */
    private final AbstractC0521b f4380h;

    /* renamed from: i  reason: collision with root package name */
    private final IntFunction f4381i;

    /* renamed from: j  reason: collision with root package name */
    private final boolean f4382j;

    /* renamed from: k  reason: collision with root package name */
    private long f4383k;

    /* renamed from: l  reason: collision with root package name */
    private long f4384l;

    R3(R3 r32, Spliterator spliterator) {
        super(r32, spliterator);
        this.f4380h = r32.f4380h;
        this.f4381i = r32.f4381i;
        this.f4382j = r32.f4382j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public R3(AbstractC0521b abstractC0521b, AbstractC0521b abstractC0521b2, Spliterator spliterator, IntFunction intFunction) {
        super(abstractC0521b2, spliterator);
        this.f4380h = abstractC0521b;
        this.f4381i = intFunction;
        this.f4382j = EnumC0540e3.ORDERED.r(abstractC0521b2.G());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        boolean z4 = true;
        boolean z5 = !d();
        D0 J3 = this.f4476a.J((z5 && this.f4382j && EnumC0540e3.SIZED.u(this.f4380h.f4450c)) ? this.f4380h.C(this.f4477b) : -1L, this.f4381i);
        Q3 q32 = (Q3) this.f4380h;
        z4 = (this.f4382j && z5) ? false : false;
        q32.getClass();
        P3 p32 = new P3(q32, J3, z4);
        this.f4476a.R(this.f4477b, p32);
        L0 a4 = J3.a();
        this.f4383k = a4.count();
        this.f4384l = p32.f4360b;
        return a4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new R3(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        L0 I2;
        Object c4;
        L0 l0;
        AbstractC0536e abstractC0536e = this.f4479d;
        if (abstractC0536e != null) {
            if (this.f4382j) {
                R3 r32 = (R3) abstractC0536e;
                long j4 = r32.f4384l;
                this.f4384l = j4;
                if (j4 == r32.f4383k) {
                    this.f4384l = j4 + ((R3) this.f4480e).f4384l;
                }
            }
            R3 r33 = (R3) abstractC0536e;
            long j5 = r33.f4383k;
            R3 r34 = (R3) this.f4480e;
            this.f4383k = j5 + r34.f4383k;
            if (r33.f4383k == 0) {
                c4 = r34.c();
            } else if (r34.f4383k == 0) {
                c4 = r33.c();
            } else {
                I2 = AbstractC0637z0.I(this.f4380h.E(), (L0) ((R3) this.f4479d).c(), (L0) ((R3) this.f4480e).c());
                l0 = I2;
                if (d() && this.f4382j) {
                    l0 = l0.h(this.f4384l, l0.count(), this.f4381i);
                }
                f(l0);
            }
            I2 = (L0) c4;
            l0 = I2;
            if (d()) {
                l0 = l0.h(this.f4384l, l0.count(), this.f4381i);
            }
            f(l0);
        }
        super.onCompletion(countedCompleter);
    }
}
