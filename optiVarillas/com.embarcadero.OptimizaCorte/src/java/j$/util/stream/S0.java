package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.BinaryOperator;
import java.util.function.LongFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class S0 extends AbstractC0536e {

    /* renamed from: h  reason: collision with root package name */
    protected final AbstractC0521b f4386h;

    /* renamed from: i  reason: collision with root package name */
    protected final LongFunction f4387i;

    /* renamed from: j  reason: collision with root package name */
    protected final BinaryOperator f4388j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public S0(S0 s02, Spliterator spliterator) {
        super(s02, spliterator);
        this.f4386h = s02.f4386h;
        this.f4387i = s02.f4387i;
        this.f4388j = s02.f4388j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public S0(AbstractC0521b abstractC0521b, Spliterator spliterator, LongFunction longFunction, BinaryOperator binaryOperator) {
        super(abstractC0521b, spliterator);
        this.f4386h = abstractC0521b;
        this.f4387i = longFunction;
        this.f4388j = binaryOperator;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public AbstractC0536e e(Spliterator spliterator) {
        return new S0(this, spliterator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    /* renamed from: h */
    public final L0 a() {
        D0 d02 = (D0) this.f4387i.apply(this.f4386h.C(this.f4477b));
        this.f4386h.R(this.f4477b, d02);
        return d02.a();
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        AbstractC0536e abstractC0536e = this.f4479d;
        if (abstractC0536e != null) {
            f((L0) this.f4388j.apply((L0) ((S0) abstractC0536e).c(), (L0) ((S0) this.f4480e).c()));
        }
        super.onCompletion(countedCompleter);
    }
}
