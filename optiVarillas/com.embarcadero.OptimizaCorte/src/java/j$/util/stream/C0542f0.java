package j$.util.stream;

import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.f0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0542f0 extends AbstractC0574l2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4504b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AbstractC0521b f4505c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0542f0(AbstractC0521b abstractC0521b, InterfaceC0599q2 interfaceC0599q2, int i4) {
        super(interfaceC0599q2);
        this.f4504b = i4;
        this.f4505c = abstractC0521b;
    }

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        switch (this.f4504b) {
            case 0:
                this.f4537a.accept((InterfaceC0599q2) ((LongFunction) ((C0615u) this.f4505c).f4593n).apply(j4));
                return;
            case 1:
                ((C0628x) this.f4505c).getClass();
                LongUnaryOperator longUnaryOperator = null;
                longUnaryOperator.applyAsLong(j4);
                throw null;
            case 2:
                ((C0624w) this.f4505c).getClass();
                LongToIntFunction longToIntFunction = null;
                longToIntFunction.applyAsInt(j4);
                throw null;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((C0620v) this.f4505c).getClass();
                LongToDoubleFunction longToDoubleFunction = null;
                longToDoubleFunction.applyAsDouble(j4);
                throw null;
            case 4:
                ((C0628x) this.f4505c).getClass();
                LongPredicate longPredicate = null;
                longPredicate.test(j4);
                throw null;
            default:
                ((LongConsumer) ((C0557i0) this.f4505c).f4527n).accept(j4);
                this.f4537a.accept(j4);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public void l(long j4) {
        switch (this.f4504b) {
            case 4:
                this.f4537a.l(-1L);
                return;
            default:
                super.l(j4);
                return;
        }
    }
}
