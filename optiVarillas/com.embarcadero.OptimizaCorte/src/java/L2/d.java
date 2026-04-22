package L2;

import D.f;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import q2.C0771a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public final ColorStateList f1491a;

    /* renamed from: b  reason: collision with root package name */
    public final String f1492b;

    /* renamed from: c  reason: collision with root package name */
    public final int f1493c;

    /* renamed from: d  reason: collision with root package name */
    public final int f1494d;

    /* renamed from: e  reason: collision with root package name */
    public final float f1495e;
    public final float f;

    /* renamed from: g  reason: collision with root package name */
    public final float f1496g;

    /* renamed from: h  reason: collision with root package name */
    public final boolean f1497h;

    /* renamed from: i  reason: collision with root package name */
    public final float f1498i;

    /* renamed from: j  reason: collision with root package name */
    public ColorStateList f1499j;

    /* renamed from: k  reason: collision with root package name */
    public float f1500k;

    /* renamed from: l  reason: collision with root package name */
    public final int f1501l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f1502m = false;

    /* renamed from: n  reason: collision with root package name */
    public Typeface f1503n;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a extends f.e {

        /* renamed from: a  reason: collision with root package name */
        public final /* synthetic */ G3.g f1504a;

        public a(G3.g gVar) {
            this.f1504a = gVar;
        }

        @Override // D.f.e
        public final void b(int i4) {
            d.this.f1502m = true;
            this.f1504a.B(i4);
        }

        @Override // D.f.e
        public final void c(Typeface typeface) {
            d dVar = d.this;
            dVar.f1503n = Typeface.create(typeface, dVar.f1493c);
            dVar.f1502m = true;
            this.f1504a.C(dVar.f1503n, false);
        }
    }

    public d(Context context, int i4) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i4, C0771a.f5604B);
        this.f1500k = obtainStyledAttributes.getDimension(0, 0.0f);
        this.f1499j = c.a(context, obtainStyledAttributes, 3);
        c.a(context, obtainStyledAttributes, 4);
        c.a(context, obtainStyledAttributes, 5);
        this.f1493c = obtainStyledAttributes.getInt(2, 0);
        this.f1494d = obtainStyledAttributes.getInt(1, 1);
        int i5 = obtainStyledAttributes.hasValue(12) ? 12 : 10;
        this.f1501l = obtainStyledAttributes.getResourceId(i5, 0);
        this.f1492b = obtainStyledAttributes.getString(i5);
        obtainStyledAttributes.getBoolean(14, false);
        this.f1491a = c.a(context, obtainStyledAttributes, 6);
        this.f1495e = obtainStyledAttributes.getFloat(7, 0.0f);
        this.f = obtainStyledAttributes.getFloat(8, 0.0f);
        this.f1496g = obtainStyledAttributes.getFloat(9, 0.0f);
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(i4, C0771a.f5625s);
        this.f1497h = obtainStyledAttributes2.hasValue(0);
        this.f1498i = obtainStyledAttributes2.getFloat(0, 0.0f);
        obtainStyledAttributes2.recycle();
    }

    public final void a() {
        String str;
        Typeface typeface = this.f1503n;
        int i4 = this.f1493c;
        if (typeface == null && (str = this.f1492b) != null) {
            this.f1503n = Typeface.create(str, i4);
        }
        if (this.f1503n == null) {
            int i5 = this.f1494d;
            if (i5 != 1) {
                if (i5 != 2) {
                    if (i5 != 3) {
                        this.f1503n = Typeface.DEFAULT;
                    } else {
                        this.f1503n = Typeface.MONOSPACE;
                    }
                } else {
                    this.f1503n = Typeface.SERIF;
                }
            } else {
                this.f1503n = Typeface.SANS_SERIF;
            }
            this.f1503n = Typeface.create(this.f1503n, i4);
        }
    }

    public final Typeface b(Context context) {
        if (this.f1502m) {
            return this.f1503n;
        }
        if (!context.isRestricted()) {
            try {
                Typeface b4 = D.f.b(context, this.f1501l);
                this.f1503n = b4;
                if (b4 != null) {
                    this.f1503n = Typeface.create(b4, this.f1493c);
                }
            } catch (Resources.NotFoundException | UnsupportedOperationException unused) {
            } catch (Exception e4) {
                Log.d("TextAppearance", "Error loading font " + this.f1492b, e4);
            }
        }
        a();
        this.f1502m = true;
        return this.f1503n;
    }

    public final void c(Context context, G3.g gVar) {
        if (d(context)) {
            b(context);
        } else {
            a();
        }
        int i4 = this.f1501l;
        if (i4 == 0) {
            this.f1502m = true;
        }
        if (this.f1502m) {
            gVar.C(this.f1503n, true);
            return;
        }
        try {
            a aVar = new a(gVar);
            ThreadLocal<TypedValue> threadLocal = D.f.f527a;
            if (context.isRestricted()) {
                aVar.a(-4);
            } else {
                D.f.c(context, i4, new TypedValue(), 0, aVar, false, false);
            }
        } catch (Resources.NotFoundException unused) {
            this.f1502m = true;
            gVar.B(1);
        } catch (Exception e4) {
            Log.d("TextAppearance", "Error loading font " + this.f1492b, e4);
            this.f1502m = true;
            gVar.B(-3);
        }
    }

    public final boolean d(Context context) {
        Typeface typeface = null;
        int i4 = this.f1501l;
        if (i4 != 0) {
            ThreadLocal<TypedValue> threadLocal = D.f.f527a;
            if (!context.isRestricted()) {
                typeface = D.f.c(context, i4, new TypedValue(), 0, null, false, true);
            }
        }
        if (typeface != null) {
            return true;
        }
        return false;
    }

    public final void e(Context context, TextPaint textPaint, G3.g gVar) {
        int i4;
        int i5;
        f(context, textPaint, gVar);
        ColorStateList colorStateList = this.f1499j;
        if (colorStateList != null) {
            i4 = colorStateList.getColorForState(textPaint.drawableState, colorStateList.getDefaultColor());
        } else {
            i4 = -16777216;
        }
        textPaint.setColor(i4);
        ColorStateList colorStateList2 = this.f1491a;
        if (colorStateList2 != null) {
            i5 = colorStateList2.getColorForState(textPaint.drawableState, colorStateList2.getDefaultColor());
        } else {
            i5 = 0;
        }
        textPaint.setShadowLayer(this.f1496g, this.f1495e, this.f, i5);
    }

    public final void f(Context context, TextPaint textPaint, G3.g gVar) {
        if (d(context)) {
            g(context, textPaint, b(context));
            return;
        }
        a();
        g(context, textPaint, this.f1503n);
        c(context, new e(this, context, textPaint, gVar));
    }

    public final void g(Context context, TextPaint textPaint, Typeface typeface) {
        boolean z4;
        float f;
        Typeface a4 = g.a(context.getResources().getConfiguration(), typeface);
        if (a4 != null) {
            typeface = a4;
        }
        textPaint.setTypeface(typeface);
        int i4 = (~typeface.getStyle()) & this.f1493c;
        if ((i4 & 1) != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        textPaint.setFakeBoldText(z4);
        if ((i4 & 2) != 0) {
            f = -0.25f;
        } else {
            f = 0.0f;
        }
        textPaint.setTextSkewX(f);
        textPaint.setTextSize(this.f1500k);
        if (this.f1497h) {
            textPaint.setLetterSpacing(this.f1498i);
        }
    }
}
