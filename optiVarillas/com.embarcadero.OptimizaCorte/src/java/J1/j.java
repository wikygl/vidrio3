package j1;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j implements InterfaceC0667e {

    /* renamed from: a  reason: collision with root package name */
    public final a f4756a;

    /* renamed from: b  reason: collision with root package name */
    public final i f4757b;

    /* renamed from: c  reason: collision with root package name */
    public final HashMap f4758c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public final Context f4759a;

        /* renamed from: b  reason: collision with root package name */
        public Map<String, String> f4760b = null;

        public a(Context context) {
            this.f4759a = context;
        }

        /* JADX WARN: Removed duplicated region for block: B:16:0x003a  */
        /* JADX WARN: Removed duplicated region for block: B:17:0x0044  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final j1.InterfaceC0666d a(java.lang.String r14) {
            /*
                Method dump skipped, instructions count: 269
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: j1.j.a.a(java.lang.String):j1.d");
        }
    }

    public j(Context context, i iVar) {
        a aVar = new a(context);
        this.f4758c = new HashMap();
        this.f4756a = aVar;
        this.f4757b = iVar;
    }

    @Override // j1.InterfaceC0667e
    public final synchronized k a(String str) {
        if (this.f4758c.containsKey(str)) {
            return (k) this.f4758c.get(str);
        }
        InterfaceC0666d a4 = this.f4756a.a(str);
        if (a4 == null) {
            return null;
        }
        i iVar = this.f4757b;
        k create = a4.create(new C0665c(iVar.f4753a, iVar.f4754b, iVar.f4755c, str));
        this.f4758c.put(str, create);
        return create;
    }
}
