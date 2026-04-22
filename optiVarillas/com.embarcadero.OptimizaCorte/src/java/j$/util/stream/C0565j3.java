package j$.util.stream;

import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.j3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0565j3 extends AbstractC0570k3 implements LongConsumer {

    /* renamed from: c  reason: collision with root package name */
    final long[] f4531c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0565j3(int i4) {
        this.f4531c = new long[i4];
    }

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        int i4 = this.f4535b;
        this.f4535b = i4 + 1;
        this.f4531c[i4] = j4;
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.AbstractC0570k3
    public final void b(Object obj, long j4) {
        LongConsumer longConsumer = (LongConsumer) obj;
        for (int i4 = 0; i4 < j4; i4++) {
            longConsumer.accept(this.f4531c[i4]);
        }
    }
}
