package j;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import j.AbstractC0647a;
import java.util.ArrayList;
import k.MenuC0682e;
import k.MenuItemC0680c;
import r.j;

/* renamed from: j.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0651e extends ActionMode {

    /* renamed from: a  reason: collision with root package name */
    public final Context f4662a;

    /* renamed from: b  reason: collision with root package name */
    public final AbstractC0647a f4663b;

    /* renamed from: j.e$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a implements AbstractC0647a.InterfaceC0054a {

        /* renamed from: a  reason: collision with root package name */
        public final ActionMode.Callback f4664a;

        /* renamed from: b  reason: collision with root package name */
        public final Context f4665b;

        /* renamed from: c  reason: collision with root package name */
        public final ArrayList<C0651e> f4666c = new ArrayList<>();

        /* renamed from: d  reason: collision with root package name */
        public final j<Menu, Menu> f4667d = new j<>();

        public a(Context context, ActionMode.Callback callback) {
            this.f4665b = context;
            this.f4664a = callback;
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean a(AbstractC0647a abstractC0647a, MenuItem menuItem) {
            return this.f4664a.onActionItemClicked(e(abstractC0647a), new MenuItemC0680c(this.f4665b, (G.b) menuItem));
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean b(AbstractC0647a abstractC0647a, androidx.appcompat.view.menu.f fVar) {
            C0651e e4 = e(abstractC0647a);
            j<Menu, Menu> jVar = this.f4667d;
            Menu orDefault = jVar.getOrDefault(fVar, null);
            if (orDefault == null) {
                orDefault = new MenuC0682e(this.f4665b, fVar);
                jVar.put(fVar, orDefault);
            }
            return this.f4664a.onCreateActionMode(e4, orDefault);
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final void c(AbstractC0647a abstractC0647a) {
            this.f4664a.onDestroyActionMode(e(abstractC0647a));
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean d(AbstractC0647a abstractC0647a, Menu menu) {
            C0651e e4 = e(abstractC0647a);
            j<Menu, Menu> jVar = this.f4667d;
            Menu orDefault = jVar.getOrDefault(menu, null);
            if (orDefault == null) {
                orDefault = new MenuC0682e(this.f4665b, (G.a) menu);
                jVar.put(menu, orDefault);
            }
            return this.f4664a.onPrepareActionMode(e4, orDefault);
        }

        public final C0651e e(AbstractC0647a abstractC0647a) {
            ArrayList<C0651e> arrayList = this.f4666c;
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                C0651e c0651e = arrayList.get(i4);
                if (c0651e != null && c0651e.f4663b == abstractC0647a) {
                    return c0651e;
                }
            }
            C0651e c0651e2 = new C0651e(this.f4665b, abstractC0647a);
            arrayList.add(c0651e2);
            return c0651e2;
        }
    }

    public C0651e(Context context, AbstractC0647a abstractC0647a) {
        this.f4662a = context;
        this.f4663b = abstractC0647a;
    }

    @Override // android.view.ActionMode
    public final void finish() {
        this.f4663b.c();
    }

    @Override // android.view.ActionMode
    public final View getCustomView() {
        return this.f4663b.d();
    }

    @Override // android.view.ActionMode
    public final Menu getMenu() {
        return new MenuC0682e(this.f4662a, this.f4663b.e());
    }

    @Override // android.view.ActionMode
    public final MenuInflater getMenuInflater() {
        return this.f4663b.f();
    }

    @Override // android.view.ActionMode
    public final CharSequence getSubtitle() {
        return this.f4663b.g();
    }

    @Override // android.view.ActionMode
    public final Object getTag() {
        return this.f4663b.f4649j;
    }

    @Override // android.view.ActionMode
    public final CharSequence getTitle() {
        return this.f4663b.h();
    }

    @Override // android.view.ActionMode
    public final boolean getTitleOptionalHint() {
        return this.f4663b.f4650k;
    }

    @Override // android.view.ActionMode
    public final void invalidate() {
        this.f4663b.i();
    }

    @Override // android.view.ActionMode
    public final boolean isTitleOptional() {
        return this.f4663b.j();
    }

    @Override // android.view.ActionMode
    public final void setCustomView(View view) {
        this.f4663b.k(view);
    }

    @Override // android.view.ActionMode
    public final void setSubtitle(CharSequence charSequence) {
        this.f4663b.m(charSequence);
    }

    @Override // android.view.ActionMode
    public final void setTag(Object obj) {
        this.f4663b.f4649j = obj;
    }

    @Override // android.view.ActionMode
    public final void setTitle(CharSequence charSequence) {
        this.f4663b.o(charSequence);
    }

    @Override // android.view.ActionMode
    public final void setTitleOptionalHint(boolean z4) {
        this.f4663b.p(z4);
    }

    @Override // android.view.ActionMode
    public final void setSubtitle(int i4) {
        this.f4663b.l(i4);
    }

    @Override // android.view.ActionMode
    public final void setTitle(int i4) {
        this.f4663b.n(i4);
    }
}
