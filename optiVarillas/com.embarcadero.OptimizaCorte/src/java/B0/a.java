package B0;

import B0.t;
import android.os.Build;
import java.util.Arrays;
import java.util.HashSet;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class a implements p {

    /* renamed from: c  reason: collision with root package name */
    public static final HashSet f283c = new HashSet();

    /* renamed from: a  reason: collision with root package name */
    public final String f284a;

    /* renamed from: b  reason: collision with root package name */
    public final String f285b;

    /* renamed from: B0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class C0001a {

        /* renamed from: a  reason: collision with root package name */
        public static final HashSet f286a = new HashSet(Arrays.asList(t.a.f292a.b()));
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 23) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 24) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class d extends a {
        @Override // B0.a
        public final boolean c() {
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class e extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 26) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class f extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 27) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class g extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 28) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class h extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 29) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class i extends a {
        @Override // B0.a
        public final boolean c() {
            if (Build.VERSION.SDK_INT >= 33) {
                return true;
            }
            return false;
        }
    }

    public a(String str, String str2) {
        this.f284a = str;
        this.f285b = str2;
        f283c.add(this);
    }

    @Override // B0.p
    public final String a() {
        return this.f284a;
    }

    @Override // B0.p
    public final boolean b() {
        if (!c() && !d()) {
            return false;
        }
        return true;
    }

    public abstract boolean c();

    public boolean d() {
        HashSet hashSet = C0001a.f286a;
        String str = this.f285b;
        if (!hashSet.contains(str)) {
            String str2 = Build.TYPE;
            if ("eng".equals(str2) || "userdebug".equals(str2)) {
                if (hashSet.contains(str + ":dev")) {
                }
            }
            return false;
        }
        return true;
    }
}
