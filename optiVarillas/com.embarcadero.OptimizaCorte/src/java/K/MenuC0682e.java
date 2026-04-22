package k;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import r.j;

/* renamed from: k.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class MenuC0682e extends AbstractC0679b implements Menu {

    /* renamed from: d  reason: collision with root package name */
    public final G.a f4925d;

    public MenuC0682e(Context context, G.a aVar) {
        super(context);
        if (aVar != null) {
            this.f4925d = aVar;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }

    @Override // android.view.Menu
    public final MenuItem add(CharSequence charSequence) {
        return c(this.f4925d.add(charSequence));
    }

    @Override // android.view.Menu
    public final int addIntentOptions(int i4, int i5, int i6, ComponentName componentName, Intent[] intentArr, Intent intent, int i7, MenuItem[] menuItemArr) {
        MenuItem[] menuItemArr2;
        if (menuItemArr != null) {
            menuItemArr2 = new MenuItem[menuItemArr.length];
        } else {
            menuItemArr2 = null;
        }
        int addIntentOptions = this.f4925d.addIntentOptions(i4, i5, i6, componentName, intentArr, intent, i7, menuItemArr2);
        if (menuItemArr2 != null) {
            int length = menuItemArr2.length;
            for (int i8 = 0; i8 < length; i8++) {
                menuItemArr[i8] = c(menuItemArr2[i8]);
            }
        }
        return addIntentOptions;
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(CharSequence charSequence) {
        return d(this.f4925d.addSubMenu(charSequence));
    }

    @Override // android.view.Menu
    public final void clear() {
        j<G.b, MenuItem> jVar = this.f4912b;
        if (jVar != null) {
            jVar.clear();
        }
        j<G.c, SubMenu> jVar2 = this.f4913c;
        if (jVar2 != null) {
            jVar2.clear();
        }
        this.f4925d.clear();
    }

    @Override // android.view.Menu
    public final void close() {
        this.f4925d.close();
    }

    @Override // android.view.Menu
    public final MenuItem findItem(int i4) {
        return c(this.f4925d.findItem(i4));
    }

    @Override // android.view.Menu
    public final MenuItem getItem(int i4) {
        return c(this.f4925d.getItem(i4));
    }

    @Override // android.view.Menu
    public final boolean hasVisibleItems() {
        return this.f4925d.hasVisibleItems();
    }

    @Override // android.view.Menu
    public final boolean isShortcutKey(int i4, KeyEvent keyEvent) {
        return this.f4925d.isShortcutKey(i4, keyEvent);
    }

    @Override // android.view.Menu
    public final boolean performIdentifierAction(int i4, int i5) {
        return this.f4925d.performIdentifierAction(i4, i5);
    }

    @Override // android.view.Menu
    public final boolean performShortcut(int i4, KeyEvent keyEvent, int i5) {
        return this.f4925d.performShortcut(i4, keyEvent, i5);
    }

    @Override // android.view.Menu
    public final void removeGroup(int i4) {
        if (this.f4912b != null) {
            int i5 = 0;
            while (true) {
                j<G.b, MenuItem> jVar = this.f4912b;
                if (i5 >= jVar.f5685l) {
                    break;
                }
                if (jVar.h(i5).getGroupId() == i4) {
                    this.f4912b.i(i5);
                    i5--;
                }
                i5++;
            }
        }
        this.f4925d.removeGroup(i4);
    }

    @Override // android.view.Menu
    public final void removeItem(int i4) {
        if (this.f4912b != null) {
            int i5 = 0;
            while (true) {
                j<G.b, MenuItem> jVar = this.f4912b;
                if (i5 >= jVar.f5685l) {
                    break;
                } else if (jVar.h(i5).getItemId() == i4) {
                    this.f4912b.i(i5);
                    break;
                } else {
                    i5++;
                }
            }
        }
        this.f4925d.removeItem(i4);
    }

    @Override // android.view.Menu
    public final void setGroupCheckable(int i4, boolean z4, boolean z5) {
        this.f4925d.setGroupCheckable(i4, z4, z5);
    }

    @Override // android.view.Menu
    public final void setGroupEnabled(int i4, boolean z4) {
        this.f4925d.setGroupEnabled(i4, z4);
    }

    @Override // android.view.Menu
    public final void setGroupVisible(int i4, boolean z4) {
        this.f4925d.setGroupVisible(i4, z4);
    }

    @Override // android.view.Menu
    public final void setQwertyMode(boolean z4) {
        this.f4925d.setQwertyMode(z4);
    }

    @Override // android.view.Menu
    public final int size() {
        return this.f4925d.size();
    }

    @Override // android.view.Menu
    public final MenuItem add(int i4) {
        return c(this.f4925d.add(i4));
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i4) {
        return d(this.f4925d.addSubMenu(i4));
    }

    @Override // android.view.Menu
    public final MenuItem add(int i4, int i5, int i6, CharSequence charSequence) {
        return c(this.f4925d.add(i4, i5, i6, charSequence));
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i4, int i5, int i6, CharSequence charSequence) {
        return d(this.f4925d.addSubMenu(i4, i5, i6, charSequence));
    }

    @Override // android.view.Menu
    public final MenuItem add(int i4, int i5, int i6, int i7) {
        return c(this.f4925d.add(i4, i5, i6, i7));
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i4, int i5, int i6, int i7) {
        return d(this.f4925d.addSubMenu(i4, i5, i6, i7));
    }
}
