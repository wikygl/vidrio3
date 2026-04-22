package j$.util.stream;

import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class E0 implements DoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4268a;

    public /* synthetic */ E0(int i4) {
        this.f4268a = i4;
    }

    private final void accept$j$$util$stream$Node$OfDouble$$ExternalSyntheticLambda0(double d4) {
    }

    private final void accept$j$$util$stream$StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0(double d4) {
    }

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        int i4 = this.f4268a;
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        switch (this.f4268a) {
            case 0:
                return j$.com.android.tools.r8.a.e(this, doubleConsumer);
            default:
                return j$.com.android.tools.r8.a.e(this, doubleConsumer);
        }
    }
}
