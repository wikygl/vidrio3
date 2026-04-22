package D;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class j {

    /* renamed from: k  reason: collision with root package name */
    public static final j f543k;

    /* renamed from: a  reason: collision with root package name */
    public final float f544a;

    /* renamed from: b  reason: collision with root package name */
    public final float f545b;

    /* renamed from: c  reason: collision with root package name */
    public final float f546c;

    /* renamed from: d  reason: collision with root package name */
    public final float f547d;

    /* renamed from: e  reason: collision with root package name */
    public final float f548e;
    public final float f;

    /* renamed from: g  reason: collision with root package name */
    public final float[] f549g;

    /* renamed from: h  reason: collision with root package name */
    public final float f550h;

    /* renamed from: i  reason: collision with root package name */
    public final float f551i;

    /* renamed from: j  reason: collision with root package name */
    public final float f552j;

    static {
        float f;
        float[] fArr = b.f511c;
        float c4 = (float) ((b.c() * 63.66197723675813d) / 100.0d);
        float[][] fArr2 = b.f509a;
        float f4 = fArr[0];
        float[] fArr3 = fArr2[0];
        float f5 = fArr[1];
        float f6 = (fArr3[1] * f5) + (fArr3[0] * f4);
        float f7 = fArr[2];
        float f8 = (fArr3[2] * f7) + f6;
        float[] fArr4 = fArr2[1];
        float f9 = (fArr4[2] * f7) + (fArr4[1] * f5) + (fArr4[0] * f4);
        float[] fArr5 = fArr2[2];
        float f10 = (f7 * fArr5[2]) + (f5 * fArr5[1]) + (f4 * fArr5[0]);
        if (1.0f >= 0.9d) {
            f = 0.69f;
        } else {
            f = 0.655f;
        }
        float exp = (1.0f - (((float) Math.exp(((-c4) - 42.0f) / 92.0f)) * 0.2777778f)) * 1.0f;
        double d4 = exp;
        if (d4 > 1.0d) {
            exp = 1.0f;
        } else if (d4 < 0.0d) {
            exp = 0.0f;
        }
        float[] fArr6 = {(((100.0f / f8) * exp) + 1.0f) - exp, (((100.0f / f9) * exp) + 1.0f) - exp, (((100.0f / f10) * exp) + 1.0f) - exp};
        float f11 = 1.0f / ((5.0f * c4) + 1.0f);
        float f12 = f11 * f11 * f11 * f11;
        float f13 = 1.0f - f12;
        float cbrt = (0.1f * f13 * f13 * ((float) Math.cbrt(c4 * 5.0d))) + (f12 * c4);
        float c5 = b.c() / fArr[1];
        double d5 = c5;
        float sqrt = ((float) Math.sqrt(d5)) + 1.48f;
        float pow = 0.725f / ((float) Math.pow(d5, 0.2d));
        float[] fArr7 = {(float) Math.pow(((fArr6[0] * cbrt) * f8) / 100.0d, 0.42d), (float) Math.pow(((fArr6[1] * cbrt) * f9) / 100.0d, 0.42d), (float) Math.pow(((fArr6[2] * cbrt) * f10) / 100.0d, 0.42d)};
        float f14 = fArr7[0];
        float f15 = (f14 * 400.0f) / (f14 + 27.13f);
        float f16 = fArr7[1];
        float f17 = (f16 * 400.0f) / (f16 + 27.13f);
        float f18 = fArr7[2];
        float[] fArr8 = {f15, f17, (400.0f * f18) / (f18 + 27.13f)};
        f543k = new j(c5, ((fArr8[2] * 0.05f) + (fArr8[0] * 2.0f) + fArr8[1]) * pow, pow, pow, f, 1.0f, fArr6, cbrt, (float) Math.pow(cbrt, 0.25d), sqrt);
    }

    public j(float f, float f4, float f5, float f6, float f7, float f8, float[] fArr, float f9, float f10, float f11) {
        this.f = f;
        this.f544a = f4;
        this.f545b = f5;
        this.f546c = f6;
        this.f547d = f7;
        this.f548e = f8;
        this.f549g = fArr;
        this.f550h = f9;
        this.f551i = f10;
        this.f552j = f11;
    }
}
