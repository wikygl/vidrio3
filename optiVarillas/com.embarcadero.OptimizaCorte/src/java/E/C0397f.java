package e;

import C1.C0149c;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.activity.ComponentActivity;
import e.AbstractC0399h;
import java.util.ArrayList;
import l.h0;

/* renamed from: e.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class C0397f extends androidx.fragment.app.p implements InterfaceC0398g {

    /* renamed from: G  reason: collision with root package name */
    public LayoutInflater$Factory2C0401j f3181G;

    public C0397f() {
        ((ComponentActivity) this).n.f5505b.b("androidx:appcompat", new C0395d(this));
        t(new C0396e(this));
    }

    public final void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        z();
        x().c(view, layoutParams);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void attachBaseContext(Context context) {
        super/*android.app.Activity*/.attachBaseContext(x().d(context));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void closeOptionsMenu() {
        y();
        if (getWindow().hasFeature(0)) {
            super/*android.app.Activity*/.closeOptionsMenu();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean dispatchKeyEvent(KeyEvent keyEvent) {
        keyEvent.getKeyCode();
        y();
        return super/*B.m*/.dispatchKeyEvent(keyEvent);
    }

    public final <T extends View> T findViewById(int i4) {
        return (T) x().e(i4);
    }

    public final MenuInflater getMenuInflater() {
        return x().h();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Resources getResources() {
        int i4 = h0.f5152a;
        return super/*android.app.Activity*/.getResources();
    }

    public final void invalidateOptionsMenu() {
        x().j();
    }

    public final void onConfigurationChanged(Configuration configuration) {
        super/*androidx.activity.ComponentActivity*/.onConfigurationChanged(configuration);
        x().l();
    }

    public final void onDestroy() {
        super.onDestroy();
        x().n();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onKeyDown(int i4, KeyEvent keyEvent) {
        Window window;
        if (Build.VERSION.SDK_INT < 26 && !keyEvent.isCtrlPressed() && !KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState()) && keyEvent.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyEvent.getKeyCode()) && (window = getWindow()) != null && window.getDecorView() != null && window.getDecorView().dispatchKeyShortcutEvent(keyEvent)) {
            return true;
        }
        return super/*android.app.Activity*/.onKeyDown(i4, keyEvent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onMenuItemSelected(int i4, MenuItem menuItem) {
        Intent a4;
        if (super.onMenuItemSelected(i4, menuItem)) {
            return true;
        }
        AbstractC0392a y4 = y();
        if (menuItem.getItemId() == 16908332 && y4 != null && (((z) y4).f3326e.n() & 4) != 0 && (a4 = B.o.a(this)) != null) {
            if (shouldUpRecreateTask(a4)) {
                ArrayList arrayList = new ArrayList();
                Intent a5 = B.o.a(this);
                if (a5 == null) {
                    a5 = B.o.a(this);
                }
                if (a5 != null) {
                    ComponentName component = a5.getComponent();
                    if (component == null) {
                        component = a5.resolveActivity(getPackageManager());
                    }
                    int size = arrayList.size();
                    try {
                        Intent b4 = B.o.b(this, component);
                        while (b4 != null) {
                            arrayList.add(size, b4);
                            b4 = B.o.b(this, b4.getComponent());
                        }
                        arrayList.add(a5);
                    } catch (PackageManager.NameNotFoundException e4) {
                        Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
                        throw new IllegalArgumentException(e4);
                    }
                }
                if (!arrayList.isEmpty()) {
                    Intent[] intentArr = (Intent[]) arrayList.toArray(new Intent[0]);
                    intentArr[0] = new Intent(intentArr[0]).addFlags(268484608);
                    startActivities(intentArr, null);
                    try {
                        finishAffinity();
                        return true;
                    } catch (IllegalStateException unused) {
                        finish();
                        return true;
                    }
                }
                throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
            }
            navigateUpTo(a4);
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onPostCreate(Bundle bundle) {
        super/*android.app.Activity*/.onPostCreate(bundle);
        ((LayoutInflater$Factory2C0401j) x()).G();
    }

    public final void onPostResume() {
        super.onPostResume();
        x().o();
    }

    public final void onStart() {
        super.onStart();
        x().p();
    }

    public final void onStop() {
        super.onStop();
        x().q();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onTitleChanged(CharSequence charSequence, int i4) {
        super/*android.app.Activity*/.onTitleChanged(charSequence, i4);
        x().w(charSequence);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void openOptionsMenu() {
        y();
        if (getWindow().hasFeature(0)) {
            super/*android.app.Activity*/.openOptionsMenu();
        }
    }

    public final void setContentView(int i4) {
        z();
        x().t(i4);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void setTheme(int i4) {
        super/*android.app.Activity*/.setTheme(i4);
        ((LayoutInflater$Factory2C0401j) x()).f3229d0 = i4;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final AbstractC0399h x() {
        if (this.f3181G == null) {
            AbstractC0399h.c cVar = AbstractC0399h.f3182j;
            this.f3181G = new LayoutInflater$Factory2C0401j(this, null, this, this);
        }
        return this.f3181G;
    }

    public final AbstractC0392a y() {
        LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = (LayoutInflater$Factory2C0401j) x();
        layoutInflater$Factory2C0401j.L();
        return layoutInflater$Factory2C0401j.f3247x;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void z() {
        C0149c.f(getWindow().getDecorView(), this);
        View decorView = getWindow().getDecorView();
        v3.h.e(decorView, "<this>");
        decorView.setTag(2131231330, this);
        B3.a.b(getWindow().getDecorView(), this);
        E1.n.a(getWindow().getDecorView(), this);
    }

    public void setContentView(View view) {
        z();
        x().u(view);
    }

    public final void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        z();
        x().v(view, layoutParams);
    }

    public final void onContentChanged() {
    }
}
