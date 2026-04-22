package l;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;

/* renamed from: l.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0710t extends RatingBar {

    /* renamed from: j  reason: collision with root package name */
    public final r f5199j;

    public C0710t(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 2130903906);
        W.a(getContext(), this);
        r rVar = new r(this);
        this.f5199j = rVar;
        rVar.a(attributeSet, 2130903906);
    }

    @Override // android.widget.RatingBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public final synchronized void onMeasure(int i4, int i5) {
        super.onMeasure(i4, i5);
        Bitmap bitmap = this.f5199j.f5194b;
        if (bitmap != null) {
            setMeasuredDimension(View.resolveSizeAndState(bitmap.getWidth() * getNumStars(), i4, 0), getMeasuredHeight());
        }
    }
}
