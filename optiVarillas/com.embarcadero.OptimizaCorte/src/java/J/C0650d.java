package j;

import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.view.menu.f;
import androidx.appcompat.widget.ActionBarContextView;
import j.AbstractC0647a;
import java.lang.ref.WeakReference;

/* renamed from: j.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0650d extends AbstractC0647a implements f.a {

    /* renamed from: l  reason: collision with root package name */
    public Context f4656l;

    /* renamed from: m  reason: collision with root package name */
    public ActionBarContextView f4657m;

    /* renamed from: n  reason: collision with root package name */
    public AbstractC0647a.InterfaceC0054a f4658n;

    /* renamed from: o  reason: collision with root package name */
    public WeakReference<View> f4659o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f4660p;

    /* renamed from: q  reason: collision with root package name */
    public androidx.appcompat.view.menu.f f4661q;

    public final boolean a(androidx.appcompat.view.menu.f fVar, MenuItem menuItem) {
        return this.f4658n.a(this, menuItem);
    }

    public final void b(androidx.appcompat.view.menu.f fVar) {
        i();
        androidx.appcompat.widget.a aVar = this.f4657m.f5094m;
        if (aVar != null) {
            aVar.l();
        }
    }

    @Override // j.AbstractC0647a
    public final void c() {
        if (this.f4660p) {
            return;
        }
        this.f4660p = true;
        this.f4658n.c(this);
    }

    @Override // j.AbstractC0647a
    public final View d() {
        WeakReference<View> weakReference = this.f4659o;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    @Override // j.AbstractC0647a
    public final androidx.appcompat.view.menu.f e() {
        return this.f4661q;
    }

    @Override // j.AbstractC0647a
    public final MenuInflater f() {
        return new C0652f(this.f4657m.getContext());
    }

    @Override // j.AbstractC0647a
    public final CharSequence g() {
        return this.f4657m.getSubtitle();
    }

    @Override // j.AbstractC0647a
    public final CharSequence h() {
        return this.f4657m.getTitle();
    }

    @Override // j.AbstractC0647a
    public final void i() {
        this.f4658n.d(this, this.f4661q);
    }

    @Override // j.AbstractC0647a
    public final boolean j() {
        return this.f4657m.B;
    }

    @Override // j.AbstractC0647a
    public final void k(View view) {
        WeakReference<View> weakReference;
        this.f4657m.setCustomView(view);
        if (view != null) {
            weakReference = new WeakReference<>(view);
        } else {
            weakReference = null;
        }
        this.f4659o = weakReference;
    }

    @Override // j.AbstractC0647a
    public final void l(int i4) {
        m(this.f4656l.getString(i4));
    }

    @Override // j.AbstractC0647a
    public final void m(CharSequence charSequence) {
        this.f4657m.setSubtitle(charSequence);
    }

    @Override // j.AbstractC0647a
    public final void n(int i4) {
        o(this.f4656l.getString(i4));
    }

    @Override // j.AbstractC0647a
    public final void o(CharSequence charSequence) {
        this.f4657m.setTitle(charSequence);
    }

    @Override // j.AbstractC0647a
    public final void p(boolean z4) {
        this.f4650k = z4;
        this.f4657m.setTitleOptional(z4);
    }
}
