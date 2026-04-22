package m0;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import n.C0739b;
import q0.InterfaceC0766a;
import r0.C0779a;

/* renamed from: m0.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0730f {

    /* renamed from: j  reason: collision with root package name */
    public static final String[] f5288j = {"UPDATE", "DELETE", "INSERT"};

    /* renamed from: b  reason: collision with root package name */
    public final String[] f5290b;

    /* renamed from: c  reason: collision with root package name */
    public final AbstractC0731g f5291c;
    public volatile r0.e f;

    /* renamed from: g  reason: collision with root package name */
    public final b f5294g;

    /* renamed from: d  reason: collision with root package name */
    public final AtomicBoolean f5292d = new AtomicBoolean(false);

    /* renamed from: e  reason: collision with root package name */
    public volatile boolean f5293e = false;
    @SuppressLint({"RestrictedApi"})

    /* renamed from: h  reason: collision with root package name */
    public final C0739b<c, d> f5295h = new C0739b<>();

    /* renamed from: i  reason: collision with root package name */
    public final a f5296i = new a();

    /* renamed from: a  reason: collision with root package name */
    public final HashMap<String, Integer> f5289a = new HashMap<>();

    /* renamed from: m0.f$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a implements Runnable {
        public a() {
        }

        public final HashSet a() {
            HashSet hashSet = new HashSet();
            Cursor g4 = C0730f.this.f5291c.g(new E1.i("SELECT * FROM room_table_modification_log WHERE invalidated = 1;"));
            while (g4.moveToNext()) {
                try {
                    hashSet.add(Integer.valueOf(g4.getInt(0)));
                } catch (Throwable th) {
                    g4.close();
                    throw th;
                }
            }
            g4.close();
            if (!hashSet.isEmpty()) {
                C0730f.this.f.i();
            }
            return hashSet;
        }

        /* JADX WARN: Removed duplicated region for block: B:34:0x007d  */
        /* JADX WARN: Removed duplicated region for block: B:48:0x00af A[ADDED_TO_REGION, ORIG_RETURN, RETURN] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void run() {
            /*
                r5 = this;
                m0.f r0 = m0.C0730f.this
                m0.g r0 = r0.f5291c
                java.util.concurrent.locks.ReentrantReadWriteLock r0 = r0.f5309h
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r0 = r0.readLock()
                r1 = 1
                r2 = 0
                r3 = 0
                r0.lock()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                m0.f r4 = m0.C0730f.this     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                boolean r4 = r4.a()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                if (r4 != 0) goto L1c
                r0.unlock()
                return
            L1c:
                m0.f r4 = m0.C0730f.this     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                java.util.concurrent.atomic.AtomicBoolean r4 = r4.f5292d     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                boolean r1 = r4.compareAndSet(r1, r2)     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                if (r1 != 0) goto L2a
                r0.unlock()
                return
            L2a:
                m0.f r1 = m0.C0730f.this     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                m0.g r1 = r1.f5291c     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                q0.b r1 = r1.f5305c     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                q0.a r1 = r1.D()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                r0.a r1 = (r0.C0779a) r1     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                android.database.sqlite.SQLiteDatabase r1 = r1.f5691j     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                boolean r1 = r1.inTransaction()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                if (r1 == 0) goto L42
                r0.unlock()
                return
            L42:
                m0.f r1 = m0.C0730f.this     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                m0.g r1 = r1.f5291c     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                boolean r2 = r1.f     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                if (r2 == 0) goto L6b
                q0.b r1 = r1.f5305c     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                q0.a r1 = r1.D()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                r0.a r1 = (r0.C0779a) r1     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                r1.a()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                java.util.HashSet r3 = r5.a()     // Catch: java.lang.Throwable -> L66
                r1.i()     // Catch: java.lang.Throwable -> L66
                r1.b()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                goto L6f
            L60:
                r1 = move-exception
                goto Lb0
            L62:
                r1 = move-exception
                goto L73
            L64:
                r1 = move-exception
                goto L73
            L66:
                r2 = move-exception
                r1.b()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
                throw r2     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
            L6b:
                java.util.HashSet r3 = r5.a()     // Catch: java.lang.Throwable -> L60 android.database.sqlite.SQLiteException -> L62 java.lang.IllegalStateException -> L64
            L6f:
                r0.unlock()
                goto L7b
            L73:
                java.lang.String r2 = "ROOM"
                java.lang.String r4 = "Cannot run invalidation tracker. Is the db closed?"
                android.util.Log.e(r2, r4, r1)     // Catch: java.lang.Throwable -> L60
                goto L6f
            L7b:
                if (r3 == 0) goto Laf
                boolean r0 = r3.isEmpty()
                if (r0 != 0) goto Laf
                m0.f r0 = m0.C0730f.this
                n.b<m0.f$c, m0.f$d> r0 = r0.f5295h
                monitor-enter(r0)
                m0.f r1 = m0.C0730f.this     // Catch: java.lang.Throwable -> L9a
                n.b<m0.f$c, m0.f$d> r1 = r1.f5295h     // Catch: java.lang.Throwable -> L9a
                java.util.Iterator r1 = r1.iterator()     // Catch: java.lang.Throwable -> L9a
                n.b$e r1 = (n.C0739b.e) r1     // Catch: java.lang.Throwable -> L9a
                boolean r2 = r1.hasNext()     // Catch: java.lang.Throwable -> L9a
                if (r2 != 0) goto L9c
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L9a
                goto Laf
            L9a:
                r1 = move-exception
                goto Lad
            L9c:
                java.lang.Object r1 = r1.next()     // Catch: java.lang.Throwable -> L9a
                java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch: java.lang.Throwable -> L9a
                java.lang.Object r1 = r1.getValue()     // Catch: java.lang.Throwable -> L9a
                m0.f$d r1 = (m0.C0730f.d) r1     // Catch: java.lang.Throwable -> L9a
                r1.getClass()     // Catch: java.lang.Throwable -> L9a
                r1 = 0
                throw r1     // Catch: java.lang.Throwable -> L9a
            Lad:
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L9a
                throw r1
            Laf:
                return
            Lb0:
                r0.unlock()
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: m0.C0730f.a.run():void");
        }
    }

    /* renamed from: m0.f$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final long[] f5298a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean[] f5299b;

        /* renamed from: c  reason: collision with root package name */
        public final int[] f5300c;

        /* renamed from: d  reason: collision with root package name */
        public boolean f5301d;

        /* renamed from: e  reason: collision with root package name */
        public boolean f5302e;

        public b(int i4) {
            long[] jArr = new long[i4];
            this.f5298a = jArr;
            boolean[] zArr = new boolean[i4];
            this.f5299b = zArr;
            this.f5300c = new int[i4];
            Arrays.fill(jArr, 0L);
            Arrays.fill(zArr, false);
        }

        public final int[] a() {
            boolean z4;
            synchronized (this) {
                try {
                    if (this.f5301d && !this.f5302e) {
                        int length = this.f5298a.length;
                        int i4 = 0;
                        while (true) {
                            int i5 = 1;
                            if (i4 < length) {
                                if (this.f5298a[i4] > 0) {
                                    z4 = true;
                                } else {
                                    z4 = false;
                                }
                                boolean[] zArr = this.f5299b;
                                if (z4 != zArr[i4]) {
                                    int[] iArr = this.f5300c;
                                    if (!z4) {
                                        i5 = 2;
                                    }
                                    iArr[i4] = i5;
                                } else {
                                    this.f5300c[i4] = 0;
                                }
                                zArr[i4] = z4;
                                i4++;
                            } else {
                                this.f5302e = true;
                                this.f5301d = false;
                                return this.f5300c;
                            }
                        }
                    }
                    return null;
                } finally {
                }
            }
        }
    }

    /* renamed from: m0.f$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class c {
        public abstract void a(Set<String> set);
    }

    /* renamed from: m0.f$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {
    }

    public C0730f(AbstractC0731g abstractC0731g, HashMap hashMap, HashMap hashMap2, String... strArr) {
        this.f5291c = abstractC0731g;
        this.f5294g = new b(strArr.length);
        Collections.newSetFromMap(new IdentityHashMap());
        int length = strArr.length;
        this.f5290b = new String[length];
        for (int i4 = 0; i4 < length; i4++) {
            String str = strArr[i4];
            Locale locale = Locale.US;
            String lowerCase = str.toLowerCase(locale);
            this.f5289a.put(lowerCase, Integer.valueOf(i4));
            String str2 = (String) hashMap.get(strArr[i4]);
            if (str2 != null) {
                this.f5290b[i4] = str2.toLowerCase(locale);
            } else {
                this.f5290b[i4] = lowerCase;
            }
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            Locale locale2 = Locale.US;
            String lowerCase2 = ((String) entry.getValue()).toLowerCase(locale2);
            if (this.f5289a.containsKey(lowerCase2)) {
                String lowerCase3 = ((String) entry.getKey()).toLowerCase(locale2);
                HashMap<String, Integer> hashMap3 = this.f5289a;
                hashMap3.put(lowerCase3, hashMap3.get(lowerCase2));
            }
        }
    }

    public final boolean a() {
        boolean z4;
        InterfaceC0766a interfaceC0766a = this.f5291c.f5303a;
        if (interfaceC0766a != null && ((C0779a) interfaceC0766a).f5691j.isOpen()) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (!z4) {
            return false;
        }
        if (!this.f5293e) {
            this.f5291c.f5305c.D();
        }
        if (this.f5293e) {
            return true;
        }
        Log.e("ROOM", "database is not initialized even though it is open");
        return false;
    }

    public final void b(InterfaceC0766a interfaceC0766a, int i4) {
        C0779a c0779a = (C0779a) interfaceC0766a;
        c0779a.d(I.h.b(i4, "INSERT OR IGNORE INTO room_table_modification_log VALUES(", ", 0)"));
        String str = this.f5290b[i4];
        StringBuilder sb = new StringBuilder();
        String[] strArr = f5288j;
        for (int i5 = 0; i5 < 3; i5++) {
            String str2 = strArr[i5];
            sb.setLength(0);
            sb.append("CREATE TEMP TRIGGER IF NOT EXISTS ");
            sb.append("`");
            sb.append("room_table_modification_trigger_");
            sb.append(str);
            sb.append("_");
            sb.append(str2);
            sb.append("`");
            sb.append(" AFTER ");
            sb.append(str2);
            sb.append(" ON `");
            sb.append(str);
            sb.append("` BEGIN UPDATE ");
            sb.append("room_table_modification_log");
            sb.append(" SET ");
            sb.append("invalidated");
            sb.append(" = 1");
            sb.append(" WHERE ");
            sb.append("table_id");
            sb.append(" = ");
            sb.append(i4);
            sb.append(" AND ");
            sb.append("invalidated");
            sb.append(" = 0");
            sb.append("; END");
            c0779a.d(sb.toString());
        }
    }

    public final void c(InterfaceC0766a interfaceC0766a) {
        if (((C0779a) interfaceC0766a).f5691j.inTransaction()) {
            return;
        }
        while (true) {
            try {
                ReentrantReadWriteLock.ReadLock readLock = this.f5291c.f5309h.readLock();
                readLock.lock();
                int[] a4 = this.f5294g.a();
                if (a4 == null) {
                    readLock.unlock();
                    return;
                }
                int length = a4.length;
                C0779a c0779a = (C0779a) interfaceC0766a;
                c0779a.a();
                for (int i4 = 0; i4 < length; i4++) {
                    int i5 = a4[i4];
                    if (i5 != 1) {
                        if (i5 == 2) {
                            String str = this.f5290b[i4];
                            StringBuilder sb = new StringBuilder();
                            String[] strArr = f5288j;
                            for (int i6 = 0; i6 < 3; i6++) {
                                String str2 = strArr[i6];
                                sb.setLength(0);
                                sb.append("DROP TRIGGER IF EXISTS ");
                                sb.append("`");
                                sb.append("room_table_modification_trigger_");
                                sb.append(str);
                                sb.append("_");
                                sb.append(str2);
                                sb.append("`");
                                ((C0779a) interfaceC0766a).d(sb.toString());
                            }
                        }
                    } else {
                        b(interfaceC0766a, i4);
                    }
                }
                c0779a.i();
                c0779a.b();
                b bVar = this.f5294g;
                synchronized (bVar) {
                    bVar.f5302e = false;
                }
                readLock.unlock();
            } catch (SQLiteException | IllegalStateException e4) {
                Log.e("ROOM", "Cannot run invalidation tracker. Is the db closed?", e4);
                return;
            }
        }
    }
}
