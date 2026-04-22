package P2;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class l {
    @Deprecated

    /* renamed from: a  reason: collision with root package name */
    public float f1896a;
    @Deprecated

    /* renamed from: b  reason: collision with root package name */
    public float f1897b;
    @Deprecated

    /* renamed from: c  reason: collision with root package name */
    public float f1898c;
    @Deprecated

    /* renamed from: d  reason: collision with root package name */
    public float f1899d;
    @Deprecated

    /* renamed from: e  reason: collision with root package name */
    public float f1900e;
    @Deprecated
    public float f;

    /* renamed from: g  reason: collision with root package name */
    public final ArrayList f1901g = new ArrayList();

    /* renamed from: h  reason: collision with root package name */
    public final ArrayList f1902h = new ArrayList();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends f {

        /* renamed from: c  reason: collision with root package name */
        public final c f1903c;

        public a(c cVar) {
            this.f1903c = cVar;
        }

        @Override // P2.l.f
        public final void a(Matrix matrix, O2.a aVar, int i4, Canvas canvas) {
            boolean z4;
            c cVar = this.f1903c;
            float f = cVar.f;
            float f4 = cVar.f1912g;
            RectF rectF = new RectF(cVar.f1908b, cVar.f1909c, cVar.f1910d, cVar.f1911e);
            aVar.getClass();
            if (f4 < 0.0f) {
                z4 = true;
            } else {
                z4 = false;
            }
            Path path = aVar.f1807g;
            int[] iArr = O2.a.f1800k;
            if (z4) {
                iArr[0] = 0;
                iArr[1] = aVar.f;
                iArr[2] = aVar.f1806e;
                iArr[3] = aVar.f1805d;
            } else {
                path.rewind();
                path.moveTo(rectF.centerX(), rectF.centerY());
                path.arcTo(rectF, f, f4);
                path.close();
                float f5 = -i4;
                rectF.inset(f5, f5);
                iArr[0] = 0;
                iArr[1] = aVar.f1805d;
                iArr[2] = aVar.f1806e;
                iArr[3] = aVar.f;
            }
            float width = rectF.width() / 2.0f;
            if (width > 0.0f) {
                float f6 = 1.0f - (i4 / width);
                float[] fArr = O2.a.f1801l;
                fArr[1] = f6;
                fArr[2] = ((1.0f - f6) / 2.0f) + f6;
                RadialGradient radialGradient = new RadialGradient(rectF.centerX(), rectF.centerY(), width, iArr, fArr, Shader.TileMode.CLAMP);
                Paint paint = aVar.f1803b;
                paint.setShader(radialGradient);
                canvas.save();
                canvas.concat(matrix);
                canvas.scale(1.0f, rectF.height() / rectF.width());
                if (!z4) {
                    canvas.clipPath(path, Region.Op.DIFFERENCE);
                    canvas.drawPath(path, aVar.f1808h);
                }
                canvas.drawArc(rectF, f, f4, true, paint);
                canvas.restore();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends f {

        /* renamed from: c  reason: collision with root package name */
        public final d f1904c;

        /* renamed from: d  reason: collision with root package name */
        public final float f1905d;

        /* renamed from: e  reason: collision with root package name */
        public final float f1906e;

        public b(d dVar, float f, float f4) {
            this.f1904c = dVar;
            this.f1905d = f;
            this.f1906e = f4;
        }

        @Override // P2.l.f
        public final void a(Matrix matrix, O2.a aVar, int i4, Canvas canvas) {
            d dVar = this.f1904c;
            float f = dVar.f1914c;
            float f4 = this.f1906e;
            float f5 = dVar.f1913b;
            float f6 = this.f1905d;
            RectF rectF = new RectF(0.0f, 0.0f, (float) Math.hypot(f - f4, f5 - f6), 0.0f);
            Matrix matrix2 = this.f1917a;
            matrix2.set(matrix);
            matrix2.preTranslate(f6, f4);
            matrix2.preRotate(b());
            aVar.getClass();
            rectF.bottom += i4;
            rectF.offset(0.0f, -i4);
            int[] iArr = O2.a.f1798i;
            iArr[0] = aVar.f;
            iArr[1] = aVar.f1806e;
            iArr[2] = aVar.f1805d;
            Paint paint = aVar.f1804c;
            float f7 = rectF.left;
            paint.setShader(new LinearGradient(f7, rectF.top, f7, rectF.bottom, iArr, O2.a.f1799j, Shader.TileMode.CLAMP));
            canvas.save();
            canvas.concat(matrix2);
            canvas.drawRect(rectF, paint);
            canvas.restore();
        }

        public final float b() {
            d dVar = this.f1904c;
            return (float) Math.toDegrees(Math.atan((dVar.f1914c - this.f1906e) / (dVar.f1913b - this.f1905d)));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends e {

        /* renamed from: h  reason: collision with root package name */
        public static final RectF f1907h = new RectF();
        @Deprecated

        /* renamed from: b  reason: collision with root package name */
        public final float f1908b;
        @Deprecated

        /* renamed from: c  reason: collision with root package name */
        public final float f1909c;
        @Deprecated

        /* renamed from: d  reason: collision with root package name */
        public final float f1910d;
        @Deprecated

        /* renamed from: e  reason: collision with root package name */
        public final float f1911e;
        @Deprecated
        public float f;
        @Deprecated

        /* renamed from: g  reason: collision with root package name */
        public float f1912g;

        public c(float f, float f4, float f5, float f6) {
            this.f1908b = f;
            this.f1909c = f4;
            this.f1910d = f5;
            this.f1911e = f6;
        }

        @Override // P2.l.e
        public final void a(Matrix matrix, Path path) {
            Matrix matrix2 = this.f1915a;
            matrix.invert(matrix2);
            path.transform(matrix2);
            RectF rectF = f1907h;
            rectF.set(this.f1908b, this.f1909c, this.f1910d, this.f1911e);
            path.arcTo(rectF, this.f, this.f1912g, false);
            path.transform(matrix);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class d extends e {

        /* renamed from: b  reason: collision with root package name */
        public float f1913b;

        /* renamed from: c  reason: collision with root package name */
        public float f1914c;

        @Override // P2.l.e
        public final void a(Matrix matrix, Path path) {
            Matrix matrix2 = this.f1915a;
            matrix.invert(matrix2);
            path.transform(matrix2);
            path.lineTo(this.f1913b, this.f1914c);
            path.transform(matrix);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class e {

        /* renamed from: a  reason: collision with root package name */
        public final Matrix f1915a = new Matrix();

        public abstract void a(Matrix matrix, Path path);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class f {

        /* renamed from: b  reason: collision with root package name */
        public static final Matrix f1916b = new Matrix();

        /* renamed from: a  reason: collision with root package name */
        public final Matrix f1917a = new Matrix();

        public abstract void a(Matrix matrix, O2.a aVar, int i4, Canvas canvas);
    }

    public l() {
        e(0.0f, 270.0f, 0.0f);
    }

    public final void a(float f4, float f5, float f6, float f7, float f8, float f9) {
        boolean z4;
        float f10;
        c cVar = new c(f4, f5, f6, f7);
        cVar.f = f8;
        cVar.f1912g = f9;
        this.f1901g.add(cVar);
        a aVar = new a(cVar);
        float f11 = f8 + f9;
        if (f9 < 0.0f) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4) {
            f8 = (f8 + 180.0f) % 360.0f;
        }
        if (z4) {
            f10 = (180.0f + f11) % 360.0f;
        } else {
            f10 = f11;
        }
        b(f8);
        this.f1902h.add(aVar);
        this.f1900e = f10;
        double d4 = f11;
        this.f1898c = (((f6 - f4) / 2.0f) * ((float) Math.cos(Math.toRadians(d4)))) + ((f4 + f6) * 0.5f);
        this.f1899d = (((f7 - f5) / 2.0f) * ((float) Math.sin(Math.toRadians(d4)))) + ((f5 + f7) * 0.5f);
    }

    public final void b(float f4) {
        float f5 = this.f1900e;
        if (f5 == f4) {
            return;
        }
        float f6 = ((f4 - f5) + 360.0f) % 360.0f;
        if (f6 > 180.0f) {
            return;
        }
        float f7 = this.f1898c;
        float f8 = this.f1899d;
        c cVar = new c(f7, f8, f7, f8);
        cVar.f = this.f1900e;
        cVar.f1912g = f6;
        this.f1902h.add(new a(cVar));
        this.f1900e = f4;
    }

    public final void c(Matrix matrix, Path path) {
        ArrayList arrayList = this.f1901g;
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            ((e) arrayList.get(i4)).a(matrix, path);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [P2.l$d, java.lang.Object, P2.l$e] */
    public final void d(float f4, float f5) {
        ?? eVar = new e();
        eVar.f1913b = f4;
        eVar.f1914c = f5;
        this.f1901g.add(eVar);
        b bVar = new b(eVar, this.f1898c, this.f1899d);
        b(bVar.b() + 270.0f);
        this.f1902h.add(bVar);
        this.f1900e = bVar.b() + 270.0f;
        this.f1898c = f4;
        this.f1899d = f5;
    }

    public final void e(float f4, float f5, float f6) {
        this.f1896a = 0.0f;
        this.f1897b = f4;
        this.f1898c = 0.0f;
        this.f1899d = f4;
        this.f1900e = f5;
        this.f = (f5 + f6) % 360.0f;
        this.f1901g.clear();
        this.f1902h.clear();
    }
}
