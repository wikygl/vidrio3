package y2;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.carousel.CarouselLayoutManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class e extends f {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ CarouselLayoutManager f6508b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public e(CarouselLayoutManager carouselLayoutManager) {
        super(0);
        this.f6508b = carouselLayoutManager;
    }

    @Override // y2.f
    public final void a(RectF rectF, RectF rectF2, RectF rectF3) {
        float f = rectF2.left;
        float f4 = rectF3.left;
        if (f < f4 && rectF2.right > f4) {
            float f5 = f4 - f;
            rectF.left += f5;
            rectF2.left += f5;
        }
        float f6 = rectF2.right;
        float f7 = rectF3.right;
        if (f6 > f7 && rectF2.left < f7) {
            float f8 = f6 - f7;
            rectF.right = Math.max(rectF.right - f8, rectF.left);
            rectF2.right = Math.max(rectF2.right - f8, rectF2.left);
        }
    }

    @Override // y2.f
    public final float b(RecyclerView.n nVar) {
        return ((ViewGroup.MarginLayoutParams) nVar).rightMargin + ((ViewGroup.MarginLayoutParams) nVar).leftMargin;
    }

    @Override // y2.f
    public final RectF c(float f, float f4, float f5, float f6) {
        return new RectF(f6, 0.0f, f4 - f6, f);
    }

    @Override // y2.f
    public final int d() {
        CarouselLayoutManager carouselLayoutManager = this.f6508b;
        return ((RecyclerView.m) carouselLayoutManager).o - carouselLayoutManager.D();
    }

    @Override // y2.f
    public final int e() {
        CarouselLayoutManager carouselLayoutManager = this.f6508b;
        if (carouselLayoutManager.Q0()) {
            return 0;
        }
        return ((RecyclerView.m) carouselLayoutManager).n;
    }

    @Override // y2.f
    public final int f() {
        return 0;
    }

    @Override // y2.f
    public final int g() {
        return ((RecyclerView.m) this.f6508b).n;
    }

    @Override // y2.f
    public final int h() {
        CarouselLayoutManager carouselLayoutManager = this.f6508b;
        if (carouselLayoutManager.Q0()) {
            return ((RecyclerView.m) carouselLayoutManager).n;
        }
        return 0;
    }

    @Override // y2.f
    public final int i() {
        return this.f6508b.G();
    }

    @Override // y2.f
    public final void j(View view, int i4, int i5) {
        CarouselLayoutManager carouselLayoutManager = this.f6508b;
        int G4 = carouselLayoutManager.G();
        RecyclerView.n layoutParams = view.getLayoutParams();
        carouselLayoutManager.getClass();
        RecyclerView.m.N(view, i4, G4, i5, RecyclerView.m.z(view) + ((ViewGroup.MarginLayoutParams) layoutParams).topMargin + ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin + G4);
    }

    @Override // y2.f
    public final void k(RectF rectF, RectF rectF2, RectF rectF3) {
        if (rectF2.right <= rectF3.left) {
            float floor = ((float) Math.floor(rectF.right)) - 1.0f;
            rectF.right = floor;
            rectF.left = Math.min(rectF.left, floor);
        }
        if (rectF2.left >= rectF3.right) {
            float ceil = ((float) Math.ceil(rectF.left)) + 1.0f;
            rectF.left = ceil;
            rectF.right = Math.max(ceil, rectF.right);
        }
    }

    @Override // y2.f
    public final void l(View view, Rect rect, float f, float f4) {
        view.offsetLeftAndRight((int) (f4 - (rect.left + f)));
    }
}
