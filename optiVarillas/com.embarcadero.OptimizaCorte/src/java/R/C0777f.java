package r;

/* renamed from: r.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0777f {

    /* renamed from: a  reason: collision with root package name */
    public static final int[] f5652a = new int[0];

    /* renamed from: b  reason: collision with root package name */
    public static final Object[] f5653b = new Object[0];

    public static int a(int i4, int i5, int[] iArr) {
        int i6 = i4 - 1;
        int i7 = 0;
        while (i7 <= i6) {
            int i8 = (i7 + i6) >>> 1;
            int i9 = iArr[i8];
            if (i9 < i5) {
                i7 = i8 + 1;
            } else if (i9 > i5) {
                i6 = i8 - 1;
            } else {
                return i8;
            }
        }
        return ~i7;
    }

    public static int b(long[] jArr, int i4, long j4) {
        int i5 = i4 - 1;
        int i6 = 0;
        while (i6 <= i5) {
            int i7 = (i6 + i5) >>> 1;
            int i8 = (jArr[i7] > j4 ? 1 : (jArr[i7] == j4 ? 0 : -1));
            if (i8 < 0) {
                i6 = i7 + 1;
            } else if (i8 > 0) {
                i5 = i7 - 1;
            } else {
                return i7;
            }
        }
        return ~i6;
    }
}
