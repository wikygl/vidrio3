package k;

import android.content.Context;
import android.view.MenuItem;
import android.view.SubMenu;
import r.j;

/* renamed from: k.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0679b {

    /* renamed from: a  reason: collision with root package name */
    public final Context f4911a;

    /* renamed from: b  reason: collision with root package name */
    public j<G.b, MenuItem> f4912b;

    /* renamed from: c  reason: collision with root package name */
    public j<G.c, SubMenu> f4913c;

    public AbstractC0679b(Context context) {
        this.f4911a = context;
    }

    public final MenuItem c(MenuItem menuItem) {
        if (menuItem instanceof G.b) {
            G.b bVar = (G.b) menuItem;
            if (this.f4912b == null) {
                this.f4912b = new j<>();
            }
            MenuItem orDefault = this.f4912b.getOrDefault(bVar, null);
            if (orDefault == null) {
                MenuItemC0680c menuItemC0680c = new MenuItemC0680c(this.f4911a, bVar);
                this.f4912b.put(bVar, menuItemC0680c);
                return menuItemC0680c;
            }
            return orDefault;
        }
        return menuItem;
    }

    public final SubMenu d(SubMenu subMenu) {
        if (subMenu instanceof G.c) {
            G.c cVar = (G.c) subMenu;
            if (this.f4913c == null) {
                this.f4913c = new j<>();
            }
            SubMenu orDefault = this.f4913c.getOrDefault(cVar, null);
            if (orDefault == null) {
                SubMenuC0684g subMenuC0684g = new SubMenuC0684g(this.f4911a, cVar);
                this.f4913c.put(cVar, subMenuC0684g);
                return subMenuC0684g;
            }
            return orDefault;
        }
        return subMenu;
    }
}
