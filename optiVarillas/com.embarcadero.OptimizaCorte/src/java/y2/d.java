package y2;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.carousel.CarouselLayoutManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class d extends f {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ CarouselLayoutManager f6507b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public d(CarouselLayoutManager carouselLayoutManager) {
        super(1);
        this.f6507b = carouselLayoutManager;
    }

    @Override // y2.f
    public final void a(RectF rectF, RectF rectF2, RectF rectF3) {
        float f = rectF2.top;
        float f4 = rectF3.top;
        if (f < f4 && rectF2.bottom > f4) {
            float f5 = f4 - f;
            rectF.top += f5;
            rectF3.top += f5;
        }
        float f6 = rectF2.bottom;
        float f7 = rectF3.bottom;
        if (f6 > f7 && rectF2.top < f7) {
            float f8 = f6 - f7;
            rectF.bottom = Math.max(rectF.bottom - f8, rectF.top);
            rectF2.bottom = Math.max(rectF2.bottom - f8, rectF2.top);
        }
    }

    @Override // y2.f
    public final float b(RecyclerView.n nVar) {
        return ((ViewGroup.MarginLayoutParams) nVar).topMargin + ((ViewGroup.MarginLayoutParams) nVar).bottomMargin;
    }

    @Override // y2.f
    public final RectF c(float f, float f4, float f5, float f6) {
        return new RectF(0.0f, f5, f4, f - f5);
    }

    @Override // y2.f
    public final int d() {
        return ((RecyclerView.m) this.f6507b).o;
    }

    @Override // y2.f
    public final int e() {
        return ((RecyclerView.m) this.f6507b).o;
    }

    @Override // y2.f
    public final int f() {
        return this.f6507b.E();
    }

    @Override // y2.f
    public final int g() {
        CarouselLayoutManager carouselLayoutManager = this.f6507b;
        return ((RecyclerView.m) carouselLayoutManager).n - carouselLayoutManager.F();
    }

    @Override // y2.f
    public final int h() {
        return 0;
    }

    @Override // y2.f
    public final int i() {
        return 0;
    }

    @Override // y2.f
    public final void j(View view, int i4, int i5) {
        CarouselLayoutManager carouselLayoutManager = this.f6507b;
        int E4 = carouselLayoutManager.E();
        RecyclerView.n layoutParams = view.getLayoutParams();
        carouselLayoutManager.getClass();
        RecyclerView.m.N(view, E4, i4, RecyclerView.m.A(view) + ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin + ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin + E4, i5);
    }

    @Override // y2.f
    public final void k(RectF rectF, RectF rectF2, RectF rectF3) {
        if (rectF2.bottom <= rectF3.top) {
            float floor = ((float) Math.floor(rectF.bottom)) - 1.0f;
            rectF.bottom = floor;
            rectF.top = Math.min(rectF.top, floor);
        }
        if (rectF2.top >= rectF3.bottom) {
            float ceil = ((float) Math.ceil(rectF.top)) + 1.0f;
            rectF.top = ceil;
            rectF.bottom = Math.max(ceil, rectF.bottom);
        }
    }

    @Override // y2.f
    public final void l(View view, Rect rect, float f, float f4) {
        view.offsetTopAndBottom((int) (f4 - (rect.top + f)));
    }
}
