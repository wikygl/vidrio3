package l;

import D.f;
import R.c;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import d.C0376a;
import e0.C0405a;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Locale;

/* renamed from: l.A  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0688A {

    /* renamed from: a  reason: collision with root package name */
    public final TextView f4931a;

    /* renamed from: b  reason: collision with root package name */
    public Z f4932b;

    /* renamed from: c  reason: collision with root package name */
    public Z f4933c;

    /* renamed from: d  reason: collision with root package name */
    public Z f4934d;

    /* renamed from: e  reason: collision with root package name */
    public Z f4935e;
    public Z f;

    /* renamed from: g  reason: collision with root package name */
    public Z f4936g;

    /* renamed from: h  reason: collision with root package name */
    public Z f4937h;

    /* renamed from: i  reason: collision with root package name */
    public final C0690C f4938i;

    /* renamed from: j  reason: collision with root package name */
    public int f4939j = 0;

    /* renamed from: k  reason: collision with root package name */
    public int f4940k = -1;

    /* renamed from: l  reason: collision with root package name */
    public Typeface f4941l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f4942m;

    /* renamed from: l.A$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public static Locale a(String str) {
            return Locale.forLanguageTag(str);
        }
    }

    /* renamed from: l.A$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class c {
        public static LocaleList a(String str) {
            return LocaleList.forLanguageTags(str);
        }

        public static void b(TextView textView, LocaleList localeList) {
            textView.setTextLocales(localeList);
        }
    }

    /* renamed from: l.A$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {
        public static int a(TextView textView) {
            return textView.getAutoSizeStepGranularity();
        }

        public static void b(TextView textView, int i4, int i5, int i6, int i7) {
            textView.setAutoSizeTextTypeUniformWithConfiguration(i4, i5, i6, i7);
        }

        public static void c(TextView textView, int[] iArr, int i4) {
            textView.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i4);
        }

        public static boolean d(TextView textView, String str) {
            return textView.setFontVariationSettings(str);
        }
    }

    /* renamed from: l.A$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class e {
        public static Typeface a(Typeface typeface, int i4, boolean z4) {
            return Typeface.create(typeface, i4, z4);
        }
    }

    public C0688A(TextView textView) {
        this.f4931a = textView;
        this.f4938i = new C0690C(textView);
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [l.Z, java.lang.Object] */
    public static Z c(Context context, C0701j c0701j, int i4) {
        ColorStateList i5;
        synchronized (c0701j) {
            i5 = c0701j.f5163a.i(context, i4);
        }
        if (i5 != null) {
            ?? obj = new Object();
            obj.f5090d = true;
            obj.f5087a = i5;
            return obj;
        }
        return null;
    }

    public static void h(TextView textView, InputConnection inputConnection, EditorInfo editorInfo) {
        int i4;
        int i5;
        CharSequence subSequence;
        int i6 = Build.VERSION.SDK_INT;
        if (i6 < 30 && inputConnection != null) {
            CharSequence text = textView.getText();
            if (i6 >= 30) {
                c.a.a(editorInfo, text);
                return;
            }
            text.getClass();
            if (i6 >= 30) {
                c.a.a(editorInfo, text);
                return;
            }
            int i7 = editorInfo.initialSelStart;
            int i8 = editorInfo.initialSelEnd;
            if (i7 > i8) {
                i4 = i8;
            } else {
                i4 = i7;
            }
            if (i7 <= i8) {
                i7 = i8;
            }
            int length = text.length();
            if (i4 >= 0 && i7 <= length) {
                int i9 = editorInfo.inputType & 4095;
                if (i9 != 129 && i9 != 225 && i9 != 18) {
                    if (length <= 2048) {
                        R.c.a(editorInfo, text, i4, i7);
                        return;
                    }
                    int i10 = i7 - i4;
                    if (i10 > 1024) {
                        i5 = 0;
                    } else {
                        i5 = i10;
                    }
                    int i11 = 2048 - i5;
                    int min = Math.min(text.length() - i7, i11 - Math.min(i4, (int) (i11 * 0.8d)));
                    int min2 = Math.min(i4, i11 - min);
                    int i12 = i4 - min2;
                    if (Character.isLowSurrogate(text.charAt(i12))) {
                        i12++;
                        min2--;
                    }
                    if (Character.isHighSurrogate(text.charAt((i7 + min) - 1))) {
                        min--;
                    }
                    int i13 = min2 + i5;
                    int i14 = i13 + min;
                    if (i5 != i10) {
                        subSequence = TextUtils.concat(text.subSequence(i12, i12 + min2), text.subSequence(i7, min + i7));
                    } else {
                        subSequence = text.subSequence(i12, i14 + i12);
                    }
                    R.c.a(editorInfo, subSequence, min2, i13);
                    return;
                }
                R.c.a(editorInfo, null, 0, 0);
                return;
            }
            R.c.a(editorInfo, null, 0, 0);
        }
    }

    public final void a(Drawable drawable, Z z4) {
        if (drawable != null && z4 != null) {
            C0701j.e(drawable, z4, this.f4931a.getDrawableState());
        }
    }

    public final void b() {
        Z z4 = this.f4932b;
        TextView textView = this.f4931a;
        if (z4 != null || this.f4933c != null || this.f4934d != null || this.f4935e != null) {
            Drawable[] compoundDrawables = textView.getCompoundDrawables();
            a(compoundDrawables[0], this.f4932b);
            a(compoundDrawables[1], this.f4933c);
            a(compoundDrawables[2], this.f4934d);
            a(compoundDrawables[3], this.f4935e);
        }
        if (this.f != null || this.f4936g != null) {
            Drawable[] compoundDrawablesRelative = textView.getCompoundDrawablesRelative();
            a(compoundDrawablesRelative[0], this.f);
            a(compoundDrawablesRelative[2], this.f4936g);
        }
    }

    public final ColorStateList d() {
        Z z4 = this.f4937h;
        if (z4 != null) {
            return z4.f5087a;
        }
        return null;
    }

    public final PorterDuff.Mode e() {
        Z z4 = this.f4937h;
        if (z4 != null) {
            return z4.f5088b;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:115:0x022e  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x023e  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0244  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x024d  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0253  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x025c  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0262  */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0282  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x02a5  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x02dc  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x02ec  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0321  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0328  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0330  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0335  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:183:0x0344  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x034c  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0351  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x035a  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x035f  */
    /* JADX WARN: Removed duplicated region for block: B:194:0x0367  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x036c  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x03c9  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x03cf  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x03d5  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x03e3  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0417  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x044a  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x0468  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x0471  */
    /* JADX WARN: Removed duplicated region for block: B:281:0x0476  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x047f  */
    /* JADX WARN: Removed duplicated region for block: B:292:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0101  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0108  */
    @android.annotation.SuppressLint({"NewApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void f(android.util.AttributeSet r29, int r30) {
        /*
            Method dump skipped, instructions count: 1188
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: l.C0688A.f(android.util.AttributeSet, int):void");
    }

    public final void g(Context context, int i4) {
        String string;
        ColorStateList a4;
        ColorStateList a5;
        ColorStateList a6;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i4, C0376a.f3150w);
        b0 b0Var = new b0(context, obtainStyledAttributes);
        boolean hasValue = obtainStyledAttributes.hasValue(14);
        TextView textView = this.f4931a;
        if (hasValue) {
            textView.setAllCaps(obtainStyledAttributes.getBoolean(14, false));
        }
        int i5 = Build.VERSION.SDK_INT;
        if (i5 < 23) {
            if (obtainStyledAttributes.hasValue(3) && (a6 = b0Var.a(3)) != null) {
                textView.setTextColor(a6);
            }
            if (obtainStyledAttributes.hasValue(5) && (a5 = b0Var.a(5)) != null) {
                textView.setLinkTextColor(a5);
            }
            if (obtainStyledAttributes.hasValue(4) && (a4 = b0Var.a(4)) != null) {
                textView.setHintTextColor(a4);
            }
        }
        if (obtainStyledAttributes.hasValue(0) && obtainStyledAttributes.getDimensionPixelSize(0, -1) == 0) {
            textView.setTextSize(0, 0.0f);
        }
        n(context, b0Var);
        if (i5 >= 26 && obtainStyledAttributes.hasValue(13) && (string = obtainStyledAttributes.getString(13)) != null) {
            d.d(textView, string);
        }
        b0Var.f();
        Typeface typeface = this.f4941l;
        if (typeface != null) {
            textView.setTypeface(typeface, this.f4939j);
        }
    }

    public final void i(int i4, int i5, int i6, int i7) {
        C0690C c0690c = this.f4938i;
        if (c0690c.j()) {
            DisplayMetrics displayMetrics = c0690c.f4967j.getResources().getDisplayMetrics();
            c0690c.k(TypedValue.applyDimension(i7, i4, displayMetrics), TypedValue.applyDimension(i7, i5, displayMetrics), TypedValue.applyDimension(i7, i6, displayMetrics));
            if (c0690c.h()) {
                c0690c.a();
            }
        }
    }

    public final void j(int[] iArr, int i4) {
        C0690C c0690c = this.f4938i;
        if (c0690c.j()) {
            int length = iArr.length;
            if (length > 0) {
                int[] iArr2 = new int[length];
                if (i4 == 0) {
                    iArr2 = Arrays.copyOf(iArr, length);
                } else {
                    DisplayMetrics displayMetrics = c0690c.f4967j.getResources().getDisplayMetrics();
                    for (int i5 = 0; i5 < length; i5++) {
                        iArr2[i5] = Math.round(TypedValue.applyDimension(i4, iArr[i5], displayMetrics));
                    }
                }
                c0690c.f = C0690C.b(iArr2);
                if (!c0690c.i()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(iArr));
                }
            } else {
                c0690c.f4964g = false;
            }
            if (c0690c.h()) {
                c0690c.a();
            }
        }
    }

    public final void k(int i4) {
        C0690C c0690c = this.f4938i;
        if (c0690c.j()) {
            if (i4 != 0) {
                if (i4 == 1) {
                    DisplayMetrics displayMetrics = c0690c.f4967j.getResources().getDisplayMetrics();
                    c0690c.k(TypedValue.applyDimension(2, 12.0f, displayMetrics), TypedValue.applyDimension(2, 112.0f, displayMetrics), 1.0f);
                    if (c0690c.h()) {
                        c0690c.a();
                        return;
                    }
                    return;
                }
                throw new IllegalArgumentException(C0405a.c("Unknown auto-size text type: ", i4));
            }
            c0690c.f4959a = 0;
            c0690c.f4962d = -1.0f;
            c0690c.f4963e = -1.0f;
            c0690c.f4961c = -1.0f;
            c0690c.f = new int[0];
            c0690c.f4960b = false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [l.Z, java.lang.Object] */
    public final void l(ColorStateList colorStateList) {
        boolean z4;
        if (this.f4937h == null) {
            this.f4937h = new Object();
        }
        Z z5 = this.f4937h;
        z5.f5087a = colorStateList;
        if (colorStateList != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        z5.f5090d = z4;
        this.f4932b = z5;
        this.f4933c = z5;
        this.f4934d = z5;
        this.f4935e = z5;
        this.f = z5;
        this.f4936g = z5;
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [l.Z, java.lang.Object] */
    public final void m(PorterDuff.Mode mode) {
        boolean z4;
        if (this.f4937h == null) {
            this.f4937h = new Object();
        }
        Z z5 = this.f4937h;
        z5.f5088b = mode;
        if (mode != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        z5.f5089c = z4;
        this.f4932b = z5;
        this.f4933c = z5;
        this.f4934d = z5;
        this.f4935e = z5;
        this.f = z5;
        this.f4936g = z5;
    }

    public final void n(Context context, b0 b0Var) {
        String string;
        boolean z4;
        boolean z5;
        int i4 = this.f4939j;
        TypedArray typedArray = b0Var.f5104b;
        this.f4939j = typedArray.getInt(2, i4);
        int i5 = Build.VERSION.SDK_INT;
        if (i5 >= 28) {
            int i6 = typedArray.getInt(11, -1);
            this.f4940k = i6;
            if (i6 != -1) {
                this.f4939j &= 2;
            }
        }
        int i7 = 10;
        boolean z6 = false;
        if (!typedArray.hasValue(10) && !typedArray.hasValue(12)) {
            if (typedArray.hasValue(1)) {
                this.f4942m = false;
                int i8 = typedArray.getInt(1, 1);
                if (i8 != 1) {
                    if (i8 != 2) {
                        if (i8 == 3) {
                            this.f4941l = Typeface.MONOSPACE;
                            return;
                        }
                        return;
                    }
                    this.f4941l = Typeface.SERIF;
                    return;
                }
                this.f4941l = Typeface.SANS_SERIF;
                return;
            }
            return;
        }
        this.f4941l = null;
        if (typedArray.hasValue(12)) {
            i7 = 12;
        }
        int i9 = this.f4940k;
        int i10 = this.f4939j;
        if (!context.isRestricted()) {
            try {
                Typeface d4 = b0Var.d(i7, this.f4939j, new a(i9, i10, new WeakReference(this.f4931a)));
                if (d4 != null) {
                    if (i5 >= 28 && this.f4940k != -1) {
                        Typeface create = Typeface.create(d4, 0);
                        int i11 = this.f4940k;
                        if ((this.f4939j & 2) != 0) {
                            z5 = true;
                        } else {
                            z5 = false;
                        }
                        this.f4941l = e.a(create, i11, z5);
                    } else {
                        this.f4941l = d4;
                    }
                }
                if (this.f4941l == null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                this.f4942m = z4;
            } catch (Resources.NotFoundException | UnsupportedOperationException unused) {
            }
        }
        if (this.f4941l == null && (string = typedArray.getString(i7)) != null) {
            if (Build.VERSION.SDK_INT >= 28 && this.f4940k != -1) {
                Typeface create2 = Typeface.create(string, 0);
                int i12 = this.f4940k;
                if ((this.f4939j & 2) != 0) {
                    z6 = true;
                }
                this.f4941l = e.a(create2, i12, z6);
                return;
            }
            this.f4941l = Typeface.create(string, this.f4939j);
        }
    }

    /* renamed from: l.A$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a extends f.e {

        /* renamed from: a  reason: collision with root package name */
        public final /* synthetic */ int f4943a;

        /* renamed from: b  reason: collision with root package name */
        public final /* synthetic */ int f4944b;

        /* renamed from: c  reason: collision with root package name */
        public final /* synthetic */ WeakReference f4945c;

        public a(int i4, int i5, WeakReference weakReference) {
            this.f4943a = i4;
            this.f4944b = i5;
            this.f4945c = weakReference;
        }

        @Override // D.f.e
        public final void c(Typeface typeface) {
            int i4;
            boolean z4;
            if (Build.VERSION.SDK_INT >= 28 && (i4 = this.f4943a) != -1) {
                if ((this.f4944b & 2) != 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                typeface = e.a(typeface, i4, z4);
            }
            C0688A c0688a = C0688A.this;
            if (c0688a.f4942m) {
                c0688a.f4941l = typeface;
                TextView textView = (TextView) this.f4945c.get();
                if (textView != null) {
                    if (textView.isAttachedToWindow()) {
                        textView.post(new K0.d(c0688a.f4939j, 2, textView, typeface));
                    } else {
                        textView.setTypeface(typeface, c0688a.f4939j);
                    }
                }
            }
        }

        @Override // D.f.e
        public final void b(int i4) {
        }
    }
}
