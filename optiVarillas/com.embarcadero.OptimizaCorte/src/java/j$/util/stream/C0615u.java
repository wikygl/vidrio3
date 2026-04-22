package j$.util.stream;

import java.util.function.Consumer;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.u  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0615u extends AbstractC0554h2 {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4592m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ Object f4593n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0615u(AbstractC0521b abstractC0521b, int i4, Object obj, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4592m = i5;
        this.f4593n = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0615u(AbstractC0521b abstractC0521b, Consumer consumer) {
        super(abstractC0521b, 0, 1);
        this.f4592m = 3;
        this.f4593n = consumer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4592m) {
            case 0:
                return new C0610t(this, interfaceC0599q2, 0);
            case 1:
                return new W(this, interfaceC0599q2, 0);
            case 2:
                return new C0542f0(this, interfaceC0599q2, 0);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return new C0586o(this, interfaceC0599q2, 1);
            default:
                return new C0586o(this, interfaceC0599q2, 2);
        }
    }
}
