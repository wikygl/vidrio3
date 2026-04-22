package b1;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class h extends ImageSpan {
    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public final void draw(Canvas canvas, CharSequence charSequence, int i4, int i5, float f, int i6, int i7, int i8, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int i9 = fontMetricsInt.descent;
        canvas.translate(f, ((i7 + i9) - ((i9 - fontMetricsInt.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2));
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public final int getSize(Paint paint, CharSequence charSequence, int i4, int i5, Paint.FontMetricsInt fontMetricsInt) {
        Rect bounds = getDrawable().getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
            int i6 = fontMetricsInt2.descent;
            int i7 = fontMetricsInt2.ascent;
            int i8 = ((i6 - i7) / 2) + i7;
            int i9 = (bounds.bottom - bounds.top) / 2;
            int i10 = i8 - i9;
            fontMetricsInt.ascent = i10;
            fontMetricsInt.top = i10;
            int i11 = i8 + i9;
            fontMetricsInt.bottom = i11;
            fontMetricsInt.descent = i11;
        }
        return bounds.right;
    }
}
