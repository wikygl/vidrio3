package l;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import j$.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/* renamed from: l.C  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0690C {

    /* renamed from: l  reason: collision with root package name */
    public static final RectF f4957l = new RectF();
    @SuppressLint({"BanConcurrentHashMap"})

    /* renamed from: m  reason: collision with root package name */
    public static final ConcurrentHashMap<String, Method> f4958m = new ConcurrentHashMap<>();

    /* renamed from: a  reason: collision with root package name */
    public int f4959a = 0;

    /* renamed from: b  reason: collision with root package name */
    public boolean f4960b = false;

    /* renamed from: c  reason: collision with root package name */
    public float f4961c = -1.0f;

    /* renamed from: d  reason: collision with root package name */
    public float f4962d = -1.0f;

    /* renamed from: e  reason: collision with root package name */
    public float f4963e = -1.0f;
    public int[] f = new int[0];

    /* renamed from: g  reason: collision with root package name */
    public boolean f4964g = false;

    /* renamed from: h  reason: collision with root package name */
    public TextPaint f4965h;

    /* renamed from: i  reason: collision with root package name */
    public final TextView f4966i;

    /* renamed from: j  reason: collision with root package name */
    public final Context f4967j;

    /* renamed from: k  reason: collision with root package name */
    public final d f4968k;

    /* renamed from: l.C$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {
        public static StaticLayout a(CharSequence charSequence, Layout.Alignment alignment, int i4, int i5, TextView textView, TextPaint textPaint, d dVar) {
            StaticLayout.Builder obtain = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), textPaint, i4);
            StaticLayout.Builder hyphenationFrequency = obtain.setAlignment(alignment).setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier()).setIncludePad(textView.getIncludeFontPadding()).setBreakStrategy(textView.getBreakStrategy()).setHyphenationFrequency(textView.getHyphenationFrequency());
            if (i5 == -1) {
                i5 = Integer.MAX_VALUE;
            }
            hyphenationFrequency.setMaxLines(i5);
            try {
                dVar.a(obtain, textView);
            } catch (ClassCastException unused) {
                Log.w("ACTVAutoSizeHelper", "Failed to obtain TextDirectionHeuristic, auto size may be incorrect");
            }
            return obtain.build();
        }
    }

    /* renamed from: l.C$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends d {
        @Override // l.C0690C.d
        public void a(StaticLayout.Builder builder, TextView textView) {
            builder.setTextDirection((TextDirectionHeuristic) C0690C.e(textView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR));
        }
    }

    /* renamed from: l.C$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class c extends b {
        @Override // l.C0690C.b, l.C0690C.d
        public void a(StaticLayout.Builder builder, TextView textView) {
            TextDirectionHeuristic textDirectionHeuristic;
            textDirectionHeuristic = textView.getTextDirectionHeuristic();
            builder.setTextDirection(textDirectionHeuristic);
        }

        @Override // l.C0690C.d
        public boolean b(TextView textView) {
            boolean isHorizontallyScrollable;
            isHorizontallyScrollable = textView.isHorizontallyScrollable();
            return isHorizontallyScrollable;
        }
    }

    public C0690C(TextView textView) {
        this.f4966i = textView;
        this.f4967j = textView.getContext();
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 29) {
            this.f4968k = new c();
        } else if (i4 >= 23) {
            this.f4968k = new b();
        } else {
            this.f4968k = new d();
        }
    }

    public static int[] b(int[] iArr) {
        int length = iArr.length;
        if (length == 0) {
            return iArr;
        }
        Arrays.sort(iArr);
        ArrayList arrayList = new ArrayList();
        for (int i4 : iArr) {
            if (i4 > 0 && Collections.binarySearch(arrayList, Integer.valueOf(i4)) < 0) {
                arrayList.add(Integer.valueOf(i4));
            }
        }
        if (length == arrayList.size()) {
            return iArr;
        }
        int size = arrayList.size();
        int[] iArr2 = new int[size];
        for (int i5 = 0; i5 < size; i5++) {
            iArr2[i5] = ((Integer) arrayList.get(i5)).intValue();
        }
        return iArr2;
    }

    public static Method d(String str) {
        try {
            ConcurrentHashMap<String, Method> concurrentHashMap = f4958m;
            Method method = concurrentHashMap.get(str);
            if (method == null && (method = TextView.class.getDeclaredMethod(str, null)) != null) {
                method.setAccessible(true);
                concurrentHashMap.put(str, method);
            }
            return method;
        } catch (Exception e4) {
            Log.w("ACTVAutoSizeHelper", "Failed to retrieve TextView#" + str + "() method", e4);
            return null;
        }
    }

    @SuppressLint({"BanUncheckedReflection"})
    public static <T> T e(Object obj, String str, T t3) {
        try {
            return (T) d(str).invoke(obj, null);
        } catch (Exception e4) {
            Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#" + str + "() method", e4);
            return t3;
        }
    }

    public final void a() {
        int measuredWidth;
        if (!f()) {
            return;
        }
        if (this.f4960b) {
            if (this.f4966i.getMeasuredHeight() > 0 && this.f4966i.getMeasuredWidth() > 0) {
                if (this.f4968k.b(this.f4966i)) {
                    measuredWidth = 1048576;
                } else {
                    measuredWidth = (this.f4966i.getMeasuredWidth() - this.f4966i.getTotalPaddingLeft()) - this.f4966i.getTotalPaddingRight();
                }
                int height = (this.f4966i.getHeight() - this.f4966i.getCompoundPaddingBottom()) - this.f4966i.getCompoundPaddingTop();
                if (measuredWidth > 0 && height > 0) {
                    RectF rectF = f4957l;
                    synchronized (rectF) {
                        try {
                            rectF.setEmpty();
                            rectF.right = measuredWidth;
                            rectF.bottom = height;
                            float c4 = c(rectF);
                            if (c4 != this.f4966i.getTextSize()) {
                                g(0, c4);
                            }
                        } finally {
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        this.f4960b = true;
    }

    public final int c(RectF rectF) {
        StaticLayout staticLayout;
        CharSequence transformation;
        int length = this.f.length;
        if (length != 0) {
            int i4 = length - 1;
            int i5 = 1;
            int i6 = 0;
            while (i5 <= i4) {
                int i7 = (i5 + i4) / 2;
                int i8 = this.f[i7];
                TextView textView = this.f4966i;
                CharSequence text = textView.getText();
                TransformationMethod transformationMethod = textView.getTransformationMethod();
                if (transformationMethod != null && (transformation = transformationMethod.getTransformation(text, textView)) != null) {
                    text = transformation;
                }
                int maxLines = textView.getMaxLines();
                TextPaint textPaint = this.f4965h;
                if (textPaint == null) {
                    this.f4965h = new TextPaint();
                } else {
                    textPaint.reset();
                }
                this.f4965h.set(textView.getPaint());
                this.f4965h.setTextSize(i8);
                Layout.Alignment alignment = (Layout.Alignment) e(textView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
                int round = Math.round(rectF.right);
                if (Build.VERSION.SDK_INT >= 23) {
                    staticLayout = a.a(text, alignment, round, maxLines, this.f4966i, this.f4965h, this.f4968k);
                } else {
                    staticLayout = new StaticLayout(text, this.f4965h, round, alignment, textView.getLineSpacingMultiplier(), textView.getLineSpacingExtra(), textView.getIncludeFontPadding());
                }
                if ((maxLines != -1 && (staticLayout.getLineCount() > maxLines || staticLayout.getLineEnd(staticLayout.getLineCount() - 1) != text.length())) || staticLayout.getHeight() > rectF.bottom) {
                    i6 = i7 - 1;
                    i4 = i6;
                } else {
                    int i9 = i7 + 1;
                    i6 = i5;
                    i5 = i9;
                }
            }
            return this.f[i6];
        }
        throw new IllegalStateException("No available text sizes to choose from.");
    }

    public final boolean f() {
        if (j() && this.f4959a != 0) {
            return true;
        }
        return false;
    }

    public final void g(int i4, float f) {
        Resources resources;
        Context context = this.f4967j;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        float applyDimension = TypedValue.applyDimension(i4, f, resources.getDisplayMetrics());
        TextView textView = this.f4966i;
        if (applyDimension != textView.getPaint().getTextSize()) {
            textView.getPaint().setTextSize(applyDimension);
            boolean isInLayout = textView.isInLayout();
            if (textView.getLayout() != null) {
                this.f4960b = false;
                try {
                    Method d4 = d("nullLayouts");
                    if (d4 != null) {
                        d4.invoke(textView, null);
                    }
                } catch (Exception e4) {
                    Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#nullLayouts() method", e4);
                }
                if (!isInLayout) {
                    textView.requestLayout();
                } else {
                    textView.forceLayout();
                }
                textView.invalidate();
            }
        }
    }

    public final boolean h() {
        if (j() && this.f4959a == 1) {
            if (!this.f4964g || this.f.length == 0) {
                int floor = ((int) Math.floor((this.f4963e - this.f4962d) / this.f4961c)) + 1;
                int[] iArr = new int[floor];
                for (int i4 = 0; i4 < floor; i4++) {
                    iArr[i4] = Math.round((i4 * this.f4961c) + this.f4962d);
                }
                this.f = b(iArr);
            }
            this.f4960b = true;
        } else {
            this.f4960b = false;
        }
        return this.f4960b;
    }

    public final boolean i() {
        boolean z4;
        int[] iArr = this.f;
        int length = iArr.length;
        if (length > 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        this.f4964g = z4;
        if (z4) {
            this.f4959a = 1;
            this.f4962d = iArr[0];
            this.f4963e = iArr[length - 1];
            this.f4961c = -1.0f;
        }
        return z4;
    }

    public final boolean j() {
        return !(this.f4966i instanceof C0702k);
    }

    public final void k(float f, float f4, float f5) {
        if (f > 0.0f) {
            if (f4 > f) {
                if (f5 > 0.0f) {
                    this.f4959a = 1;
                    this.f4962d = f;
                    this.f4963e = f4;
                    this.f4961c = f5;
                    this.f4964g = false;
                    return;
                }
                throw new IllegalArgumentException("The auto-size step granularity (" + f5 + "px) is less or equal to (0px)");
            }
            throw new IllegalArgumentException("Maximum auto-size text size (" + f4 + "px) is less or equal to minimum auto-size text size (" + f + "px)");
        }
        throw new IllegalArgumentException("Minimum auto-size text size (" + f + "px) is less or equal to (0px)");
    }

    /* renamed from: l.C$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {
        public boolean b(TextView textView) {
            return ((Boolean) C0690C.e(textView, "getHorizontallyScrolling", Boolean.FALSE)).booleanValue();
        }

        public void a(StaticLayout.Builder builder, TextView textView) {
        }
    }
}
