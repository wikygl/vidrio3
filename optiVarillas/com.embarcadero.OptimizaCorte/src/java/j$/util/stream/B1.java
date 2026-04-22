package j$.util.stream;

import java.util.function.LongBinaryOperator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class B1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    final /* synthetic */ LongBinaryOperator f4255h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ long f4256i;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public B1(EnumC0545f3 enumC0545f3, LongBinaryOperator longBinaryOperator, long j4) {
        super(enumC0545f3);
        this.f4255h = longBinaryOperator;
        this.f4256i = j4;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        return new T1(this.f4256i, this.f4255h);
    }
}
