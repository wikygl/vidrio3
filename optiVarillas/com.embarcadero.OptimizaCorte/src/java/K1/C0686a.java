package k1;

/* renamed from: k1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0686a<T> implements k3.a<T> {

    /* renamed from: l  reason: collision with root package name */
    public static final Object f4928l = new Object();

    /* renamed from: j  reason: collision with root package name */
    public volatile k3.a<T> f4929j;

    /* renamed from: k  reason: collision with root package name */
    public volatile Object f4930k;

    /* JADX WARN: Type inference failed for: r0v1, types: [k1.a, k3.a<T>, java.lang.Object] */
    public static <P extends k3.a<T>, T> k3.a<T> a(P p4) {
        if (p4 instanceof C0686a) {
            return p4;
        }
        ?? r02 = (k3.a<T>) new Object();
        r02.f4930k = f4928l;
        r02.f4929j = p4;
        return r02;
    }

    @Override // k3.a
    public final T get() {
        T t3 = (T) this.f4930k;
        Object obj = f4928l;
        if (t3 == obj) {
            synchronized (this) {
                try {
                    t3 = this.f4930k;
                    if (t3 == obj) {
                        t3 = this.f4929j.get();
                        Object obj2 = this.f4930k;
                        if (obj2 != obj && obj2 != t3) {
                            throw new IllegalStateException("Scoped provider was invoked recursively returning different results: " + obj2 + " & " + t3 + ". This is likely due to a circular dependency.");
                        }
                        this.f4930k = t3;
                        this.f4929j = null;
                    }
                } finally {
                }
            }
        }
        return t3;
    }
}
