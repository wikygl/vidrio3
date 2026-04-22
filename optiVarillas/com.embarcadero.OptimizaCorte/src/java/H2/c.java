package H2;

import H2.m;
import K.h;
import M.O;
import M.V;
import a0.C0340b;
import android.animation.TimeInterpolator;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import java.util.WeakHashMap;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class c {

    /* renamed from: A  reason: collision with root package name */
    public CharSequence f1022A;

    /* renamed from: B  reason: collision with root package name */
    public CharSequence f1023B;

    /* renamed from: C  reason: collision with root package name */
    public boolean f1024C;

    /* renamed from: E  reason: collision with root package name */
    public Bitmap f1026E;

    /* renamed from: F  reason: collision with root package name */
    public float f1027F;

    /* renamed from: G  reason: collision with root package name */
    public float f1028G;

    /* renamed from: H  reason: collision with root package name */
    public float f1029H;

    /* renamed from: I  reason: collision with root package name */
    public float f1030I;

    /* renamed from: J  reason: collision with root package name */
    public float f1031J;

    /* renamed from: K  reason: collision with root package name */
    public int f1032K;

    /* renamed from: L  reason: collision with root package name */
    public int[] f1033L;

    /* renamed from: M  reason: collision with root package name */
    public boolean f1034M;

    /* renamed from: N  reason: collision with root package name */
    public final TextPaint f1035N;

    /* renamed from: O  reason: collision with root package name */
    public final TextPaint f1036O;

    /* renamed from: P  reason: collision with root package name */
    public TimeInterpolator f1037P;

    /* renamed from: Q  reason: collision with root package name */
    public TimeInterpolator f1038Q;

    /* renamed from: R  reason: collision with root package name */
    public float f1039R;

    /* renamed from: S  reason: collision with root package name */
    public float f1040S;

    /* renamed from: T  reason: collision with root package name */
    public float f1041T;

    /* renamed from: U  reason: collision with root package name */
    public ColorStateList f1042U;

    /* renamed from: V  reason: collision with root package name */
    public float f1043V;

    /* renamed from: W  reason: collision with root package name */
    public float f1044W;

    /* renamed from: X  reason: collision with root package name */
    public float f1045X;

    /* renamed from: Y  reason: collision with root package name */
    public StaticLayout f1046Y;

    /* renamed from: Z  reason: collision with root package name */
    public float f1047Z;

    /* renamed from: a  reason: collision with root package name */
    public final View f1048a;

    /* renamed from: a0  reason: collision with root package name */
    public float f1049a0;

    /* renamed from: b  reason: collision with root package name */
    public float f1050b;

    /* renamed from: b0  reason: collision with root package name */
    public float f1051b0;

    /* renamed from: c  reason: collision with root package name */
    public final Rect f1052c;

    /* renamed from: c0  reason: collision with root package name */
    public CharSequence f1053c0;

    /* renamed from: d  reason: collision with root package name */
    public final Rect f1054d;

    /* renamed from: e  reason: collision with root package name */
    public final RectF f1056e;

    /* renamed from: j  reason: collision with root package name */
    public ColorStateList f1062j;

    /* renamed from: k  reason: collision with root package name */
    public ColorStateList f1063k;

    /* renamed from: l  reason: collision with root package name */
    public float f1064l;

    /* renamed from: m  reason: collision with root package name */
    public float f1065m;

    /* renamed from: n  reason: collision with root package name */
    public float f1066n;

    /* renamed from: o  reason: collision with root package name */
    public float f1067o;

    /* renamed from: p  reason: collision with root package name */
    public float f1068p;

    /* renamed from: q  reason: collision with root package name */
    public float f1069q;

    /* renamed from: r  reason: collision with root package name */
    public Typeface f1070r;

    /* renamed from: s  reason: collision with root package name */
    public Typeface f1071s;

    /* renamed from: t  reason: collision with root package name */
    public Typeface f1072t;

    /* renamed from: u  reason: collision with root package name */
    public Typeface f1073u;

    /* renamed from: v  reason: collision with root package name */
    public Typeface f1074v;

    /* renamed from: w  reason: collision with root package name */
    public Typeface f1075w;

    /* renamed from: x  reason: collision with root package name */
    public Typeface f1076x;

    /* renamed from: y  reason: collision with root package name */
    public L2.a f1077y;
    public int f = 16;

    /* renamed from: g  reason: collision with root package name */
    public int f1059g = 16;

    /* renamed from: h  reason: collision with root package name */
    public float f1060h = 15.0f;

    /* renamed from: i  reason: collision with root package name */
    public float f1061i = 15.0f;

    /* renamed from: z  reason: collision with root package name */
    public final TextUtils.TruncateAt f1078z = TextUtils.TruncateAt.END;

    /* renamed from: D  reason: collision with root package name */
    public final boolean f1025D = true;

    /* renamed from: d0  reason: collision with root package name */
    public final int f1055d0 = 1;

    /* renamed from: e0  reason: collision with root package name */
    public final float f1057e0 = 1.0f;

    /* renamed from: f0  reason: collision with root package name */
    public final int f1058f0 = m.f1095m;

    public c(View view) {
        this.f1048a = view;
        TextPaint textPaint = new TextPaint(129);
        this.f1035N = textPaint;
        this.f1036O = new TextPaint(textPaint);
        this.f1054d = new Rect();
        this.f1052c = new Rect();
        this.f1056e = new RectF();
        g(view.getContext().getResources().getConfiguration());
    }

    public static int a(float f, int i4, int i5) {
        float f4 = 1.0f - f;
        return Color.argb(Math.round((Color.alpha(i5) * f) + (Color.alpha(i4) * f4)), Math.round((Color.red(i5) * f) + (Color.red(i4) * f4)), Math.round((Color.green(i5) * f) + (Color.green(i4) * f4)), Math.round((Color.blue(i5) * f) + (Color.blue(i4) * f4)));
    }

    public static float f(float f, float f4, float f5, TimeInterpolator timeInterpolator) {
        if (timeInterpolator != null) {
            f5 = timeInterpolator.getInterpolation(f5);
        }
        return C0783a.a(f, f4, f5);
    }

    public final boolean b(CharSequence charSequence) {
        h.d dVar;
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        boolean z4 = true;
        if (this.f1048a.getLayoutDirection() != 1) {
            z4 = false;
        }
        if (this.f1025D) {
            if (z4) {
                dVar = K.h.f1262d;
            } else {
                dVar = K.h.f1261c;
            }
            return dVar.b(charSequence, charSequence.length());
        }
        return z4;
    }

    public final void c(float f, boolean z4) {
        boolean z5;
        float f4;
        float f5;
        Typeface typeface;
        boolean z6;
        StaticLayout staticLayout;
        Layout.Alignment alignment;
        boolean z7;
        boolean z8;
        boolean z9;
        StaticLayout staticLayout2;
        boolean z10;
        boolean z11;
        if (this.f1022A == null) {
            return;
        }
        float width = this.f1054d.width();
        float width2 = this.f1052c.width();
        if (Math.abs(f - 1.0f) < 1.0E-5f) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5) {
            f4 = this.f1061i;
            f5 = this.f1043V;
            this.f1027F = 1.0f;
            typeface = this.f1070r;
        } else {
            float f6 = this.f1060h;
            float f7 = this.f1044W;
            Typeface typeface2 = this.f1073u;
            if (Math.abs(f - 0.0f) < 1.0E-5f) {
                this.f1027F = 1.0f;
            } else {
                this.f1027F = f(this.f1060h, this.f1061i, f, this.f1038Q) / this.f1060h;
            }
            float f8 = this.f1061i / this.f1060h;
            float f9 = width2 * f8;
            if (!z4 && f9 > width) {
                width = Math.min(width / f8, width2);
            } else {
                width = width2;
            }
            f4 = f6;
            f5 = f7;
            typeface = typeface2;
        }
        TextPaint textPaint = this.f1035N;
        if (width > 0.0f) {
            if (this.f1028G != f4) {
                z7 = true;
            } else {
                z7 = false;
            }
            if (this.f1045X != f5) {
                z8 = true;
            } else {
                z8 = false;
            }
            if (this.f1076x != typeface) {
                z9 = true;
            } else {
                z9 = false;
            }
            if (this.f1046Y != null && width != staticLayout2.getWidth()) {
                z10 = true;
            } else {
                z10 = false;
            }
            if (!z7 && !z8 && !z10 && !z9 && !this.f1034M) {
                z6 = false;
            } else {
                z6 = true;
            }
            this.f1028G = f4;
            this.f1045X = f5;
            this.f1076x = typeface;
            this.f1034M = false;
            if (this.f1027F != 1.0f) {
                z11 = true;
            } else {
                z11 = false;
            }
            textPaint.setLinearText(z11);
        } else {
            z6 = false;
        }
        if (this.f1023B == null || z6) {
            textPaint.setTextSize(this.f1028G);
            textPaint.setTypeface(this.f1076x);
            textPaint.setLetterSpacing(this.f1045X);
            boolean b4 = b(this.f1022A);
            this.f1024C = b4;
            int i4 = (this.f1055d0 <= 1 || b4) ? 1 : 1;
            try {
                if (i4 == 1) {
                    alignment = Layout.Alignment.ALIGN_NORMAL;
                } else {
                    int absoluteGravity = Gravity.getAbsoluteGravity(this.f, b4 ? 1 : 0) & 7;
                    if (absoluteGravity != 1) {
                        if (absoluteGravity != 5) {
                            if (this.f1024C) {
                                alignment = Layout.Alignment.ALIGN_OPPOSITE;
                            } else {
                                alignment = Layout.Alignment.ALIGN_NORMAL;
                            }
                        } else if (this.f1024C) {
                            alignment = Layout.Alignment.ALIGN_NORMAL;
                        } else {
                            alignment = Layout.Alignment.ALIGN_OPPOSITE;
                        }
                    } else {
                        alignment = Layout.Alignment.ALIGN_CENTER;
                    }
                }
                m mVar = new m(this.f1022A, textPaint, (int) width);
                mVar.f1109l = this.f1078z;
                mVar.f1108k = b4;
                mVar.f1103e = alignment;
                mVar.f1107j = false;
                mVar.f = i4;
                float f10 = this.f1057e0;
                mVar.f1104g = 0.0f;
                mVar.f1105h = f10;
                mVar.f1106i = this.f1058f0;
                staticLayout = mVar.a();
            } catch (m.a e4) {
                Log.e("CollapsingTextHelper", e4.getCause().getMessage(), e4);
                staticLayout = null;
            }
            staticLayout.getClass();
            this.f1046Y = staticLayout;
            this.f1023B = staticLayout.getText();
        }
    }

    public final float d() {
        TextPaint textPaint = this.f1036O;
        textPaint.setTextSize(this.f1061i);
        textPaint.setTypeface(this.f1070r);
        textPaint.setLetterSpacing(this.f1043V);
        return -textPaint.ascent();
    }

    public final int e(ColorStateList colorStateList) {
        if (colorStateList == null) {
            return 0;
        }
        int[] iArr = this.f1033L;
        if (iArr != null) {
            return colorStateList.getColorForState(iArr, 0);
        }
        return colorStateList.getDefaultColor();
    }

    public final void g(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 31) {
            Typeface typeface = this.f1072t;
            if (typeface != null) {
                this.f1071s = L2.g.a(configuration, typeface);
            }
            Typeface typeface2 = this.f1075w;
            if (typeface2 != null) {
                this.f1074v = L2.g.a(configuration, typeface2);
            }
            Typeface typeface3 = this.f1071s;
            if (typeface3 == null) {
                typeface3 = this.f1072t;
            }
            this.f1070r = typeface3;
            Typeface typeface4 = this.f1074v;
            if (typeface4 == null) {
                typeface4 = this.f1075w;
            }
            this.f1073u = typeface4;
            h(true);
        }
    }

    public final void h(boolean z4) {
        float f;
        float f4;
        StaticLayout staticLayout;
        View view = this.f1048a;
        if ((view.getHeight() > 0 && view.getWidth() > 0) || z4) {
            c(1.0f, z4);
            CharSequence charSequence = this.f1023B;
            TextPaint textPaint = this.f1035N;
            if (charSequence != null && (staticLayout = this.f1046Y) != null) {
                this.f1053c0 = TextUtils.ellipsize(charSequence, textPaint, staticLayout.getWidth(), this.f1078z);
            }
            CharSequence charSequence2 = this.f1053c0;
            if (charSequence2 != null) {
                this.f1047Z = textPaint.measureText(charSequence2, 0, charSequence2.length());
            } else {
                this.f1047Z = 0.0f;
            }
            int absoluteGravity = Gravity.getAbsoluteGravity(this.f1059g, this.f1024C ? 1 : 0);
            int i4 = absoluteGravity & 112;
            Rect rect = this.f1054d;
            if (i4 != 48) {
                if (i4 != 80) {
                    this.f1065m = rect.centerY() - ((textPaint.descent() - textPaint.ascent()) / 2.0f);
                } else {
                    this.f1065m = textPaint.ascent() + rect.bottom;
                }
            } else {
                this.f1065m = rect.top;
            }
            int i5 = absoluteGravity & 8388615;
            if (i5 != 1) {
                if (i5 != 5) {
                    this.f1067o = rect.left;
                } else {
                    this.f1067o = rect.right - this.f1047Z;
                }
            } else {
                this.f1067o = rect.centerX() - (this.f1047Z / 2.0f);
            }
            c(0.0f, z4);
            StaticLayout staticLayout2 = this.f1046Y;
            if (staticLayout2 != null) {
                f = staticLayout2.getHeight();
            } else {
                f = 0.0f;
            }
            StaticLayout staticLayout3 = this.f1046Y;
            if (staticLayout3 != null && this.f1055d0 > 1) {
                f4 = staticLayout3.getWidth();
            } else {
                CharSequence charSequence3 = this.f1023B;
                if (charSequence3 != null) {
                    f4 = textPaint.measureText(charSequence3, 0, charSequence3.length());
                } else {
                    f4 = 0.0f;
                }
            }
            StaticLayout staticLayout4 = this.f1046Y;
            if (staticLayout4 != null) {
                staticLayout4.getLineCount();
            }
            int absoluteGravity2 = Gravity.getAbsoluteGravity(this.f, this.f1024C ? 1 : 0);
            int i6 = absoluteGravity2 & 112;
            Rect rect2 = this.f1052c;
            if (i6 != 48) {
                if (i6 != 80) {
                    this.f1064l = rect2.centerY() - (f / 2.0f);
                } else {
                    this.f1064l = textPaint.descent() + (rect2.bottom - f);
                }
            } else {
                this.f1064l = rect2.top;
            }
            int i7 = absoluteGravity2 & 8388615;
            if (i7 != 1) {
                if (i7 != 5) {
                    this.f1066n = rect2.left;
                } else {
                    this.f1066n = rect2.right - f4;
                }
            } else {
                this.f1066n = rect2.centerX() - (f4 / 2.0f);
            }
            Bitmap bitmap = this.f1026E;
            if (bitmap != null) {
                bitmap.recycle();
                this.f1026E = null;
            }
            l(this.f1050b);
            float f5 = this.f1050b;
            float f6 = f(rect2.left, rect.left, f5, this.f1037P);
            RectF rectF = this.f1056e;
            rectF.left = f6;
            rectF.top = f(this.f1064l, this.f1065m, f5, this.f1037P);
            rectF.right = f(rect2.right, rect.right, f5, this.f1037P);
            rectF.bottom = f(rect2.bottom, rect.bottom, f5, this.f1037P);
            this.f1068p = f(this.f1066n, this.f1067o, f5, this.f1037P);
            this.f1069q = f(this.f1064l, this.f1065m, f5, this.f1037P);
            l(f5);
            C0340b c0340b = C0783a.f5710b;
            this.f1049a0 = 1.0f - f(0.0f, 1.0f, 1.0f - f5, c0340b);
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            view.postInvalidateOnAnimation();
            this.f1051b0 = f(1.0f, 0.0f, f5, c0340b);
            view.postInvalidateOnAnimation();
            ColorStateList colorStateList = this.f1063k;
            ColorStateList colorStateList2 = this.f1062j;
            if (colorStateList != colorStateList2) {
                textPaint.setColor(a(f5, e(colorStateList2), e(this.f1063k)));
            } else {
                textPaint.setColor(e(colorStateList));
            }
            float f7 = this.f1043V;
            float f8 = this.f1044W;
            if (f7 != f8) {
                textPaint.setLetterSpacing(f(f8, f7, f5, c0340b));
            } else {
                textPaint.setLetterSpacing(f7);
            }
            this.f1029H = C0783a.a(0.0f, this.f1039R, f5);
            this.f1030I = C0783a.a(0.0f, this.f1040S, f5);
            this.f1031J = C0783a.a(0.0f, this.f1041T, f5);
            int a4 = a(f5, 0, e(this.f1042U));
            this.f1032K = a4;
            textPaint.setShadowLayer(this.f1029H, this.f1030I, this.f1031J, a4);
            view.postInvalidateOnAnimation();
        }
    }

    public final void i(ColorStateList colorStateList) {
        if (this.f1063k != colorStateList || this.f1062j != colorStateList) {
            this.f1063k = colorStateList;
            this.f1062j = colorStateList;
            h(false);
        }
    }

    public final boolean j(Typeface typeface) {
        L2.a aVar = this.f1077y;
        if (aVar != null) {
            aVar.f1490m = true;
        }
        if (this.f1072t != typeface) {
            this.f1072t = typeface;
            Typeface a4 = L2.g.a(this.f1048a.getContext().getResources().getConfiguration(), typeface);
            this.f1071s = a4;
            if (a4 == null) {
                a4 = this.f1072t;
            }
            this.f1070r = a4;
            return true;
        }
        return false;
    }

    public final void k(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f != this.f1050b) {
            this.f1050b = f;
            Rect rect = this.f1052c;
            Rect rect2 = this.f1054d;
            float f4 = f(rect.left, rect2.left, f, this.f1037P);
            RectF rectF = this.f1056e;
            rectF.left = f4;
            rectF.top = f(this.f1064l, this.f1065m, f, this.f1037P);
            rectF.right = f(rect.right, rect2.right, f, this.f1037P);
            rectF.bottom = f(rect.bottom, rect2.bottom, f, this.f1037P);
            this.f1068p = f(this.f1066n, this.f1067o, f, this.f1037P);
            this.f1069q = f(this.f1064l, this.f1065m, f, this.f1037P);
            l(f);
            C0340b c0340b = C0783a.f5710b;
            this.f1049a0 = 1.0f - f(0.0f, 1.0f, 1.0f - f, c0340b);
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            View view = this.f1048a;
            view.postInvalidateOnAnimation();
            this.f1051b0 = f(1.0f, 0.0f, f, c0340b);
            view.postInvalidateOnAnimation();
            ColorStateList colorStateList = this.f1063k;
            ColorStateList colorStateList2 = this.f1062j;
            TextPaint textPaint = this.f1035N;
            if (colorStateList != colorStateList2) {
                textPaint.setColor(a(f, e(colorStateList2), e(this.f1063k)));
            } else {
                textPaint.setColor(e(colorStateList));
            }
            float f5 = this.f1043V;
            float f6 = this.f1044W;
            if (f5 != f6) {
                textPaint.setLetterSpacing(f(f6, f5, f, c0340b));
            } else {
                textPaint.setLetterSpacing(f5);
            }
            this.f1029H = C0783a.a(0.0f, this.f1039R, f);
            this.f1030I = C0783a.a(0.0f, this.f1040S, f);
            this.f1031J = C0783a.a(0.0f, this.f1041T, f);
            int a4 = a(f, 0, e(this.f1042U));
            this.f1032K = a4;
            textPaint.setShadowLayer(this.f1029H, this.f1030I, this.f1031J, a4);
            view.postInvalidateOnAnimation();
        }
    }

    public final void l(float f) {
        c(f, false);
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        this.f1048a.postInvalidateOnAnimation();
    }

    public final void m(Typeface typeface) {
        boolean z4;
        boolean j4 = j(typeface);
        if (this.f1075w != typeface) {
            this.f1075w = typeface;
            Typeface a4 = L2.g.a(this.f1048a.getContext().getResources().getConfiguration(), typeface);
            this.f1074v = a4;
            if (a4 == null) {
                a4 = this.f1075w;
            }
            this.f1073u = a4;
            z4 = true;
        } else {
            z4 = false;
        }
        if (j4 || z4) {
            h(false);
        }
    }
}
