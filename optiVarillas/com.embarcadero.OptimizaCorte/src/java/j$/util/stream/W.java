package j$.util.stream;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class W extends AbstractC0569k2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4427b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AbstractC0521b f4428c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ W(AbstractC0521b abstractC0521b, InterfaceC0599q2 interfaceC0599q2, int i4) {
        super(interfaceC0599q2);
        this.f4427b = i4;
        this.f4428c = abstractC0521b;
    }

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        switch (this.f4427b) {
            case 0:
                this.f4534a.accept((InterfaceC0599q2) ((IntFunction) ((C0615u) this.f4428c).f4593n).apply(i4));
                return;
            case 1:
                ((IntConsumer) ((X) this.f4428c).f4432n).accept(i4);
                this.f4534a.accept(i4);
                return;
            case 2:
                ((C0624w) this.f4428c).getClass();
                IntUnaryOperator intUnaryOperator = null;
                intUnaryOperator.applyAsInt(i4);
                throw null;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((C0628x) this.f4428c).getClass();
                IntToLongFunction intToLongFunction = null;
                intToLongFunction.applyAsLong(i4);
                throw null;
            case 4:
                ((C0620v) this.f4428c).getClass();
                IntToDoubleFunction intToDoubleFunction = null;
                intToDoubleFunction.applyAsDouble(i4);
                throw null;
            default:
                ((C0624w) this.f4428c).getClass();
                IntPredicate intPredicate = null;
                intPredicate.test(i4);
                throw null;
        }
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public void l(long j4) {
        switch (this.f4427b) {
            case 5:
                this.f4534a.l(-1L);
                return;
            default:
                super.l(j4);
                return;
        }
    }
}
