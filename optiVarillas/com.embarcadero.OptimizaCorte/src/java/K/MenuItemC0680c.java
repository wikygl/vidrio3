package k;

import M.AbstractC0220b;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.CollapsibleActionView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.view.menu.h;
import j.InterfaceC0648b;
import java.lang.reflect.Method;

/* renamed from: k.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class MenuItemC0680c extends AbstractC0679b implements MenuItem {

    /* renamed from: d  reason: collision with root package name */
    public final G.b f4914d;

    /* renamed from: e  reason: collision with root package name */
    public Method f4915e;

    /* renamed from: k.c$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a extends AbstractC0220b implements ActionProvider.VisibilityListener {

        /* renamed from: b  reason: collision with root package name */
        public AbstractC0220b.a f4916b;

        /* renamed from: c  reason: collision with root package name */
        public final ActionProvider f4917c;

        public a(ActionProvider actionProvider) {
            this.f4917c = actionProvider;
        }

        @Override // M.AbstractC0220b
        public final boolean a() {
            return this.f4917c.hasSubMenu();
        }

        @Override // M.AbstractC0220b
        public final boolean b() {
            return this.f4917c.isVisible();
        }

        @Override // M.AbstractC0220b
        public final View c() {
            return this.f4917c.onCreateActionView();
        }

        @Override // M.AbstractC0220b
        public final View d(MenuItem menuItem) {
            return this.f4917c.onCreateActionView(menuItem);
        }

        @Override // M.AbstractC0220b
        public final boolean e() {
            return this.f4917c.onPerformDefaultAction();
        }

        @Override // M.AbstractC0220b
        public final void f(SubMenu subMenu) {
            this.f4917c.onPrepareSubMenu(MenuItemC0680c.this.d(subMenu));
        }

        @Override // M.AbstractC0220b
        public final boolean g() {
            return this.f4917c.overridesItemVisibility();
        }

        @Override // M.AbstractC0220b
        public final void h(h.a aVar) {
            this.f4916b = aVar;
            this.f4917c.setVisibilityListener(this);
        }

        @Override // android.view.ActionProvider.VisibilityListener
        public final void onActionProviderVisibilityChanged(boolean z4) {
            h.a aVar = this.f4916b;
            if (aVar != null) {
                androidx.appcompat.view.menu.f fVar = aVar.a.n;
                fVar.h = true;
                fVar.p(true);
            }
        }
    }

    /* renamed from: k.c$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends FrameLayout implements InterfaceC0648b {

        /* renamed from: j  reason: collision with root package name */
        public final CollapsibleActionView f4919j;

        public b(View view) {
            super(view.getContext());
            this.f4919j = (CollapsibleActionView) view;
            addView(view);
        }

        @Override // j.InterfaceC0648b
        public final void c() {
            this.f4919j.onActionViewExpanded();
        }

        @Override // j.InterfaceC0648b
        public final void d() {
            this.f4919j.onActionViewCollapsed();
        }
    }

    /* renamed from: k.c$c  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class MenuItem$OnActionExpandListenerC0056c implements MenuItem.OnActionExpandListener {

        /* renamed from: a  reason: collision with root package name */
        public final MenuItem.OnActionExpandListener f4920a;

        public MenuItem$OnActionExpandListenerC0056c(MenuItem.OnActionExpandListener onActionExpandListener) {
            this.f4920a = onActionExpandListener;
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public final boolean onMenuItemActionCollapse(MenuItem menuItem) {
            return this.f4920a.onMenuItemActionCollapse(MenuItemC0680c.this.c(menuItem));
        }

        @Override // android.view.MenuItem.OnActionExpandListener
        public final boolean onMenuItemActionExpand(MenuItem menuItem) {
            return this.f4920a.onMenuItemActionExpand(MenuItemC0680c.this.c(menuItem));
        }
    }

    /* renamed from: k.c$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class d implements MenuItem.OnMenuItemClickListener {

        /* renamed from: a  reason: collision with root package name */
        public final MenuItem.OnMenuItemClickListener f4922a;

        public d(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
            this.f4922a = onMenuItemClickListener;
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public final boolean onMenuItemClick(MenuItem menuItem) {
            return this.f4922a.onMenuItemClick(MenuItemC0680c.this.c(menuItem));
        }
    }

    public MenuItemC0680c(Context context, G.b bVar) {
        super(context);
        if (bVar != null) {
            this.f4914d = bVar;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }

    @Override // android.view.MenuItem
    public final boolean collapseActionView() {
        return this.f4914d.collapseActionView();
    }

    @Override // android.view.MenuItem
    public final boolean expandActionView() {
        return this.f4914d.expandActionView();
    }

    @Override // android.view.MenuItem
    public final ActionProvider getActionProvider() {
        AbstractC0220b b4 = this.f4914d.b();
        if (b4 instanceof a) {
            return ((a) b4).f4917c;
        }
        return null;
    }

    @Override // android.view.MenuItem
    public final View getActionView() {
        View actionView = this.f4914d.getActionView();
        if (actionView instanceof b) {
            return (View) ((b) actionView).f4919j;
        }
        return actionView;
    }

    @Override // android.view.MenuItem
    public final int getAlphabeticModifiers() {
        return this.f4914d.getAlphabeticModifiers();
    }

    @Override // android.view.MenuItem
    public final char getAlphabeticShortcut() {
        return this.f4914d.getAlphabeticShortcut();
    }

    @Override // android.view.MenuItem
    public final CharSequence getContentDescription() {
        return this.f4914d.getContentDescription();
    }

    @Override // android.view.MenuItem
    public final int getGroupId() {
        return this.f4914d.getGroupId();
    }

    @Override // android.view.MenuItem
    public final Drawable getIcon() {
        return this.f4914d.getIcon();
    }

    @Override // android.view.MenuItem
    public final ColorStateList getIconTintList() {
        return this.f4914d.getIconTintList();
    }

    @Override // android.view.MenuItem
    public final PorterDuff.Mode getIconTintMode() {
        return this.f4914d.getIconTintMode();
    }

    @Override // android.view.MenuItem
    public final Intent getIntent() {
        return this.f4914d.getIntent();
    }

    @Override // android.view.MenuItem
    public final int getItemId() {
        return this.f4914d.getItemId();
    }

    @Override // android.view.MenuItem
    public final ContextMenu.ContextMenuInfo getMenuInfo() {
        return this.f4914d.getMenuInfo();
    }

    @Override // android.view.MenuItem
    public final int getNumericModifiers() {
        return this.f4914d.getNumericModifiers();
    }

    @Override // android.view.MenuItem
    public final char getNumericShortcut() {
        return this.f4914d.getNumericShortcut();
    }

    @Override // android.view.MenuItem
    public final int getOrder() {
        return this.f4914d.getOrder();
    }

    @Override // android.view.MenuItem
    public final SubMenu getSubMenu() {
        return d(this.f4914d.getSubMenu());
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitle() {
        return this.f4914d.getTitle();
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitleCondensed() {
        return this.f4914d.getTitleCondensed();
    }

    @Override // android.view.MenuItem
    public final CharSequence getTooltipText() {
        return this.f4914d.getTooltipText();
    }

    @Override // android.view.MenuItem
    public final boolean hasSubMenu() {
        return this.f4914d.hasSubMenu();
    }

    @Override // android.view.MenuItem
    public final boolean isActionViewExpanded() {
        return this.f4914d.isActionViewExpanded();
    }

    @Override // android.view.MenuItem
    public final boolean isCheckable() {
        return this.f4914d.isCheckable();
    }

    @Override // android.view.MenuItem
    public final boolean isChecked() {
        return this.f4914d.isChecked();
    }

    @Override // android.view.MenuItem
    public final boolean isEnabled() {
        return this.f4914d.isEnabled();
    }

    @Override // android.view.MenuItem
    public final boolean isVisible() {
        return this.f4914d.isVisible();
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionProvider(ActionProvider actionProvider) {
        a aVar = new a(actionProvider);
        if (actionProvider == null) {
            aVar = null;
        }
        this.f4914d.a(aVar);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(View view) {
        if (view instanceof CollapsibleActionView) {
            view = new b(view);
        }
        this.f4914d.setActionView(view);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c4) {
        this.f4914d.setAlphabeticShortcut(c4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setCheckable(boolean z4) {
        this.f4914d.setCheckable(z4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setChecked(boolean z4) {
        this.f4914d.setChecked(z4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setContentDescription(CharSequence charSequence) {
        this.f4914d.setContentDescription(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setEnabled(boolean z4) {
        this.f4914d.setEnabled(z4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(Drawable drawable) {
        this.f4914d.setIcon(drawable);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIconTintList(ColorStateList colorStateList) {
        this.f4914d.setIconTintList(colorStateList);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIconTintMode(PorterDuff.Mode mode) {
        this.f4914d.setIconTintMode(mode);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIntent(Intent intent) {
        this.f4914d.setIntent(intent);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setNumericShortcut(char c4) {
        this.f4914d.setNumericShortcut(c4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        MenuItem$OnActionExpandListenerC0056c menuItem$OnActionExpandListenerC0056c;
        if (onActionExpandListener != null) {
            menuItem$OnActionExpandListenerC0056c = new MenuItem$OnActionExpandListenerC0056c(onActionExpandListener);
        } else {
            menuItem$OnActionExpandListenerC0056c = null;
        }
        this.f4914d.setOnActionExpandListener(menuItem$OnActionExpandListenerC0056c);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        d dVar;
        if (onMenuItemClickListener != null) {
            dVar = new d(onMenuItemClickListener);
        } else {
            dVar = null;
        }
        this.f4914d.setOnMenuItemClickListener(dVar);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setShortcut(char c4, char c5) {
        this.f4914d.setShortcut(c4, c5);
        return this;
    }

    @Override // android.view.MenuItem
    public final void setShowAsAction(int i4) {
        this.f4914d.setShowAsAction(i4);
    }

    @Override // android.view.MenuItem
    public final MenuItem setShowAsActionFlags(int i4) {
        this.f4914d.setShowAsActionFlags(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(CharSequence charSequence) {
        this.f4914d.setTitle(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitleCondensed(CharSequence charSequence) {
        this.f4914d.setTitleCondensed(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTooltipText(CharSequence charSequence) {
        this.f4914d.setTooltipText(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setVisible(boolean z4) {
        return this.f4914d.setVisible(z4);
    }

    @Override // android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c4, int i4) {
        this.f4914d.setAlphabeticShortcut(c4, i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(int i4) {
        this.f4914d.setIcon(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setNumericShortcut(char c4, int i4) {
        this.f4914d.setNumericShortcut(c4, i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setShortcut(char c4, char c5, int i4, int i5) {
        this.f4914d.setShortcut(c4, c5, i4, i5);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(int i4) {
        this.f4914d.setTitle(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(int i4) {
        G.b bVar = this.f4914d;
        bVar.setActionView(i4);
        View actionView = bVar.getActionView();
        if (actionView instanceof CollapsibleActionView) {
            bVar.setActionView(new b(actionView));
        }
        return this;
    }
}
