package j$.util.stream;

import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.z  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0636z extends B {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4629m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ Object f4630n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0636z(AbstractC0521b abstractC0521b, int i4, Object obj, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4629m = i5;
        this.f4630n = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0636z(AbstractC0521b abstractC0521b, DoubleConsumer doubleConsumer) {
        super(abstractC0521b, 0, 1);
        this.f4629m = 1;
        this.f4630n = doubleConsumer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4629m) {
            case 0:
                return new C0632y(this, interfaceC0599q2);
            case 1:
                return new C0610t(this, interfaceC0599q2, 5);
            case 2:
                return new C0586o(this, interfaceC0599q2, 6);
            default:
                return new C0534d2(this, interfaceC0599q2);
        }
    }
}
