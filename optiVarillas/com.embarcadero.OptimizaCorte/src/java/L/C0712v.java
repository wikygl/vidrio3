package l;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* renamed from: l.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0712v extends SeekBar {

    /* renamed from: j  reason: collision with root package name */
    public final C0713w f5200j;

    public C0712v(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 2130903928);
        W.a(getContext(), this);
        C0713w c0713w = new C0713w(this);
        this.f5200j = c0713w;
        c0713w.a(attributeSet, 2130903928);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0713w c0713w = this.f5200j;
        Drawable drawable = c0713w.f5202e;
        if (drawable != null && drawable.isStateful()) {
            SeekBar seekBar = c0713w.f5201d;
            if (drawable.setState(seekBar.getDrawableState())) {
                seekBar.invalidateDrawable(drawable);
            }
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public final void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.f5200j.f5202e;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public final synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.f5200j.d(canvas);
    }
}
