package v2;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/* renamed from: v2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0832a extends FloatingActionButton.a {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f6287a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ BottomAppBar f6288b;

    /* renamed from: v2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0076a extends FloatingActionButton.a {
        public C0076a() {
        }

        public final void b() {
            BottomAppBar bottomAppBar = C0832a.this.f6288b;
            int i4 = BottomAppBar.p0;
            bottomAppBar.getClass();
        }
    }

    public C0832a(BottomAppBar bottomAppBar, int i4) {
        this.f6288b = bottomAppBar;
        this.f6287a = i4;
    }

    public final void a(FloatingActionButton floatingActionButton) {
        floatingActionButton.setTranslationX(this.f6288b.C(this.f6287a));
        floatingActionButton.k(new C0076a(), true);
    }
}
