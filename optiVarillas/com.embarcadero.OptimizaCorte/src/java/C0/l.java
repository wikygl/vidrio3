package C0;

import android.annotation.SuppressLint;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public interface l {
    @SuppressLint({"SyntheticAccessor"})

    /* renamed from: a  reason: collision with root package name */
    public static final a.c f331a = new a.c();
    @SuppressLint({"SyntheticAccessor"})

    /* renamed from: b  reason: collision with root package name */
    public static final a.b f332b = new Object();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static abstract class a {

        /* renamed from: C0.l$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public static final class C0003a extends a {

            /* renamed from: a  reason: collision with root package name */
            public final Throwable f333a;

            public C0003a(Throwable th) {
                this.f333a = th;
            }

            public final String toString() {
                return C.b.b("FAILURE (", this.f333a.getMessage(), ")");
            }
        }

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public static final class b extends a {
            public final String toString() {
                return "IN_PROGRESS";
            }
        }

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public static final class c extends a {
            public final String toString() {
                return "SUCCESS";
            }
        }
    }
}
