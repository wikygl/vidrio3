package i2;

import e0.C0405a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class J {
    public static void a(int i4, int i5) {
        String a4;
        if (i4 >= 0 && i4 < i5) {
            return;
        }
        if (i4 >= 0) {
            if (i5 < 0) {
                throw new IllegalArgumentException(C0405a.c("negative size: ", i5));
            }
            a4 = K.a("%s (%s) must be less than size (%s)", "index", Integer.valueOf(i4), Integer.valueOf(i5));
        } else {
            a4 = K.a("%s (%s) must not be negative", "index", Integer.valueOf(i4));
        }
        throw new IndexOutOfBoundsException(a4);
    }

    public static void b(int i4, int i5, int i6) {
        String c4;
        if (i4 >= 0 && i5 >= i4 && i5 <= i6) {
            return;
        }
        if (i4 >= 0 && i4 <= i6) {
            if (i5 >= 0 && i5 <= i6) {
                c4 = K.a("end index (%s) must not be less than start index (%s)", Integer.valueOf(i5), Integer.valueOf(i4));
            } else {
                c4 = c(i5, i6, "end index");
            }
        } else {
            c4 = c(i4, i6, "start index");
        }
        throw new IndexOutOfBoundsException(c4);
    }

    public static String c(int i4, int i5, String str) {
        if (i4 < 0) {
            return K.a("%s (%s) must not be negative", str, Integer.valueOf(i4));
        }
        if (i5 >= 0) {
            return K.a("%s (%s) must not be greater than size (%s)", str, Integer.valueOf(i4), Integer.valueOf(i5));
        }
        throw new IllegalArgumentException(C0405a.c("negative size: ", i5));
    }
}
