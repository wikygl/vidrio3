package v0;

import D.i;
import E.d;
import F.a;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import r.C0773b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class h extends v0.g {

    /* renamed from: s  reason: collision with root package name */
    public static final PorterDuff.Mode f6226s = PorterDuff.Mode.SRC_IN;

    /* renamed from: k  reason: collision with root package name */
    public g f6227k;

    /* renamed from: l  reason: collision with root package name */
    public PorterDuffColorFilter f6228l;

    /* renamed from: m  reason: collision with root package name */
    public ColorFilter f6229m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f6230n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f6231o;

    /* renamed from: p  reason: collision with root package name */
    public final float[] f6232p;

    /* renamed from: q  reason: collision with root package name */
    public final Matrix f6233q;

    /* renamed from: r  reason: collision with root package name */
    public final Rect f6234r;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class a extends e {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends e {

        /* renamed from: e  reason: collision with root package name */
        public D.d f6235e;

        /* renamed from: g  reason: collision with root package name */
        public D.d f6236g;
        public float f = 0.0f;

        /* renamed from: h  reason: collision with root package name */
        public float f6237h = 1.0f;

        /* renamed from: i  reason: collision with root package name */
        public float f6238i = 1.0f;

        /* renamed from: j  reason: collision with root package name */
        public float f6239j = 0.0f;

        /* renamed from: k  reason: collision with root package name */
        public float f6240k = 1.0f;

        /* renamed from: l  reason: collision with root package name */
        public float f6241l = 0.0f;

        /* renamed from: m  reason: collision with root package name */
        public Paint.Cap f6242m = Paint.Cap.BUTT;

        /* renamed from: n  reason: collision with root package name */
        public Paint.Join f6243n = Paint.Join.MITER;

        /* renamed from: o  reason: collision with root package name */
        public float f6244o = 4.0f;

        @Override // v0.h.d
        public final boolean a() {
            if (!this.f6236g.b() && !this.f6235e.b()) {
                return false;
            }
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0025  */
        @Override // v0.h.d
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final boolean b(int[] r7) {
            /*
                r6 = this;
                D.d r0 = r6.f6236g
                boolean r1 = r0.b()
                r2 = 0
                r3 = 1
                if (r1 == 0) goto L1c
                android.content.res.ColorStateList r1 = r0.f515b
                int r4 = r1.getDefaultColor()
                int r1 = r1.getColorForState(r7, r4)
                int r4 = r0.f516c
                if (r1 == r4) goto L1c
                r0.f516c = r1
                r0 = 1
                goto L1d
            L1c:
                r0 = 0
            L1d:
                D.d r1 = r6.f6235e
                boolean r4 = r1.b()
                if (r4 == 0) goto L36
                android.content.res.ColorStateList r4 = r1.f515b
                int r5 = r4.getDefaultColor()
                int r7 = r4.getColorForState(r7, r5)
                int r4 = r1.f516c
                if (r7 == r4) goto L36
                r1.f516c = r7
                r2 = 1
            L36:
                r7 = r0 | r2
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: v0.h.b.b(int[]):boolean");
        }

        public float getFillAlpha() {
            return this.f6238i;
        }

        public int getFillColor() {
            return this.f6236g.f516c;
        }

        public float getStrokeAlpha() {
            return this.f6237h;
        }

        public int getStrokeColor() {
            return this.f6235e.f516c;
        }

        public float getStrokeWidth() {
            return this.f;
        }

        public float getTrimPathEnd() {
            return this.f6240k;
        }

        public float getTrimPathOffset() {
            return this.f6241l;
        }

        public float getTrimPathStart() {
            return this.f6239j;
        }

        public void setFillAlpha(float f) {
            this.f6238i = f;
        }

        public void setFillColor(int i4) {
            this.f6236g.f516c = i4;
        }

        public void setStrokeAlpha(float f) {
            this.f6237h = f;
        }

        public void setStrokeColor(int i4) {
            this.f6235e.f516c = i4;
        }

        public void setStrokeWidth(float f) {
            this.f = f;
        }

        public void setTrimPathEnd(float f) {
            this.f6240k = f;
        }

        public void setTrimPathOffset(float f) {
            this.f6241l = f;
        }

        public void setTrimPathStart(float f) {
            this.f6239j = f;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class d {
        public boolean a() {
            return false;
        }

        public boolean b(int[] iArr) {
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class g extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public int f6275a;

        /* renamed from: b  reason: collision with root package name */
        public f f6276b;

        /* renamed from: c  reason: collision with root package name */
        public ColorStateList f6277c;

        /* renamed from: d  reason: collision with root package name */
        public PorterDuff.Mode f6278d;

        /* renamed from: e  reason: collision with root package name */
        public boolean f6279e;
        public Bitmap f;

        /* renamed from: g  reason: collision with root package name */
        public ColorStateList f6280g;

        /* renamed from: h  reason: collision with root package name */
        public PorterDuff.Mode f6281h;

        /* renamed from: i  reason: collision with root package name */
        public int f6282i;

        /* renamed from: j  reason: collision with root package name */
        public boolean f6283j;

        /* renamed from: k  reason: collision with root package name */
        public boolean f6284k;

        /* renamed from: l  reason: collision with root package name */
        public Paint f6285l;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.f6275a;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new h(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources) {
            return new h(this);
        }
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [v0.h$g, android.graphics.drawable.Drawable$ConstantState] */
    public h() {
        this.f6231o = true;
        this.f6232p = new float[9];
        this.f6233q = new Matrix();
        this.f6234r = new Rect();
        ?? constantState = new Drawable.ConstantState();
        constantState.f6277c = null;
        constantState.f6278d = f6226s;
        constantState.f6276b = new f();
        this.f6227k = constantState;
    }

    public final PorterDuffColorFilter a(ColorStateList colorStateList, PorterDuff.Mode mode) {
        if (colorStateList != null && mode != null) {
            return new PorterDuffColorFilter(colorStateList.getColorForState(getState(), 0), mode);
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean canApplyTheme() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            a.C0006a.b(drawable);
            return false;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        Paint paint;
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.draw(canvas);
            return;
        }
        Rect rect = this.f6234r;
        copyBounds(rect);
        if (rect.width() > 0 && rect.height() > 0) {
            ColorFilter colorFilter = this.f6229m;
            if (colorFilter == null) {
                colorFilter = this.f6228l;
            }
            Matrix matrix = this.f6233q;
            canvas.getMatrix(matrix);
            float[] fArr = this.f6232p;
            matrix.getValues(fArr);
            float abs = Math.abs(fArr[0]);
            float abs2 = Math.abs(fArr[4]);
            float abs3 = Math.abs(fArr[1]);
            float abs4 = Math.abs(fArr[3]);
            if (abs3 != 0.0f || abs4 != 0.0f) {
                abs = 1.0f;
                abs2 = 1.0f;
            }
            int min = Math.min(2048, (int) (rect.width() * abs));
            int min2 = Math.min(2048, (int) (rect.height() * abs2));
            if (min > 0 && min2 > 0) {
                int save = canvas.save();
                canvas.translate(rect.left, rect.top);
                if (isAutoMirrored() && F.a.b(this) == 1) {
                    canvas.translate(rect.width(), 0.0f);
                    canvas.scale(-1.0f, 1.0f);
                }
                rect.offsetTo(0, 0);
                g gVar = this.f6227k;
                Bitmap bitmap = gVar.f;
                if (bitmap == null || min != bitmap.getWidth() || min2 != gVar.f.getHeight()) {
                    gVar.f = Bitmap.createBitmap(min, min2, Bitmap.Config.ARGB_8888);
                    gVar.f6284k = true;
                }
                if (!this.f6231o) {
                    g gVar2 = this.f6227k;
                    gVar2.f.eraseColor(0);
                    Canvas canvas2 = new Canvas(gVar2.f);
                    f fVar = gVar2.f6276b;
                    fVar.a(fVar.f6266g, f.f6260p, canvas2, min, min2);
                } else {
                    g gVar3 = this.f6227k;
                    if (gVar3.f6284k || gVar3.f6280g != gVar3.f6277c || gVar3.f6281h != gVar3.f6278d || gVar3.f6283j != gVar3.f6279e || gVar3.f6282i != gVar3.f6276b.getRootAlpha()) {
                        g gVar4 = this.f6227k;
                        gVar4.f.eraseColor(0);
                        Canvas canvas3 = new Canvas(gVar4.f);
                        f fVar2 = gVar4.f6276b;
                        fVar2.a(fVar2.f6266g, f.f6260p, canvas3, min, min2);
                        g gVar5 = this.f6227k;
                        gVar5.f6280g = gVar5.f6277c;
                        gVar5.f6281h = gVar5.f6278d;
                        gVar5.f6282i = gVar5.f6276b.getRootAlpha();
                        gVar5.f6283j = gVar5.f6279e;
                        gVar5.f6284k = false;
                    }
                }
                g gVar6 = this.f6227k;
                if (gVar6.f6276b.getRootAlpha() >= 255 && colorFilter == null) {
                    paint = null;
                } else {
                    if (gVar6.f6285l == null) {
                        Paint paint2 = new Paint();
                        gVar6.f6285l = paint2;
                        paint2.setFilterBitmap(true);
                    }
                    gVar6.f6285l.setAlpha(gVar6.f6276b.getRootAlpha());
                    gVar6.f6285l.setColorFilter(colorFilter);
                    paint = gVar6.f6285l;
                }
                canvas.drawBitmap(gVar6.f, (Rect) null, rect, paint);
                canvas.restoreToCount(save);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getAlpha();
        }
        return this.f6227k.f6276b.getRootAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getChangingConfigurations();
        }
        return super.getChangingConfigurations() | this.f6227k.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public final ColorFilter getColorFilter() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return a.C0006a.c(drawable);
        }
        return this.f6229m;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        if (this.f6225j != null && Build.VERSION.SDK_INT >= 24) {
            return new C0074h(this.f6225j.getConstantState());
        }
        this.f6227k.f6275a = getChangingConfigurations();
        return this.f6227k;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return (int) this.f6227k.f6276b.f6268i;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return (int) this.f6227k.f6276b.f6267h;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getOpacity();
        }
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.inflate(resources, xmlPullParser, attributeSet);
        } else {
            inflate(resources, xmlPullParser, attributeSet, null);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void invalidateSelf() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.invalidateSelf();
        } else {
            super.invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.isAutoMirrored();
        }
        return this.f6227k.f6279e;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        ColorStateList colorStateList;
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.isStateful();
        }
        if (!super.isStateful()) {
            g gVar = this.f6227k;
            if (gVar != null) {
                f fVar = gVar.f6276b;
                if (fVar.f6273n == null) {
                    fVar.f6273n = Boolean.valueOf(fVar.f6266g.a());
                }
                if (fVar.f6273n.booleanValue() || ((colorStateList = this.f6227k.f6277c) != null && colorStateList.isStateful())) {
                }
            }
            return false;
        }
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [v0.h$g, android.graphics.drawable.Drawable$ConstantState] */
    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.mutate();
            return this;
        }
        if (!this.f6230n && super.mutate() == this) {
            g gVar = this.f6227k;
            ?? constantState = new Drawable.ConstantState();
            constantState.f6277c = null;
            constantState.f6278d = f6226s;
            if (gVar != null) {
                constantState.f6275a = gVar.f6275a;
                f fVar = new f(gVar.f6276b);
                constantState.f6276b = fVar;
                if (gVar.f6276b.f6265e != null) {
                    fVar.f6265e = new Paint(gVar.f6276b.f6265e);
                }
                if (gVar.f6276b.f6264d != null) {
                    constantState.f6276b.f6264d = new Paint(gVar.f6276b.f6264d);
                }
                constantState.f6277c = gVar.f6277c;
                constantState.f6278d = gVar.f6278d;
                constantState.f6279e = gVar.f6279e;
            }
            this.f6227k = constantState;
            this.f6230n = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onStateChange(int[] iArr) {
        boolean z4;
        PorterDuff.Mode mode;
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.setState(iArr);
        }
        g gVar = this.f6227k;
        ColorStateList colorStateList = gVar.f6277c;
        if (colorStateList != null && (mode = gVar.f6278d) != null) {
            this.f6228l = a(colorStateList, mode);
            invalidateSelf();
            z4 = true;
        } else {
            z4 = false;
        }
        f fVar = gVar.f6276b;
        if (fVar.f6273n == null) {
            fVar.f6273n = Boolean.valueOf(fVar.f6266g.a());
        }
        if (fVar.f6273n.booleanValue()) {
            boolean b4 = gVar.f6276b.f6266g.b(iArr);
            gVar.f6284k |= b4;
            if (b4) {
                invalidateSelf();
                return true;
            }
        }
        return z4;
    }

    @Override // android.graphics.drawable.Drawable
    public final void scheduleSelf(Runnable runnable, long j4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.scheduleSelf(runnable, j4);
        } else {
            super.scheduleSelf(runnable, j4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setAlpha(i4);
        } else if (this.f6227k.f6276b.getRootAlpha() != i4) {
            this.f6227k.f6276b.setRootAlpha(i4);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAutoMirrored(boolean z4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setAutoMirrored(z4);
        } else {
            this.f6227k.f6279e = z4;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
            return;
        }
        this.f6229m = colorFilter;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            F.a.d(drawable, i4);
        } else {
            setTintList(ColorStateList.valueOf(i4));
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            a.C0006a.h(drawable, colorStateList);
            return;
        }
        g gVar = this.f6227k;
        if (gVar.f6277c != colorStateList) {
            gVar.f6277c = colorStateList;
            this.f6228l = a(colorStateList, gVar.f6278d);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            a.C0006a.i(drawable, mode);
            return;
        }
        g gVar = this.f6227k;
        if (gVar.f6278d != mode) {
            gVar.f6278d = mode;
            this.f6228l = a(gVar.f6277c, mode);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean z4, boolean z5) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.setVisible(z4, z5);
        }
        return super.setVisible(z4, z5);
    }

    @Override // android.graphics.drawable.Drawable
    public final void unscheduleSelf(Runnable runnable) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.unscheduleSelf(runnable);
        } else {
            super.unscheduleSelf(runnable);
        }
    }

    /* renamed from: v0.h$h  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class C0074h extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public final Drawable.ConstantState f6286a;

        public C0074h(Drawable.ConstantState constantState) {
            this.f6286a = constantState;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final boolean canApplyTheme() {
            return this.f6286a.canApplyTheme();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.f6286a.getChangingConfigurations();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            h hVar = new h();
            hVar.f6225j = (VectorDrawable) this.f6286a.newDrawable();
            return hVar;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources) {
            h hVar = new h();
            hVar.f6225j = (VectorDrawable) this.f6286a.newDrawable(resources);
            return hVar;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources, Resources.Theme theme) {
            h hVar = new h();
            hVar.f6225j = (VectorDrawable) this.f6286a.newDrawable(resources, theme);
            return hVar;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class e extends d {

        /* renamed from: a  reason: collision with root package name */
        public d.a[] f6256a;

        /* renamed from: b  reason: collision with root package name */
        public String f6257b;

        /* renamed from: c  reason: collision with root package name */
        public int f6258c;

        /* renamed from: d  reason: collision with root package name */
        public final int f6259d;

        public e() {
            this.f6256a = null;
            this.f6258c = 0;
        }

        public d.a[] getPathData() {
            return this.f6256a;
        }

        public String getPathName() {
            return this.f6257b;
        }

        public void setPathData(d.a[] aVarArr) {
            if (!E.d.a(this.f6256a, aVarArr)) {
                this.f6256a = E.d.e(aVarArr);
                return;
            }
            d.a[] aVarArr2 = this.f6256a;
            for (int i4 = 0; i4 < aVarArr.length; i4++) {
                aVarArr2[i4].f808a = aVarArr[i4].f808a;
                int i5 = 0;
                while (true) {
                    float[] fArr = aVarArr[i4].f809b;
                    if (i5 < fArr.length) {
                        aVarArr2[i4].f809b[i5] = fArr[i5];
                        i5++;
                    }
                }
            }
        }

        public e(e eVar) {
            this.f6256a = null;
            this.f6258c = 0;
            this.f6257b = eVar.f6257b;
            this.f6259d = eVar.f6259d;
            this.f6256a = E.d.e(eVar.f6256a);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        f fVar;
        int i4;
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            a.C0006a.d(drawable, resources, xmlPullParser, attributeSet, theme);
            return;
        }
        g gVar = this.f6227k;
        gVar.f6276b = new f();
        TypedArray d4 = i.d(resources, theme, attributeSet, C0828a.f6199a);
        g gVar2 = this.f6227k;
        f fVar2 = gVar2.f6276b;
        int i5 = !i.c(xmlPullParser, "tintMode") ? -1 : d4.getInt(6, -1);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        if (i5 == 3) {
            mode = PorterDuff.Mode.SRC_OVER;
        } else if (i5 != 5) {
            if (i5 != 9) {
                switch (i5) {
                    case 14:
                        mode = PorterDuff.Mode.MULTIPLY;
                        break;
                    case 15:
                        mode = PorterDuff.Mode.SCREEN;
                        break;
                    case 16:
                        mode = PorterDuff.Mode.ADD;
                        break;
                }
            } else {
                mode = PorterDuff.Mode.SRC_ATOP;
            }
        }
        gVar2.f6278d = mode;
        int i6 = 1;
        ColorStateList colorStateList = null;
        if (i.c(xmlPullParser, "tint")) {
            TypedValue typedValue = new TypedValue();
            d4.getValue(1, typedValue);
            int i7 = typedValue.type;
            if (i7 == 2) {
                throw new UnsupportedOperationException("Failed to resolve attribute at index 1: " + typedValue);
            } else if (i7 >= 28 && i7 <= 31) {
                colorStateList = ColorStateList.valueOf(typedValue.data);
            } else {
                Resources resources2 = d4.getResources();
                int resourceId = d4.getResourceId(1, 0);
                ThreadLocal<TypedValue> threadLocal = D.c.f513a;
                try {
                    colorStateList = D.c.a(resources2, resources2.getXml(resourceId), theme);
                } catch (Exception e4) {
                    Log.e("CSLCompat", "Failed to inflate ColorStateList.", e4);
                }
            }
        }
        ColorStateList colorStateList2 = colorStateList;
        if (colorStateList2 != null) {
            gVar2.f6277c = colorStateList2;
        }
        boolean z4 = gVar2.f6279e;
        if (i.c(xmlPullParser, "autoMirrored")) {
            z4 = d4.getBoolean(5, z4);
        }
        gVar2.f6279e = z4;
        float f4 = fVar2.f6269j;
        if (i.c(xmlPullParser, "viewportWidth")) {
            f4 = d4.getFloat(7, f4);
        }
        fVar2.f6269j = f4;
        float f5 = fVar2.f6270k;
        if (i.c(xmlPullParser, "viewportHeight")) {
            f5 = d4.getFloat(8, f5);
        }
        fVar2.f6270k = f5;
        if (fVar2.f6269j <= 0.0f) {
            throw new XmlPullParserException(d4.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        } else if (f5 > 0.0f) {
            fVar2.f6267h = d4.getDimension(3, fVar2.f6267h);
            float dimension = d4.getDimension(2, fVar2.f6268i);
            fVar2.f6268i = dimension;
            if (fVar2.f6267h <= 0.0f) {
                throw new XmlPullParserException(d4.getPositionDescription() + "<vector> tag requires width > 0");
            } else if (dimension > 0.0f) {
                float alpha = fVar2.getAlpha();
                if (i.c(xmlPullParser, "alpha")) {
                    alpha = d4.getFloat(4, alpha);
                }
                fVar2.setAlpha(alpha);
                String string = d4.getString(0);
                if (string != null) {
                    fVar2.f6272m = string;
                    fVar2.f6274o.put(string, fVar2);
                }
                d4.recycle();
                gVar.f6275a = getChangingConfigurations();
                gVar.f6284k = true;
                g gVar3 = this.f6227k;
                f fVar3 = gVar3.f6276b;
                ArrayDeque arrayDeque = new ArrayDeque();
                arrayDeque.push(fVar3.f6266g);
                int eventType = xmlPullParser.getEventType();
                int depth = xmlPullParser.getDepth() + 1;
                boolean z5 = true;
                for (int i8 = 3; eventType != i6 && (xmlPullParser.getDepth() >= depth || eventType != i8); i8 = 3) {
                    if (eventType == 2) {
                        String name = xmlPullParser.getName();
                        c cVar = (c) arrayDeque.peek();
                        boolean equals = "path".equals(name);
                        i4 = depth;
                        C0773b<String, Object> c0773b = fVar3.f6274o;
                        if (equals) {
                            b bVar = new b();
                            TypedArray d5 = i.d(resources, theme, attributeSet, C0828a.f6201c);
                            if (i.c(xmlPullParser, "pathData")) {
                                String string2 = d5.getString(0);
                                if (string2 != null) {
                                    bVar.f6257b = string2;
                                }
                                String string3 = d5.getString(2);
                                if (string3 != null) {
                                    bVar.f6256a = E.d.c(string3);
                                }
                                bVar.f6236g = i.a(d5, xmlPullParser, theme, "fillColor", 1);
                                float f6 = bVar.f6238i;
                                if (i.c(xmlPullParser, "fillAlpha")) {
                                    f6 = d5.getFloat(12, f6);
                                }
                                bVar.f6238i = f6;
                                int i9 = !i.c(xmlPullParser, "strokeLineCap") ? -1 : d5.getInt(8, -1);
                                Paint.Cap cap = bVar.f6242m;
                                if (i9 != 0) {
                                    fVar = fVar3;
                                    if (i9 == 1) {
                                        cap = Paint.Cap.ROUND;
                                    } else if (i9 == 2) {
                                        cap = Paint.Cap.SQUARE;
                                    }
                                } else {
                                    fVar = fVar3;
                                    cap = Paint.Cap.BUTT;
                                }
                                bVar.f6242m = cap;
                                int i10 = !i.c(xmlPullParser, "strokeLineJoin") ? -1 : d5.getInt(9, -1);
                                Paint.Join join = bVar.f6243n;
                                if (i10 == 0) {
                                    join = Paint.Join.MITER;
                                } else if (i10 == 1) {
                                    join = Paint.Join.ROUND;
                                } else if (i10 == 2) {
                                    join = Paint.Join.BEVEL;
                                }
                                bVar.f6243n = join;
                                float f7 = bVar.f6244o;
                                if (i.c(xmlPullParser, "strokeMiterLimit")) {
                                    f7 = d5.getFloat(10, f7);
                                }
                                bVar.f6244o = f7;
                                bVar.f6235e = i.a(d5, xmlPullParser, theme, "strokeColor", 3);
                                float f8 = bVar.f6237h;
                                if (i.c(xmlPullParser, "strokeAlpha")) {
                                    f8 = d5.getFloat(11, f8);
                                }
                                bVar.f6237h = f8;
                                float f9 = bVar.f;
                                if (i.c(xmlPullParser, "strokeWidth")) {
                                    f9 = d5.getFloat(4, f9);
                                }
                                bVar.f = f9;
                                float f10 = bVar.f6240k;
                                if (i.c(xmlPullParser, "trimPathEnd")) {
                                    f10 = d5.getFloat(6, f10);
                                }
                                bVar.f6240k = f10;
                                float f11 = bVar.f6241l;
                                if (i.c(xmlPullParser, "trimPathOffset")) {
                                    f11 = d5.getFloat(7, f11);
                                }
                                bVar.f6241l = f11;
                                float f12 = bVar.f6239j;
                                if (i.c(xmlPullParser, "trimPathStart")) {
                                    f12 = d5.getFloat(5, f12);
                                }
                                bVar.f6239j = f12;
                                int i11 = bVar.f6258c;
                                if (i.c(xmlPullParser, "fillType")) {
                                    i11 = d5.getInt(13, i11);
                                }
                                bVar.f6258c = i11;
                            } else {
                                fVar = fVar3;
                            }
                            d5.recycle();
                            cVar.f6246b.add(bVar);
                            if (bVar.getPathName() != null) {
                                c0773b.put(bVar.getPathName(), bVar);
                            }
                            gVar3.f6275a |= bVar.f6259d;
                            z5 = false;
                        } else {
                            fVar = fVar3;
                            if ("clip-path".equals(name)) {
                                a aVar = new a();
                                if (i.c(xmlPullParser, "pathData")) {
                                    TypedArray d6 = i.d(resources, theme, attributeSet, C0828a.f6202d);
                                    String string4 = d6.getString(0);
                                    if (string4 != null) {
                                        aVar.f6257b = string4;
                                    }
                                    String string5 = d6.getString(1);
                                    if (string5 != null) {
                                        aVar.f6256a = E.d.c(string5);
                                    }
                                    aVar.f6258c = !i.c(xmlPullParser, "fillType") ? 0 : d6.getInt(2, 0);
                                    d6.recycle();
                                }
                                cVar.f6246b.add(aVar);
                                if (aVar.getPathName() != null) {
                                    c0773b.put(aVar.getPathName(), aVar);
                                }
                                gVar3.f6275a = aVar.f6259d | gVar3.f6275a;
                            } else if ("group".equals(name)) {
                                c cVar2 = new c();
                                TypedArray d7 = i.d(resources, theme, attributeSet, C0828a.f6200b);
                                float f13 = cVar2.f6247c;
                                if (i.c(xmlPullParser, "rotation")) {
                                    f13 = d7.getFloat(5, f13);
                                }
                                cVar2.f6247c = f13;
                                cVar2.f6248d = d7.getFloat(1, cVar2.f6248d);
                                cVar2.f6249e = d7.getFloat(2, cVar2.f6249e);
                                float f14 = cVar2.f;
                                if (i.c(xmlPullParser, "scaleX")) {
                                    f14 = d7.getFloat(3, f14);
                                }
                                cVar2.f = f14;
                                float f15 = cVar2.f6250g;
                                if (i.c(xmlPullParser, "scaleY")) {
                                    f15 = d7.getFloat(4, f15);
                                }
                                cVar2.f6250g = f15;
                                float f16 = cVar2.f6251h;
                                if (i.c(xmlPullParser, "translateX")) {
                                    f16 = d7.getFloat(6, f16);
                                }
                                cVar2.f6251h = f16;
                                float f17 = cVar2.f6252i;
                                if (i.c(xmlPullParser, "translateY")) {
                                    f17 = d7.getFloat(7, f17);
                                }
                                cVar2.f6252i = f17;
                                String string6 = d7.getString(0);
                                if (string6 != null) {
                                    cVar2.f6255l = string6;
                                }
                                cVar2.c();
                                d7.recycle();
                                cVar.f6246b.add(cVar2);
                                arrayDeque.push(cVar2);
                                if (cVar2.getGroupName() != null) {
                                    c0773b.put(cVar2.getGroupName(), cVar2);
                                }
                                gVar3.f6275a = cVar2.f6254k | gVar3.f6275a;
                            }
                        }
                    } else {
                        fVar = fVar3;
                        i4 = depth;
                        if (eventType == 3 && "group".equals(xmlPullParser.getName())) {
                            arrayDeque.pop();
                        }
                    }
                    eventType = xmlPullParser.next();
                    depth = i4;
                    fVar3 = fVar;
                    i6 = 1;
                }
                if (!z5) {
                    this.f6228l = a(gVar.f6277c, gVar.f6278d);
                    return;
                }
                throw new XmlPullParserException("no path defined");
            } else {
                throw new XmlPullParserException(d4.getPositionDescription() + "<vector> tag requires height > 0");
            }
        } else {
            throw new XmlPullParserException(d4.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends d {

        /* renamed from: a  reason: collision with root package name */
        public final Matrix f6245a;

        /* renamed from: b  reason: collision with root package name */
        public final ArrayList<d> f6246b;

        /* renamed from: c  reason: collision with root package name */
        public float f6247c;

        /* renamed from: d  reason: collision with root package name */
        public float f6248d;

        /* renamed from: e  reason: collision with root package name */
        public float f6249e;
        public float f;

        /* renamed from: g  reason: collision with root package name */
        public float f6250g;

        /* renamed from: h  reason: collision with root package name */
        public float f6251h;

        /* renamed from: i  reason: collision with root package name */
        public float f6252i;

        /* renamed from: j  reason: collision with root package name */
        public final Matrix f6253j;

        /* renamed from: k  reason: collision with root package name */
        public final int f6254k;

        /* renamed from: l  reason: collision with root package name */
        public String f6255l;

        public c() {
            this.f6245a = new Matrix();
            this.f6246b = new ArrayList<>();
            this.f6247c = 0.0f;
            this.f6248d = 0.0f;
            this.f6249e = 0.0f;
            this.f = 1.0f;
            this.f6250g = 1.0f;
            this.f6251h = 0.0f;
            this.f6252i = 0.0f;
            this.f6253j = new Matrix();
            this.f6255l = null;
        }

        @Override // v0.h.d
        public final boolean a() {
            int i4 = 0;
            while (true) {
                ArrayList<d> arrayList = this.f6246b;
                if (i4 >= arrayList.size()) {
                    return false;
                }
                if (arrayList.get(i4).a()) {
                    return true;
                }
                i4++;
            }
        }

        @Override // v0.h.d
        public final boolean b(int[] iArr) {
            int i4 = 0;
            boolean z4 = false;
            while (true) {
                ArrayList<d> arrayList = this.f6246b;
                if (i4 < arrayList.size()) {
                    z4 |= arrayList.get(i4).b(iArr);
                    i4++;
                } else {
                    return z4;
                }
            }
        }

        public final void c() {
            Matrix matrix = this.f6253j;
            matrix.reset();
            matrix.postTranslate(-this.f6248d, -this.f6249e);
            matrix.postScale(this.f, this.f6250g);
            matrix.postRotate(this.f6247c, 0.0f, 0.0f);
            matrix.postTranslate(this.f6251h + this.f6248d, this.f6252i + this.f6249e);
        }

        public String getGroupName() {
            return this.f6255l;
        }

        public Matrix getLocalMatrix() {
            return this.f6253j;
        }

        public float getPivotX() {
            return this.f6248d;
        }

        public float getPivotY() {
            return this.f6249e;
        }

        public float getRotation() {
            return this.f6247c;
        }

        public float getScaleX() {
            return this.f;
        }

        public float getScaleY() {
            return this.f6250g;
        }

        public float getTranslateX() {
            return this.f6251h;
        }

        public float getTranslateY() {
            return this.f6252i;
        }

        public void setPivotX(float f) {
            if (f != this.f6248d) {
                this.f6248d = f;
                c();
            }
        }

        public void setPivotY(float f) {
            if (f != this.f6249e) {
                this.f6249e = f;
                c();
            }
        }

        public void setRotation(float f) {
            if (f != this.f6247c) {
                this.f6247c = f;
                c();
            }
        }

        public void setScaleX(float f) {
            if (f != this.f) {
                this.f = f;
                c();
            }
        }

        public void setScaleY(float f) {
            if (f != this.f6250g) {
                this.f6250g = f;
                c();
            }
        }

        public void setTranslateX(float f) {
            if (f != this.f6251h) {
                this.f6251h = f;
                c();
            }
        }

        public void setTranslateY(float f) {
            if (f != this.f6252i) {
                this.f6252i = f;
                c();
            }
        }

        /* JADX WARN: Type inference failed for: r4v6, types: [v0.h$e, v0.h$b] */
        public c(c cVar, C0773b<String, Object> c0773b) {
            e eVar;
            this.f6245a = new Matrix();
            this.f6246b = new ArrayList<>();
            this.f6247c = 0.0f;
            this.f6248d = 0.0f;
            this.f6249e = 0.0f;
            this.f = 1.0f;
            this.f6250g = 1.0f;
            this.f6251h = 0.0f;
            this.f6252i = 0.0f;
            Matrix matrix = new Matrix();
            this.f6253j = matrix;
            this.f6255l = null;
            this.f6247c = cVar.f6247c;
            this.f6248d = cVar.f6248d;
            this.f6249e = cVar.f6249e;
            this.f = cVar.f;
            this.f6250g = cVar.f6250g;
            this.f6251h = cVar.f6251h;
            this.f6252i = cVar.f6252i;
            String str = cVar.f6255l;
            this.f6255l = str;
            this.f6254k = cVar.f6254k;
            if (str != null) {
                c0773b.put(str, this);
            }
            matrix.set(cVar.f6253j);
            ArrayList<d> arrayList = cVar.f6246b;
            for (int i4 = 0; i4 < arrayList.size(); i4++) {
                d dVar = arrayList.get(i4);
                if (dVar instanceof c) {
                    this.f6246b.add(new c((c) dVar, c0773b));
                } else {
                    if (dVar instanceof b) {
                        b bVar = (b) dVar;
                        ?? eVar2 = new e(bVar);
                        eVar2.f = 0.0f;
                        eVar2.f6237h = 1.0f;
                        eVar2.f6238i = 1.0f;
                        eVar2.f6239j = 0.0f;
                        eVar2.f6240k = 1.0f;
                        eVar2.f6241l = 0.0f;
                        eVar2.f6242m = Paint.Cap.BUTT;
                        eVar2.f6243n = Paint.Join.MITER;
                        eVar2.f6244o = 4.0f;
                        eVar2.f6235e = bVar.f6235e;
                        eVar2.f = bVar.f;
                        eVar2.f6237h = bVar.f6237h;
                        eVar2.f6236g = bVar.f6236g;
                        eVar2.f6258c = bVar.f6258c;
                        eVar2.f6238i = bVar.f6238i;
                        eVar2.f6239j = bVar.f6239j;
                        eVar2.f6240k = bVar.f6240k;
                        eVar2.f6241l = bVar.f6241l;
                        eVar2.f6242m = bVar.f6242m;
                        eVar2.f6243n = bVar.f6243n;
                        eVar2.f6244o = bVar.f6244o;
                        eVar = eVar2;
                    } else if (dVar instanceof a) {
                        eVar = new e((a) dVar);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    this.f6246b.add(eVar);
                    String str2 = eVar.f6257b;
                    if (str2 != null) {
                        c0773b.put(str2, eVar);
                    }
                }
            }
        }
    }

    public h(g gVar) {
        this.f6231o = true;
        this.f6232p = new float[9];
        this.f6233q = new Matrix();
        this.f6234r = new Rect();
        this.f6227k = gVar;
        this.f6228l = a(gVar.f6277c, gVar.f6278d);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class f {

        /* renamed from: p  reason: collision with root package name */
        public static final Matrix f6260p = new Matrix();

        /* renamed from: a  reason: collision with root package name */
        public final Path f6261a;

        /* renamed from: b  reason: collision with root package name */
        public final Path f6262b;

        /* renamed from: c  reason: collision with root package name */
        public final Matrix f6263c;

        /* renamed from: d  reason: collision with root package name */
        public Paint f6264d;

        /* renamed from: e  reason: collision with root package name */
        public Paint f6265e;
        public PathMeasure f;

        /* renamed from: g  reason: collision with root package name */
        public final c f6266g;

        /* renamed from: h  reason: collision with root package name */
        public float f6267h;

        /* renamed from: i  reason: collision with root package name */
        public float f6268i;

        /* renamed from: j  reason: collision with root package name */
        public float f6269j;

        /* renamed from: k  reason: collision with root package name */
        public float f6270k;

        /* renamed from: l  reason: collision with root package name */
        public int f6271l;

        /* renamed from: m  reason: collision with root package name */
        public String f6272m;

        /* renamed from: n  reason: collision with root package name */
        public Boolean f6273n;

        /* renamed from: o  reason: collision with root package name */
        public final C0773b<String, Object> f6274o;

        public f() {
            this.f6263c = new Matrix();
            this.f6267h = 0.0f;
            this.f6268i = 0.0f;
            this.f6269j = 0.0f;
            this.f6270k = 0.0f;
            this.f6271l = 255;
            this.f6272m = null;
            this.f6273n = null;
            this.f6274o = new C0773b<>();
            this.f6266g = new c();
            this.f6261a = new Path();
            this.f6262b = new Path();
        }

        /* JADX WARN: Code restructure failed: missing block: B:32:0x00e6, code lost:
            if (r0.f6240k != 1.0f) goto L69;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r11v0 */
        /* JADX WARN: Type inference failed for: r11v1, types: [boolean] */
        /* JADX WARN: Type inference failed for: r11v16 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void a(v0.h.c r19, android.graphics.Matrix r20, android.graphics.Canvas r21, int r22, int r23) {
            /*
                Method dump skipped, instructions count: 552
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: v0.h.f.a(v0.h$c, android.graphics.Matrix, android.graphics.Canvas, int, int):void");
        }

        public float getAlpha() {
            return getRootAlpha() / 255.0f;
        }

        public int getRootAlpha() {
            return this.f6271l;
        }

        public void setAlpha(float f) {
            setRootAlpha((int) (f * 255.0f));
        }

        public void setRootAlpha(int i4) {
            this.f6271l = i4;
        }

        public f(f fVar) {
            this.f6263c = new Matrix();
            this.f6267h = 0.0f;
            this.f6268i = 0.0f;
            this.f6269j = 0.0f;
            this.f6270k = 0.0f;
            this.f6271l = 255;
            this.f6272m = null;
            this.f6273n = null;
            C0773b<String, Object> c0773b = new C0773b<>();
            this.f6274o = c0773b;
            this.f6266g = new c(fVar.f6266g, c0773b);
            this.f6261a = new Path(fVar.f6261a);
            this.f6262b = new Path(fVar.f6262b);
            this.f6267h = fVar.f6267h;
            this.f6268i = fVar.f6268i;
            this.f6269j = fVar.f6269j;
            this.f6270k = fVar.f6270k;
            this.f6271l = fVar.f6271l;
            this.f6272m = fVar.f6272m;
            String str = fVar.f6272m;
            if (str != null) {
                c0773b.put(str, this);
            }
            this.f6273n = fVar.f6273n;
        }
    }
}
