package c0;

import G3.g;
import androidx.lifecycle.C;
import androidx.lifecycle.E;
import androidx.lifecycle.G;
import androidx.lifecycle.k;
import androidx.lifecycle.p;
import b0.C0351c;
import java.io.PrintWriter;

/* renamed from: c0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0367a extends g {

    /* renamed from: k  reason: collision with root package name */
    public final k f2930k;

    /* renamed from: l  reason: collision with root package name */
    public final b f2931l;

    /* renamed from: c0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class C0040a<D> extends p<D> {
        public final void e() {
            throw null;
        }

        public final void f() {
            throw null;
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("LoaderInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" #0 : ");
            sb.append("null");
            sb.append("}}");
            return sb.toString();
        }
    }

    /* renamed from: c0.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends C {

        /* renamed from: d  reason: collision with root package name */
        public static final C0041a f2932d = new Object();

        /* renamed from: c  reason: collision with root package name */
        public final r.k<C0040a> f2933c = new r.k<>();

        /* renamed from: c0.a$b$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
        public static class C0041a implements E.a {
            public final <T extends C> T a(Class<T> cls) {
                return new b();
            }

            public final C b(Class cls, C0351c c0351c) {
                return a(cls);
            }
        }

        public final void a() {
            r.k<C0040a> kVar = this.f2933c;
            int i4 = kVar.f5689l;
            if (i4 <= 0) {
                Object[] objArr = kVar.f5688k;
                for (int i5 = 0; i5 < i4; i5++) {
                    objArr[i5] = null;
                }
                kVar.f5689l = 0;
                return;
            }
            ((C0040a) kVar.f5688k[0]).getClass();
            throw null;
        }
    }

    public C0367a(k kVar, G g4) {
        this.f2930k = kVar;
        E e4 = new E(g4, b.f2932d);
        String canonicalName = b.class.getCanonicalName();
        if (canonicalName != null) {
            this.f2931l = (b) e4.a(b.class, "androidx.lifecycle.ViewModelProvider.DefaultKey:".concat(canonicalName));
            return;
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }

    @Deprecated
    public final void F(String str, PrintWriter printWriter) {
        r.k<C0040a> kVar = this.f2931l.f2933c;
        if (kVar.f5689l > 0) {
            printWriter.print(str);
            printWriter.println("Loaders:");
            String str2 = str + "    ";
            if (kVar.f5689l > 0) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(kVar.f5687j[0]);
                printWriter.print(": ");
                printWriter.println(((C0040a) kVar.f5688k[0]).toString());
                printWriter.print(str2);
                printWriter.print("mId=");
                printWriter.print(0);
                printWriter.print(" mArgs=");
                printWriter.println((Object) null);
                printWriter.print(str2);
                printWriter.print("mLoader=");
                printWriter.println((Object) null);
                throw null;
            }
        }
    }

    public final String toString() {
        int lastIndexOf;
        StringBuilder sb = new StringBuilder(128);
        sb.append("LoaderManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        k kVar = this.f2930k;
        if (kVar == null) {
            sb.append("null");
        } else {
            String simpleName = kVar.getClass().getSimpleName();
            if (simpleName.length() <= 0 && (lastIndexOf = (simpleName = kVar.getClass().getName()).lastIndexOf(46)) > 0) {
                simpleName = simpleName.substring(lastIndexOf + 1);
            }
            sb.append(simpleName);
            sb.append('{');
            sb.append(Integer.toHexString(System.identityHashCode(kVar)));
        }
        sb.append("}}");
        return sb.toString();
    }
}
