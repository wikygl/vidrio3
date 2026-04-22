package j$.util.stream;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class M1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4329h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ BiConsumer f4330i;

    /* renamed from: j  reason: collision with root package name */
    final /* synthetic */ Supplier f4331j;

    /* renamed from: k  reason: collision with root package name */
    final /* synthetic */ Collector f4332k;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M1(EnumC0545f3 enumC0545f3, BinaryOperator binaryOperator, BiConsumer biConsumer, Supplier supplier, Collector collector) {
        super(enumC0545f3);
        this.f4329h = binaryOperator;
        this.f4330i = biConsumer;
        this.f4331j = supplier;
        this.f4332k = collector;
    }

    @Override // j$.util.stream.AbstractC0637z0, j$.util.stream.K3
    public final int d() {
        if (this.f4332k.characteristics().contains(EnumC0556i.UNORDERED)) {
            return EnumC0540e3.f4493r;
        }
        return 0;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        return new N1(this.f4331j, this.f4330i, this.f4329h);
    }
}
