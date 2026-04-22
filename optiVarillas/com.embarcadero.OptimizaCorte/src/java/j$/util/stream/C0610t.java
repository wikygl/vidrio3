package j$.util.stream;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0610t extends AbstractC0564j2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4586b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AbstractC0521b f4587c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0610t(AbstractC0521b abstractC0521b, InterfaceC0599q2 interfaceC0599q2, int i4) {
        super(interfaceC0599q2);
        this.f4586b = i4;
        this.f4587c = abstractC0521b;
    }

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        switch (this.f4586b) {
            case 0:
                this.f4530a.accept((InterfaceC0599q2) ((DoubleFunction) ((C0615u) this.f4587c).f4593n).apply(d4));
                return;
            case 1:
                ((C0620v) this.f4587c).getClass();
                DoubleUnaryOperator doubleUnaryOperator = null;
                doubleUnaryOperator.applyAsDouble(d4);
                throw null;
            case 2:
                ((C0624w) this.f4587c).getClass();
                DoubleToIntFunction doubleToIntFunction = null;
                doubleToIntFunction.applyAsInt(d4);
                throw null;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((C0628x) this.f4587c).getClass();
                DoubleToLongFunction doubleToLongFunction = null;
                doubleToLongFunction.applyAsLong(d4);
                throw null;
            case 4:
                ((C0620v) this.f4587c).getClass();
                DoublePredicate doublePredicate = null;
                doublePredicate.test(d4);
                throw null;
            default:
                ((DoubleConsumer) ((C0636z) this.f4587c).f4630n).accept(d4);
                this.f4530a.accept(d4);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public void l(long j4) {
        switch (this.f4586b) {
            case 4:
                this.f4530a.l(-1L);
                return;
            default:
                super.l(j4);
                return;
        }
    }
}
