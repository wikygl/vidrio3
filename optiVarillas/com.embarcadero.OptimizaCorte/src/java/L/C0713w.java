package l;

import F.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;
import d.C0376a;

/* renamed from: l.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0713w extends r {

    /* renamed from: d  reason: collision with root package name */
    public final SeekBar f5201d;

    /* renamed from: e  reason: collision with root package name */
    public Drawable f5202e;
    public ColorStateList f;

    /* renamed from: g  reason: collision with root package name */
    public PorterDuff.Mode f5203g;

    /* renamed from: h  reason: collision with root package name */
    public boolean f5204h;

    /* renamed from: i  reason: collision with root package name */
    public boolean f5205i;

    public C0713w(SeekBar seekBar) {
        super(seekBar);
        this.f = null;
        this.f5203g = null;
        this.f5204h = false;
        this.f5205i = false;
        this.f5201d = seekBar;
    }

    @Override // l.r
    public final void a(AttributeSet attributeSet, int i4) {
        super.a(attributeSet, 2130903928);
        SeekBar seekBar = this.f5201d;
        Context context = seekBar.getContext();
        int[] iArr = C0376a.f3134g;
        b0 e4 = b0.e(context, attributeSet, iArr, 2130903928, 0);
        M.O.o(seekBar, seekBar.getContext(), iArr, attributeSet, e4.f5104b, 2130903928);
        Drawable c4 = e4.c(0);
        if (c4 != null) {
            seekBar.setThumb(c4);
        }
        Drawable b4 = e4.b(1);
        Drawable drawable = this.f5202e;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        this.f5202e = b4;
        if (b4 != null) {
            b4.setCallback(seekBar);
            F.a.c(b4, seekBar.getLayoutDirection());
            if (b4.isStateful()) {
                b4.setState(seekBar.getDrawableState());
            }
            c();
        }
        seekBar.invalidate();
        TypedArray typedArray = e4.f5104b;
        if (typedArray.hasValue(3)) {
            this.f5203g = G.c(typedArray.getInt(3, -1), this.f5203g);
            this.f5205i = true;
        }
        if (typedArray.hasValue(2)) {
            this.f = e4.a(2);
            this.f5204h = true;
        }
        e4.f();
        c();
    }

    public final void c() {
        Drawable drawable = this.f5202e;
        if (drawable != null) {
            if (this.f5204h || this.f5205i) {
                Drawable g4 = F.a.g(drawable.mutate());
                this.f5202e = g4;
                if (this.f5204h) {
                    a.C0006a.h(g4, this.f);
                }
                if (this.f5205i) {
                    a.C0006a.i(this.f5202e, this.f5203g);
                }
                if (this.f5202e.isStateful()) {
                    this.f5202e.setState(this.f5201d.getDrawableState());
                }
            }
        }
    }

    public final void d(Canvas canvas) {
        int i4;
        if (this.f5202e != null) {
            SeekBar seekBar = this.f5201d;
            int max = seekBar.getMax();
            int i5 = 1;
            if (max > 1) {
                int intrinsicWidth = this.f5202e.getIntrinsicWidth();
                int intrinsicHeight = this.f5202e.getIntrinsicHeight();
                if (intrinsicWidth >= 0) {
                    i4 = intrinsicWidth / 2;
                } else {
                    i4 = 1;
                }
                if (intrinsicHeight >= 0) {
                    i5 = intrinsicHeight / 2;
                }
                this.f5202e.setBounds(-i4, -i5, i4, i5);
                float width = ((seekBar.getWidth() - seekBar.getPaddingLeft()) - seekBar.getPaddingRight()) / max;
                int save = canvas.save();
                canvas.translate(seekBar.getPaddingLeft(), seekBar.getHeight() / 2);
                for (int i6 = 0; i6 <= max; i6++) {
                    this.f5202e.draw(canvas);
                    canvas.translate(width, 0.0f);
                }
                canvas.restoreToCount(save);
            }
        }
    }
}
