package M;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class k0 {

    /* renamed from: a  reason: collision with root package name */
    public final e f1638a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        public final Window f1639a;

        /* renamed from: b  reason: collision with root package name */
        public final D f1640b;

        public a(Window window, D d4) {
            this.f1639a = window;
            this.f1640b = d4;
        }

        @Override // M.k0.e
        public final void c() {
            for (int i4 = 1; i4 <= 256; i4 <<= 1) {
                if ((8 & i4) != 0) {
                    if (i4 != 1) {
                        if (i4 != 2) {
                            if (i4 == 8) {
                                this.f1640b.f1516a.a();
                            }
                        } else {
                            d(2);
                        }
                    } else {
                        d(4);
                        this.f1639a.clearFlags(1024);
                    }
                }
            }
        }

        public final void d(int i4) {
            View decorView = this.f1639a.getDecorView();
            decorView.setSystemUiVisibility((~i4) & decorView.getSystemUiVisibility());
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends a {
        @Override // M.k0.e
        public final void b(boolean z4) {
            if (z4) {
                Window window = this.f1639a;
                window.clearFlags(67108864);
                window.addFlags(Integer.MIN_VALUE);
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(8192 | decorView.getSystemUiVisibility());
                return;
            }
            d(8192);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class c extends b {
        @Override // M.k0.e
        public final void a(boolean z4) {
            if (z4) {
                Window window = this.f1639a;
                window.clearFlags(134217728);
                window.addFlags(Integer.MIN_VALUE);
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(16 | decorView.getSystemUiVisibility());
                return;
            }
            d(16);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class d extends e {

        /* renamed from: a  reason: collision with root package name */
        public final WindowInsetsController f1641a;

        /* renamed from: b  reason: collision with root package name */
        public final D f1642b;

        /* renamed from: c  reason: collision with root package name */
        public Window f1643c;

        public d(WindowInsetsController windowInsetsController, D d4) {
            new r.j();
            this.f1641a = windowInsetsController;
            this.f1642b = d4;
        }

        @Override // M.k0.e
        public final void a(boolean z4) {
            Window window = this.f1643c;
            if (z4) {
                if (window != null) {
                    View decorView = window.getDecorView();
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 16);
                }
                this.f1641a.setSystemBarsAppearance(16, 16);
                return;
            }
            if (window != null) {
                View decorView2 = window.getDecorView();
                decorView2.setSystemUiVisibility(decorView2.getSystemUiVisibility() & (-17));
            }
            this.f1641a.setSystemBarsAppearance(0, 16);
        }

        @Override // M.k0.e
        public final void b(boolean z4) {
            Window window = this.f1643c;
            if (z4) {
                if (window != null) {
                    View decorView = window.getDecorView();
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 8192);
                }
                this.f1641a.setSystemBarsAppearance(8, 8);
                return;
            }
            if (window != null) {
                View decorView2 = window.getDecorView();
                decorView2.setSystemUiVisibility(decorView2.getSystemUiVisibility() & (-8193));
            }
            this.f1641a.setSystemBarsAppearance(0, 8);
        }

        @Override // M.k0.e
        public final void c() {
            this.f1642b.f1516a.a();
            this.f1641a.show(0);
        }
    }

    @Deprecated
    public k0(WindowInsetsController windowInsetsController) {
        this.f1638a = new d(windowInsetsController, new D(windowInsetsController));
    }

    public k0(Window window, View view) {
        WindowInsetsController insetsController;
        D d4 = new D(view);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 30) {
            insetsController = window.getInsetsController();
            d dVar = new d(insetsController, d4);
            dVar.f1643c = window;
            this.f1638a = dVar;
        } else if (i4 >= 26) {
            this.f1638a = new a(window, d4);
        } else if (i4 >= 23) {
            this.f1638a = new a(window, d4);
        } else {
            this.f1638a = new a(window, d4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e {
        public void c() {
            throw null;
        }

        public void a(boolean z4) {
        }

        public void b(boolean z4) {
        }
    }
}
