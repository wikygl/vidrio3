package j$.util.function;

import java.util.function.Predicate;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class f implements Predicate {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4219a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Predicate f4220b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Predicate f4221c;

    public /* synthetic */ f(Predicate predicate, Predicate predicate2, int i4) {
        this.f4219a = i4;
        this.f4220b = predicate;
        this.f4221c = predicate2;
    }

    @Override // java.util.function.Predicate
    public final /* synthetic */ Predicate and(Predicate predicate) {
        switch (this.f4219a) {
            case 0:
                return j$.com.android.tools.r8.a.a(this, predicate);
            default:
                return j$.com.android.tools.r8.a.a(this, predicate);
        }
    }

    @Override // java.util.function.Predicate
    public final Predicate negate() {
        switch (this.f4219a) {
            case 0:
                return new a(this, 2);
            default:
                return new a(this, 2);
        }
    }

    @Override // java.util.function.Predicate
    public final /* synthetic */ Predicate or(Predicate predicate) {
        switch (this.f4219a) {
            case 0:
                return j$.com.android.tools.r8.a.h(this, predicate);
            default:
                return j$.com.android.tools.r8.a.h(this, predicate);
        }
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        switch (this.f4219a) {
            case 0:
                return this.f4220b.test(obj) && this.f4221c.test(obj);
            default:
                return this.f4220b.test(obj) || this.f4221c.test(obj);
        }
    }
}
