package j$.util.stream;

import java.util.function.IntBinaryOperator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Q1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    final /* synthetic */ IntBinaryOperator f4366h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ int f4367i;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Q1(EnumC0545f3 enumC0545f3, IntBinaryOperator intBinaryOperator, int i4) {
        super(enumC0545f3);
        this.f4366h = intBinaryOperator;
        this.f4367i = i4;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        return new P1(this.f4367i, this.f4366h);
    }
}
