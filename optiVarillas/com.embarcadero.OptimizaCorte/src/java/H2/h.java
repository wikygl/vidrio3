package H2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h {

    /* renamed from: a  reason: collision with root package name */
    public final ArrayList<b> f1091a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    public ValueAnimator f1092b = null;

    /* renamed from: c  reason: collision with root package name */
    public final a f1093c = new a();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class a extends AnimatorListenerAdapter {
        public a() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
            h hVar = h.this;
            if (hVar.f1092b == animator) {
                hVar.f1092b = null;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void a(int[] iArr, ValueAnimator valueAnimator) {
        Object obj = new Object();
        valueAnimator.addListener(this.f1093c);
        this.f1091a.add(obj);
    }
}
