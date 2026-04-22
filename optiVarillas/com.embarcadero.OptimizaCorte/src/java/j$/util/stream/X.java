package j$.util.stream;

import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class X extends AbstractC0522b0 {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4431m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ Object f4432n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ X(AbstractC0521b abstractC0521b, int i4, Object obj, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4431m = i5;
        this.f4432n = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public X(AbstractC0521b abstractC0521b, IntConsumer intConsumer) {
        super(abstractC0521b, 0, 1);
        this.f4431m = 0;
        this.f4432n = intConsumer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4431m) {
            case 0:
                return new W(this, interfaceC0599q2, 1);
            case 1:
                return new Z(this, interfaceC0599q2);
            case 2:
                return new C0586o(this, interfaceC0599q2, 4);
            default:
                return new C0534d2(this, interfaceC0599q2);
        }
    }
}
