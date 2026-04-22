package D1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.vb;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class T {

    /* renamed from: a  reason: collision with root package name */
    public final HashMap f651a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    public final ArrayList f652b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    public final Context f653c;

    public T(Context context) {
        this.f653c = context;
    }

    public final void a() {
        SharedPreferences sharedPreferences;
        vb vbVar = Gb.i9;
        A1.r rVar = A1.r.f168d;
        if (!((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            return;
        }
        t0 t0Var = z1.p.f6575A.f6578c;
        HashMap H4 = t0.H((String) rVar.f171c.a(Gb.m9));
        for (String str : H4.keySet()) {
            synchronized (this) {
                try {
                    if (!this.f651a.containsKey(str)) {
                        if (Objects.equals(str, "__default__")) {
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.f653c);
                        } else {
                            sharedPreferences = this.f653c.getSharedPreferences(str, 0);
                        }
                        S s4 = new S(this, str);
                        this.f651a.put(str, s4);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(s4);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        b(new Q(H4));
    }

    public final synchronized void b(Q q4) {
        this.f652b.add(q4);
    }
}
