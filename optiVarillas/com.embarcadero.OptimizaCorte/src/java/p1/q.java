package p1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import f1.C0412b;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l1.C0717a;
import l1.C0718b;
import l1.C0719c;
import l1.C0720d;
import l1.C0721e;
import m1.C0736a;
import p1.q;
import q1.InterfaceC0770b;
import r1.InterfaceC0782a;
import s1.C0795a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class q implements d, InterfaceC0770b, c {

    /* renamed from: o  reason: collision with root package name */
    public static final C0412b f5533o = new C0412b("proto");

    /* renamed from: j  reason: collision with root package name */
    public final x f5534j;

    /* renamed from: k  reason: collision with root package name */
    public final InterfaceC0782a f5535k;

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceC0782a f5536l;

    /* renamed from: m  reason: collision with root package name */
    public final e f5537m;

    /* renamed from: n  reason: collision with root package name */
    public final k3.a<String> f5538n;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface a<T, U> {
        U apply(T t3);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final String f5539a;

        /* renamed from: b  reason: collision with root package name */
        public final String f5540b;

        public b(String str, String str2) {
            this.f5539a = str;
            this.f5540b = str2;
        }
    }

    public q(InterfaceC0782a interfaceC0782a, InterfaceC0782a interfaceC0782a2, e eVar, x xVar, k3.a<String> aVar) {
        this.f5534j = xVar;
        this.f5535k = interfaceC0782a;
        this.f5536l = interfaceC0782a2;
        this.f5537m = eVar;
        this.f5538n = aVar;
    }

    /* JADX WARN: Type inference failed for: r12v6, types: [p1.q$a, java.lang.Object] */
    public static Long i(SQLiteDatabase sQLiteDatabase, i1.j jVar) {
        StringBuilder sb = new StringBuilder("backend_name = ? and priority = ?");
        ArrayList arrayList = new ArrayList(Arrays.asList(jVar.f3640a, String.valueOf(C0795a.a(jVar.f3642c))));
        byte[] bArr = jVar.f3641b;
        if (bArr != null) {
            sb.append(" and extras = ?");
            arrayList.add(Base64.encodeToString(bArr, 0));
        } else {
            sb.append(" and extras is null");
        }
        return (Long) q(sQLiteDatabase.query("transport_contexts", new String[]{"_id"}, sb.toString(), (String[]) arrayList.toArray(new String[0]), null, null, null), new Object());
    }

    public static String p(Iterable<i> iterable) {
        StringBuilder sb = new StringBuilder("(");
        Iterator<i> it = iterable.iterator();
        while (it.hasNext()) {
            sb.append(it.next().b());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public static <T> T q(Cursor cursor, a<Cursor, T> aVar) {
        try {
            return aVar.apply(cursor);
        } finally {
            cursor.close();
        }
    }

    @Override // p1.d
    public final void A(Iterable<i> iterable) {
        if (!iterable.iterator().hasNext()) {
            return;
        }
        String str = "UPDATE events SET num_attempts = num_attempts + 1 WHERE _id in " + p(iterable);
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            g4.compileStatement(str).execute();
            Cursor rawQuery = g4.rawQuery("SELECT COUNT(*), transport_name FROM events WHERE num_attempts >= 16 GROUP BY transport_name", null);
            while (rawQuery.moveToNext()) {
                b(rawQuery.getInt(0), rawQuery.getString(1), C0719c.a.f5252o);
            }
            rawQuery.close();
            g4.compileStatement("DELETE FROM events WHERE num_attempts >= 16").execute();
            g4.setTransactionSuccessful();
        } finally {
            g4.endTransaction();
        }
    }

    @Override // q1.InterfaceC0770b
    public final <T> T a(InterfaceC0770b.a<T> aVar) {
        SQLiteDatabase g4 = g();
        InterfaceC0782a interfaceC0782a = this.f5536l;
        long a4 = interfaceC0782a.a();
        while (true) {
            try {
                g4.beginTransaction();
                try {
                    T a5 = aVar.a();
                    g4.setTransactionSuccessful();
                    return a5;
                } finally {
                    g4.endTransaction();
                }
            } catch (SQLiteDatabaseLockedException e4) {
                if (interfaceC0782a.a() < this.f5537m.a() + a4) {
                    SystemClock.sleep(50L);
                } else {
                    throw new RuntimeException("Timed out while trying to acquire the lock.", e4);
                }
            }
        }
    }

    @Override // p1.c
    public final void b(final long j4, final String str, final C0719c.a aVar) {
        k(new a() { // from class: p1.m
            /* JADX WARN: Type inference failed for: r3v1, types: [p1.q$a, java.lang.Object] */
            @Override // p1.q.a
            public final Object apply(Object obj) {
                SQLiteDatabase sQLiteDatabase = (SQLiteDatabase) obj;
                C0719c.a aVar2 = aVar;
                String num = Integer.toString(aVar2.f5256j);
                String str2 = str;
                boolean booleanValue = ((Boolean) q.q(sQLiteDatabase.rawQuery("SELECT 1 FROM log_event_dropped WHERE log_source = ? AND reason = ?", new String[]{str2, num}), new Object())).booleanValue();
                long j5 = j4;
                int i4 = aVar2.f5256j;
                if (!booleanValue) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("log_source", str2);
                    contentValues.put("reason", Integer.valueOf(i4));
                    contentValues.put("events_dropped_count", Long.valueOf(j5));
                    sQLiteDatabase.insert("log_event_dropped", null, contentValues);
                } else {
                    sQLiteDatabase.execSQL("UPDATE log_event_dropped SET events_dropped_count = events_dropped_count + " + j5 + " WHERE log_source = ? AND reason = ?", new String[]{str2, Integer.toString(i4)});
                }
                return null;
            }
        });
    }

    @Override // p1.d
    public final int c() {
        long a4 = this.f5535k.a() - this.f5537m.b();
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            String[] strArr = {String.valueOf(a4)};
            Cursor rawQuery = g4.rawQuery("SELECT COUNT(*), transport_name FROM events WHERE timestamp_ms < ? GROUP BY transport_name", strArr);
            while (rawQuery.moveToNext()) {
                b(rawQuery.getInt(0), rawQuery.getString(1), C0719c.a.f5249l);
            }
            rawQuery.close();
            int delete = g4.delete("events", "timestamp_ms < ?", strArr);
            g4.setTransactionSuccessful();
            return delete;
        } finally {
            g4.endTransaction();
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        this.f5534j.close();
    }

    @Override // p1.c
    public final C0717a d() {
        int i4 = C0717a.f5236e;
        final C0717a.C0058a c0058a = new C0717a.C0058a();
        final HashMap hashMap = new HashMap();
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            C0717a c0717a = (C0717a) q(g4.rawQuery("SELECT log_source, reason, events_dropped_count FROM log_event_dropped", new String[0]), new a() { // from class: p1.o
                @Override // p1.q.a
                public final Object apply(Object obj) {
                    Map map;
                    Cursor cursor = (Cursor) obj;
                    q qVar = q.this;
                    qVar.getClass();
                    while (true) {
                        boolean moveToNext = cursor.moveToNext();
                        map = hashMap;
                        if (!moveToNext) {
                            break;
                        }
                        String string = cursor.getString(0);
                        int i5 = cursor.getInt(1);
                        C0719c.a aVar = C0719c.a.f5248k;
                        if (i5 != 0) {
                            if (i5 == 1) {
                                aVar = C0719c.a.f5249l;
                            } else if (i5 == 2) {
                                aVar = C0719c.a.f5250m;
                            } else if (i5 == 3) {
                                aVar = C0719c.a.f5251n;
                            } else if (i5 == 4) {
                                aVar = C0719c.a.f5252o;
                            } else if (i5 == 5) {
                                aVar = C0719c.a.f5253p;
                            } else if (i5 == 6) {
                                aVar = C0719c.a.f5254q;
                            } else {
                                C0736a.a(Integer.valueOf(i5), "SQLiteEventStore", "%n is not valid. No matched LogEventDropped-Reason found. Treated it as REASON_UNKNOWN");
                            }
                        }
                        long j4 = cursor.getLong(2);
                        if (!map.containsKey(string)) {
                            map.put(string, new ArrayList());
                        }
                        ((List) map.get(string)).add(new C0719c(j4, aVar));
                    }
                    Iterator it = map.entrySet().iterator();
                    while (true) {
                        boolean hasNext = it.hasNext();
                        C0717a.C0058a c0058a2 = c0058a;
                        if (hasNext) {
                            Map.Entry entry = (Map.Entry) it.next();
                            int i6 = C0720d.f5257c;
                            new ArrayList();
                            c0058a2.f5242b.add(new C0720d((String) entry.getKey(), Collections.unmodifiableList((List) entry.getValue())));
                        } else {
                            final long a4 = qVar.f5535k.a();
                            SQLiteDatabase g5 = qVar.g();
                            g5.beginTransaction();
                            try {
                                l1.f fVar = (l1.f) q.q(g5.rawQuery("SELECT last_metrics_upload_ms FROM global_log_event_state LIMIT 1", new String[0]), new q.a() { // from class: p1.p
                                    @Override // p1.q.a
                                    public final Object apply(Object obj2) {
                                        Cursor cursor2 = (Cursor) obj2;
                                        cursor2.moveToNext();
                                        return new l1.f(cursor2.getLong(0), a4);
                                    }
                                });
                                g5.setTransactionSuccessful();
                                g5.endTransaction();
                                c0058a2.f5241a = fVar;
                                c0058a2.f5243c = new C0718b(new C0721e(qVar.g().compileStatement("PRAGMA page_size").simpleQueryForLong() * qVar.g().compileStatement("PRAGMA page_count").simpleQueryForLong(), e.f5514a.f5507b));
                                c0058a2.f5244d = qVar.f5538n.get();
                                return new C0717a(c0058a2.f5241a, Collections.unmodifiableList(c0058a2.f5242b), c0058a2.f5243c, c0058a2.f5244d);
                            } catch (Throwable th) {
                                g5.endTransaction();
                                throw th;
                            }
                        }
                    }
                }
            });
            g4.setTransactionSuccessful();
            return c0717a;
        } finally {
            g4.endTransaction();
        }
    }

    @Override // p1.d
    public final void e(Iterable<i> iterable) {
        if (!iterable.iterator().hasNext()) {
            return;
        }
        g().compileStatement("DELETE FROM events WHERE _id in " + p(iterable)).execute();
    }

    @Override // p1.c
    public final void f() {
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            g4.compileStatement("DELETE FROM log_event_dropped").execute();
            g4.compileStatement("UPDATE global_log_event_state SET last_metrics_upload_ms=" + this.f5535k.a()).execute();
            g4.setTransactionSuccessful();
        } finally {
            g4.endTransaction();
        }
    }

    public final SQLiteDatabase g() {
        x xVar = this.f5534j;
        Objects.requireNonNull(xVar);
        InterfaceC0782a interfaceC0782a = this.f5536l;
        long a4 = interfaceC0782a.a();
        while (true) {
            try {
                return xVar.getWritableDatabase();
            } catch (SQLiteDatabaseLockedException e4) {
                if (interfaceC0782a.a() < this.f5537m.a() + a4) {
                    SystemClock.sleep(50L);
                } else {
                    throw new RuntimeException("Timed out while trying to open db.", e4);
                }
            }
        }
    }

    @Override // p1.d
    public final p1.b h(final i1.j jVar, final i1.n nVar) {
        String g4 = nVar.g();
        String c4 = C0736a.c("SQLiteEventStore");
        if (Log.isLoggable(c4, 3)) {
            Log.d(c4, "Storing event with priority=" + jVar.f3642c + ", name=" + g4 + " for destination " + jVar.f3640a);
        }
        long longValue = ((Long) k(new a() { // from class: p1.k
            @Override // p1.q.a
            public final Object apply(Object obj) {
                long insert;
                boolean z4;
                byte[] bArr;
                SQLiteDatabase sQLiteDatabase = (SQLiteDatabase) obj;
                q qVar = q.this;
                long simpleQueryForLong = qVar.g().compileStatement("PRAGMA page_size").simpleQueryForLong() * qVar.g().compileStatement("PRAGMA page_count").simpleQueryForLong();
                e eVar = qVar.f5537m;
                long e4 = eVar.e();
                i1.n nVar2 = nVar;
                if (simpleQueryForLong >= e4) {
                    qVar.b(1L, nVar2.g(), C0719c.a.f5250m);
                    return -1L;
                }
                i1.s sVar = jVar;
                Long i4 = q.i(sQLiteDatabase, (i1.j) sVar);
                if (i4 != null) {
                    insert = i4.longValue();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("backend_name", sVar.a());
                    contentValues.put("priority", Integer.valueOf(C0795a.a(sVar.c())));
                    contentValues.put("next_request_ms", (Integer) 0);
                    if (sVar.b() != null) {
                        contentValues.put("extras", Base64.encodeToString(sVar.b(), 0));
                    }
                    insert = sQLiteDatabase.insert("transport_contexts", null, contentValues);
                }
                int d4 = eVar.d();
                byte[] bArr2 = nVar2.d().f3651b;
                if (bArr2.length <= d4) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("context_id", Long.valueOf(insert));
                contentValues2.put("transport_name", nVar2.g());
                contentValues2.put("timestamp_ms", Long.valueOf(nVar2.e()));
                contentValues2.put("uptime_ms", Long.valueOf(nVar2.h()));
                contentValues2.put("payload_encoding", nVar2.d().f3650a.f3392a);
                contentValues2.put("code", nVar2.c());
                contentValues2.put("num_attempts", (Integer) 0);
                contentValues2.put("inline", Boolean.valueOf(z4));
                if (z4) {
                    bArr = bArr2;
                } else {
                    bArr = new byte[0];
                }
                contentValues2.put("payload", bArr);
                long insert2 = sQLiteDatabase.insert("events", null, contentValues2);
                if (!z4) {
                    int ceil = (int) Math.ceil(bArr2.length / d4);
                    for (int i5 = 1; i5 <= ceil; i5++) {
                        byte[] copyOfRange = Arrays.copyOfRange(bArr2, (i5 - 1) * d4, Math.min(i5 * d4, bArr2.length));
                        ContentValues contentValues3 = new ContentValues();
                        contentValues3.put("event_id", Long.valueOf(insert2));
                        contentValues3.put("sequence_num", Integer.valueOf(i5));
                        contentValues3.put("bytes", copyOfRange);
                        sQLiteDatabase.insert("event_payloads", null, contentValues3);
                    }
                }
                for (Map.Entry entry : Collections.unmodifiableMap(nVar2.b()).entrySet()) {
                    ContentValues contentValues4 = new ContentValues();
                    contentValues4.put("event_id", Long.valueOf(insert2));
                    contentValues4.put("name", (String) entry.getKey());
                    contentValues4.put("value", (String) entry.getValue());
                    sQLiteDatabase.insert("event_metadata", null, contentValues4);
                }
                return Long.valueOf(insert2);
            }
        })).longValue();
        if (longValue < 1) {
            return null;
        }
        return new p1.b(longValue, jVar, nVar);
    }

    @Override // p1.d
    public final void j(final long j4, final i1.j jVar) {
        k(new a() { // from class: p1.l
            @Override // p1.q.a
            public final Object apply(Object obj) {
                SQLiteDatabase sQLiteDatabase = (SQLiteDatabase) obj;
                ContentValues contentValues = new ContentValues();
                contentValues.put("next_request_ms", Long.valueOf(j4));
                i1.s sVar = jVar;
                if (sQLiteDatabase.update("transport_contexts", contentValues, "backend_name = ? and priority = ?", new String[]{sVar.a(), String.valueOf(C0795a.a(sVar.c()))}) < 1) {
                    contentValues.put("backend_name", sVar.a());
                    contentValues.put("priority", Integer.valueOf(C0795a.a(sVar.c())));
                    sQLiteDatabase.insert("transport_contexts", null, contentValues);
                }
                return null;
            }
        });
    }

    public final <T> T k(a<SQLiteDatabase, T> aVar) {
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            T apply = aVar.apply(g4);
            g4.setTransactionSuccessful();
            return apply;
        } finally {
            g4.endTransaction();
        }
    }

    public final ArrayList l(SQLiteDatabase sQLiteDatabase, final i1.j jVar, int i4) {
        final ArrayList arrayList = new ArrayList();
        Long i5 = i(sQLiteDatabase, jVar);
        if (i5 == null) {
            return arrayList;
        }
        q(sQLiteDatabase.query("events", new String[]{"_id", "transport_name", "timestamp_ms", "uptime_ms", "payload_encoding", "payload", "code", "inline"}, "context_id = ?", new String[]{i5.toString()}, null, null, null, String.valueOf(i4)), new a() { // from class: p1.n
            /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.Object, i1.h$a] */
            @Override // p1.q.a
            public final Object apply(Object obj) {
                boolean z4;
                C0412b c0412b;
                C0412b c0412b2;
                Cursor cursor = (Cursor) obj;
                q qVar = q.this;
                qVar.getClass();
                while (cursor.moveToNext()) {
                    long j4 = cursor.getLong(0);
                    if (cursor.getInt(7) != 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    ?? obj2 = new Object();
                    obj2.f = new HashMap();
                    String string = cursor.getString(1);
                    if (string != null) {
                        obj2.f3630a = string;
                        obj2.f3633d = Long.valueOf(cursor.getLong(2));
                        obj2.f3634e = Long.valueOf(cursor.getLong(3));
                        if (z4) {
                            String string2 = cursor.getString(4);
                            if (string2 == null) {
                                c0412b2 = q.f5533o;
                            } else {
                                c0412b2 = new C0412b(string2);
                            }
                            obj2.f3632c = new i1.m(c0412b2, cursor.getBlob(5));
                        } else {
                            String string3 = cursor.getString(4);
                            if (string3 == null) {
                                c0412b = q.f5533o;
                            } else {
                                c0412b = new C0412b(string3);
                            }
                            Cursor query = qVar.g().query("event_payloads", new String[]{"bytes"}, "event_id = ?", new String[]{String.valueOf(j4)}, null, null, "sequence_num");
                            try {
                                ArrayList arrayList2 = new ArrayList();
                                int i6 = 0;
                                while (query.moveToNext()) {
                                    byte[] blob = query.getBlob(0);
                                    arrayList2.add(blob);
                                    i6 += blob.length;
                                }
                                byte[] bArr = new byte[i6];
                                int i7 = 0;
                                for (int i8 = 0; i8 < arrayList2.size(); i8++) {
                                    byte[] bArr2 = (byte[]) arrayList2.get(i8);
                                    System.arraycopy(bArr2, 0, bArr, i7, bArr2.length);
                                    i7 += bArr2.length;
                                }
                                query.close();
                                obj2.f3632c = new i1.m(c0412b, bArr);
                            } catch (Throwable th) {
                                query.close();
                                throw th;
                            }
                        }
                        if (!cursor.isNull(6)) {
                            obj2.f3631b = Integer.valueOf(cursor.getInt(6));
                        }
                        arrayList.add(new b(j4, jVar, obj2.b()));
                    } else {
                        throw new NullPointerException("Null transportName");
                    }
                }
                return null;
            }
        });
        return arrayList;
    }

    @Override // p1.d
    public final boolean m(i1.j jVar) {
        Boolean bool;
        SQLiteDatabase g4 = g();
        g4.beginTransaction();
        try {
            Long i4 = i(g4, jVar);
            if (i4 == null) {
                bool = Boolean.FALSE;
            } else {
                Cursor rawQuery = g().rawQuery("SELECT 1 FROM events WHERE context_id = ? LIMIT 1", new String[]{i4.toString()});
                Boolean valueOf = Boolean.valueOf(rawQuery.moveToNext());
                rawQuery.close();
                bool = valueOf;
            }
            g4.setTransactionSuccessful();
            g4.endTransaction();
            return bool.booleanValue();
        } catch (Throwable th) {
            g4.endTransaction();
            throw th;
        }
    }

    @Override // p1.d
    public final Iterable<i1.s> n() {
        return (Iterable) k(new Object());
    }

    @Override // p1.d
    public final Iterable o(i1.j jVar) {
        return (Iterable) k(new o1.i(this, jVar));
    }

    @Override // p1.d
    public final long x(i1.s sVar) {
        Long l2;
        Cursor rawQuery = g().rawQuery("SELECT next_request_ms FROM transport_contexts WHERE backend_name = ? and priority = ?", new String[]{sVar.a(), String.valueOf(C0795a.a(sVar.c()))});
        try {
            if (rawQuery.moveToNext()) {
                l2 = Long.valueOf(rawQuery.getLong(0));
            } else {
                l2 = 0L;
            }
            rawQuery.close();
            return l2.longValue();
        } catch (Throwable th) {
            rawQuery.close();
            throw th;
        }
    }
}
