package e;

import C1.C0149c;
import M.C0232n;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import e.AbstractC0399h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class t extends androidx.activity.j implements InterfaceC0398g {

    /* renamed from: m  reason: collision with root package name */
    public LayoutInflater$Factory2C0401j f3289m;

    /* renamed from: n  reason: collision with root package name */
    public final s f3290n;

    /* JADX WARN: Illegal instructions before constructor call */
    /* JADX WARN: Type inference failed for: r2v2, types: [e.s] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public t(android.content.Context r5, int r6) {
        /*
            r4 = this;
            r0 = 1
            r1 = 2130903398(0x7f030166, float:1.7413613E38)
            if (r6 != 0) goto L15
            android.util.TypedValue r2 = new android.util.TypedValue
            r2.<init>()
            android.content.res.Resources$Theme r3 = r5.getTheme()
            r3.resolveAttribute(r1, r2, r0)
            int r2 = r2.resourceId
            goto L16
        L15:
            r2 = r6
        L16:
            r4.<init>(r5, r2)
            e.s r2 = new e.s
            r2.<init>()
            r4.f3290n = r2
            e.h r2 = r4.q()
            if (r6 != 0) goto L34
            android.util.TypedValue r6 = new android.util.TypedValue
            r6.<init>()
            android.content.res.Resources$Theme r5 = r5.getTheme()
            r5.resolveAttribute(r1, r6, r0)
            int r6 = r6.resourceId
        L34:
            r5 = r2
            e.j r5 = (e.LayoutInflater$Factory2C0401j) r5
            r5.f3229d0 = r6
            r2.m()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: e.t.<init>(android.content.Context, int):void");
    }

    public final void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        q().c(view, layoutParams);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void dismiss() {
        super/*android.app.Dialog*/.dismiss();
        q().n();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return C0232n.b(this.f3290n, getWindow().getDecorView(), this, keyEvent);
    }

    public final <T extends View> T findViewById(int i4) {
        return (T) q().e(i4);
    }

    public final void invalidateOptionsMenu() {
        q().j();
    }

    public void onCreate(Bundle bundle) {
        q().i();
        super.onCreate(bundle);
        q().m();
    }

    public final void onStop() {
        super.onStop();
        q().q();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final AbstractC0399h q() {
        if (this.f3289m == null) {
            AbstractC0399h.c cVar = AbstractC0399h.f3182j;
            this.f3289m = new LayoutInflater$Factory2C0401j(getContext(), getWindow(), this, this);
        }
        return this.f3289m;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void s() {
        C0149c.f(getWindow().getDecorView(), this);
        B3.a.b(getWindow().getDecorView(), this);
        E1.n.a(getWindow().getDecorView(), this);
    }

    public final void setContentView(int i4) {
        s();
        q().t(i4);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setTitle(CharSequence charSequence) {
        super/*android.app.Dialog*/.setTitle(charSequence);
        q().w(charSequence);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean t(KeyEvent keyEvent) {
        return super/*android.app.Dialog*/.dispatchKeyEvent(keyEvent);
    }

    public final void setContentView(View view) {
        s();
        q().u(view);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void setTitle(int i4) {
        super/*android.app.Dialog*/.setTitle(i4);
        q().w(getContext().getString(i4));
    }

    public final void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        s();
        q().v(view, layoutParams);
    }
}
