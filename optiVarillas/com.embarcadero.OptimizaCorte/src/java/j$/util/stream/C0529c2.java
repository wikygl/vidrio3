package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.c2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0529c2 extends AbstractC0536e {

    /* renamed from: h  reason: collision with root package name */
    private final AbstractC0637z0 f4463h;

    C0529c2(C0529c2 c0529c2, Spliterator spliterator) {
        super(c0529c2, spliterator);
        this.f4463h = c0529c2.f4463h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0529c2(AbstractC0637z0 abstractC0637z0, AbstractC0521b abstractC0521b, Spliterator spliterator) {
        super(abstractC0521b, spliterator);
        this.f4463h = abstractC0637z0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        AbstractC0521b abstractC0521b = this.f4476a;
        V1 f02 = this.f4463h.f0();
        abstractC0521b.R(this.f4477b, f02);
        return f02;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new C0529c2(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        AbstractC0536e abstractC0536e = this.f4479d;
        if (abstractC0536e != null) {
            V1 v12 = (V1) ((C0529c2) abstractC0536e).c();
            v12.g((V1) ((C0529c2) this.f4480e).c());
            f(v12);
        }
        super.onCompletion(countedCompleter);
    }
}
