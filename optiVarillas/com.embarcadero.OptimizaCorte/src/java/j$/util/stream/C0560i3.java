package j$.util.stream;

import java.util.function.IntConsumer;

/* renamed from: j$.util.stream.i3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0560i3 extends AbstractC0570k3 implements IntConsumer {

    /* renamed from: c  reason: collision with root package name */
    final int[] f4528c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0560i3(int i4) {
        this.f4528c = new int[i4];
    }

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        int i5 = this.f4535b;
        this.f4535b = i5 + 1;
        this.f4528c[i5] = i4;
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.AbstractC0570k3
    public final void b(Object obj, long j4) {
        IntConsumer intConsumer = (IntConsumer) obj;
        for (int i4 = 0; i4 < j4; i4++) {
            intConsumer.accept(this.f4528c[i4]);
        }
    }
}
