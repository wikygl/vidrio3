package M;

import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class D {

    /* renamed from: a  reason: collision with root package name */
    public final a f1516a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a extends c {

        /* renamed from: a  reason: collision with root package name */
        public final View f1517a;

        public a(View view) {
            this.f1517a = view;
        }

        public void a() {
            View view;
            View view2 = this.f1517a;
            if (view2 == null) {
                return;
            }
            if (!view2.isInEditMode() && !view2.onCheckIsTextEditor()) {
                view = view2.getRootView().findFocus();
            } else {
                view2.requestFocus();
                view = view2;
            }
            if (view == null) {
                view = view2.getRootView().findViewById(16908290);
            }
            if (view != null && view.hasWindowFocus()) {
                view.post(new C(0, view));
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends a {

        /* renamed from: b  reason: collision with root package name */
        public View f1518b;

        /* renamed from: c  reason: collision with root package name */
        public WindowInsetsController f1519c;

        @Override // M.D.a
        public final void a() {
            int ime;
            View view = this.f1518b;
            if (view != null && Build.VERSION.SDK_INT < 33) {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive();
            }
            WindowInsetsController windowInsetsController = this.f1519c;
            if (windowInsetsController == null) {
                if (view != null) {
                    windowInsetsController = view.getWindowInsetsController();
                } else {
                    windowInsetsController = null;
                }
            }
            if (windowInsetsController != null) {
                ime = WindowInsets.Type.ime();
                windowInsetsController.show(ime);
                return;
            }
            super.a();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c {
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [M.D$a, M.D$b] */
    public D(View view) {
        if (Build.VERSION.SDK_INT >= 30) {
            ?? aVar = new a(view);
            aVar.f1518b = view;
            this.f1516a = aVar;
            return;
        }
        this.f1516a = new a(view);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [M.D$a, M.D$b] */
    @Deprecated
    public D(WindowInsetsController windowInsetsController) {
        ?? aVar = new a(null);
        aVar.f1519c = windowInsetsController;
        this.f1516a = aVar;
    }
}
