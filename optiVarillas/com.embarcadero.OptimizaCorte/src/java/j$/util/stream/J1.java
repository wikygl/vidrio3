package j$.util.stream;

import java.util.function.DoubleBinaryOperator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class J1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    final /* synthetic */ DoubleBinaryOperator f4310h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ double f4311i;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J1(EnumC0545f3 enumC0545f3, DoubleBinaryOperator doubleBinaryOperator, double d4) {
        super(enumC0545f3);
        this.f4310h = doubleBinaryOperator;
        this.f4311i = d4;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        return new E1(this.f4311i, this.f4310h);
    }
}
