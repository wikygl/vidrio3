package k;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* renamed from: k.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class SubMenuC0684g extends MenuC0682e implements SubMenu {

    /* renamed from: e  reason: collision with root package name */
    public final G.c f4926e;

    public SubMenuC0684g(Context context, G.c cVar) {
        super(context, cVar);
        this.f4926e = cVar;
    }

    @Override // android.view.SubMenu
    public final void clearHeader() {
        this.f4926e.clearHeader();
    }

    @Override // android.view.SubMenu
    public final MenuItem getItem() {
        return c(this.f4926e.getItem());
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(int i4) {
        this.f4926e.setHeaderIcon(i4);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(int i4) {
        this.f4926e.setHeaderTitle(i4);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderView(View view) {
        this.f4926e.setHeaderView(view);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(int i4) {
        this.f4926e.setIcon(i4);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderIcon(Drawable drawable) {
        this.f4926e.setHeaderIcon(drawable);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setHeaderTitle(CharSequence charSequence) {
        this.f4926e.setHeaderTitle(charSequence);
        return this;
    }

    @Override // android.view.SubMenu
    public final SubMenu setIcon(Drawable drawable) {
        this.f4926e.setIcon(drawable);
        return this;
    }
}
