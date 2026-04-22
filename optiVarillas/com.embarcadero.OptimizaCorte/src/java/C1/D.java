package C1;

import A1.InterfaceC0084a;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import c2.InterfaceC0374a;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.internal.ads.Fh;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.ws;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class D extends Fh {

    /* renamed from: k  reason: collision with root package name */
    public final AdOverlayInfoParcel f358k;

    /* renamed from: l  reason: collision with root package name */
    public final Activity f359l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f360m = false;

    /* renamed from: n  reason: collision with root package name */
    public boolean f361n = false;

    /* renamed from: o  reason: collision with root package name */
    public boolean f362o = false;

    public D(Activity activity, AdOverlayInfoParcel adOverlayInfoParcel) {
        this.f358k = adOverlayInfoParcel;
        this.f359l = activity;
    }

    public final synchronized void C4() {
        try {
            if (!this.f361n) {
                u uVar = this.f358k.l;
                if (uVar != null) {
                    uVar.U3(4);
                }
                this.f361n = true;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public final void I() {
        this.f362o = true;
    }

    public final void J() {
        u uVar = this.f358k.l;
        if (uVar != null) {
            uVar.b4();
        }
    }

    public final void V0(Bundle bundle) {
        u uVar;
        boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.S7)).booleanValue();
        Activity activity = this.f359l;
        if (booleanValue && !this.f362o) {
            activity.requestWindowFeature(1);
        }
        boolean z4 = false;
        if (bundle != null && bundle.getBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", false)) {
            z4 = true;
        }
        AdOverlayInfoParcel adOverlayInfoParcel = this.f358k;
        if (adOverlayInfoParcel == null) {
            activity.finish();
        } else if (z4) {
            activity.finish();
        } else {
            if (bundle == null) {
                InterfaceC0084a interfaceC0084a = adOverlayInfoParcel.k;
                if (interfaceC0084a != null) {
                    interfaceC0084a.m();
                }
                ws wsVar = adOverlayInfoParcel.D;
                if (wsVar != null) {
                    wsVar.c0();
                }
                if (activity.getIntent() != null && activity.getIntent().getBooleanExtra("shouldCallOnOverlayOpened", true) && (uVar = adOverlayInfoParcel.l) != null) {
                    uVar.L1();
                }
            }
            C0147a c0147a = z1.p.f6575A.f6576a;
            i iVar = adOverlayInfoParcel.j;
            if (!C0147a.b(activity, iVar, adOverlayInfoParcel.r, iVar.f371r)) {
                activity.finish();
            }
        }
    }

    public final boolean k0() {
        return false;
    }

    public final void n() {
        u uVar = this.f358k.l;
        if (uVar != null) {
            uVar.q4();
        }
        if (this.f359l.isFinishing()) {
            C4();
        }
    }

    public final void o() {
        if (this.f359l.isFinishing()) {
            C4();
        }
    }

    public final void t() {
        if (this.f360m) {
            this.f359l.finish();
            return;
        }
        this.f360m = true;
        u uVar = this.f358k.l;
        if (uVar != null) {
            uVar.k3();
        }
    }

    public final void u1(Bundle bundle) {
        bundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", this.f360m);
    }

    public final void x() {
        if (this.f359l.isFinishing()) {
            C4();
        }
    }

    public final void D() {
    }

    public final void f() {
    }

    public final void q() {
    }

    public final void d4(InterfaceC0374a interfaceC0374a) {
    }

    public final void G3(int i4, String[] strArr, int[] iArr) {
    }

    public final void O2(int i4, int i5, Intent intent) {
    }
}
