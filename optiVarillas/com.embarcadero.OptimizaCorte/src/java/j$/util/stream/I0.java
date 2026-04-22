package j$.util.stream;

import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class I0 implements LongConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4302a;

    public /* synthetic */ I0(int i4) {
        this.f4302a = i4;
    }

    private final void accept$j$$util$stream$Node$OfLong$$ExternalSyntheticLambda0(long j4) {
    }

    private final void accept$j$$util$stream$StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0(long j4) {
    }

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        int i4 = this.f4302a;
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        switch (this.f4302a) {
            case 0:
                return j$.com.android.tools.r8.a.g(this, longConsumer);
            default:
                return j$.com.android.tools.r8.a.g(this, longConsumer);
        }
    }
}
