package k;

import C.a;
import F.a;
import M.AbstractC0220b;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* renamed from: k.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0678a implements G.b {

    /* renamed from: a  reason: collision with root package name */
    public CharSequence f4896a;

    /* renamed from: b  reason: collision with root package name */
    public CharSequence f4897b;

    /* renamed from: c  reason: collision with root package name */
    public Intent f4898c;

    /* renamed from: d  reason: collision with root package name */
    public char f4899d;
    public char f;

    /* renamed from: h  reason: collision with root package name */
    public Drawable f4902h;

    /* renamed from: i  reason: collision with root package name */
    public final Context f4903i;

    /* renamed from: j  reason: collision with root package name */
    public CharSequence f4904j;

    /* renamed from: k  reason: collision with root package name */
    public CharSequence f4905k;

    /* renamed from: e  reason: collision with root package name */
    public int f4900e = 4096;

    /* renamed from: g  reason: collision with root package name */
    public int f4901g = 4096;

    /* renamed from: l  reason: collision with root package name */
    public ColorStateList f4906l = null;

    /* renamed from: m  reason: collision with root package name */
    public PorterDuff.Mode f4907m = null;

    /* renamed from: n  reason: collision with root package name */
    public boolean f4908n = false;

    /* renamed from: o  reason: collision with root package name */
    public boolean f4909o = false;

    /* renamed from: p  reason: collision with root package name */
    public int f4910p = 16;

    public C0678a(Context context, CharSequence charSequence) {
        this.f4903i = context;
        this.f4896a = charSequence;
    }

    @Override // G.b
    public final G.b a(AbstractC0220b abstractC0220b) {
        throw new UnsupportedOperationException();
    }

    @Override // G.b
    public final AbstractC0220b b() {
        return null;
    }

    public final void c() {
        Drawable drawable = this.f4902h;
        if (drawable != null) {
            if (this.f4908n || this.f4909o) {
                Drawable g4 = F.a.g(drawable);
                this.f4902h = g4;
                Drawable mutate = g4.mutate();
                this.f4902h = mutate;
                if (this.f4908n) {
                    a.C0006a.h(mutate, this.f4906l);
                }
                if (this.f4909o) {
                    a.C0006a.i(this.f4902h, this.f4907m);
                }
            }
        }
    }

    @Override // android.view.MenuItem
    public final boolean collapseActionView() {
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean expandActionView() {
        return false;
    }

    @Override // android.view.MenuItem
    public final ActionProvider getActionProvider() {
        throw new UnsupportedOperationException();
    }

    @Override // android.view.MenuItem
    public final View getActionView() {
        return null;
    }

    @Override // G.b, android.view.MenuItem
    public final int getAlphabeticModifiers() {
        return this.f4901g;
    }

    @Override // android.view.MenuItem
    public final char getAlphabeticShortcut() {
        return this.f;
    }

    @Override // G.b, android.view.MenuItem
    public final CharSequence getContentDescription() {
        return this.f4904j;
    }

    @Override // android.view.MenuItem
    public final int getGroupId() {
        return 0;
    }

    @Override // android.view.MenuItem
    public final Drawable getIcon() {
        return this.f4902h;
    }

    @Override // G.b, android.view.MenuItem
    public final ColorStateList getIconTintList() {
        return this.f4906l;
    }

    @Override // G.b, android.view.MenuItem
    public final PorterDuff.Mode getIconTintMode() {
        return this.f4907m;
    }

    @Override // android.view.MenuItem
    public final Intent getIntent() {
        return this.f4898c;
    }

    @Override // android.view.MenuItem
    public final int getItemId() {
        return 16908332;
    }

    @Override // android.view.MenuItem
    public final ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override // G.b, android.view.MenuItem
    public final int getNumericModifiers() {
        return this.f4900e;
    }

    @Override // android.view.MenuItem
    public final char getNumericShortcut() {
        return this.f4899d;
    }

    @Override // android.view.MenuItem
    public final int getOrder() {
        return 0;
    }

    @Override // android.view.MenuItem
    public final SubMenu getSubMenu() {
        return null;
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitle() {
        return this.f4896a;
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitleCondensed() {
        CharSequence charSequence = this.f4897b;
        if (charSequence == null) {
            return this.f4896a;
        }
        return charSequence;
    }

    @Override // G.b, android.view.MenuItem
    public final CharSequence getTooltipText() {
        return this.f4905k;
    }

    @Override // android.view.MenuItem
    public final boolean hasSubMenu() {
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean isActionViewExpanded() {
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean isCheckable() {
        if ((this.f4910p & 1) != 0) {
            return true;
        }
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean isChecked() {
        if ((this.f4910p & 2) != 0) {
            return true;
        }
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean isEnabled() {
        if ((this.f4910p & 16) != 0) {
            return true;
        }
        return false;
    }

    @Override // android.view.MenuItem
    public final boolean isVisible() {
        if ((this.f4910p & 8) == 0) {
            return true;
        }
        return false;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionProvider(ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(View view) {
        throw new UnsupportedOperationException();
    }

    @Override // android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c4) {
        this.f = Character.toLowerCase(c4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setCheckable(boolean z4) {
        this.f4910p = (z4 ? 1 : 0) | (this.f4910p & (-2));
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setChecked(boolean z4) {
        int i4;
        int i5 = this.f4910p & (-3);
        if (z4) {
            i4 = 2;
        } else {
            i4 = 0;
        }
        this.f4910p = i4 | i5;
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final G.b setContentDescription(CharSequence charSequence) {
        this.f4904j = charSequence;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setEnabled(boolean z4) {
        int i4;
        int i5 = this.f4910p & (-17);
        if (z4) {
            i4 = 16;
        } else {
            i4 = 0;
        }
        this.f4910p = i4 | i5;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(Drawable drawable) {
        this.f4902h = drawable;
        c();
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final MenuItem setIconTintList(ColorStateList colorStateList) {
        this.f4906l = colorStateList;
        this.f4908n = true;
        c();
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final MenuItem setIconTintMode(PorterDuff.Mode mode) {
        this.f4907m = mode;
        this.f4909o = true;
        c();
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIntent(Intent intent) {
        this.f4898c = intent;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setNumericShortcut(char c4) {
        this.f4899d = c4;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        throw new UnsupportedOperationException();
    }

    @Override // android.view.MenuItem
    public final MenuItem setShortcut(char c4, char c5) {
        this.f4899d = c4;
        this.f = Character.toLowerCase(c5);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(CharSequence charSequence) {
        this.f4896a = charSequence;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitleCondensed(CharSequence charSequence) {
        this.f4897b = charSequence;
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final G.b setTooltipText(CharSequence charSequence) {
        this.f4905k = charSequence;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setVisible(boolean z4) {
        int i4 = 8;
        int i5 = this.f4910p & 8;
        if (z4) {
            i4 = 0;
        }
        this.f4910p = i5 | i4;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(int i4) {
        throw new UnsupportedOperationException();
    }

    @Override // G.b, android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c4, int i4) {
        this.f = Character.toLowerCase(c4);
        this.f4901g = KeyEvent.normalizeMetaState(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setContentDescription(CharSequence charSequence) {
        this.f4904j = charSequence;
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final MenuItem setNumericShortcut(char c4, int i4) {
        this.f4899d = c4;
        this.f4900e = KeyEvent.normalizeMetaState(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(int i4) {
        this.f4896a = this.f4903i.getResources().getString(i4);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTooltipText(CharSequence charSequence) {
        this.f4905k = charSequence;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(int i4) {
        this.f4902h = a.C0002a.b(this.f4903i, i4);
        c();
        return this;
    }

    @Override // G.b, android.view.MenuItem
    public final MenuItem setShortcut(char c4, char c5, int i4, int i5) {
        this.f4899d = c4;
        this.f4900e = KeyEvent.normalizeMetaState(i4);
        this.f = Character.toLowerCase(c5);
        this.f4901g = KeyEvent.normalizeMetaState(i5);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        return this;
    }

    @Override // android.view.MenuItem
    public final void setShowAsAction(int i4) {
    }

    @Override // android.view.MenuItem
    public final MenuItem setShowAsActionFlags(int i4) {
        return this;
    }
}
