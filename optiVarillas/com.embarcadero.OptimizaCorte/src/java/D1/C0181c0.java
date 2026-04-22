package D1;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import com.google.android.gms.internal.ads.Fk;
import com.google.android.gms.internal.ads.Gk;
import com.google.android.gms.internal.ads.KV;
import java.lang.ref.WeakReference;

/* renamed from: D1.c0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0181c0 {

    /* renamed from: a  reason: collision with root package name */
    public final View f673a;

    /* renamed from: b  reason: collision with root package name */
    public Activity f674b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f675c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f676d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f677e;
    public final ViewTreeObserver.OnGlobalLayoutListener f;

    public C0181c0(Activity activity, View view, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        this.f674b = activity;
        this.f673a = view;
        this.f = onGlobalLayoutListener;
    }

    public final void a() {
        ViewTreeObserver viewTreeObserver;
        ViewTreeObserver viewTreeObserver2;
        View decorView;
        if (!this.f675c) {
            Activity activity = this.f674b;
            ViewTreeObserver viewTreeObserver3 = null;
            ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = this.f;
            if (activity != null) {
                Window window = activity.getWindow();
                if (window != null && (decorView = window.getDecorView()) != null) {
                    viewTreeObserver2 = decorView.getViewTreeObserver();
                } else {
                    viewTreeObserver2 = null;
                }
                if (viewTreeObserver2 != null) {
                    viewTreeObserver2.addOnGlobalLayoutListener(onGlobalLayoutListener);
                }
            }
            Fk fk = z1.p.f6575A.f6600z;
            Gk gk = new Gk(this.f673a, onGlobalLayoutListener);
            View view = (View) ((WeakReference) ((KV) gk).j).get();
            if (view != null && (viewTreeObserver = view.getViewTreeObserver()) != null && viewTreeObserver.isAlive()) {
                viewTreeObserver3 = viewTreeObserver;
            }
            if (viewTreeObserver3 != null) {
                gk.k(viewTreeObserver3);
            }
            this.f675c = true;
        }
    }
}
