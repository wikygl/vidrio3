package t1;

import A1.A0;
import A1.G1;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

/* renamed from: t1.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0811m {

    /* renamed from: a  reason: collision with root package name */
    public final A0 f5805a;

    /* renamed from: b  reason: collision with root package name */
    public final ArrayList f5806b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    public final C0805g f5807c;

    public C0811m(A0 a02) {
        C0805g c0805g;
        this.f5805a = a02;
        if (a02 != null) {
            try {
                List<G1> j4 = a02.j();
                if (j4 != null) {
                    for (G1 g12 : j4) {
                        if (g12 != null) {
                            c0805g = new C0805g(g12);
                        } else {
                            c0805g = null;
                        }
                        if (c0805g != null) {
                            this.f5806b.add(c0805g);
                        }
                    }
                }
            } catch (RemoteException e4) {
                E1.m.e("Could not forward getAdapterResponseInfo to ResponseInfo.", e4);
            }
        }
        A0 a03 = this.f5805a;
        if (a03 != null) {
            try {
                G1 d4 = a03.d();
                if (d4 != null) {
                    this.f5807c = new C0805g(d4);
                }
            } catch (RemoteException e5) {
                E1.m.e("Could not forward getLoadedAdapterResponse to ResponseInfo.", e5);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001c  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0020  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0038  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x004c A[LOOP:0: B:23:0x0046->B:25:0x004c, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0080  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x006e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0025 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final org.json.JSONObject a() {
        /*
            r6 = this;
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            A1.A0 r1 = r6.f5805a
            r2 = 0
            if (r1 == 0) goto L15
            java.lang.String r3 = r1.f()     // Catch: android.os.RemoteException -> Lf
            goto L16
        Lf:
            r3 = move-exception
            java.lang.String r4 = "Could not forward getResponseId to ResponseInfo."
            E1.m.e(r4, r3)
        L15:
            r3 = r2
        L16:
            java.lang.String r4 = "null"
            java.lang.String r5 = "Response ID"
            if (r3 != 0) goto L20
            r0.put(r5, r4)
            goto L23
        L20:
            r0.put(r5, r3)
        L23:
            if (r1 == 0) goto L30
            java.lang.String r2 = r1.h()     // Catch: android.os.RemoteException -> L2a
            goto L30
        L2a:
            r3 = move-exception
            java.lang.String r5 = "Could not forward getMediationAdapterClassName to ResponseInfo."
            E1.m.e(r5, r3)
        L30:
            java.lang.String r3 = "Mediation Adapter Class Name"
            if (r2 != 0) goto L38
            r0.put(r3, r4)
            goto L3b
        L38:
            r0.put(r3, r2)
        L3b:
            org.json.JSONArray r2 = new org.json.JSONArray
            r2.<init>()
            java.util.ArrayList r3 = r6.f5806b
            java.util.Iterator r3 = r3.iterator()
        L46:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L5a
            java.lang.Object r4 = r3.next()
            t1.g r4 = (t1.C0805g) r4
            org.json.JSONObject r4 = r4.a()
            r2.put(r4)
            goto L46
        L5a:
            java.lang.String r3 = "Adapter Responses"
            r0.put(r3, r2)
            t1.g r2 = r6.f5807c
            if (r2 == 0) goto L6c
            org.json.JSONObject r2 = r2.a()
            java.lang.String r3 = "Loaded Adapter Response"
            r0.put(r3, r2)
        L6c:
            if (r1 == 0) goto L79
            android.os.Bundle r1 = r1.b()     // Catch: android.os.RemoteException -> L73
            goto L7e
        L73:
            r1 = move-exception
            java.lang.String r2 = "Could not forward getResponseExtras to ResponseInfo."
            E1.m.e(r2, r1)
        L79:
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
        L7e:
            if (r1 == 0) goto L8d
            A1.p r2 = A1.C0124p.f
            E1.f r2 = r2.f161a
            org.json.JSONObject r1 = r2.g(r1)
            java.lang.String r2 = "Response Extras"
            r0.put(r2, r1)
        L8d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: t1.C0811m.a():org.json.JSONObject");
    }

    public final String toString() {
        try {
            return a().toString(2);
        } catch (JSONException unused) {
            return "Error forming toString output.";
        }
    }
}
