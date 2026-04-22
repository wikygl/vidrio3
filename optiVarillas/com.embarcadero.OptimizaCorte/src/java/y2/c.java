package y2;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.o;
import com.google.android.material.carousel.CarouselLayoutManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends o {

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ CarouselLayoutManager f6506q;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public c(CarouselLayoutManager carouselLayoutManager, Context context) {
        super(context);
        this.f6506q = carouselLayoutManager;
    }

    public final PointF a(int i4) {
        return this.f6506q.a(i4);
    }

    public final int f(View view, int i4) {
        CarouselLayoutManager carouselLayoutManager = this.f6506q;
        if (carouselLayoutManager.u != null && carouselLayoutManager.P0()) {
            int H4 = RecyclerView.m.H(view);
            return (int) (carouselLayoutManager.p - carouselLayoutManager.M0(H4, carouselLayoutManager.L0(H4)));
        }
        return 0;
    }

    public final int g(View view, int i4) {
        CarouselLayoutManager carouselLayoutManager = this.f6506q;
        if (carouselLayoutManager.u != null && !carouselLayoutManager.P0()) {
            int H4 = RecyclerView.m.H(view);
            return (int) (carouselLayoutManager.p - carouselLayoutManager.M0(H4, carouselLayoutManager.L0(H4)));
        }
        return 0;
    }
}
