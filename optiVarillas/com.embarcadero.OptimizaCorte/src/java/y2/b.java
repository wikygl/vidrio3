package y2;

import S0.RunnableC0293z;
import android.view.View;
import com.google.android.material.carousel.CarouselLayoutManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class b implements View.OnLayoutChangeListener {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ CarouselLayoutManager f6505a;

    @Override // android.view.View.OnLayoutChangeListener
    public final void onLayoutChange(View view, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) {
        CarouselLayoutManager carouselLayoutManager = this.f6505a;
        carouselLayoutManager.getClass();
        if (i4 != i8 || i5 != i9 || i6 != i10 || i7 != i11) {
            view.post(new RunnableC0293z(5, carouselLayoutManager));
        }
    }
}
