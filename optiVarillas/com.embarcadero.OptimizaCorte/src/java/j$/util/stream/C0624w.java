package j$.util.stream;

import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0624w extends AbstractC0522b0 {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4604m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0624w(AbstractC0521b abstractC0521b, int i4, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4604m = i5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4604m) {
            case 0:
                return new C0610t(this, interfaceC0599q2, 2);
            case 1:
                return new W(this, interfaceC0599q2, 2);
            case 2:
                return interfaceC0599q2;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return new W(this, interfaceC0599q2, 5);
            default:
                return new C0542f0(this, interfaceC0599q2, 2);
        }
    }
}
