package H2;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import java.lang.reflect.Constructor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m {

    /* renamed from: m  reason: collision with root package name */
    public static final int f1095m;

    /* renamed from: n  reason: collision with root package name */
    public static boolean f1096n;

    /* renamed from: o  reason: collision with root package name */
    public static Constructor<StaticLayout> f1097o;

    /* renamed from: p  reason: collision with root package name */
    public static TextDirectionHeuristic f1098p;

    /* renamed from: a  reason: collision with root package name */
    public CharSequence f1099a;

    /* renamed from: b  reason: collision with root package name */
    public final TextPaint f1100b;

    /* renamed from: c  reason: collision with root package name */
    public final int f1101c;

    /* renamed from: d  reason: collision with root package name */
    public int f1102d;

    /* renamed from: k  reason: collision with root package name */
    public boolean f1108k;

    /* renamed from: e  reason: collision with root package name */
    public Layout.Alignment f1103e = Layout.Alignment.ALIGN_NORMAL;
    public int f = Integer.MAX_VALUE;

    /* renamed from: g  reason: collision with root package name */
    public float f1104g = 0.0f;

    /* renamed from: h  reason: collision with root package name */
    public float f1105h = 1.0f;

    /* renamed from: i  reason: collision with root package name */
    public int f1106i = f1095m;

    /* renamed from: j  reason: collision with root package name */
    public boolean f1107j = true;

    /* renamed from: l  reason: collision with root package name */
    public TextUtils.TruncateAt f1109l = null;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a extends Exception {
        public a(Exception exc) {
            super("Error thrown initializing StaticLayout " + exc.getMessage(), exc);
        }
    }

    static {
        int i4;
        if (Build.VERSION.SDK_INT >= 23) {
            i4 = 1;
        } else {
            i4 = 0;
        }
        f1095m = i4;
    }

    public m(CharSequence charSequence, TextPaint textPaint, int i4) {
        this.f1099a = charSequence;
        this.f1100b = textPaint;
        this.f1101c = i4;
        this.f1102d = charSequence.length();
    }

    public final StaticLayout a() {
        boolean z4;
        TextDirectionHeuristic textDirectionHeuristic;
        StaticLayout.Builder obtain;
        TextDirectionHeuristic textDirectionHeuristic2;
        StaticLayout build;
        TextPaint textPaint = this.f1100b;
        if (this.f1099a == null) {
            this.f1099a = "";
        }
        int max = Math.max(0, this.f1101c);
        CharSequence charSequence = this.f1099a;
        if (this.f == 1) {
            charSequence = TextUtils.ellipsize(charSequence, textPaint, max, this.f1109l);
        }
        int min = Math.min(charSequence.length(), this.f1102d);
        this.f1102d = min;
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 23) {
            if (this.f1108k && this.f == 1) {
                this.f1103e = Layout.Alignment.ALIGN_OPPOSITE;
            }
            obtain = StaticLayout.Builder.obtain(charSequence, 0, min, textPaint, max);
            obtain.setAlignment(this.f1103e);
            obtain.setIncludePad(this.f1107j);
            if (this.f1108k) {
                textDirectionHeuristic2 = TextDirectionHeuristics.RTL;
            } else {
                textDirectionHeuristic2 = TextDirectionHeuristics.LTR;
            }
            obtain.setTextDirection(textDirectionHeuristic2);
            TextUtils.TruncateAt truncateAt = this.f1109l;
            if (truncateAt != null) {
                obtain.setEllipsize(truncateAt);
            }
            obtain.setMaxLines(this.f);
            float f = this.f1104g;
            if (f != 0.0f || this.f1105h != 1.0f) {
                obtain.setLineSpacing(f, this.f1105h);
            }
            if (this.f > 1) {
                obtain.setHyphenationFrequency(this.f1106i);
            }
            build = obtain.build();
            return build;
        }
        if (!f1096n) {
            try {
                if (this.f1108k && i4 >= 23) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z4) {
                    textDirectionHeuristic = TextDirectionHeuristics.RTL;
                } else {
                    textDirectionHeuristic = TextDirectionHeuristics.LTR;
                }
                f1098p = textDirectionHeuristic;
                Class cls = Integer.TYPE;
                Class cls2 = Float.TYPE;
                Constructor<StaticLayout> declaredConstructor = StaticLayout.class.getDeclaredConstructor(CharSequence.class, cls, cls, TextPaint.class, cls, Layout.Alignment.class, TextDirectionHeuristic.class, cls2, cls2, Boolean.TYPE, TextUtils.TruncateAt.class, cls, cls);
                f1097o = declaredConstructor;
                declaredConstructor.setAccessible(true);
                f1096n = true;
            } catch (Exception e4) {
                throw new a(e4);
            }
        }
        try {
            Constructor<StaticLayout> constructor = f1097o;
            constructor.getClass();
            Integer valueOf = Integer.valueOf(this.f1102d);
            Integer valueOf2 = Integer.valueOf(max);
            Layout.Alignment alignment = this.f1103e;
            TextDirectionHeuristic textDirectionHeuristic3 = f1098p;
            textDirectionHeuristic3.getClass();
            return constructor.newInstance(charSequence, 0, valueOf, textPaint, valueOf2, alignment, textDirectionHeuristic3, Float.valueOf(1.0f), Float.valueOf(0.0f), Boolean.valueOf(this.f1107j), null, Integer.valueOf(max), Integer.valueOf(this.f));
        } catch (Exception e5) {
            throw new a(e5);
        }
    }
}
