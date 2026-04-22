package i2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class X implements Z {

    /* renamed from: l  reason: collision with root package name */
    public static final Object f3704l = new Object();

    /* renamed from: j  reason: collision with root package name */
    public volatile Z f3705j;

    /* renamed from: k  reason: collision with root package name */
    public volatile Object f3706k;

    /* JADX WARN: Type inference failed for: r0v1, types: [i2.X, java.lang.Object] */
    public static X b(Z z4) {
        if (z4 instanceof X) {
            return (X) z4;
        }
        ?? obj = new Object();
        obj.f3706k = f3704l;
        obj.f3705j = z4;
        return obj;
    }

    @Override // i2.Z
    public final Object a() {
        Object obj = this.f3706k;
        Object obj2 = f3704l;
        if (obj == obj2) {
            synchronized (this) {
                try {
                    obj = this.f3706k;
                    if (obj == obj2) {
                        obj = this.f3705j.a();
                        Object obj3 = this.f3706k;
                        if (obj3 != obj2 && obj3 != obj) {
                            throw new IllegalStateException("Scoped provider was invoked recursively returning different results: " + obj3 + " & " + obj + ". This is likely due to a circular dependency.");
                        }
                        this.f3706k = obj;
                        this.f3705j = null;
                    }
                } finally {
                }
            }
        }
        return obj;
    }
}
