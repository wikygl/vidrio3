package w3;

import java.util.Random;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends w3.a {

    /* renamed from: l  reason: collision with root package name */
    public final a f6412l = new ThreadLocal();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a extends ThreadLocal<Random> {
        @Override // java.lang.ThreadLocal
        public final Random initialValue() {
            return new Random();
        }
    }

    @Override // w3.a
    public final Random c() {
        Random random = this.f6412l.get();
        h.d(random, "implStorage.get()");
        return random;
    }
}
