package U1;

import D1.E;
import U1.a.c;
import U1.d;
import V1.InterfaceC0297c;
import V1.InterfaceC0303i;
import W1.AbstractC0314b;
import W1.C0315c;
import W1.InterfaceC0320h;
import android.accounts.Account;
import android.content.Context;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a<O extends c> {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0026a f2374a;

    /* renamed from: b  reason: collision with root package name */
    public final String f2375b;

    /* renamed from: U1.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class AbstractC0026a<T extends e, O> extends d<T, O> {
        @Deprecated
        public T a(Context context, Looper looper, C0315c c0315c, O o4, d.a aVar, d.b bVar) {
            return b(context, looper, c0315c, o4, aVar, bVar);
        }

        public T b(Context context, Looper looper, C0315c c0315c, O o4, InterfaceC0297c interfaceC0297c, InterfaceC0303i interfaceC0303i) {
            throw new UnsupportedOperationException("buildClient must be implemented");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b<C> {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface c {

        /* renamed from: a  reason: collision with root package name */
        public static final C0028c f2376a = new Object();

        /* renamed from: U1.a$c$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
        public interface InterfaceC0027a extends c {
            Account a();
        }

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
        public interface b extends c {
            GoogleSignInAccount b();
        }

        /* renamed from: U1.a$c$c  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
        public static final class C0028c implements c {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class d<T, O> {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface e {
        boolean a();

        Set<Scope> b();

        void c(String str);

        void d(InterfaceC0320h interfaceC0320h, Set<Scope> set);

        boolean e();

        int f();

        boolean g();

        T1.d[] h();

        String i();

        void k(AbstractC0314b.c cVar);

        String l();

        void m(E e4);

        void n();

        boolean o();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class f<C extends e> extends b<C> {
    }

    public <C extends e> a(String str, AbstractC0026a<C, O> abstractC0026a, f<C> fVar) {
        this.f2375b = str;
        this.f2374a = abstractC0026a;
    }
}
