package N0;

import a3.InterfaceFutureC0346a;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a<V> implements InterfaceFutureC0346a<V> {

    /* renamed from: m  reason: collision with root package name */
    public static final boolean f1760m = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));

    /* renamed from: n  reason: collision with root package name */
    public static final Logger f1761n = Logger.getLogger(a.class.getName());

    /* renamed from: o  reason: collision with root package name */
    public static final AbstractC0017a f1762o;

    /* renamed from: p  reason: collision with root package name */
    public static final Object f1763p;

    /* renamed from: j  reason: collision with root package name */
    public volatile Object f1764j;

    /* renamed from: k  reason: collision with root package name */
    public volatile d f1765k;

    /* renamed from: l  reason: collision with root package name */
    public volatile h f1766l;

    /* renamed from: N0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class AbstractC0017a {
        public abstract boolean a(a<?> aVar, d dVar, d dVar2);

        public abstract boolean b(a<?> aVar, Object obj, Object obj2);

        public abstract boolean c(a<?> aVar, h hVar, h hVar2);

        public abstract void d(h hVar, h hVar2);

        public abstract void e(h hVar, Thread thread);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class b {

        /* renamed from: c  reason: collision with root package name */
        public static final b f1767c;

        /* renamed from: d  reason: collision with root package name */
        public static final b f1768d;

        /* renamed from: a  reason: collision with root package name */
        public final boolean f1769a;

        /* renamed from: b  reason: collision with root package name */
        public final Throwable f1770b;

        static {
            if (a.f1760m) {
                f1768d = null;
                f1767c = null;
                return;
            }
            f1768d = new b(false, null);
            f1767c = new b(true, null);
        }

        public b(boolean z4, CancellationException cancellationException) {
            this.f1769a = z4;
            this.f1770b = cancellationException;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class c {

        /* renamed from: b  reason: collision with root package name */
        public static final c f1771b = new c(new Throwable("Failure occurred while trying to finish a future."));

        /* renamed from: a  reason: collision with root package name */
        public final Throwable f1772a;

        /* renamed from: N0.a$c$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
        public class C0018a extends Throwable {
            @Override // java.lang.Throwable
            public final synchronized Throwable fillInStackTrace() {
                return this;
            }
        }

        public c(Throwable th) {
            boolean z4 = a.f1760m;
            th.getClass();
            this.f1772a = th;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class d {

        /* renamed from: d  reason: collision with root package name */
        public static final d f1773d = new d(null, null);

        /* renamed from: a  reason: collision with root package name */
        public final Runnable f1774a;

        /* renamed from: b  reason: collision with root package name */
        public final Executor f1775b;

        /* renamed from: c  reason: collision with root package name */
        public d f1776c;

        public d(Runnable runnable, Executor executor) {
            this.f1774a = runnable;
            this.f1775b = executor;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class e extends AbstractC0017a {

        /* renamed from: a  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<h, Thread> f1777a;

        /* renamed from: b  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<h, h> f1778b;

        /* renamed from: c  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<a, h> f1779c;

        /* renamed from: d  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<a, d> f1780d;

        /* renamed from: e  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<a, Object> f1781e;

        public e(AtomicReferenceFieldUpdater<h, Thread> atomicReferenceFieldUpdater, AtomicReferenceFieldUpdater<h, h> atomicReferenceFieldUpdater2, AtomicReferenceFieldUpdater<a, h> atomicReferenceFieldUpdater3, AtomicReferenceFieldUpdater<a, d> atomicReferenceFieldUpdater4, AtomicReferenceFieldUpdater<a, Object> atomicReferenceFieldUpdater5) {
            this.f1777a = atomicReferenceFieldUpdater;
            this.f1778b = atomicReferenceFieldUpdater2;
            this.f1779c = atomicReferenceFieldUpdater3;
            this.f1780d = atomicReferenceFieldUpdater4;
            this.f1781e = atomicReferenceFieldUpdater5;
        }

        @Override // N0.a.AbstractC0017a
        public final boolean a(a<?> aVar, d dVar, d dVar2) {
            AtomicReferenceFieldUpdater<a, d> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f1780d;
                if (atomicReferenceFieldUpdater.compareAndSet(aVar, dVar, dVar2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(aVar) == dVar);
            return false;
        }

        @Override // N0.a.AbstractC0017a
        public final boolean b(a<?> aVar, Object obj, Object obj2) {
            AtomicReferenceFieldUpdater<a, Object> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f1781e;
                if (atomicReferenceFieldUpdater.compareAndSet(aVar, obj, obj2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(aVar) == obj);
            return false;
        }

        @Override // N0.a.AbstractC0017a
        public final boolean c(a<?> aVar, h hVar, h hVar2) {
            AtomicReferenceFieldUpdater<a, h> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f1779c;
                if (atomicReferenceFieldUpdater.compareAndSet(aVar, hVar, hVar2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(aVar) == hVar);
            return false;
        }

        @Override // N0.a.AbstractC0017a
        public final void d(h hVar, h hVar2) {
            this.f1778b.lazySet(hVar, hVar2);
        }

        @Override // N0.a.AbstractC0017a
        public final void e(h hVar, Thread thread) {
            this.f1777a.lazySet(hVar, thread);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class f<V> implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final a<V> f1782j;

        /* renamed from: k  reason: collision with root package name */
        public final InterfaceFutureC0346a<? extends V> f1783k;

        public f(a<V> aVar, InterfaceFutureC0346a<? extends V> interfaceFutureC0346a) {
            this.f1782j = aVar;
            this.f1783k = interfaceFutureC0346a;
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (this.f1782j.f1764j != this) {
                return;
            }
            if (a.f1762o.b(this.f1782j, this, a.f(this.f1783k))) {
                a.c(this.f1782j);
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class g extends AbstractC0017a {
        @Override // N0.a.AbstractC0017a
        public final boolean a(a<?> aVar, d dVar, d dVar2) {
            synchronized (aVar) {
                try {
                    if (aVar.f1765k == dVar) {
                        aVar.f1765k = dVar2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // N0.a.AbstractC0017a
        public final boolean b(a<?> aVar, Object obj, Object obj2) {
            synchronized (aVar) {
                try {
                    if (aVar.f1764j == obj) {
                        aVar.f1764j = obj2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // N0.a.AbstractC0017a
        public final boolean c(a<?> aVar, h hVar, h hVar2) {
            synchronized (aVar) {
                try {
                    if (aVar.f1766l == hVar) {
                        aVar.f1766l = hVar2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // N0.a.AbstractC0017a
        public final void d(h hVar, h hVar2) {
            hVar.f1786b = hVar2;
        }

        @Override // N0.a.AbstractC0017a
        public final void e(h hVar, Thread thread) {
            hVar.f1785a = thread;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class h {

        /* renamed from: c  reason: collision with root package name */
        public static final h f1784c = new Object();

        /* renamed from: a  reason: collision with root package name */
        public volatile Thread f1785a;

        /* renamed from: b  reason: collision with root package name */
        public volatile h f1786b;

        public h() {
            a.f1762o.e(this, Thread.currentThread());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [N0.a$a] */
    /* JADX WARN: Type inference failed for: r2v7 */
    static {
        e eVar;
        try {
            th = null;
            eVar = new e(AtomicReferenceFieldUpdater.newUpdater(h.class, Thread.class, "a"), AtomicReferenceFieldUpdater.newUpdater(h.class, h.class, "b"), AtomicReferenceFieldUpdater.newUpdater(a.class, h.class, "l"), AtomicReferenceFieldUpdater.newUpdater(a.class, d.class, "k"), AtomicReferenceFieldUpdater.newUpdater(a.class, Object.class, "j"));
        } catch (Throwable th) {
            th = th;
            eVar = new Object();
        }
        f1762o = eVar;
        if (th != null) {
            f1761n.log(Level.SEVERE, "SafeAtomicHelper is broken!", th);
        }
        f1763p = new Object();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v2, types: [N0.a$a] */
    /* JADX WARN: Type inference failed for: r5v0, types: [N0.a<?>] */
    /* JADX WARN: Type inference failed for: r5v1, types: [N0.a] */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v7, types: [N0.a, N0.a<V>] */
    public static void c(a<?> aVar) {
        d dVar;
        d dVar2;
        d dVar3 = null;
        while (true) {
            h hVar = ((a) aVar).f1766l;
            if (f1762o.c((a) aVar, hVar, h.f1784c)) {
                while (hVar != null) {
                    Thread thread = hVar.f1785a;
                    if (thread != null) {
                        hVar.f1785a = null;
                        LockSupport.unpark(thread);
                    }
                    hVar = hVar.f1786b;
                }
                do {
                    dVar = ((a) aVar).f1765k;
                } while (!f1762o.a((a) aVar, dVar, d.f1773d));
                while (true) {
                    dVar2 = dVar3;
                    dVar3 = dVar;
                    if (dVar3 == null) {
                        break;
                    }
                    dVar = dVar3.f1776c;
                    dVar3.f1776c = dVar2;
                }
                while (dVar2 != null) {
                    dVar3 = dVar2.f1776c;
                    Runnable runnable = dVar2.f1774a;
                    if (runnable instanceof f) {
                        f fVar = (f) runnable;
                        aVar = fVar.f1782j;
                        if (aVar.f1764j == fVar) {
                            if (f1762o.b(aVar, fVar, f(fVar.f1783k))) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        d(runnable, dVar2.f1775b);
                    }
                    dVar2 = dVar3;
                }
                return;
            }
        }
    }

    public static void d(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException e4) {
            Level level = Level.SEVERE;
            f1761n.log(level, "RuntimeException while executing runnable " + runnable + " with executor " + executor, (Throwable) e4);
        }
    }

    public static Object e(Object obj) {
        if (!(obj instanceof b)) {
            if (!(obj instanceof c)) {
                if (obj == f1763p) {
                    return null;
                }
                return obj;
            }
            throw new ExecutionException(((c) obj).f1772a);
        }
        Throwable th = ((b) obj).f1770b;
        CancellationException cancellationException = new CancellationException("Task was cancelled.");
        cancellationException.initCause(th);
        throw cancellationException;
    }

    public static Object f(InterfaceFutureC0346a<?> interfaceFutureC0346a) {
        if (interfaceFutureC0346a instanceof a) {
            Object obj = ((a) interfaceFutureC0346a).f1764j;
            if (obj instanceof b) {
                b bVar = (b) obj;
                if (bVar.f1769a) {
                    if (bVar.f1770b != null) {
                        return new b(false, (CancellationException) bVar.f1770b);
                    }
                    return b.f1768d;
                }
                return obj;
            }
            return obj;
        }
        boolean isCancelled = interfaceFutureC0346a.isCancelled();
        if ((!f1760m) & isCancelled) {
            return b.f1768d;
        }
        try {
            Object g4 = g(interfaceFutureC0346a);
            if (g4 == null) {
                return f1763p;
            }
            return g4;
        } catch (CancellationException e4) {
            if (!isCancelled) {
                return new c(new IllegalArgumentException("get() threw CancellationException, despite reporting isCancelled() == false: " + interfaceFutureC0346a, e4));
            }
            return new b(false, e4);
        } catch (ExecutionException e5) {
            return new c(e5.getCause());
        } catch (Throwable th) {
            return new c(th);
        }
    }

    public static <V> V g(Future<V> future) {
        V v4;
        boolean z4 = false;
        while (true) {
            try {
                v4 = future.get();
                break;
            } catch (InterruptedException unused) {
                z4 = true;
            } catch (Throwable th) {
                if (z4) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (z4) {
            Thread.currentThread().interrupt();
        }
        return v4;
    }

    @Override // a3.InterfaceFutureC0346a
    public final void a(Runnable runnable, Executor executor) {
        executor.getClass();
        d dVar = this.f1765k;
        d dVar2 = d.f1773d;
        if (dVar != dVar2) {
            d dVar3 = new d(runnable, executor);
            do {
                dVar3.f1776c = dVar;
                if (f1762o.a(this, dVar, dVar3)) {
                    return;
                }
                dVar = this.f1765k;
            } while (dVar != dVar2);
            d(runnable, executor);
        }
        d(runnable, executor);
    }

    public final void b(StringBuilder sb) {
        String valueOf;
        try {
            Object g4 = g(this);
            sb.append("SUCCESS, result=[");
            if (g4 == this) {
                valueOf = "this future";
            } else {
                valueOf = String.valueOf(g4);
            }
            sb.append(valueOf);
            sb.append("]");
        } catch (CancellationException unused) {
            sb.append("CANCELLED");
        } catch (RuntimeException e4) {
            sb.append("UNKNOWN, cause=[");
            sb.append(e4.getClass());
            sb.append(" thrown from get()]");
        } catch (ExecutionException e5) {
            sb.append("FAILURE, cause=[");
            sb.append(e5.getCause());
            sb.append("]");
        }
    }

    @Override // java.util.concurrent.Future
    public final boolean cancel(boolean z4) {
        boolean z5;
        b bVar;
        boolean z6;
        Object obj = this.f1764j;
        if (obj == null) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5 | (obj instanceof f)) {
            if (f1760m) {
                bVar = new b(z4, new CancellationException("Future.cancel() was called."));
            } else if (z4) {
                bVar = b.f1767c;
            } else {
                bVar = b.f1768d;
            }
            boolean z7 = false;
            a<V> aVar = this;
            while (true) {
                if (f1762o.b(aVar, obj, bVar)) {
                    c(aVar);
                    if (!(obj instanceof f)) {
                        return true;
                    }
                    InterfaceFutureC0346a<? extends V> interfaceFutureC0346a = ((f) obj).f1783k;
                    if (interfaceFutureC0346a instanceof a) {
                        aVar = (a) interfaceFutureC0346a;
                        obj = aVar.f1764j;
                        if (obj == null) {
                            z6 = true;
                        } else {
                            z6 = false;
                        }
                        if (!(z6 | (obj instanceof f))) {
                            return true;
                        }
                        z7 = true;
                    } else {
                        interfaceFutureC0346a.cancel(z4);
                        return true;
                    }
                } else {
                    obj = aVar.f1764j;
                    if (!(obj instanceof f)) {
                        return z7;
                    }
                }
            }
        } else {
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00b5  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:56:0x00a8 -> B:36:0x0074). Please submit an issue!!! */
    @Override // java.util.concurrent.Future
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final V get(long r20, java.util.concurrent.TimeUnit r22) {
        /*
            Method dump skipped, instructions count: 365
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: N0.a.get(long, java.util.concurrent.TimeUnit):java.lang.Object");
    }

    public final String h() {
        String valueOf;
        Object obj = this.f1764j;
        if (obj instanceof f) {
            StringBuilder sb = new StringBuilder("setFuture=[");
            InterfaceFutureC0346a<? extends V> interfaceFutureC0346a = ((f) obj).f1783k;
            if (interfaceFutureC0346a == this) {
                valueOf = "this future";
            } else {
                valueOf = String.valueOf(interfaceFutureC0346a);
            }
            return C.b.c(sb, valueOf, "]");
        } else if (this instanceof ScheduledFuture) {
            return "remaining delay=[" + ((ScheduledFuture) this).getDelay(TimeUnit.MILLISECONDS) + " ms]";
        } else {
            return null;
        }
    }

    public final void i(h hVar) {
        hVar.f1785a = null;
        while (true) {
            h hVar2 = this.f1766l;
            if (hVar2 == h.f1784c) {
                return;
            }
            h hVar3 = null;
            while (hVar2 != null) {
                h hVar4 = hVar2.f1786b;
                if (hVar2.f1785a != null) {
                    hVar3 = hVar2;
                } else if (hVar3 != null) {
                    hVar3.f1786b = hVar4;
                    if (hVar3.f1785a == null) {
                        break;
                    }
                } else if (!f1762o.c(this, hVar2, hVar4)) {
                    break;
                }
                hVar2 = hVar4;
            }
            return;
        }
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return this.f1764j instanceof b;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        boolean z4;
        Object obj = this.f1764j;
        if (obj != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        return (!(obj instanceof f)) & z4;
    }

    public final String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("[status=");
        if (this.f1764j instanceof b) {
            sb.append("CANCELLED");
        } else if (isDone()) {
            b(sb);
        } else {
            try {
                str = h();
            } catch (RuntimeException e4) {
                str = "Exception thrown from implementation: " + e4.getClass();
            }
            if (str != null && !str.isEmpty()) {
                sb.append("PENDING, info=[");
                sb.append(str);
                sb.append("]");
            } else if (isDone()) {
                b(sb);
            } else {
                sb.append("PENDING");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // java.util.concurrent.Future
    public final V get() {
        Object obj;
        if (!Thread.interrupted()) {
            Object obj2 = this.f1764j;
            if ((obj2 != null) & (!(obj2 instanceof f))) {
                return (V) e(obj2);
            }
            h hVar = this.f1766l;
            h hVar2 = h.f1784c;
            if (hVar != hVar2) {
                h hVar3 = new h();
                do {
                    AbstractC0017a abstractC0017a = f1762o;
                    abstractC0017a.d(hVar3, hVar);
                    if (abstractC0017a.c(this, hVar, hVar3)) {
                        do {
                            LockSupport.park(this);
                            if (!Thread.interrupted()) {
                                obj = this.f1764j;
                            } else {
                                i(hVar3);
                                throw new InterruptedException();
                            }
                        } while (!((obj != null) & (!(obj instanceof f))));
                        return (V) e(obj);
                    }
                    hVar = this.f1766l;
                } while (hVar != hVar2);
                return (V) e(this.f1764j);
            }
            return (V) e(this.f1764j);
        }
        throw new InterruptedException();
    }
}
