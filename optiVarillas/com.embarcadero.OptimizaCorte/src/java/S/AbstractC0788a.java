package s;

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

/* renamed from: s.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class AbstractC0788a<V> implements InterfaceFutureC0346a<V> {

    /* renamed from: m  reason: collision with root package name */
    public static final boolean f5726m = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));

    /* renamed from: n  reason: collision with root package name */
    public static final Logger f5727n = Logger.getLogger(AbstractC0788a.class.getName());

    /* renamed from: o  reason: collision with root package name */
    public static final AbstractC0067a f5728o;

    /* renamed from: p  reason: collision with root package name */
    public static final Object f5729p;

    /* renamed from: j  reason: collision with root package name */
    public volatile Object f5730j;

    /* renamed from: k  reason: collision with root package name */
    public volatile d f5731k;

    /* renamed from: l  reason: collision with root package name */
    public volatile h f5732l;

    /* renamed from: s.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class AbstractC0067a {
        public abstract boolean a(AbstractC0788a<?> abstractC0788a, d dVar, d dVar2);

        public abstract boolean b(AbstractC0788a<?> abstractC0788a, Object obj, Object obj2);

        public abstract boolean c(AbstractC0788a<?> abstractC0788a, h hVar, h hVar2);

        public abstract void d(h hVar, h hVar2);

        public abstract void e(h hVar, Thread thread);
    }

    /* renamed from: s.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class b {

        /* renamed from: b  reason: collision with root package name */
        public static final b f5733b;

        /* renamed from: c  reason: collision with root package name */
        public static final b f5734c;

        /* renamed from: a  reason: collision with root package name */
        public final Throwable f5735a;

        static {
            if (AbstractC0788a.f5726m) {
                f5734c = null;
                f5733b = null;
                return;
            }
            f5734c = new b(false, null);
            f5733b = new b(true, null);
        }

        public b(boolean z4, CancellationException cancellationException) {
            this.f5735a = cancellationException;
        }
    }

    /* renamed from: s.a$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class c {

        /* renamed from: a  reason: collision with root package name */
        public final Throwable f5736a;

        /* renamed from: s.a$c$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public class C0068a extends Throwable {
            @Override // java.lang.Throwable
            public final synchronized Throwable fillInStackTrace() {
                return this;
            }
        }

        static {
            new c(new Throwable("Failure occurred while trying to finish a future."));
        }

        public c(Throwable th) {
            boolean z4 = AbstractC0788a.f5726m;
            th.getClass();
            this.f5736a = th;
        }
    }

    /* renamed from: s.a$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class d {

        /* renamed from: d  reason: collision with root package name */
        public static final d f5737d = new d(null, null);

        /* renamed from: a  reason: collision with root package name */
        public final Runnable f5738a;

        /* renamed from: b  reason: collision with root package name */
        public final Executor f5739b;

        /* renamed from: c  reason: collision with root package name */
        public d f5740c;

        public d(Runnable runnable, Executor executor) {
            this.f5738a = runnable;
            this.f5739b = executor;
        }
    }

    /* renamed from: s.a$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class e extends AbstractC0067a {

        /* renamed from: a  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<h, Thread> f5741a;

        /* renamed from: b  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<h, h> f5742b;

        /* renamed from: c  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<AbstractC0788a, h> f5743c;

        /* renamed from: d  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<AbstractC0788a, d> f5744d;

        /* renamed from: e  reason: collision with root package name */
        public final AtomicReferenceFieldUpdater<AbstractC0788a, Object> f5745e;

        public e(AtomicReferenceFieldUpdater<h, Thread> atomicReferenceFieldUpdater, AtomicReferenceFieldUpdater<h, h> atomicReferenceFieldUpdater2, AtomicReferenceFieldUpdater<AbstractC0788a, h> atomicReferenceFieldUpdater3, AtomicReferenceFieldUpdater<AbstractC0788a, d> atomicReferenceFieldUpdater4, AtomicReferenceFieldUpdater<AbstractC0788a, Object> atomicReferenceFieldUpdater5) {
            this.f5741a = atomicReferenceFieldUpdater;
            this.f5742b = atomicReferenceFieldUpdater2;
            this.f5743c = atomicReferenceFieldUpdater3;
            this.f5744d = atomicReferenceFieldUpdater4;
            this.f5745e = atomicReferenceFieldUpdater5;
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean a(AbstractC0788a<?> abstractC0788a, d dVar, d dVar2) {
            AtomicReferenceFieldUpdater<AbstractC0788a, d> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f5744d;
                if (atomicReferenceFieldUpdater.compareAndSet(abstractC0788a, dVar, dVar2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(abstractC0788a) == dVar);
            return false;
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean b(AbstractC0788a<?> abstractC0788a, Object obj, Object obj2) {
            AtomicReferenceFieldUpdater<AbstractC0788a, Object> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f5745e;
                if (atomicReferenceFieldUpdater.compareAndSet(abstractC0788a, obj, obj2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(abstractC0788a) == obj);
            return false;
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean c(AbstractC0788a<?> abstractC0788a, h hVar, h hVar2) {
            AtomicReferenceFieldUpdater<AbstractC0788a, h> atomicReferenceFieldUpdater;
            do {
                atomicReferenceFieldUpdater = this.f5743c;
                if (atomicReferenceFieldUpdater.compareAndSet(abstractC0788a, hVar, hVar2)) {
                    return true;
                }
            } while (atomicReferenceFieldUpdater.get(abstractC0788a) == hVar);
            return false;
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final void d(h hVar, h hVar2) {
            this.f5742b.lazySet(hVar, hVar2);
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final void e(h hVar, Thread thread) {
            this.f5741a.lazySet(hVar, thread);
        }
    }

    /* renamed from: s.a$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class f<V> implements Runnable {
        @Override // java.lang.Runnable
        public final void run() {
            throw null;
        }
    }

    /* renamed from: s.a$g */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class g extends AbstractC0067a {
        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean a(AbstractC0788a<?> abstractC0788a, d dVar, d dVar2) {
            synchronized (abstractC0788a) {
                try {
                    if (abstractC0788a.f5731k == dVar) {
                        abstractC0788a.f5731k = dVar2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean b(AbstractC0788a<?> abstractC0788a, Object obj, Object obj2) {
            synchronized (abstractC0788a) {
                try {
                    if (abstractC0788a.f5730j == obj) {
                        abstractC0788a.f5730j = obj2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final boolean c(AbstractC0788a<?> abstractC0788a, h hVar, h hVar2) {
            synchronized (abstractC0788a) {
                try {
                    if (abstractC0788a.f5732l == hVar) {
                        abstractC0788a.f5732l = hVar2;
                        return true;
                    }
                    return false;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final void d(h hVar, h hVar2) {
            hVar.f5748b = hVar2;
        }

        @Override // s.AbstractC0788a.AbstractC0067a
        public final void e(h hVar, Thread thread) {
            hVar.f5747a = thread;
        }
    }

    /* renamed from: s.a$h */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class h {

        /* renamed from: c  reason: collision with root package name */
        public static final h f5746c = new Object();

        /* renamed from: a  reason: collision with root package name */
        public volatile Thread f5747a;

        /* renamed from: b  reason: collision with root package name */
        public volatile h f5748b;

        public h() {
            AbstractC0788a.f5728o.e(this, Thread.currentThread());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [s.a$a] */
    /* JADX WARN: Type inference failed for: r2v7 */
    static {
        e eVar;
        try {
            th = null;
            eVar = new e(AtomicReferenceFieldUpdater.newUpdater(h.class, Thread.class, "a"), AtomicReferenceFieldUpdater.newUpdater(h.class, h.class, "b"), AtomicReferenceFieldUpdater.newUpdater(AbstractC0788a.class, h.class, "l"), AtomicReferenceFieldUpdater.newUpdater(AbstractC0788a.class, d.class, "k"), AtomicReferenceFieldUpdater.newUpdater(AbstractC0788a.class, Object.class, "j"));
        } catch (Throwable th) {
            th = th;
            eVar = new Object();
        }
        f5728o = eVar;
        if (th != null) {
            f5727n.log(Level.SEVERE, "SafeAtomicHelper is broken!", th);
        }
        f5729p = new Object();
    }

    public static void c(AbstractC0788a<?> abstractC0788a) {
        h hVar;
        d dVar;
        do {
            hVar = abstractC0788a.f5732l;
        } while (!f5728o.c(abstractC0788a, hVar, h.f5746c));
        while (hVar != null) {
            Thread thread = hVar.f5747a;
            if (thread != null) {
                hVar.f5747a = null;
                LockSupport.unpark(thread);
            }
            hVar = hVar.f5748b;
        }
        do {
            dVar = abstractC0788a.f5731k;
        } while (!f5728o.a(abstractC0788a, dVar, d.f5737d));
        d dVar2 = null;
        while (dVar != null) {
            d dVar3 = dVar.f5740c;
            dVar.f5740c = dVar2;
            dVar2 = dVar;
            dVar = dVar3;
        }
        while (dVar2 != null) {
            d dVar4 = dVar2.f5740c;
            Runnable runnable = dVar2.f5738a;
            if (!(runnable instanceof f)) {
                d(runnable, dVar2.f5739b);
                dVar2 = dVar4;
            } else {
                ((f) runnable).getClass();
                throw null;
            }
        }
    }

    public static void d(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException e4) {
            Level level = Level.SEVERE;
            f5727n.log(level, "RuntimeException while executing runnable " + runnable + " with executor " + executor, (Throwable) e4);
        }
    }

    public static Object e(Object obj) {
        if (!(obj instanceof b)) {
            if (!(obj instanceof c)) {
                if (obj == f5729p) {
                    return null;
                }
                return obj;
            }
            throw new ExecutionException(((c) obj).f5736a);
        }
        Throwable th = ((b) obj).f5735a;
        CancellationException cancellationException = new CancellationException("Task was cancelled.");
        cancellationException.initCause(th);
        throw cancellationException;
    }

    public static <V> V f(Future<V> future) {
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
        d dVar = this.f5731k;
        d dVar2 = d.f5737d;
        if (dVar != dVar2) {
            d dVar3 = new d(runnable, executor);
            do {
                dVar3.f5740c = dVar;
                if (f5728o.a(this, dVar, dVar3)) {
                    return;
                }
                dVar = this.f5731k;
            } while (dVar != dVar2);
            d(runnable, executor);
        }
        d(runnable, executor);
    }

    public final void b(StringBuilder sb) {
        String valueOf;
        try {
            Object f4 = f(this);
            sb.append("SUCCESS, result=[");
            if (f4 == this) {
                valueOf = "this future";
            } else {
                valueOf = String.valueOf(f4);
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
        Object obj = this.f5730j;
        if (obj == null) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5 | (obj instanceof f)) {
            if (f5726m) {
                bVar = new b(z4, new CancellationException("Future.cancel() was called."));
            } else if (z4) {
                bVar = b.f5733b;
            } else {
                bVar = b.f5734c;
            }
            while (!f5728o.b(this, obj, bVar)) {
                obj = this.f5730j;
                if (!(obj instanceof f)) {
                    return false;
                }
            }
            c(this);
            if (!(obj instanceof f)) {
                return true;
            }
            ((f) obj).getClass();
            throw null;
        }
        return false;
    }

    public String g() {
        Object obj = this.f5730j;
        if (obj instanceof f) {
            StringBuilder sb = new StringBuilder("setFuture=[");
            ((f) obj).getClass();
            sb.append("null");
            sb.append("]");
            return sb.toString();
        } else if (this instanceof ScheduledFuture) {
            return "remaining delay=[" + ((ScheduledFuture) this).getDelay(TimeUnit.MILLISECONDS) + " ms]";
        } else {
            return null;
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
        throw new UnsupportedOperationException("Method not decompiled: s.AbstractC0788a.get(long, java.util.concurrent.TimeUnit):java.lang.Object");
    }

    public final void h(h hVar) {
        hVar.f5747a = null;
        while (true) {
            h hVar2 = this.f5732l;
            if (hVar2 == h.f5746c) {
                return;
            }
            h hVar3 = null;
            while (hVar2 != null) {
                h hVar4 = hVar2.f5748b;
                if (hVar2.f5747a != null) {
                    hVar3 = hVar2;
                } else if (hVar3 != null) {
                    hVar3.f5748b = hVar4;
                    if (hVar3.f5747a == null) {
                        break;
                    }
                } else if (!f5728o.c(this, hVar2, hVar4)) {
                    break;
                }
                hVar2 = hVar4;
            }
            return;
        }
    }

    public boolean i(Throwable th) {
        th.getClass();
        if (f5728o.b(this, null, new c(th))) {
            c(this);
            return true;
        }
        return false;
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return this.f5730j instanceof b;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        boolean z4;
        Object obj = this.f5730j;
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
        if (this.f5730j instanceof b) {
            sb.append("CANCELLED");
        } else if (isDone()) {
            b(sb);
        } else {
            try {
                str = g();
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
            Object obj2 = this.f5730j;
            if ((obj2 != null) & (!(obj2 instanceof f))) {
                return (V) e(obj2);
            }
            h hVar = this.f5732l;
            h hVar2 = h.f5746c;
            if (hVar != hVar2) {
                h hVar3 = new h();
                do {
                    AbstractC0067a abstractC0067a = f5728o;
                    abstractC0067a.d(hVar3, hVar);
                    if (abstractC0067a.c(this, hVar, hVar3)) {
                        do {
                            LockSupport.park(this);
                            if (!Thread.interrupted()) {
                                obj = this.f5730j;
                            } else {
                                h(hVar3);
                                throw new InterruptedException();
                            }
                        } while (!((obj != null) & (!(obj instanceof f))));
                        return (V) e(obj);
                    }
                    hVar = this.f5732l;
                } while (hVar != hVar2);
                return (V) e(this.f5730j);
            }
            return (V) e(this.f5730j);
        }
        throw new InterruptedException();
    }
}
