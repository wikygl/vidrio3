package j$.util.stream;

import java.util.function.Function;

/* renamed from: j$.util.stream.f2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0544f2 extends AbstractC0554h2 {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4506m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ Function f4507n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0544f2(AbstractC0521b abstractC0521b, int i4, Function function, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4506m = i5;
        this.f4507n = function;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4506m) {
            case 0:
                return new C0586o(this, interfaceC0599q2, 3);
            default:
                return new C0581n(this, interfaceC0599q2);
        }
    }
}
