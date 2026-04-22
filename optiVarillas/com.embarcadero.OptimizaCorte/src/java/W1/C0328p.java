package W1;

import U1.a;
import java.util.Arrays;

/* renamed from: W1.p  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0328p implements a.c {

    /* renamed from: c  reason: collision with root package name */
    public static final C0328p f2766c = new C0328p();

    /* renamed from: b  reason: collision with root package name */
    public final String f2767b = null;

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof C0328p)) {
            return false;
        }
        return C0323k.a(this.f2767b, ((C0328p) obj).f2767b);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f2767b});
    }
}
