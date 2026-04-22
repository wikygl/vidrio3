package j$.util.stream;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0586o extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4555b;

    /* renamed from: c  reason: collision with root package name */
    Object f4556c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0586o(AbstractC0521b abstractC0521b, InterfaceC0599q2 interfaceC0599q2, int i4) {
        super(interfaceC0599q2);
        this.f4555b = i4;
        this.f4556c = abstractC0521b;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0586o(InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4555b = 0;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        switch (this.f4555b) {
            case 0:
                if (((HashSet) this.f4556c).contains(obj)) {
                    return;
                }
                ((HashSet) this.f4556c).add(obj);
                this.f4545a.accept((InterfaceC0599q2) obj);
                return;
            case 1:
                ((Consumer) ((C0615u) this.f4556c).f4593n).accept(obj);
                this.f4545a.accept((InterfaceC0599q2) obj);
                return;
            case 2:
                if (((Predicate) ((C0615u) this.f4556c).f4593n).test(obj)) {
                    this.f4545a.accept((InterfaceC0599q2) obj);
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                this.f4545a.accept((InterfaceC0599q2) ((C0544f2) this.f4556c).f4507n.apply(obj));
                return;
            case 4:
                this.f4545a.accept(((ToIntFunction) ((X) this.f4556c).f4432n).applyAsInt(obj));
                return;
            case 5:
                this.f4545a.accept(((ToLongFunction) ((C0557i0) this.f4556c).f4527n).applyAsLong(obj));
                return;
            default:
                this.f4545a.accept(((ToDoubleFunction) ((C0636z) this.f4556c).f4630n).applyAsDouble(obj));
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public void k() {
        switch (this.f4555b) {
            case 0:
                this.f4556c = null;
                this.f4545a.k();
                return;
            default:
                super.k();
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public void l(long j4) {
        switch (this.f4555b) {
            case 0:
                this.f4556c = new HashSet();
                this.f4545a.l(-1L);
                return;
            case 1:
            default:
                super.l(j4);
                return;
            case 2:
                this.f4545a.l(-1L);
                return;
        }
    }
}
