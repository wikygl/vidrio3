package A1;

import C1.C0147a;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.internal.ads.ws;
import java.util.LinkedHashMap;
import y1.InterfaceC0861a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public /* synthetic */ class P0 implements InterfaceC0861a, E1.e, H1.c, com.google.gson.internal.i {
    public /* synthetic */ P0(E1.f fVar) {
    }

    public static boolean b(EditText editText) {
        if (editText.getInputType() != 0) {
            return true;
        }
        return false;
    }

    public static void c(InputConnection inputConnection, EditorInfo editorInfo, View view) {
        if (inputConnection != null && editorInfo.hintText == null) {
            for (ViewParent parent = view.getParent(); parent instanceof View; parent = parent.getParent()) {
                if (parent instanceof l.j0) {
                    editorInfo.hintText = ((l.j0) parent).a();
                    return;
                }
            }
        }
    }

    public static final void d(Context context, AdOverlayInfoParcel adOverlayInfoParcel, boolean z4) {
        C1.C c4;
        if (adOverlayInfoParcel.t == 4 && adOverlayInfoParcel.l == null) {
            InterfaceC0084a interfaceC0084a = adOverlayInfoParcel.k;
            if (interfaceC0084a != null) {
                interfaceC0084a.m();
            }
            ws wsVar = adOverlayInfoParcel.D;
            if (wsVar != null) {
                wsVar.c0();
            }
            Activity f = adOverlayInfoParcel.m.f();
            C1.i iVar = adOverlayInfoParcel.j;
            if (iVar != null && iVar.f372s && f != null) {
                context = f;
            }
            C0147a c0147a = z1.p.f6575A.f6576a;
            if (iVar != null) {
                c4 = iVar.f371r;
            } else {
                c4 = null;
            }
            C0147a.b(context, iVar, adOverlayInfoParcel.r, c4);
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(context, "com.google.android.gms.ads.AdActivity");
        intent.putExtra("com.google.android.gms.ads.internal.overlay.useClientJar", adOverlayInfoParcel.v.f847m);
        intent.putExtra("shouldCallOnOverlayOpened", z4);
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", adOverlayInfoParcel);
        intent.putExtra("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo", bundle);
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        D1.t0 t0Var = z1.p.f6575A.f6578c;
        D1.t0.p(context, intent);
    }

    public void a(P2.l lVar, float f, float f4) {
        throw null;
    }

    @Override // E1.e
    public boolean i(String str) {
        new E1.d(str).start();
        return true;
    }

    public Object k() {
        return new LinkedHashMap();
    }
}
