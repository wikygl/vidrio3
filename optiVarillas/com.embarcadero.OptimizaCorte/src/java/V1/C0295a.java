package V1;

import U1.a;
import U1.a.c;
import W1.C0323k;
import java.util.Arrays;

/* renamed from: V1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0295a<O extends a.c> {

    /* renamed from: a  reason: collision with root package name */
    public final int f2562a;

    /* renamed from: b  reason: collision with root package name */
    public final U1.a f2563b;

    /* renamed from: c  reason: collision with root package name */
    public final a.c f2564c;

    /* renamed from: d  reason: collision with root package name */
    public final String f2565d;

    public C0295a(U1.a aVar, a.c cVar, String str) {
        this.f2563b = aVar;
        this.f2564c = cVar;
        this.f2565d = str;
        this.f2562a = Arrays.hashCode(new Object[]{aVar, cVar, str});
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof C0295a)) {
            return false;
        }
        C0295a c0295a = (C0295a) obj;
        if (!C0323k.a(this.f2563b, c0295a.f2563b) || !C0323k.a(this.f2564c, c0295a.f2564c) || !C0323k.a(this.f2565d, c0295a.f2565d)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this.f2562a;
    }
}
