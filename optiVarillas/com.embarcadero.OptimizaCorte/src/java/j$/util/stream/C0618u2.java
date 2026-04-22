package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.u2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0618u2 extends AbstractC0522b0 {

    /* renamed from: m  reason: collision with root package name */
    final /* synthetic */ long f4595m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ long f4596n;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0618u2(AbstractC0521b abstractC0521b, int i4, long j4, long j5) {
        super(abstractC0521b, i4, 0);
        this.f4595m = j4;
        this.f4596n = j5;
    }

    /* JADX WARN: Type inference failed for: r13v5, types: [j$.util.Spliterator, j$.util.stream.G3] */
    @Override // j$.util.stream.AbstractC0521b
    final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        long j4;
        long j5;
        long C4 = abstractC0521b.C(spliterator);
        if (C4 <= 0 || !spliterator.hasCharacteristics(16384)) {
            if (EnumC0540e3.ORDERED.r(abstractC0521b.G())) {
                return (L0) new A2(this, abstractC0521b, spliterator, intFunction, this.f4595m, this.f4596n).invoke();
            }
            j$.util.J j6 = (j$.util.J) abstractC0521b.T(spliterator);
            long j7 = this.f4595m;
            long j8 = this.f4596n;
            if (j7 <= C4) {
                j4 = j8 >= 0 ? Math.min(j8, C4 - j7) : C4 - j7;
                j5 = 0;
            } else {
                j4 = j8;
                j5 = j7;
            }
            return AbstractC0637z0.G(this, new G3(j6, j5, j4), true);
        }
        return AbstractC0637z0.G(abstractC0521b, AbstractC0637z0.C(abstractC0521b.F(), spliterator, this.f4595m, this.f4596n), true);
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [j$.util.Spliterator, j$.util.stream.G3] */
    @Override // j$.util.stream.AbstractC0521b
    final Spliterator L(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        long j4;
        long j5;
        long C4 = abstractC0521b.C(spliterator);
        long j6 = this.f4596n;
        if (C4 > 0 && spliterator.hasCharacteristics(16384)) {
            long j7 = this.f4595m;
            return new x3((j$.util.J) abstractC0521b.T(spliterator), j7, AbstractC0637z0.B(j7, j6));
        }
        if (EnumC0540e3.ORDERED.r(abstractC0521b.G())) {
            return ((L0) new A2(this, abstractC0521b, spliterator, new C0537e0(5), this.f4595m, this.f4596n).invoke()).spliterator();
        }
        j$.util.J j8 = (j$.util.J) abstractC0521b.T(spliterator);
        long j9 = this.f4595m;
        if (j9 <= C4) {
            long j10 = C4 - j9;
            if (j6 >= 0) {
                j10 = Math.min(j6, j10);
            }
            j4 = j10;
            j5 = 0;
        } else {
            j4 = j6;
            j5 = j9;
        }
        return new G3(j8, j5, j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        return new C0613t2(this, interfaceC0599q2);
    }
}
