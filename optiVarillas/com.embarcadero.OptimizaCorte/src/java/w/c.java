package W;

import java.nio.ByteBuffer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public class c {

    /* renamed from: a  reason: collision with root package name */
    public int f2628a;

    /* renamed from: b  reason: collision with root package name */
    public ByteBuffer f2629b;

    /* renamed from: c  reason: collision with root package name */
    public int f2630c;

    /* renamed from: d  reason: collision with root package name */
    public int f2631d;

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, A0.c] */
    public c() {
        if (A0.c.f6j == null) {
            A0.c.f6j = new Object();
        }
    }

    public final int a(int i4) {
        if (i4 < this.f2631d) {
            return this.f2629b.getShort(this.f2630c + i4);
        }
        return 0;
    }
}
