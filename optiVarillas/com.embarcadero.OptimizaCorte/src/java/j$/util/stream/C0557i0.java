package j$.util.stream;

import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.i0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0557i0 extends AbstractC0567k0 {

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ int f4526m;

    /* renamed from: n  reason: collision with root package name */
    final /* synthetic */ Object f4527n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0557i0(AbstractC0521b abstractC0521b, int i4, Object obj, int i5) {
        super(abstractC0521b, i4, 1);
        this.f4526m = i5;
        this.f4527n = obj;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0557i0(AbstractC0521b abstractC0521b, LongConsumer longConsumer) {
        super(abstractC0521b, 0, 1);
        this.f4526m = 1;
        this.f4527n = longConsumer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        switch (this.f4526m) {
            case 0:
                return new C0552h0(this, interfaceC0599q2);
            case 1:
                return new C0542f0(this, interfaceC0599q2, 5);
            case 2:
                return new C0534d2(this, interfaceC0599q2);
            default:
                return new C0586o(this, interfaceC0599q2, 5);
        }
    }
}
