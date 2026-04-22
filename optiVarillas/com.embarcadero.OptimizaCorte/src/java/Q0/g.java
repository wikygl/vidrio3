package Q0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class g {

    /* renamed from: a  reason: collision with root package name */
    public final com.google.android.gms.internal.play_billing.h f1967a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public com.google.android.gms.internal.play_billing.h f1968a;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final String f1969a;

        /* renamed from: b  reason: collision with root package name */
        public final String f1970b;

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
        public static class a {

            /* renamed from: a  reason: collision with root package name */
            public String f1971a;

            /* renamed from: b  reason: collision with root package name */
            public String f1972b;

            public final b a() {
                if (!"first_party".equals(this.f1972b)) {
                    if (this.f1971a != null) {
                        if (this.f1972b != null) {
                            return new b(this);
                        }
                        throw new IllegalArgumentException("Product type must be provided.");
                    }
                    throw new IllegalArgumentException("Product id must be provided.");
                }
                throw new IllegalArgumentException("Serialized doc id must be provided for first party products.");
            }
        }

        public /* synthetic */ b(a aVar) {
            this.f1969a = aVar.f1971a;
            this.f1970b = aVar.f1972b;
        }

        public final String a() {
            return this.f1970b;
        }
    }

    public final String a() {
        return ((b) this.f1967a.get(0)).a();
    }
}
