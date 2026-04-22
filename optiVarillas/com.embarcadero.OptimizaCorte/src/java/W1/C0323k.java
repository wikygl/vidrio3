package W1;

import java.util.ArrayList;

/* renamed from: W1.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0323k {

    /* renamed from: W1.k$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final ArrayList f2754a;

        /* renamed from: b  reason: collision with root package name */
        public final Object f2755b;

        public /* synthetic */ a(Object obj) {
            C0324l.d(obj);
            this.f2755b = obj;
            this.f2754a = new ArrayList();
        }

        public final void a(Object obj, String str) {
            this.f2754a.add(X1.b.e(str, "=", String.valueOf(obj)));
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append(this.f2755b.getClass().getSimpleName());
            sb.append('{');
            ArrayList arrayList = this.f2754a;
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                sb.append((String) arrayList.get(i4));
                if (i4 < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append('}');
            return sb.toString();
        }
    }

    public static boolean a(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj != null && obj.equals(obj2)) {
            return true;
        }
        return false;
    }
}
