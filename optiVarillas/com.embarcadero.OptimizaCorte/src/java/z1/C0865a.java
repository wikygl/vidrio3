package z1;

import D1.t0;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Cj;
import com.google.android.gms.internal.ads.ii;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* renamed from: z1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0865a {

    /* renamed from: a  reason: collision with root package name */
    public final Context f6525a;

    /* renamed from: b  reason: collision with root package name */
    public boolean f6526b;

    /* renamed from: c  reason: collision with root package name */
    public final Cj f6527c;

    /* renamed from: d  reason: collision with root package name */
    public final ii f6528d = new ii(Collections.emptyList(), false);

    public C0865a(Context context, Cj cj) {
        this.f6525a = context;
        this.f6527c = cj;
    }

    public final void a(String str) {
        List<String> list;
        ii iiVar = this.f6528d;
        Cj cj = this.f6527c;
        if ((cj != null && cj.a().o) || iiVar.j) {
            if (str == null) {
                str = "";
            }
            if (cj != null) {
                cj.c(str, (Map) null, 3);
            } else if (iiVar.j && (list = iiVar.k) != null) {
                for (String str2 : list) {
                    if (!TextUtils.isEmpty(str2)) {
                        String replace = str2.replace("{NAVIGATION_URL}", Uri.encode(str));
                        t0 t0Var = p.f6575A.f6578c;
                        t0.j(this.f6525a, "", replace);
                    }
                }
            }
        }
    }

    public final boolean b() {
        Cj cj = this.f6527c;
        if (((cj != null && cj.a().o) || this.f6528d.j) && !this.f6526b) {
            return false;
        }
        return true;
    }
}
