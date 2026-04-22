package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.y2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0635y2 extends B {

    /* renamed from: m  reason: collision with root package name */
    final /* synthetic */ long f4627m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ long f4628n;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0635y2(AbstractC0521b abstractC0521b, int i4, long j4, long j5) {
        super(abstractC0521b, i4, 0);
        this.f4627m = j4;
        this.f4628n = j5;
    }

    /* JADX WARN: Type inference failed for: r13v5, types: [j$.util.Spliterator, j$.util.stream.G3] */
    @Override // j$.util.stream.AbstractC0521b
    final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        long j4;
        long j5;
        long C4 = abstractC0521b.C(spliterator);
        if (C4 <= 0 || !spliterator.hasCharacteristics(16384)) {
            if (EnumC0540e3.ORDERED.r(abstractC0521b.G())) {
                return (L0) new A2(this, abstractC0521b, spliterator, intFunction, this.f4627m, this.f4628n).invoke();
            }
            j$.util.G g4 = (j$.util.G) abstractC0521b.T(spliterator);
            long j6 = this.f4627m;
            long j7 = this.f4628n;
            if (j6 <= C4) {
                j4 = j7 >= 0 ? Math.min(j7, C4 - j6) : C4 - j6;
                j5 = 0;
            } else {
                j4 = j7;
                j5 = j6;
            }
            return AbstractC0637z0.F(this, new G3(g4, j5, j4), true);
        }
        return AbstractC0637z0.F(abstractC0521b, AbstractC0637z0.C(abstractC0521b.F(), spliterator, this.f4627m, this.f4628n), true);
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [j$.util.Spliterator, j$.util.stream.G3] */
    @Override // j$.util.stream.AbstractC0521b
    final Spliterator L(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        long j4;
        long j5;
        long C4 = abstractC0521b.C(spliterator);
        long j6 = this.f4628n;
        if (C4 > 0 && spliterator.hasCharacteristics(16384)) {
            long j7 = this.f4627m;
            return new x3((j$.util.G) abstractC0521b.T(spliterator), j7, AbstractC0637z0.B(j7, j6));
        }
        if (EnumC0540e3.ORDERED.r(abstractC0521b.G())) {
            return ((L0) new A2(this, abstractC0521b, spliterator, new C0537e0(7), this.f4627m, this.f4628n).invoke()).spliterator();
        }
        j$.util.G g4 = (j$.util.G) abstractC0521b.T(spliterator);
        long j8 = this.f4627m;
        if (j8 <= C4) {
            long j9 = C4 - j8;
            if (j6 >= 0) {
                j9 = Math.min(j6, j9);
            }
            j4 = j9;
            j5 = 0;
        } else {
            j4 = j6;
            j5 = j8;
        }
        return new G3(g4, j5, j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        return new C0631x2(this, interfaceC0599q2);
    }
}
