package y3;

import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class a implements Iterable<Integer> {

    /* renamed from: j  reason: collision with root package name */
    public final int f6515j;

    /* renamed from: k  reason: collision with root package name */
    public final int f6516k;

    /* renamed from: l  reason: collision with root package name */
    public final int f6517l;

    public a(int i4, int i5, int i6) {
        if (i6 != 0) {
            if (i6 != Integer.MIN_VALUE) {
                this.f6515j = i4;
                if (i6 > 0) {
                    if (i4 < i5) {
                        int i7 = i5 % i6;
                        int i8 = i4 % i6;
                        int i9 = ((i7 < 0 ? i7 + i6 : i7) - (i8 < 0 ? i8 + i6 : i8)) % i6;
                        i5 -= i9 < 0 ? i9 + i6 : i9;
                    }
                } else if (i6 < 0) {
                    if (i4 > i5) {
                        int i10 = -i6;
                        int i11 = i4 % i10;
                        int i12 = i5 % i10;
                        int i13 = ((i11 < 0 ? i11 + i10 : i11) - (i12 < 0 ? i12 + i10 : i12)) % i10;
                        i5 += i13 < 0 ? i13 + i10 : i13;
                    }
                } else {
                    throw new IllegalArgumentException("Step is zero.");
                }
                this.f6516k = i5;
                this.f6517l = i6;
                return;
            }
            throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
        }
        throw new IllegalArgumentException("Step must be non-zero.");
    }

    @Override // java.lang.Iterable
    public final Iterator<Integer> iterator() {
        return new b(this.f6515j, this.f6516k, this.f6517l);
    }
}
