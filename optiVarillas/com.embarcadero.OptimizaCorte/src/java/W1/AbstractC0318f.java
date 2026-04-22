package W1;

import U1.a;
import android.accounts.Account;
import android.os.IInterface;
import com.google.android.gms.common.api.Scope;
import java.util.Collections;
import java.util.Set;

/* renamed from: W1.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class AbstractC0318f<T extends IInterface> extends AbstractC0314b<T> implements a.e {

    /* renamed from: y  reason: collision with root package name */
    public final Set f2739y;

    /* renamed from: z  reason: collision with root package name */
    public final Account f2740z;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AbstractC0318f(android.content.Context r10, android.os.Looper r11, int r12, W1.C0315c r13, V1.InterfaceC0297c r14, V1.InterfaceC0303i r15) {
        /*
            r9 = this;
            W1.Y r3 = W1.AbstractC0319g.a(r10)
            T1.e r4 = T1.e.f2352d
            W1.C0324l.d(r14)
            W1.C0324l.d(r15)
            W1.w r6 = new W1.w
            r6.<init>(r14)
            W1.x r7 = new W1.x
            r7.<init>(r15)
            java.lang.String r8 = r13.f
            r0 = r9
            r1 = r10
            r2 = r11
            r5 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            android.accounts.Account r10 = r13.f2706a
            r9.f2740z = r10
            java.util.Set r10 = r13.f2708c
            java.util.Iterator r11 = r10.iterator()
        L29:
            boolean r12 = r11.hasNext()
            if (r12 == 0) goto L44
            java.lang.Object r12 = r11.next()
            com.google.android.gms.common.api.Scope r12 = (com.google.android.gms.common.api.Scope) r12
            boolean r12 = r10.contains(r12)
            if (r12 == 0) goto L3c
            goto L29
        L3c:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r11 = "Expanding scopes is not permitted, use implied scopes instead"
            r10.<init>(r11)
            throw r10
        L44:
            r9.f2739y = r10
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: W1.AbstractC0318f.<init>(android.content.Context, android.os.Looper, int, W1.c, V1.c, V1.i):void");
    }

    @Override // U1.a.e
    public final Set<Scope> b() {
        if (o()) {
            return this.f2739y;
        }
        return Collections.emptySet();
    }

    @Override // W1.AbstractC0314b
    public final Account s() {
        return this.f2740z;
    }

    @Override // W1.AbstractC0314b
    public final Set<Scope> v() {
        return this.f2739y;
    }
}
