package v0;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;

/* renamed from: v0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0829b extends Animatable2.AnimationCallback {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ AbstractC0830c f6209a;

    public C0829b(AbstractC0830c abstractC0830c) {
        this.f6209a = abstractC0830c;
    }

    @Override // android.graphics.drawable.Animatable2.AnimationCallback
    public final void onAnimationEnd(Drawable drawable) {
        this.f6209a.a(drawable);
    }

    @Override // android.graphics.drawable.Animatable2.AnimationCallback
    public final void onAnimationStart(Drawable drawable) {
        this.f6209a.b(drawable);
    }
}
