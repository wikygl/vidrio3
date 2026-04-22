package E;

import android.graphics.Insets;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b {

    /* renamed from: e  reason: collision with root package name */
    public static final b f802e = new b(0, 0, 0, 0);

    /* renamed from: a  reason: collision with root package name */
    public final int f803a;

    /* renamed from: b  reason: collision with root package name */
    public final int f804b;

    /* renamed from: c  reason: collision with root package name */
    public final int f805c;

    /* renamed from: d  reason: collision with root package name */
    public final int f806d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static Insets a(int i4, int i5, int i6, int i7) {
            return Insets.of(i4, i5, i6, i7);
        }
    }

    public b(int i4, int i5, int i6, int i7) {
        this.f803a = i4;
        this.f804b = i5;
        this.f805c = i6;
        this.f806d = i7;
    }

    public static b a(b bVar, b bVar2) {
        return b(Math.max(bVar.f803a, bVar2.f803a), Math.max(bVar.f804b, bVar2.f804b), Math.max(bVar.f805c, bVar2.f805c), Math.max(bVar.f806d, bVar2.f806d));
    }

    public static b b(int i4, int i5, int i6, int i7) {
        if (i4 == 0 && i5 == 0 && i6 == 0 && i7 == 0) {
            return f802e;
        }
        return new b(i4, i5, i6, i7);
    }

    public static b c(Insets insets) {
        int i4;
        int i5;
        int i6;
        int i7;
        i4 = insets.left;
        i5 = insets.top;
        i6 = insets.right;
        i7 = insets.bottom;
        return b(i4, i5, i6, i7);
    }

    public final Insets d() {
        return a.a(this.f803a, this.f804b, this.f805c, this.f806d);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || b.class != obj.getClass()) {
            return false;
        }
        b bVar = (b) obj;
        if (this.f806d == bVar.f806d && this.f803a == bVar.f803a && this.f805c == bVar.f805c && this.f804b == bVar.f804b) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (((((this.f803a * 31) + this.f804b) * 31) + this.f805c) * 31) + this.f806d;
    }

    public final String toString() {
        return "Insets{left=" + this.f803a + ", top=" + this.f804b + ", right=" + this.f805c + ", bottom=" + this.f806d + '}';
    }
}
