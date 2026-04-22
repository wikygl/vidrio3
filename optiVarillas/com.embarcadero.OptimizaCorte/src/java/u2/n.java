package U2;

import S0.M0;
import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.textfield.TextInputLayout;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class n {

    /* renamed from: a  reason: collision with root package name */
    public final TextInputLayout f2423a;

    /* renamed from: b  reason: collision with root package name */
    public final com.google.android.material.textfield.a f2424b;

    /* renamed from: c  reason: collision with root package name */
    public final Context f2425c;

    /* renamed from: d  reason: collision with root package name */
    public final CheckableImageButton f2426d;

    public n(com.google.android.material.textfield.a aVar) {
        this.f2423a = aVar.j;
        this.f2424b = aVar;
        this.f2425c = aVar.getContext();
        this.f2426d = aVar.p;
    }

    public int c() {
        return 0;
    }

    public int d() {
        return 0;
    }

    public View.OnFocusChangeListener e() {
        return null;
    }

    public View.OnClickListener f() {
        return null;
    }

    public View.OnFocusChangeListener g() {
        return null;
    }

    public M0 h() {
        return null;
    }

    public boolean i(int i4) {
        return true;
    }

    public boolean j() {
        return false;
    }

    public boolean k() {
        return this instanceof m;
    }

    public boolean l() {
        return false;
    }

    public final void q() {
        this.f2424b.f(false);
    }

    public void a() {
    }

    public void b() {
    }

    public void r() {
    }

    public void s() {
    }

    public void m(EditText editText) {
    }

    public void n(N.l lVar) {
    }

    public void o(AccessibilityEvent accessibilityEvent) {
    }

    public void p(boolean z4) {
    }
}
