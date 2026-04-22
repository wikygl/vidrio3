package K;

import android.os.Build;
import android.os.LocaleList;
import android.text.PrecomputedText;
import android.text.Spannable;
import android.text.TextDirectionHeuristic;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f implements Spannable {
    @Override // java.lang.CharSequence
    public final char charAt(int i4) {
        throw null;
    }

    @Override // android.text.Spanned
    public final int getSpanEnd(Object obj) {
        throw null;
    }

    @Override // android.text.Spanned
    public final int getSpanFlags(Object obj) {
        throw null;
    }

    @Override // android.text.Spanned
    public final int getSpanStart(Object obj) {
        throw null;
    }

    @Override // android.text.Spanned
    public final <T> T[] getSpans(int i4, int i5, Class<T> cls) {
        if (Build.VERSION.SDK_INT >= 29) {
            throw null;
        }
        throw null;
    }

    @Override // java.lang.CharSequence
    public final int length() {
        throw null;
    }

    @Override // android.text.Spanned
    public final int nextSpanTransition(int i4, int i5, Class cls) {
        throw null;
    }

    @Override // android.text.Spannable
    public final void removeSpan(Object obj) {
        if (!(obj instanceof MetricAffectingSpan)) {
            if (Build.VERSION.SDK_INT >= 29) {
                throw null;
            }
            throw null;
        }
        throw new IllegalArgumentException("MetricAffectingSpan can not be removed from PrecomputedText.");
    }

    @Override // android.text.Spannable
    public final void setSpan(Object obj, int i4, int i5, int i6) {
        if (!(obj instanceof MetricAffectingSpan)) {
            if (Build.VERSION.SDK_INT >= 29) {
                throw null;
            }
            throw null;
        }
        throw new IllegalArgumentException("MetricAffectingSpan can not be set to PrecomputedText.");
    }

    @Override // java.lang.CharSequence
    public final CharSequence subSequence(int i4, int i5) {
        throw null;
    }

    @Override // java.lang.CharSequence
    public final String toString() {
        throw null;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final TextPaint f1255a;

        /* renamed from: b  reason: collision with root package name */
        public final TextDirectionHeuristic f1256b;

        /* renamed from: c  reason: collision with root package name */
        public final int f1257c;

        /* renamed from: d  reason: collision with root package name */
        public final int f1258d;

        public a(TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic, int i4, int i5) {
            PrecomputedText.Params.Builder breakStrategy;
            PrecomputedText.Params.Builder hyphenationFrequency;
            PrecomputedText.Params.Builder textDirection;
            if (Build.VERSION.SDK_INT >= 29) {
                breakStrategy = d.a(textPaint).setBreakStrategy(i4);
                hyphenationFrequency = breakStrategy.setHyphenationFrequency(i5);
                textDirection = hyphenationFrequency.setTextDirection(textDirectionHeuristic);
                textDirection.build();
            }
            this.f1255a = textPaint;
            this.f1256b = textDirectionHeuristic;
            this.f1257c = i4;
            this.f1258d = i5;
        }

        public final boolean a(a aVar) {
            LocaleList textLocales;
            LocaleList textLocales2;
            boolean equals;
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 23) {
                if (this.f1257c != aVar.f1257c || this.f1258d != aVar.f1258d) {
                    return false;
                }
            }
            TextPaint textPaint = this.f1255a;
            if (textPaint.getTextSize() != aVar.f1255a.getTextSize()) {
                return false;
            }
            float textScaleX = textPaint.getTextScaleX();
            TextPaint textPaint2 = aVar.f1255a;
            if (textScaleX != textPaint2.getTextScaleX() || textPaint.getTextSkewX() != textPaint2.getTextSkewX() || textPaint.getLetterSpacing() != textPaint2.getLetterSpacing() || !TextUtils.equals(textPaint.getFontFeatureSettings(), textPaint2.getFontFeatureSettings()) || textPaint.getFlags() != textPaint2.getFlags()) {
                return false;
            }
            if (i4 >= 24) {
                textLocales = textPaint.getTextLocales();
                textLocales2 = textPaint2.getTextLocales();
                equals = textLocales.equals(textLocales2);
                if (!equals) {
                    return false;
                }
            } else if (!textPaint.getTextLocale().equals(textPaint2.getTextLocale())) {
                return false;
            }
            if (textPaint.getTypeface() == null) {
                if (textPaint2.getTypeface() != null) {
                    return false;
                }
                return true;
            } else if (!textPaint.getTypeface().equals(textPaint2.getTypeface())) {
                return false;
            } else {
                return true;
            }
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof a)) {
                return false;
            }
            a aVar = (a) obj;
            if (a(aVar) && this.f1256b == aVar.f1256b) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            LocaleList textLocales;
            TextDirectionHeuristic textDirectionHeuristic = this.f1256b;
            int i4 = Build.VERSION.SDK_INT;
            int i5 = this.f1258d;
            int i6 = this.f1257c;
            TextPaint textPaint = this.f1255a;
            if (i4 >= 24) {
                Float valueOf = Float.valueOf(textPaint.getTextSize());
                Float valueOf2 = Float.valueOf(textPaint.getTextScaleX());
                Float valueOf3 = Float.valueOf(textPaint.getTextSkewX());
                Float valueOf4 = Float.valueOf(textPaint.getLetterSpacing());
                Integer valueOf5 = Integer.valueOf(textPaint.getFlags());
                textLocales = textPaint.getTextLocales();
                return Objects.hash(valueOf, valueOf2, valueOf3, valueOf4, valueOf5, textLocales, textPaint.getTypeface(), Boolean.valueOf(textPaint.isElegantTextHeight()), textDirectionHeuristic, Integer.valueOf(i6), Integer.valueOf(i5));
            }
            return Objects.hash(Float.valueOf(textPaint.getTextSize()), Float.valueOf(textPaint.getTextScaleX()), Float.valueOf(textPaint.getTextSkewX()), Float.valueOf(textPaint.getLetterSpacing()), Integer.valueOf(textPaint.getFlags()), textPaint.getTextLocale(), textPaint.getTypeface(), Boolean.valueOf(textPaint.isElegantTextHeight()), textDirectionHeuristic, Integer.valueOf(i6), Integer.valueOf(i5));
        }

        public final String toString() {
            String fontVariationSettings;
            LocaleList textLocales;
            StringBuilder sb = new StringBuilder("{");
            StringBuilder sb2 = new StringBuilder("textSize=");
            TextPaint textPaint = this.f1255a;
            sb2.append(textPaint.getTextSize());
            sb.append(sb2.toString());
            sb.append(", textScaleX=" + textPaint.getTextScaleX());
            sb.append(", textSkewX=" + textPaint.getTextSkewX());
            int i4 = Build.VERSION.SDK_INT;
            sb.append(", letterSpacing=" + textPaint.getLetterSpacing());
            sb.append(", elegantTextHeight=" + textPaint.isElegantTextHeight());
            if (i4 >= 24) {
                StringBuilder sb3 = new StringBuilder(", textLocale=");
                textLocales = textPaint.getTextLocales();
                sb3.append(textLocales);
                sb.append(sb3.toString());
            } else {
                sb.append(", textLocale=" + textPaint.getTextLocale());
            }
            sb.append(", typeface=" + textPaint.getTypeface());
            if (i4 >= 26) {
                StringBuilder sb4 = new StringBuilder(", variationSettings=");
                fontVariationSettings = textPaint.getFontVariationSettings();
                sb4.append(fontVariationSettings);
                sb.append(sb4.toString());
            }
            sb.append(", textDir=" + this.f1256b);
            sb.append(", breakStrategy=" + this.f1257c);
            sb.append(", hyphenationFrequency=" + this.f1258d);
            sb.append("}");
            return sb.toString();
        }

        public a(PrecomputedText.Params params) {
            TextPaint textPaint;
            TextDirectionHeuristic textDirection;
            int breakStrategy;
            int hyphenationFrequency;
            textPaint = params.getTextPaint();
            this.f1255a = textPaint;
            textDirection = params.getTextDirection();
            this.f1256b = textDirection;
            breakStrategy = params.getBreakStrategy();
            this.f1257c = breakStrategy;
            hyphenationFrequency = params.getHyphenationFrequency();
            this.f1258d = hyphenationFrequency;
        }
    }
}
