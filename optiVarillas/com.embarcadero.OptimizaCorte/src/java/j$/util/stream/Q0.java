package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.LongFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class Q0 extends S0 {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f4365k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ Q0(AbstractC0521b abstractC0521b, Spliterator spliterator, LongFunction longFunction, BinaryOperator binaryOperator, int i4) {
        super(abstractC0521b, spliterator, longFunction, binaryOperator);
        this.f4365k = i4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.S0, j$.util.stream.AbstractC0536e
    public final /* bridge */ /* synthetic */ Object a() {
        switch (this.f4365k) {
            case 0:
                return a();
            case 1:
                return a();
            case 2:
                return a();
            default:
                return a();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.S0, j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        switch (this.f4365k) {
            case 0:
                return new S0(this, spliterator);
            case 1:
                return new S0(this, spliterator);
            case 2:
                return new S0(this, spliterator);
            default:
                return new S0(this, spliterator);
        }
    }
}
