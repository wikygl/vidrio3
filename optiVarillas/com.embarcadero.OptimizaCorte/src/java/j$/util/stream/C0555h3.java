package j$.util.stream;

import java.util.function.DoubleConsumer;

/* renamed from: j$.util.stream.h3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0555h3 extends AbstractC0570k3 implements DoubleConsumer {

    /* renamed from: c  reason: collision with root package name */
    final double[] f4524c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0555h3(int i4) {
        this.f4524c = new double[i4];
    }

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        int i4 = this.f4535b;
        this.f4535b = i4 + 1;
        this.f4524c[i4] = d4;
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0570k3
    public final void b(Object obj, long j4) {
        DoubleConsumer doubleConsumer = (DoubleConsumer) obj;
        for (int i4 = 0; i4 < j4; i4++) {
            doubleConsumer.accept(this.f4524c[i4]);
        }
    }
}
