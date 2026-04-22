package u1;

import A1.L;
import A1.O0;
import A1.s1;
import E1.m;
import android.os.RemoteException;
import t1.AbstractC0806h;
import t1.C0803e;
import t1.C0813o;
import t1.C0814p;

/* renamed from: u1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0821a extends AbstractC0806h {
    public C0803e[] getAdSizes() {
        return this.f5800j.f72g;
    }

    public InterfaceC0823c getAppEventListener() {
        return this.f5800j.f73h;
    }

    public C0813o getVideoController() {
        return this.f5800j.f69c;
    }

    public C0814p getVideoOptions() {
        return this.f5800j.f75j;
    }

    public void setAdSizes(C0803e... c0803eArr) {
        if (c0803eArr != null && c0803eArr.length > 0) {
            this.f5800j.d(c0803eArr);
            return;
        }
        throw new IllegalArgumentException("The supported ad sizes must contain at least one valid ad size.");
    }

    public void setAppEventListener(InterfaceC0823c interfaceC0823c) {
        this.f5800j.e(interfaceC0823c);
    }

    public void setManualImpressionsEnabled(boolean z4) {
        O0 o02 = this.f5800j;
        o02.f79n = z4;
        try {
            L l2 = o02.f74i;
            if (l2 != null) {
                l2.p4(z4);
            }
        } catch (RemoteException e4) {
            m.i("#007 Could not call remote method.", e4);
        }
    }

    public void setVideoOptions(C0814p c0814p) {
        s1 s1Var;
        O0 o02 = this.f5800j;
        o02.f75j = c0814p;
        try {
            L l2 = o02.f74i;
            if (l2 != null) {
                if (c0814p == null) {
                    s1Var = null;
                } else {
                    s1Var = new s1(c0814p);
                }
                l2.v0(s1Var);
            }
        } catch (RemoteException e4) {
            m.i("#007 Could not call remote method.", e4);
        }
    }
}
