package m0;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;
import androidx.work.impl.WorkDatabase;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import n0.AbstractC0740a;
import q0.InterfaceC0766a;
import q0.InterfaceC0767b;
import q0.InterfaceC0768c;
import r0.C0779a;

/* renamed from: m0.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0731g {
    @Deprecated

    /* renamed from: a  reason: collision with root package name */
    public volatile InterfaceC0766a f5303a;

    /* renamed from: b  reason: collision with root package name */
    public Executor f5304b;

    /* renamed from: c  reason: collision with root package name */
    public InterfaceC0767b f5305c;

    /* renamed from: d  reason: collision with root package name */
    public final C0730f f5306d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f5307e;
    public boolean f;
    @Deprecated

    /* renamed from: g  reason: collision with root package name */
    public List<b> f5308g;

    /* renamed from: h  reason: collision with root package name */
    public final ReentrantReadWriteLock f5309h = new ReentrantReadWriteLock();

    /* renamed from: i  reason: collision with root package name */
    public final ThreadLocal<Integer> f5310i = new ThreadLocal<>();

    /* renamed from: m0.g$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a<T extends AbstractC0731g> {

        /* renamed from: b  reason: collision with root package name */
        public final String f5312b;

        /* renamed from: c  reason: collision with root package name */
        public final Context f5313c;

        /* renamed from: d  reason: collision with root package name */
        public ArrayList<b> f5314d;

        /* renamed from: e  reason: collision with root package name */
        public Executor f5315e;
        public Executor f;

        /* renamed from: g  reason: collision with root package name */
        public InterfaceC0767b.c f5316g;

        /* renamed from: h  reason: collision with root package name */
        public boolean f5317h;

        /* renamed from: j  reason: collision with root package name */
        public boolean f5319j;

        /* renamed from: k  reason: collision with root package name */
        public final d f5320k;

        /* renamed from: l  reason: collision with root package name */
        public HashSet f5321l;

        /* renamed from: a  reason: collision with root package name */
        public final Class<T> f5311a = WorkDatabase.class;

        /* renamed from: i  reason: collision with root package name */
        public boolean f5318i = true;

        /* JADX WARN: Type inference failed for: r1v3, types: [m0.g$d, java.lang.Object] */
        public a(Context context, String str) {
            this.f5313c = context;
            this.f5312b = str;
            ?? obj = new Object();
            obj.f5325a = new HashMap<>();
            this.f5320k = obj;
        }

        public final void a(AbstractC0740a... abstractC0740aArr) {
            if (this.f5321l == null) {
                this.f5321l = new HashSet();
            }
            for (AbstractC0740a abstractC0740a : abstractC0740aArr) {
                this.f5321l.add(Integer.valueOf(abstractC0740a.f5364a));
                this.f5321l.add(Integer.valueOf(abstractC0740a.f5365b));
            }
            d dVar = this.f5320k;
            dVar.getClass();
            for (AbstractC0740a abstractC0740a2 : abstractC0740aArr) {
                int i4 = abstractC0740a2.f5364a;
                HashMap<Integer, TreeMap<Integer, AbstractC0740a>> hashMap = dVar.f5325a;
                TreeMap<Integer, AbstractC0740a> treeMap = hashMap.get(Integer.valueOf(i4));
                if (treeMap == null) {
                    treeMap = new TreeMap<>();
                    hashMap.put(Integer.valueOf(i4), treeMap);
                }
                int i5 = abstractC0740a2.f5365b;
                AbstractC0740a abstractC0740a3 = treeMap.get(Integer.valueOf(i5));
                if (abstractC0740a3 != null) {
                    Log.w("ROOM", "Overriding migration " + abstractC0740a3 + " with " + abstractC0740a2);
                }
                treeMap.put(Integer.valueOf(i5), abstractC0740a2);
            }
        }
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* renamed from: m0.g$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class c {

        /* renamed from: j  reason: collision with root package name */
        public static final c f5322j;

        /* renamed from: k  reason: collision with root package name */
        public static final c f5323k;

        /* renamed from: l  reason: collision with root package name */
        public static final /* synthetic */ c[] f5324l;
        /* JADX INFO: Fake field, exist only in values array */
        c EF3;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v1, types: [m0.g$c, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r5v1, types: [m0.g$c, java.lang.Enum] */
        static {
            Enum r32 = new Enum("AUTOMATIC", 0);
            ?? r4 = new Enum("TRUNCATE", 1);
            f5322j = r4;
            ?? r5 = new Enum("WRITE_AHEAD_LOGGING", 2);
            f5323k = r5;
            f5324l = new c[]{r32, r4, r5};
        }

        public c() {
            throw null;
        }

        public static c valueOf(String str) {
            return (c) Enum.valueOf(c.class, str);
        }

        public static c[] values() {
            return (c[]) f5324l.clone();
        }
    }

    /* renamed from: m0.g$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {

        /* renamed from: a  reason: collision with root package name */
        public HashMap<Integer, TreeMap<Integer, AbstractC0740a>> f5325a;
    }

    public AbstractC0731g() {
        new ConcurrentHashMap();
        this.f5306d = d();
    }

    public final void a() {
        if (this.f5307e || Looper.getMainLooper().getThread() != Thread.currentThread()) {
            return;
        }
        throw new IllegalStateException("Cannot access database on the main thread since it may potentially lock the UI for a long period of time.");
    }

    public final void b() {
        if (!((C0779a) this.f5305c.D()).f5691j.inTransaction() && this.f5310i.get() != null) {
            throw new IllegalStateException("Cannot access database on a different coroutine context inherited from a suspending transaction.");
        }
    }

    @Deprecated
    public final void c() {
        a();
        InterfaceC0766a D4 = this.f5305c.D();
        this.f5306d.c(D4);
        ((C0779a) D4).a();
    }

    public abstract C0730f d();

    public abstract InterfaceC0767b e(C0725a c0725a);

    @Deprecated
    public final void f() {
        ((C0779a) this.f5305c.D()).b();
        if (!((C0779a) this.f5305c.D()).f5691j.inTransaction()) {
            C0730f c0730f = this.f5306d;
            if (c0730f.f5292d.compareAndSet(false, true)) {
                c0730f.f5291c.f5304b.execute(c0730f.f5296i);
            }
        }
    }

    public final Cursor g(InterfaceC0768c interfaceC0768c) {
        a();
        b();
        return ((C0779a) this.f5305c.D()).g(interfaceC0768c);
    }

    @Deprecated
    public final void h() {
        ((C0779a) this.f5305c.D()).i();
    }

    /* renamed from: m0.g$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class b {
        public void a(C0779a c0779a) {
        }
    }
}
