package P2;

import D2.f;
import P2.j;
import P2.l;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import j$.util.Objects;
import java.util.BitSet;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class f extends Drawable implements F.b, m {

    /* renamed from: G  reason: collision with root package name */
    public static final Paint f1813G;

    /* renamed from: A  reason: collision with root package name */
    public final j f1814A;

    /* renamed from: B  reason: collision with root package name */
    public PorterDuffColorFilter f1815B;

    /* renamed from: C  reason: collision with root package name */
    public PorterDuffColorFilter f1816C;

    /* renamed from: D  reason: collision with root package name */
    public int f1817D;

    /* renamed from: E  reason: collision with root package name */
    public final RectF f1818E;

    /* renamed from: F  reason: collision with root package name */
    public final boolean f1819F;

    /* renamed from: j  reason: collision with root package name */
    public b f1820j;

    /* renamed from: k  reason: collision with root package name */
    public final l.f[] f1821k;

    /* renamed from: l  reason: collision with root package name */
    public final l.f[] f1822l;

    /* renamed from: m  reason: collision with root package name */
    public final BitSet f1823m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f1824n;

    /* renamed from: o  reason: collision with root package name */
    public final Matrix f1825o;

    /* renamed from: p  reason: collision with root package name */
    public final Path f1826p;

    /* renamed from: q  reason: collision with root package name */
    public final Path f1827q;

    /* renamed from: r  reason: collision with root package name */
    public final RectF f1828r;

    /* renamed from: s  reason: collision with root package name */
    public final RectF f1829s;

    /* renamed from: t  reason: collision with root package name */
    public final Region f1830t;

    /* renamed from: u  reason: collision with root package name */
    public final Region f1831u;

    /* renamed from: v  reason: collision with root package name */
    public i f1832v;

    /* renamed from: w  reason: collision with root package name */
    public final Paint f1833w;

    /* renamed from: x  reason: collision with root package name */
    public final Paint f1834x;

    /* renamed from: y  reason: collision with root package name */
    public final O2.a f1835y;

    /* renamed from: z  reason: collision with root package name */
    public final a f1836z;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements j.b {
        public a() {
        }
    }

    static {
        Paint paint = new Paint(1);
        f1813G = paint;
        paint.setColor(-1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public f() {
        this(new i());
    }

    public final void b(RectF rectF, Path path) {
        b bVar = this.f1820j;
        this.f1814A.a(bVar.f1838a, bVar.f1846j, rectF, this.f1836z, path);
        if (this.f1820j.f1845i != 1.0f) {
            Matrix matrix = this.f1825o;
            matrix.reset();
            float f = this.f1820j.f1845i;
            matrix.setScale(f, f, rectF.width() / 2.0f, rectF.height() / 2.0f);
            path.transform(matrix);
        }
        path.computeBounds(this.f1818E, true);
    }

    public final PorterDuffColorFilter c(ColorStateList colorStateList, PorterDuff.Mode mode, Paint paint, boolean z4) {
        PorterDuffColorFilter porterDuffColorFilter;
        if (colorStateList != null && mode != null) {
            int colorForState = colorStateList.getColorForState(getState(), 0);
            if (z4) {
                colorForState = d(colorForState);
            }
            this.f1817D = colorForState;
            return new PorterDuffColorFilter(colorForState, mode);
        }
        if (z4) {
            int color = paint.getColor();
            int d4 = d(color);
            this.f1817D = d4;
            if (d4 != color) {
                porterDuffColorFilter = new PorterDuffColorFilter(d4, PorterDuff.Mode.SRC_IN);
                return porterDuffColorFilter;
            }
        }
        porterDuffColorFilter = null;
        return porterDuffColorFilter;
    }

    public final int d(int i4) {
        float f;
        int i5;
        b bVar = this.f1820j;
        float f4 = bVar.f1850n + bVar.f1851o + bVar.f1849m;
        E2.a aVar = bVar.f1839b;
        if (aVar != null && aVar.f877a && E.a.d(i4, 255) == aVar.f880d) {
            float f5 = aVar.f881e;
            if (f5 > 0.0f && f4 > 0.0f) {
                f = Math.min(((((float) Math.log1p(f4 / f5)) * 4.5f) + 2.0f) / 100.0f, 1.0f);
            } else {
                f = 0.0f;
            }
            int alpha = Color.alpha(i4);
            int i6 = B2.a.i(f, E.a.d(i4, 255), aVar.f878b);
            if (f > 0.0f && (i5 = aVar.f879c) != 0) {
                i6 = E.a.b(E.a.d(i5, E2.a.f), i6);
            }
            return E.a.d(i6, alpha);
        }
        return i4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00f3, code lost:
        if (r1 < 29) goto L35;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void draw(android.graphics.Canvas r19) {
        /*
            Method dump skipped, instructions count: 471
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: P2.f.draw(android.graphics.Canvas):void");
    }

    public final void e(Canvas canvas) {
        if (this.f1823m.cardinality() > 0) {
            Log.w("f", "Compatibility shadow requested but can't be drawn for all operations in this shape.");
        }
        int i4 = this.f1820j.f1854r;
        Path path = this.f1826p;
        O2.a aVar = this.f1835y;
        if (i4 != 0) {
            canvas.drawPath(path, aVar.f1802a);
        }
        for (int i5 = 0; i5 < 4; i5++) {
            l.f fVar = this.f1821k[i5];
            int i6 = this.f1820j.f1853q;
            Matrix matrix = l.f.f1916b;
            fVar.a(matrix, aVar, i6, canvas);
            this.f1822l[i5].a(matrix, aVar, this.f1820j.f1853q, canvas);
        }
        if (this.f1819F) {
            b bVar = this.f1820j;
            int sin = (int) (Math.sin(Math.toRadians(bVar.f1855s)) * bVar.f1854r);
            b bVar2 = this.f1820j;
            int cos = (int) (Math.cos(Math.toRadians(bVar2.f1855s)) * bVar2.f1854r);
            canvas.translate(-sin, -cos);
            canvas.drawPath(path, f1813G);
            canvas.translate(sin, cos);
        }
    }

    public final void f(Canvas canvas, Paint paint, Path path, i iVar, RectF rectF) {
        if (iVar.d(rectF)) {
            float a4 = iVar.f.a(rectF) * this.f1820j.f1846j;
            canvas.drawRoundRect(rectF, a4, a4, paint);
            return;
        }
        canvas.drawPath(path, paint);
    }

    public void g(Canvas canvas) {
        float f;
        Paint paint = this.f1834x;
        Path path = this.f1827q;
        i iVar = this.f1832v;
        RectF rectF = this.f1829s;
        rectF.set(h());
        if (i()) {
            f = paint.getStrokeWidth() / 2.0f;
        } else {
            f = 0.0f;
        }
        rectF.inset(f, f);
        f(canvas, paint, path, iVar, rectF);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.f1820j.f1848l;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        return this.f1820j;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    @TargetApi(21)
    public void getOutline(Outline outline) {
        b bVar = this.f1820j;
        if (bVar.f1852p == 2) {
            return;
        }
        if (bVar.f1838a.d(h())) {
            outline.setRoundRect(getBounds(), this.f1820j.f1838a.f1864e.a(h()) * this.f1820j.f1846j);
            return;
        }
        RectF h4 = h();
        Path path = this.f1826p;
        b(h4, path);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 30) {
            f.b.a(outline, path);
        } else if (i4 >= 29) {
            try {
                f.a.a(outline, path);
            } catch (IllegalArgumentException unused) {
            }
        } else if (path.isConvex()) {
            f.a.a(outline, path);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean getPadding(Rect rect) {
        Rect rect2 = this.f1820j.f1844h;
        if (rect2 != null) {
            rect.set(rect2);
            return true;
        }
        return super.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public final Region getTransparentRegion() {
        Rect bounds = getBounds();
        Region region = this.f1830t;
        region.set(bounds);
        RectF h4 = h();
        Path path = this.f1826p;
        b(h4, path);
        Region region2 = this.f1831u;
        region2.setPath(path, region);
        region.op(region2, Region.Op.DIFFERENCE);
        return region;
    }

    public final RectF h() {
        RectF rectF = this.f1828r;
        rectF.set(getBounds());
        return rectF;
    }

    public final boolean i() {
        Paint.Style style = this.f1820j.f1857u;
        if ((style == Paint.Style.FILL_AND_STROKE || style == Paint.Style.STROKE) && this.f1834x.getStrokeWidth() > 0.0f) {
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void invalidateSelf() {
        this.f1824n = true;
        super.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        ColorStateList colorStateList3;
        ColorStateList colorStateList4;
        if (!super.isStateful() && (((colorStateList = this.f1820j.f) == null || !colorStateList.isStateful()) && (((colorStateList2 = this.f1820j.f1842e) == null || !colorStateList2.isStateful()) && (((colorStateList3 = this.f1820j.f1841d) == null || !colorStateList3.isStateful()) && ((colorStateList4 = this.f1820j.f1840c) == null || !colorStateList4.isStateful()))))) {
            return false;
        }
        return true;
    }

    public final void j(Context context) {
        this.f1820j.f1839b = new E2.a(context);
        o();
    }

    public final void k(float f) {
        b bVar = this.f1820j;
        if (bVar.f1850n != f) {
            bVar.f1850n = f;
            o();
        }
    }

    public final void l(ColorStateList colorStateList) {
        b bVar = this.f1820j;
        if (bVar.f1840c != colorStateList) {
            bVar.f1840c = colorStateList;
            onStateChange(getState());
        }
    }

    public final boolean m(int[] iArr) {
        boolean z4;
        Paint paint;
        int color;
        int colorForState;
        Paint paint2;
        int color2;
        int colorForState2;
        if (this.f1820j.f1840c != null && color2 != (colorForState2 = this.f1820j.f1840c.getColorForState(iArr, (color2 = (paint2 = this.f1833w).getColor())))) {
            paint2.setColor(colorForState2);
            z4 = true;
        } else {
            z4 = false;
        }
        if (this.f1820j.f1841d != null && color != (colorForState = this.f1820j.f1841d.getColorForState(iArr, (color = (paint = this.f1834x).getColor())))) {
            paint.setColor(colorForState);
            return true;
        }
        return z4;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        this.f1820j = new b(this.f1820j);
        return this;
    }

    public final boolean n() {
        PorterDuffColorFilter porterDuffColorFilter = this.f1815B;
        PorterDuffColorFilter porterDuffColorFilter2 = this.f1816C;
        b bVar = this.f1820j;
        this.f1815B = c(bVar.f, bVar.f1843g, this.f1833w, true);
        b bVar2 = this.f1820j;
        this.f1816C = c(bVar2.f1842e, bVar2.f1843g, this.f1834x, false);
        b bVar3 = this.f1820j;
        if (bVar3.f1856t) {
            int colorForState = bVar3.f.getColorForState(getState(), 0);
            O2.a aVar = this.f1835y;
            aVar.getClass();
            aVar.f1805d = E.a.d(colorForState, 68);
            aVar.f1806e = E.a.d(colorForState, 20);
            aVar.f = E.a.d(colorForState, 0);
            aVar.f1802a.setColor(aVar.f1805d);
        }
        if (!Objects.equals(porterDuffColorFilter, this.f1815B) || !Objects.equals(porterDuffColorFilter2, this.f1816C)) {
            return true;
        }
        return false;
    }

    public final void o() {
        b bVar = this.f1820j;
        float f = bVar.f1850n + bVar.f1851o;
        bVar.f1853q = (int) Math.ceil(0.75f * f);
        this.f1820j.f1854r = (int) Math.ceil(f * 0.25f);
        n();
        super.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        this.f1824n = true;
        super.onBoundsChange(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] iArr) {
        boolean z4;
        boolean m4 = m(iArr);
        boolean n4 = n();
        if (!m4 && !n4) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (z4) {
            invalidateSelf();
        }
        return z4;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i4) {
        b bVar = this.f1820j;
        if (bVar.f1848l != i4) {
            bVar.f1848l = i4;
            super.invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.f1820j.getClass();
        super.invalidateSelf();
    }

    @Override // P2.m
    public final void setShapeAppearanceModel(i iVar) {
        this.f1820j.f1838a = iVar;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        setTintList(ColorStateList.valueOf(i4));
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        this.f1820j.f = colorStateList;
        n();
        super.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode mode) {
        b bVar = this.f1820j;
        if (bVar.f1843g != mode) {
            bVar.f1843g = mode;
            n();
            super.invalidateSelf();
        }
    }

    public f(Context context, AttributeSet attributeSet, int i4, int i5) {
        this(i.b(context, attributeSet, i4, i5).a());
    }

    public f(i iVar) {
        this(new b(iVar));
    }

    public f(b bVar) {
        j jVar;
        this.f1821k = new l.f[4];
        this.f1822l = new l.f[4];
        this.f1823m = new BitSet(8);
        this.f1825o = new Matrix();
        this.f1826p = new Path();
        this.f1827q = new Path();
        this.f1828r = new RectF();
        this.f1829s = new RectF();
        this.f1830t = new Region();
        this.f1831u = new Region();
        Paint paint = new Paint(1);
        this.f1833w = paint;
        Paint paint2 = new Paint(1);
        this.f1834x = paint2;
        this.f1835y = new O2.a();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            jVar = j.a.f1893a;
        } else {
            jVar = new j();
        }
        this.f1814A = jVar;
        this.f1818E = new RectF();
        this.f1819F = true;
        this.f1820j = bVar;
        paint2.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        n();
        m(getState());
        this.f1836z = new a();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public i f1838a;

        /* renamed from: b  reason: collision with root package name */
        public E2.a f1839b;

        /* renamed from: c  reason: collision with root package name */
        public ColorStateList f1840c;

        /* renamed from: d  reason: collision with root package name */
        public ColorStateList f1841d;

        /* renamed from: e  reason: collision with root package name */
        public final ColorStateList f1842e;
        public ColorStateList f;

        /* renamed from: g  reason: collision with root package name */
        public PorterDuff.Mode f1843g;

        /* renamed from: h  reason: collision with root package name */
        public Rect f1844h;

        /* renamed from: i  reason: collision with root package name */
        public final float f1845i;

        /* renamed from: j  reason: collision with root package name */
        public float f1846j;

        /* renamed from: k  reason: collision with root package name */
        public float f1847k;

        /* renamed from: l  reason: collision with root package name */
        public int f1848l;

        /* renamed from: m  reason: collision with root package name */
        public float f1849m;

        /* renamed from: n  reason: collision with root package name */
        public float f1850n;

        /* renamed from: o  reason: collision with root package name */
        public final float f1851o;

        /* renamed from: p  reason: collision with root package name */
        public final int f1852p;

        /* renamed from: q  reason: collision with root package name */
        public int f1853q;

        /* renamed from: r  reason: collision with root package name */
        public int f1854r;

        /* renamed from: s  reason: collision with root package name */
        public final int f1855s;

        /* renamed from: t  reason: collision with root package name */
        public final boolean f1856t;

        /* renamed from: u  reason: collision with root package name */
        public final Paint.Style f1857u;

        public b(i iVar) {
            this.f1840c = null;
            this.f1841d = null;
            this.f1842e = null;
            this.f = null;
            this.f1843g = PorterDuff.Mode.SRC_IN;
            this.f1844h = null;
            this.f1845i = 1.0f;
            this.f1846j = 1.0f;
            this.f1848l = 255;
            this.f1849m = 0.0f;
            this.f1850n = 0.0f;
            this.f1851o = 0.0f;
            this.f1852p = 0;
            this.f1853q = 0;
            this.f1854r = 0;
            this.f1855s = 0;
            this.f1856t = false;
            this.f1857u = Paint.Style.FILL_AND_STROKE;
            this.f1838a = iVar;
            this.f1839b = null;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            f fVar = new f(this);
            fVar.f1824n = true;
            return fVar;
        }

        public b(b bVar) {
            this.f1840c = null;
            this.f1841d = null;
            this.f1842e = null;
            this.f = null;
            this.f1843g = PorterDuff.Mode.SRC_IN;
            this.f1844h = null;
            this.f1845i = 1.0f;
            this.f1846j = 1.0f;
            this.f1848l = 255;
            this.f1849m = 0.0f;
            this.f1850n = 0.0f;
            this.f1851o = 0.0f;
            this.f1852p = 0;
            this.f1853q = 0;
            this.f1854r = 0;
            this.f1855s = 0;
            this.f1856t = false;
            this.f1857u = Paint.Style.FILL_AND_STROKE;
            this.f1838a = bVar.f1838a;
            this.f1839b = bVar.f1839b;
            this.f1847k = bVar.f1847k;
            this.f1840c = bVar.f1840c;
            this.f1841d = bVar.f1841d;
            this.f1843g = bVar.f1843g;
            this.f = bVar.f;
            this.f1848l = bVar.f1848l;
            this.f1845i = bVar.f1845i;
            this.f1854r = bVar.f1854r;
            this.f1852p = bVar.f1852p;
            this.f1856t = bVar.f1856t;
            this.f1846j = bVar.f1846j;
            this.f1849m = bVar.f1849m;
            this.f1850n = bVar.f1850n;
            this.f1851o = bVar.f1851o;
            this.f1853q = bVar.f1853q;
            this.f1855s = bVar.f1855s;
            this.f1842e = bVar.f1842e;
            this.f1857u = bVar.f1857u;
            if (bVar.f1844h != null) {
                this.f1844h = new Rect(bVar.f1844h);
            }
        }
    }
}
