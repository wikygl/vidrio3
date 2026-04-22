package e;

import android.view.ViewGroup;

/* renamed from: e.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0392a {

    /* renamed from: e.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface b {
        void a();
    }

    public abstract void a(boolean z4);

    public abstract void b(String str);

    public abstract void c(String str);

    /* renamed from: e.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class C0043a extends ViewGroup.MarginLayoutParams {

        /* renamed from: a  reason: collision with root package name */
        public int f3174a;

        public C0043a(C0043a c0043a) {
            super((ViewGroup.MarginLayoutParams) c0043a);
            this.f3174a = 0;
            this.f3174a = c0043a.f3174a;
        }

        public C0043a(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.f3174a = 0;
        }
    }
}
