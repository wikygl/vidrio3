package p0;

import android.os.Bundle;
import androidx.lifecycle.f;
import androidx.lifecycle.i;
import androidx.lifecycle.k;
import androidx.lifecycle.l;
import androidx.savedstate.Recreator;
import androidx.savedstate.a;
import java.util.Map;
import n.C0739b;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public final c f5504a;

    /* renamed from: b  reason: collision with root package name */
    public final androidx.savedstate.a f5505b = new androidx.savedstate.a();

    /* renamed from: c  reason: collision with root package name */
    public boolean f5506c;

    public b(c cVar) {
        this.f5504a = cVar;
    }

    public final void a() {
        c cVar = this.f5504a;
        l r4 = cVar.r();
        if (r4.c == f.b.k) {
            r4.a(new Recreator(cVar));
            final androidx.savedstate.a aVar = this.f5505b;
            aVar.getClass();
            if (!aVar.b) {
                r4.a(new i() { // from class: p0.a
                    public final void c(k kVar, f.a aVar2) {
                        androidx.savedstate.a aVar3 = aVar;
                        h.e(aVar3, "this$0");
                        if (aVar2 == f.a.ON_START) {
                            aVar3.getClass();
                        } else if (aVar2 == f.a.ON_STOP) {
                            aVar3.getClass();
                        }
                    }
                });
                aVar.b = true;
                this.f5506c = true;
                return;
            }
            throw new IllegalStateException("SavedStateRegistry was already attached.".toString());
        }
        throw new IllegalStateException("Restarter must be created only during owner's initialization stage".toString());
    }

    public final void b(Bundle bundle) {
        boolean z4;
        Bundle bundle2;
        if (!this.f5506c) {
            a();
        }
        l r4 = this.f5504a.r();
        if (r4.c.compareTo(f.b.m) >= 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (!z4) {
            androidx.savedstate.a aVar = this.f5505b;
            if (aVar.b) {
                if (!aVar.d) {
                    if (bundle != null) {
                        bundle2 = bundle.getBundle("androidx.lifecycle.BundlableSavedStateRegistry.key");
                    } else {
                        bundle2 = null;
                    }
                    aVar.c = bundle2;
                    aVar.d = true;
                    return;
                }
                throw new IllegalStateException("SavedStateRegistry was already restored.".toString());
            }
            throw new IllegalStateException("You must call performAttach() before calling performRestore(Bundle).".toString());
        }
        throw new IllegalStateException(("performRestore cannot be called when owner is " + r4.c).toString());
    }

    public final void c(Bundle bundle) {
        h.e(bundle, "outBundle");
        androidx.savedstate.a aVar = this.f5505b;
        aVar.getClass();
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = aVar.c;
        if (bundle3 != null) {
            bundle2.putAll(bundle3);
        }
        C0739b c0739b = aVar.a;
        c0739b.getClass();
        C0739b.d dVar = new C0739b.d();
        c0739b.f5353l.put(dVar, Boolean.FALSE);
        while (dVar.hasNext()) {
            Map.Entry entry = (Map.Entry) dVar.next();
            bundle2.putBundle((String) entry.getKey(), ((a.b) entry.getValue()).a());
        }
        if (!bundle2.isEmpty()) {
            bundle.putBundle("androidx.lifecycle.BundlableSavedStateRegistry.key", bundle2);
        }
    }
}
