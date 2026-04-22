package W1;

import android.accounts.Account;
import android.view.View;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import n2.C0744a;
import r.C0775d;

/* renamed from: W1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0315c {

    /* renamed from: a  reason: collision with root package name */
    public final Account f2706a;

    /* renamed from: b  reason: collision with root package name */
    public final Set f2707b;

    /* renamed from: c  reason: collision with root package name */
    public final Set f2708c;

    /* renamed from: d  reason: collision with root package name */
    public final View f2709d;

    /* renamed from: e  reason: collision with root package name */
    public final String f2710e;
    public final String f;

    /* renamed from: g  reason: collision with root package name */
    public final C0744a f2711g;

    /* renamed from: h  reason: collision with root package name */
    public Integer f2712h;

    /* renamed from: W1.c$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public Account f2713a;

        /* renamed from: b  reason: collision with root package name */
        public C0775d f2714b;

        /* renamed from: c  reason: collision with root package name */
        public String f2715c;

        /* renamed from: d  reason: collision with root package name */
        public String f2716d;
    }

    public C0315c(Account account, Set set, String str, String str2) {
        Set unmodifiableSet;
        C0744a c0744a = C0744a.f5378b;
        this.f2706a = account;
        if (set == null) {
            unmodifiableSet = Collections.emptySet();
        } else {
            unmodifiableSet = Collections.unmodifiableSet(set);
        }
        this.f2707b = unmodifiableSet;
        Map emptyMap = Collections.emptyMap();
        this.f2710e = str;
        this.f = str2;
        this.f2711g = c0744a;
        HashSet hashSet = new HashSet(unmodifiableSet);
        for (r rVar : emptyMap.values()) {
            rVar.getClass();
            hashSet.addAll(null);
        }
        this.f2708c = Collections.unmodifiableSet(hashSet);
    }
}
