package E;

import android.graphics.Color;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<double[]> f801a = new ThreadLocal<>();

    public static int a(double d4, double d5, double d6) {
        double d7;
        double d8;
        double d9;
        int min;
        int min2;
        double d10 = (((-0.4986d) * d6) + (((-1.5372d) * d5) + (3.2406d * d4))) / 100.0d;
        double d11 = ((0.0415d * d6) + ((1.8758d * d5) + ((-0.9689d) * d4))) / 100.0d;
        double d12 = ((1.057d * d6) + (((-0.204d) * d5) + (0.0557d * d4))) / 100.0d;
        if (d10 > 0.0031308d) {
            d7 = (Math.pow(d10, 0.4166666666666667d) * 1.055d) - 0.055d;
        } else {
            d7 = d10 * 12.92d;
        }
        if (d11 > 0.0031308d) {
            d8 = (Math.pow(d11, 0.4166666666666667d) * 1.055d) - 0.055d;
        } else {
            d8 = d11 * 12.92d;
        }
        if (d12 > 0.0031308d) {
            d9 = (Math.pow(d12, 0.4166666666666667d) * 1.055d) - 0.055d;
        } else {
            d9 = 12.92d * d12;
        }
        int round = (int) Math.round(d7 * 255.0d);
        int i4 = 0;
        if (round < 0) {
            min = 0;
        } else {
            min = Math.min(round, 255);
        }
        int round2 = (int) Math.round(d8 * 255.0d);
        if (round2 < 0) {
            min2 = 0;
        } else {
            min2 = Math.min(round2, 255);
        }
        int round3 = (int) Math.round(d9 * 255.0d);
        if (round3 >= 0) {
            i4 = Math.min(round3, 255);
        }
        return Color.rgb(min, min2, i4);
    }

    public static int b(int i4, int i5) {
        int alpha = Color.alpha(i5);
        int alpha2 = Color.alpha(i4);
        int i6 = 255 - (((255 - alpha2) * (255 - alpha)) / 255);
        return Color.argb(i6, c(Color.red(i4), alpha2, Color.red(i5), alpha, i6), c(Color.green(i4), alpha2, Color.green(i5), alpha, i6), c(Color.blue(i4), alpha2, Color.blue(i5), alpha, i6));
    }

    public static int c(int i4, int i5, int i6, int i7, int i8) {
        if (i8 == 0) {
            return 0;
        }
        return (((255 - i5) * (i6 * i7)) + ((i4 * 255) * i5)) / (i8 * 255);
    }

    public static int d(int i4, int i5) {
        if (i5 >= 0 && i5 <= 255) {
            return (i4 & 16777215) | (i5 << 24);
        }
        throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }
}
