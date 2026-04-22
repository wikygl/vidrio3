package j$.util.stream;

import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class D1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    public final /* synthetic */ int f4264h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ Object f4265i;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ D1(EnumC0545f3 enumC0545f3, Object obj, int i4) {
        super(enumC0545f3);
        this.f4264h = i4;
        this.f4265i = obj;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        switch (this.f4264h) {
            case 0:
                return new U1((LongBinaryOperator) this.f4265i);
            case 1:
                return new G1((DoubleBinaryOperator) this.f4265i);
            case 2:
                return new L1((BinaryOperator) this.f4265i);
            default:
                return new R1((IntBinaryOperator) this.f4265i);
        }
    }
}
