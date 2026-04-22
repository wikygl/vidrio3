package U2;

import P2.f;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class h extends P2.f {

    /* renamed from: I  reason: collision with root package name */
    public static final /* synthetic */ int f2403I = 0;

    /* renamed from: H  reason: collision with root package name */
    public a f2404H;

    @TargetApi(18)
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends h {
        @Override // P2.f
        public final void g(Canvas canvas) {
            if (this.f2404H.f2405v.isEmpty()) {
                super.g(canvas);
                return;
            }
            canvas.save();
            if (Build.VERSION.SDK_INT >= 26) {
                canvas.clipOutRect(this.f2404H.f2405v);
            } else {
                canvas.clipRect(this.f2404H.f2405v, Region.Op.DIFFERENCE);
            }
            super.g(canvas);
            canvas.restore();
        }
    }

    public h(a aVar) {
        super(aVar);
        this.f2404H = aVar;
    }

    @Override // P2.f, android.graphics.drawable.Drawable
    public final Drawable mutate() {
        this.f2404H = new a(this.f2404H);
        return this;
    }

    public final void p(float f, float f4, float f5, float f6) {
        RectF rectF = this.f2404H.f2405v;
        if (f != rectF.left || f4 != rectF.top || f5 != rectF.right || f6 != rectF.bottom) {
            rectF.set(f, f4, f5, f6);
            invalidateSelf();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a extends f.b {

        /* renamed from: v  reason: collision with root package name */
        public final RectF f2405v;

        public a(P2.i iVar, RectF rectF) {
            super(iVar);
            this.f2405v = rectF;
        }

        @Override // P2.f.b, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            h hVar = new h(this);
            hVar.invalidateSelf();
            return hVar;
        }

        public a(a aVar) {
            super(aVar);
            this.f2405v = aVar.f2405v;
        }
    }
}
