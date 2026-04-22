package L0;

import L0.p;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;
import m0.C0733i;
import r0.C0779a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class r implements q {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1469a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1470b;

    /* renamed from: c  reason: collision with root package name */
    public final b f1471c;

    /* renamed from: d  reason: collision with root package name */
    public final c f1472d;

    /* renamed from: e  reason: collision with root package name */
    public final d f1473e;
    public final e f;

    /* renamed from: g  reason: collision with root package name */
    public final f f1474g;

    /* renamed from: h  reason: collision with root package name */
    public final g f1475h;

    /* renamed from: i  reason: collision with root package name */
    public final h f1476i;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<p> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR IGNORE INTO `WorkSpec` (`id`,`state`,`worker_class_name`,`input_merger_class_name`,`input`,`output`,`initial_delay`,`interval_duration`,`flex_duration`,`run_attempt_count`,`backoff_policy`,`backoff_delay_duration`,`period_start_time`,`minimum_retention_duration`,`schedule_requested_at`,`run_in_foreground`,`out_of_quota_policy`,`required_network_type`,`requires_charging`,`requires_device_idle`,`requires_battery_not_low`,`requires_storage_not_low`,`trigger_content_update_delay`,`trigger_max_content_delay`,`content_uri_triggers`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:90:0x01cd  */
        /* JADX WARN: Removed duplicated region for block: B:91:0x01d3  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:77:0x01ad -> B:88:0x01c7). Please submit an issue!!! */
        @Override // m0.AbstractC0726b
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void d(r0.e r17, L0.p r18) {
            /*
                Method dump skipped, instructions count: 523
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: L0.r.a.d(r0.e, java.lang.Object):void");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class b extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "DELETE FROM workspec WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class c extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET output=? WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class d extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET period_start_time=? WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class e extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET run_attempt_count=run_attempt_count+1 WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class f extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET run_attempt_count=0 WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class g extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET schedule_requested_at=? WHERE id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class h extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "UPDATE workspec SET schedule_requested_at=-1 WHERE state NOT IN (2, 3, 5)";
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.r$a] */
    /* JADX WARN: Type inference failed for: r0v1, types: [m0.k, L0.r$b] */
    /* JADX WARN: Type inference failed for: r0v2, types: [m0.k, L0.r$c] */
    /* JADX WARN: Type inference failed for: r0v3, types: [m0.k, L0.r$d] */
    /* JADX WARN: Type inference failed for: r0v4, types: [m0.k, L0.r$e] */
    /* JADX WARN: Type inference failed for: r0v5, types: [L0.r$f, m0.k] */
    /* JADX WARN: Type inference failed for: r0v6, types: [L0.r$g, m0.k] */
    /* JADX WARN: Type inference failed for: r0v7, types: [m0.k, L0.r$h] */
    public r(AbstractC0731g abstractC0731g) {
        this.f1469a = abstractC0731g;
        this.f1470b = new AbstractC0735k(abstractC0731g);
        this.f1471c = new AbstractC0735k(abstractC0731g);
        this.f1472d = new AbstractC0735k(abstractC0731g);
        this.f1473e = new AbstractC0735k(abstractC0731g);
        this.f = new AbstractC0735k(abstractC0731g);
        this.f1474g = new AbstractC0735k(abstractC0731g);
        this.f1475h = new AbstractC0735k(abstractC0731g);
        this.f1476i = new AbstractC0735k(abstractC0731g);
        new AtomicBoolean(false);
    }

    public final void a(String str) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        b bVar = this.f1471c;
        r0.e a4 = bVar.a();
        if (str == null) {
            a4.f(1);
        } else {
            a4.g(str, 1);
        }
        abstractC0731g.c();
        try {
            a4.i();
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
            bVar.c(a4);
        }
    }

    public final ArrayList b() {
        C0733i c0733i;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        C0733i b4 = C0733i.b("SELECT `required_network_type`, `requires_charging`, `requires_device_idle`, `requires_battery_not_low`, `requires_storage_not_low`, `trigger_content_update_delay`, `trigger_max_content_delay`, `content_uri_triggers`, `WorkSpec`.`id` AS `id`, `WorkSpec`.`state` AS `state`, `WorkSpec`.`worker_class_name` AS `worker_class_name`, `WorkSpec`.`input_merger_class_name` AS `input_merger_class_name`, `WorkSpec`.`input` AS `input`, `WorkSpec`.`output` AS `output`, `WorkSpec`.`initial_delay` AS `initial_delay`, `WorkSpec`.`interval_duration` AS `interval_duration`, `WorkSpec`.`flex_duration` AS `flex_duration`, `WorkSpec`.`run_attempt_count` AS `run_attempt_count`, `WorkSpec`.`backoff_policy` AS `backoff_policy`, `WorkSpec`.`backoff_delay_duration` AS `backoff_delay_duration`, `WorkSpec`.`period_start_time` AS `period_start_time`, `WorkSpec`.`minimum_retention_duration` AS `minimum_retention_duration`, `WorkSpec`.`schedule_requested_at` AS `schedule_requested_at`, `WorkSpec`.`run_in_foreground` AS `run_in_foreground`, `WorkSpec`.`out_of_quota_policy` AS `out_of_quota_policy` FROM workspec WHERE state=0 ORDER BY period_start_time LIMIT ?", 1);
        b4.f(1, 200);
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            int e4 = B2.a.e(g4, "required_network_type");
            int e5 = B2.a.e(g4, "requires_charging");
            int e6 = B2.a.e(g4, "requires_device_idle");
            int e7 = B2.a.e(g4, "requires_battery_not_low");
            int e8 = B2.a.e(g4, "requires_storage_not_low");
            int e9 = B2.a.e(g4, "trigger_content_update_delay");
            int e10 = B2.a.e(g4, "trigger_max_content_delay");
            int e11 = B2.a.e(g4, "content_uri_triggers");
            int e12 = B2.a.e(g4, "id");
            int e13 = B2.a.e(g4, "state");
            int e14 = B2.a.e(g4, "worker_class_name");
            int e15 = B2.a.e(g4, "input_merger_class_name");
            int e16 = B2.a.e(g4, "input");
            int e17 = B2.a.e(g4, "output");
            c0733i = b4;
            try {
                int e18 = B2.a.e(g4, "initial_delay");
                int e19 = B2.a.e(g4, "interval_duration");
                int e20 = B2.a.e(g4, "flex_duration");
                int e21 = B2.a.e(g4, "run_attempt_count");
                int e22 = B2.a.e(g4, "backoff_policy");
                int e23 = B2.a.e(g4, "backoff_delay_duration");
                int e24 = B2.a.e(g4, "period_start_time");
                int e25 = B2.a.e(g4, "minimum_retention_duration");
                int e26 = B2.a.e(g4, "schedule_requested_at");
                int e27 = B2.a.e(g4, "run_in_foreground");
                int e28 = B2.a.e(g4, "out_of_quota_policy");
                int i4 = e17;
                ArrayList arrayList = new ArrayList(g4.getCount());
                while (g4.moveToNext()) {
                    String string = g4.getString(e12);
                    int i5 = e12;
                    String string2 = g4.getString(e14);
                    int i6 = e14;
                    C0.c cVar = new C0.c();
                    int i7 = e4;
                    cVar.f304a = v.c(g4.getInt(e4));
                    if (g4.getInt(e5) != 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    cVar.f305b = z4;
                    if (g4.getInt(e6) != 0) {
                        z5 = true;
                    } else {
                        z5 = false;
                    }
                    cVar.f306c = z5;
                    if (g4.getInt(e7) != 0) {
                        z6 = true;
                    } else {
                        z6 = false;
                    }
                    cVar.f307d = z6;
                    if (g4.getInt(e8) != 0) {
                        z7 = true;
                    } else {
                        z7 = false;
                    }
                    cVar.f308e = z7;
                    int i8 = e5;
                    int i9 = e6;
                    cVar.f = g4.getLong(e9);
                    cVar.f309g = g4.getLong(e10);
                    cVar.f310h = v.a(g4.getBlob(e11));
                    p pVar = new p(string, string2);
                    pVar.f1451b = v.e(g4.getInt(e13));
                    pVar.f1453d = g4.getString(e15);
                    pVar.f1454e = androidx.work.b.a(g4.getBlob(e16));
                    int i10 = i4;
                    pVar.f = androidx.work.b.a(g4.getBlob(i10));
                    int i11 = e16;
                    int i12 = e18;
                    pVar.f1455g = g4.getLong(i12);
                    int i13 = e7;
                    int i14 = e19;
                    pVar.f1456h = g4.getLong(i14);
                    int i15 = e20;
                    pVar.f1457i = g4.getLong(i15);
                    int i16 = e21;
                    pVar.f1459k = g4.getInt(i16);
                    int i17 = e22;
                    pVar.f1460l = v.b(g4.getInt(i17));
                    int i18 = e23;
                    pVar.f1461m = g4.getLong(i18);
                    int i19 = e24;
                    pVar.f1462n = g4.getLong(i19);
                    int i20 = e25;
                    pVar.f1463o = g4.getLong(i20);
                    int i21 = e26;
                    pVar.f1464p = g4.getLong(i21);
                    int i22 = e27;
                    if (g4.getInt(i22) != 0) {
                        z8 = true;
                    } else {
                        z8 = false;
                    }
                    pVar.f1465q = z8;
                    int i23 = e28;
                    pVar.f1466r = v.d(g4.getInt(i23));
                    pVar.f1458j = cVar;
                    arrayList.add(pVar);
                    i4 = i10;
                    e5 = i8;
                    e18 = i12;
                    e19 = i14;
                    e23 = i18;
                    e24 = i19;
                    e27 = i22;
                    e14 = i6;
                    e4 = i7;
                    e28 = i23;
                    e26 = i21;
                    e16 = i11;
                    e12 = i5;
                    e6 = i9;
                    e25 = i20;
                    e7 = i13;
                    e20 = i15;
                    e21 = i16;
                    e22 = i17;
                }
                g4.close();
                c0733i.k();
                return arrayList;
            } catch (Throwable th) {
                th = th;
                g4.close();
                c0733i.k();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            c0733i = b4;
        }
    }

    public final ArrayList c(int i4) {
        C0733i c0733i;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        C0733i b4 = C0733i.b("SELECT `required_network_type`, `requires_charging`, `requires_device_idle`, `requires_battery_not_low`, `requires_storage_not_low`, `trigger_content_update_delay`, `trigger_max_content_delay`, `content_uri_triggers`, `WorkSpec`.`id` AS `id`, `WorkSpec`.`state` AS `state`, `WorkSpec`.`worker_class_name` AS `worker_class_name`, `WorkSpec`.`input_merger_class_name` AS `input_merger_class_name`, `WorkSpec`.`input` AS `input`, `WorkSpec`.`output` AS `output`, `WorkSpec`.`initial_delay` AS `initial_delay`, `WorkSpec`.`interval_duration` AS `interval_duration`, `WorkSpec`.`flex_duration` AS `flex_duration`, `WorkSpec`.`run_attempt_count` AS `run_attempt_count`, `WorkSpec`.`backoff_policy` AS `backoff_policy`, `WorkSpec`.`backoff_delay_duration` AS `backoff_delay_duration`, `WorkSpec`.`period_start_time` AS `period_start_time`, `WorkSpec`.`minimum_retention_duration` AS `minimum_retention_duration`, `WorkSpec`.`schedule_requested_at` AS `schedule_requested_at`, `WorkSpec`.`run_in_foreground` AS `run_in_foreground`, `WorkSpec`.`out_of_quota_policy` AS `out_of_quota_policy` FROM workspec WHERE state=0 AND schedule_requested_at=-1 ORDER BY period_start_time LIMIT (SELECT MAX(?-COUNT(*), 0) FROM workspec WHERE schedule_requested_at<>-1 AND state NOT IN (2, 3, 5))", 1);
        b4.f(1, i4);
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            int e4 = B2.a.e(g4, "required_network_type");
            int e5 = B2.a.e(g4, "requires_charging");
            int e6 = B2.a.e(g4, "requires_device_idle");
            int e7 = B2.a.e(g4, "requires_battery_not_low");
            int e8 = B2.a.e(g4, "requires_storage_not_low");
            int e9 = B2.a.e(g4, "trigger_content_update_delay");
            int e10 = B2.a.e(g4, "trigger_max_content_delay");
            int e11 = B2.a.e(g4, "content_uri_triggers");
            int e12 = B2.a.e(g4, "id");
            int e13 = B2.a.e(g4, "state");
            int e14 = B2.a.e(g4, "worker_class_name");
            int e15 = B2.a.e(g4, "input_merger_class_name");
            int e16 = B2.a.e(g4, "input");
            int e17 = B2.a.e(g4, "output");
            c0733i = b4;
            try {
                int e18 = B2.a.e(g4, "initial_delay");
                int e19 = B2.a.e(g4, "interval_duration");
                int e20 = B2.a.e(g4, "flex_duration");
                int e21 = B2.a.e(g4, "run_attempt_count");
                int e22 = B2.a.e(g4, "backoff_policy");
                int e23 = B2.a.e(g4, "backoff_delay_duration");
                int e24 = B2.a.e(g4, "period_start_time");
                int e25 = B2.a.e(g4, "minimum_retention_duration");
                int e26 = B2.a.e(g4, "schedule_requested_at");
                int e27 = B2.a.e(g4, "run_in_foreground");
                int e28 = B2.a.e(g4, "out_of_quota_policy");
                int i5 = e17;
                ArrayList arrayList = new ArrayList(g4.getCount());
                while (g4.moveToNext()) {
                    String string = g4.getString(e12);
                    int i6 = e12;
                    String string2 = g4.getString(e14);
                    int i7 = e14;
                    C0.c cVar = new C0.c();
                    int i8 = e4;
                    cVar.f304a = v.c(g4.getInt(e4));
                    if (g4.getInt(e5) != 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    cVar.f305b = z4;
                    if (g4.getInt(e6) != 0) {
                        z5 = true;
                    } else {
                        z5 = false;
                    }
                    cVar.f306c = z5;
                    if (g4.getInt(e7) != 0) {
                        z6 = true;
                    } else {
                        z6 = false;
                    }
                    cVar.f307d = z6;
                    if (g4.getInt(e8) != 0) {
                        z7 = true;
                    } else {
                        z7 = false;
                    }
                    cVar.f308e = z7;
                    int i9 = e5;
                    int i10 = e6;
                    cVar.f = g4.getLong(e9);
                    cVar.f309g = g4.getLong(e10);
                    cVar.f310h = v.a(g4.getBlob(e11));
                    p pVar = new p(string, string2);
                    pVar.f1451b = v.e(g4.getInt(e13));
                    pVar.f1453d = g4.getString(e15);
                    pVar.f1454e = androidx.work.b.a(g4.getBlob(e16));
                    int i11 = i5;
                    pVar.f = androidx.work.b.a(g4.getBlob(i11));
                    int i12 = e18;
                    int i13 = e16;
                    pVar.f1455g = g4.getLong(i12);
                    int i14 = e7;
                    int i15 = e19;
                    pVar.f1456h = g4.getLong(i15);
                    int i16 = e20;
                    pVar.f1457i = g4.getLong(i16);
                    int i17 = e21;
                    pVar.f1459k = g4.getInt(i17);
                    int i18 = e22;
                    pVar.f1460l = v.b(g4.getInt(i18));
                    int i19 = e23;
                    pVar.f1461m = g4.getLong(i19);
                    int i20 = e24;
                    pVar.f1462n = g4.getLong(i20);
                    int i21 = e25;
                    pVar.f1463o = g4.getLong(i21);
                    int i22 = e26;
                    pVar.f1464p = g4.getLong(i22);
                    int i23 = e27;
                    if (g4.getInt(i23) != 0) {
                        z8 = true;
                    } else {
                        z8 = false;
                    }
                    pVar.f1465q = z8;
                    int i24 = e28;
                    pVar.f1466r = v.d(g4.getInt(i24));
                    pVar.f1458j = cVar;
                    arrayList.add(pVar);
                    i5 = i11;
                    e5 = i9;
                    e27 = i23;
                    e12 = i6;
                    e14 = i7;
                    e4 = i8;
                    e28 = i24;
                    e16 = i13;
                    e18 = i12;
                    e19 = i15;
                    e23 = i19;
                    e24 = i20;
                    e26 = i22;
                    e6 = i10;
                    e25 = i21;
                    e7 = i14;
                    e20 = i16;
                    e21 = i17;
                    e22 = i18;
                }
                g4.close();
                c0733i.k();
                return arrayList;
            } catch (Throwable th) {
                th = th;
                g4.close();
                c0733i.k();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            c0733i = b4;
        }
    }

    public final ArrayList d() {
        C0733i c0733i;
        int e4;
        int e5;
        int e6;
        int e7;
        int e8;
        int e9;
        int e10;
        int e11;
        int e12;
        int e13;
        int e14;
        int e15;
        int e16;
        int e17;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        C0733i b4 = C0733i.b("SELECT `required_network_type`, `requires_charging`, `requires_device_idle`, `requires_battery_not_low`, `requires_storage_not_low`, `trigger_content_update_delay`, `trigger_max_content_delay`, `content_uri_triggers`, `WorkSpec`.`id` AS `id`, `WorkSpec`.`state` AS `state`, `WorkSpec`.`worker_class_name` AS `worker_class_name`, `WorkSpec`.`input_merger_class_name` AS `input_merger_class_name`, `WorkSpec`.`input` AS `input`, `WorkSpec`.`output` AS `output`, `WorkSpec`.`initial_delay` AS `initial_delay`, `WorkSpec`.`interval_duration` AS `interval_duration`, `WorkSpec`.`flex_duration` AS `flex_duration`, `WorkSpec`.`run_attempt_count` AS `run_attempt_count`, `WorkSpec`.`backoff_policy` AS `backoff_policy`, `WorkSpec`.`backoff_delay_duration` AS `backoff_delay_duration`, `WorkSpec`.`period_start_time` AS `period_start_time`, `WorkSpec`.`minimum_retention_duration` AS `minimum_retention_duration`, `WorkSpec`.`schedule_requested_at` AS `schedule_requested_at`, `WorkSpec`.`run_in_foreground` AS `run_in_foreground`, `WorkSpec`.`out_of_quota_policy` AS `out_of_quota_policy` FROM workspec WHERE state=1", 0);
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            e4 = B2.a.e(g4, "required_network_type");
            e5 = B2.a.e(g4, "requires_charging");
            e6 = B2.a.e(g4, "requires_device_idle");
            e7 = B2.a.e(g4, "requires_battery_not_low");
            e8 = B2.a.e(g4, "requires_storage_not_low");
            e9 = B2.a.e(g4, "trigger_content_update_delay");
            e10 = B2.a.e(g4, "trigger_max_content_delay");
            e11 = B2.a.e(g4, "content_uri_triggers");
            e12 = B2.a.e(g4, "id");
            e13 = B2.a.e(g4, "state");
            e14 = B2.a.e(g4, "worker_class_name");
            e15 = B2.a.e(g4, "input_merger_class_name");
            e16 = B2.a.e(g4, "input");
            e17 = B2.a.e(g4, "output");
            c0733i = b4;
        } catch (Throwable th) {
            th = th;
            c0733i = b4;
        }
        try {
            int e18 = B2.a.e(g4, "initial_delay");
            int e19 = B2.a.e(g4, "interval_duration");
            int e20 = B2.a.e(g4, "flex_duration");
            int e21 = B2.a.e(g4, "run_attempt_count");
            int e22 = B2.a.e(g4, "backoff_policy");
            int e23 = B2.a.e(g4, "backoff_delay_duration");
            int e24 = B2.a.e(g4, "period_start_time");
            int e25 = B2.a.e(g4, "minimum_retention_duration");
            int e26 = B2.a.e(g4, "schedule_requested_at");
            int e27 = B2.a.e(g4, "run_in_foreground");
            int e28 = B2.a.e(g4, "out_of_quota_policy");
            int i4 = e17;
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                String string = g4.getString(e12);
                int i5 = e12;
                String string2 = g4.getString(e14);
                int i6 = e14;
                C0.c cVar = new C0.c();
                int i7 = e4;
                cVar.f304a = v.c(g4.getInt(e4));
                if (g4.getInt(e5) != 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                cVar.f305b = z4;
                if (g4.getInt(e6) != 0) {
                    z5 = true;
                } else {
                    z5 = false;
                }
                cVar.f306c = z5;
                if (g4.getInt(e7) != 0) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                cVar.f307d = z6;
                if (g4.getInt(e8) != 0) {
                    z7 = true;
                } else {
                    z7 = false;
                }
                cVar.f308e = z7;
                int i8 = e5;
                int i9 = e6;
                cVar.f = g4.getLong(e9);
                cVar.f309g = g4.getLong(e10);
                cVar.f310h = v.a(g4.getBlob(e11));
                p pVar = new p(string, string2);
                pVar.f1451b = v.e(g4.getInt(e13));
                pVar.f1453d = g4.getString(e15);
                pVar.f1454e = androidx.work.b.a(g4.getBlob(e16));
                int i10 = i4;
                pVar.f = androidx.work.b.a(g4.getBlob(i10));
                int i11 = e16;
                int i12 = e18;
                pVar.f1455g = g4.getLong(i12);
                int i13 = e7;
                int i14 = e19;
                pVar.f1456h = g4.getLong(i14);
                int i15 = e20;
                pVar.f1457i = g4.getLong(i15);
                int i16 = e21;
                pVar.f1459k = g4.getInt(i16);
                int i17 = e22;
                pVar.f1460l = v.b(g4.getInt(i17));
                int i18 = e23;
                pVar.f1461m = g4.getLong(i18);
                int i19 = e24;
                pVar.f1462n = g4.getLong(i19);
                int i20 = e25;
                pVar.f1463o = g4.getLong(i20);
                int i21 = e26;
                pVar.f1464p = g4.getLong(i21);
                int i22 = e27;
                if (g4.getInt(i22) != 0) {
                    z8 = true;
                } else {
                    z8 = false;
                }
                pVar.f1465q = z8;
                int i23 = e28;
                pVar.f1466r = v.d(g4.getInt(i23));
                pVar.f1458j = cVar;
                arrayList.add(pVar);
                i4 = i10;
                e5 = i8;
                e18 = i12;
                e19 = i14;
                e23 = i18;
                e24 = i19;
                e27 = i22;
                e14 = i6;
                e4 = i7;
                e28 = i23;
                e26 = i21;
                e16 = i11;
                e12 = i5;
                e6 = i9;
                e25 = i20;
                e7 = i13;
                e20 = i15;
                e21 = i16;
                e22 = i17;
            }
            g4.close();
            c0733i.k();
            return arrayList;
        } catch (Throwable th2) {
            th = th2;
            g4.close();
            c0733i.k();
            throw th;
        }
    }

    public final ArrayList e() {
        C0733i c0733i;
        int e4;
        int e5;
        int e6;
        int e7;
        int e8;
        int e9;
        int e10;
        int e11;
        int e12;
        int e13;
        int e14;
        int e15;
        int e16;
        int e17;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        C0733i b4 = C0733i.b("SELECT `required_network_type`, `requires_charging`, `requires_device_idle`, `requires_battery_not_low`, `requires_storage_not_low`, `trigger_content_update_delay`, `trigger_max_content_delay`, `content_uri_triggers`, `WorkSpec`.`id` AS `id`, `WorkSpec`.`state` AS `state`, `WorkSpec`.`worker_class_name` AS `worker_class_name`, `WorkSpec`.`input_merger_class_name` AS `input_merger_class_name`, `WorkSpec`.`input` AS `input`, `WorkSpec`.`output` AS `output`, `WorkSpec`.`initial_delay` AS `initial_delay`, `WorkSpec`.`interval_duration` AS `interval_duration`, `WorkSpec`.`flex_duration` AS `flex_duration`, `WorkSpec`.`run_attempt_count` AS `run_attempt_count`, `WorkSpec`.`backoff_policy` AS `backoff_policy`, `WorkSpec`.`backoff_delay_duration` AS `backoff_delay_duration`, `WorkSpec`.`period_start_time` AS `period_start_time`, `WorkSpec`.`minimum_retention_duration` AS `minimum_retention_duration`, `WorkSpec`.`schedule_requested_at` AS `schedule_requested_at`, `WorkSpec`.`run_in_foreground` AS `run_in_foreground`, `WorkSpec`.`out_of_quota_policy` AS `out_of_quota_policy` FROM workspec WHERE state=0 AND schedule_requested_at<>-1", 0);
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            e4 = B2.a.e(g4, "required_network_type");
            e5 = B2.a.e(g4, "requires_charging");
            e6 = B2.a.e(g4, "requires_device_idle");
            e7 = B2.a.e(g4, "requires_battery_not_low");
            e8 = B2.a.e(g4, "requires_storage_not_low");
            e9 = B2.a.e(g4, "trigger_content_update_delay");
            e10 = B2.a.e(g4, "trigger_max_content_delay");
            e11 = B2.a.e(g4, "content_uri_triggers");
            e12 = B2.a.e(g4, "id");
            e13 = B2.a.e(g4, "state");
            e14 = B2.a.e(g4, "worker_class_name");
            e15 = B2.a.e(g4, "input_merger_class_name");
            e16 = B2.a.e(g4, "input");
            e17 = B2.a.e(g4, "output");
            c0733i = b4;
        } catch (Throwable th) {
            th = th;
            c0733i = b4;
        }
        try {
            int e18 = B2.a.e(g4, "initial_delay");
            int e19 = B2.a.e(g4, "interval_duration");
            int e20 = B2.a.e(g4, "flex_duration");
            int e21 = B2.a.e(g4, "run_attempt_count");
            int e22 = B2.a.e(g4, "backoff_policy");
            int e23 = B2.a.e(g4, "backoff_delay_duration");
            int e24 = B2.a.e(g4, "period_start_time");
            int e25 = B2.a.e(g4, "minimum_retention_duration");
            int e26 = B2.a.e(g4, "schedule_requested_at");
            int e27 = B2.a.e(g4, "run_in_foreground");
            int e28 = B2.a.e(g4, "out_of_quota_policy");
            int i4 = e17;
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                String string = g4.getString(e12);
                int i5 = e12;
                String string2 = g4.getString(e14);
                int i6 = e14;
                C0.c cVar = new C0.c();
                int i7 = e4;
                cVar.f304a = v.c(g4.getInt(e4));
                if (g4.getInt(e5) != 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                cVar.f305b = z4;
                if (g4.getInt(e6) != 0) {
                    z5 = true;
                } else {
                    z5 = false;
                }
                cVar.f306c = z5;
                if (g4.getInt(e7) != 0) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                cVar.f307d = z6;
                if (g4.getInt(e8) != 0) {
                    z7 = true;
                } else {
                    z7 = false;
                }
                cVar.f308e = z7;
                int i8 = e5;
                int i9 = e6;
                cVar.f = g4.getLong(e9);
                cVar.f309g = g4.getLong(e10);
                cVar.f310h = v.a(g4.getBlob(e11));
                p pVar = new p(string, string2);
                pVar.f1451b = v.e(g4.getInt(e13));
                pVar.f1453d = g4.getString(e15);
                pVar.f1454e = androidx.work.b.a(g4.getBlob(e16));
                int i10 = i4;
                pVar.f = androidx.work.b.a(g4.getBlob(i10));
                int i11 = e16;
                int i12 = e18;
                pVar.f1455g = g4.getLong(i12);
                int i13 = e7;
                int i14 = e19;
                pVar.f1456h = g4.getLong(i14);
                int i15 = e20;
                pVar.f1457i = g4.getLong(i15);
                int i16 = e21;
                pVar.f1459k = g4.getInt(i16);
                int i17 = e22;
                pVar.f1460l = v.b(g4.getInt(i17));
                int i18 = e23;
                pVar.f1461m = g4.getLong(i18);
                int i19 = e24;
                pVar.f1462n = g4.getLong(i19);
                int i20 = e25;
                pVar.f1463o = g4.getLong(i20);
                int i21 = e26;
                pVar.f1464p = g4.getLong(i21);
                int i22 = e27;
                if (g4.getInt(i22) != 0) {
                    z8 = true;
                } else {
                    z8 = false;
                }
                pVar.f1465q = z8;
                int i23 = e28;
                pVar.f1466r = v.d(g4.getInt(i23));
                pVar.f1458j = cVar;
                arrayList.add(pVar);
                i4 = i10;
                e5 = i8;
                e18 = i12;
                e19 = i14;
                e23 = i18;
                e24 = i19;
                e27 = i22;
                e14 = i6;
                e4 = i7;
                e28 = i23;
                e26 = i21;
                e16 = i11;
                e12 = i5;
                e6 = i9;
                e25 = i20;
                e7 = i13;
                e20 = i15;
                e21 = i16;
                e22 = i17;
            }
            g4.close();
            c0733i.k();
            return arrayList;
        } catch (Throwable th2) {
            th = th2;
            g4.close();
            c0733i.k();
            throw th;
        }
    }

    public final C0.o f(String str) {
        C0.o oVar;
        C0733i b4 = C0733i.b("SELECT state FROM workspec WHERE id=?", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            if (g4.moveToFirst()) {
                oVar = v.e(g4.getInt(0));
            } else {
                oVar = null;
            }
            return oVar;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final ArrayList g(String str) {
        C0733i b4 = C0733i.b("SELECT id FROM workspec WHERE state NOT IN (2, 3, 5) AND id IN (SELECT work_spec_id FROM workname WHERE name=?)", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                arrayList.add(g4.getString(0));
            }
            return arrayList;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final ArrayList h(String str) {
        C0733i b4 = C0733i.b("SELECT id FROM workspec WHERE state NOT IN (2, 3, 5) AND id IN (SELECT work_spec_id FROM worktag WHERE tag=?)", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                arrayList.add(g4.getString(0));
            }
            return arrayList;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final p i(String str) {
        C0733i c0733i;
        int e4;
        int e5;
        int e6;
        int e7;
        int e8;
        int e9;
        int e10;
        int e11;
        int e12;
        int e13;
        int e14;
        int e15;
        int e16;
        int e17;
        p pVar;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        C0733i b4 = C0733i.b("SELECT `required_network_type`, `requires_charging`, `requires_device_idle`, `requires_battery_not_low`, `requires_storage_not_low`, `trigger_content_update_delay`, `trigger_max_content_delay`, `content_uri_triggers`, `WorkSpec`.`id` AS `id`, `WorkSpec`.`state` AS `state`, `WorkSpec`.`worker_class_name` AS `worker_class_name`, `WorkSpec`.`input_merger_class_name` AS `input_merger_class_name`, `WorkSpec`.`input` AS `input`, `WorkSpec`.`output` AS `output`, `WorkSpec`.`initial_delay` AS `initial_delay`, `WorkSpec`.`interval_duration` AS `interval_duration`, `WorkSpec`.`flex_duration` AS `flex_duration`, `WorkSpec`.`run_attempt_count` AS `run_attempt_count`, `WorkSpec`.`backoff_policy` AS `backoff_policy`, `WorkSpec`.`backoff_delay_duration` AS `backoff_delay_duration`, `WorkSpec`.`period_start_time` AS `period_start_time`, `WorkSpec`.`minimum_retention_duration` AS `minimum_retention_duration`, `WorkSpec`.`schedule_requested_at` AS `schedule_requested_at`, `WorkSpec`.`run_in_foreground` AS `run_in_foreground`, `WorkSpec`.`out_of_quota_policy` AS `out_of_quota_policy` FROM workspec WHERE id=?", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            e4 = B2.a.e(g4, "required_network_type");
            e5 = B2.a.e(g4, "requires_charging");
            e6 = B2.a.e(g4, "requires_device_idle");
            e7 = B2.a.e(g4, "requires_battery_not_low");
            e8 = B2.a.e(g4, "requires_storage_not_low");
            e9 = B2.a.e(g4, "trigger_content_update_delay");
            e10 = B2.a.e(g4, "trigger_max_content_delay");
            e11 = B2.a.e(g4, "content_uri_triggers");
            e12 = B2.a.e(g4, "id");
            e13 = B2.a.e(g4, "state");
            e14 = B2.a.e(g4, "worker_class_name");
            e15 = B2.a.e(g4, "input_merger_class_name");
            e16 = B2.a.e(g4, "input");
            e17 = B2.a.e(g4, "output");
            c0733i = b4;
        } catch (Throwable th) {
            th = th;
            c0733i = b4;
        }
        try {
            int e18 = B2.a.e(g4, "initial_delay");
            int e19 = B2.a.e(g4, "interval_duration");
            int e20 = B2.a.e(g4, "flex_duration");
            int e21 = B2.a.e(g4, "run_attempt_count");
            int e22 = B2.a.e(g4, "backoff_policy");
            int e23 = B2.a.e(g4, "backoff_delay_duration");
            int e24 = B2.a.e(g4, "period_start_time");
            int e25 = B2.a.e(g4, "minimum_retention_duration");
            int e26 = B2.a.e(g4, "schedule_requested_at");
            int e27 = B2.a.e(g4, "run_in_foreground");
            int e28 = B2.a.e(g4, "out_of_quota_policy");
            if (g4.moveToFirst()) {
                String string = g4.getString(e12);
                String string2 = g4.getString(e14);
                C0.c cVar = new C0.c();
                cVar.f304a = v.c(g4.getInt(e4));
                if (g4.getInt(e5) != 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                cVar.f305b = z4;
                if (g4.getInt(e6) != 0) {
                    z5 = true;
                } else {
                    z5 = false;
                }
                cVar.f306c = z5;
                if (g4.getInt(e7) != 0) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                cVar.f307d = z6;
                if (g4.getInt(e8) != 0) {
                    z7 = true;
                } else {
                    z7 = false;
                }
                cVar.f308e = z7;
                cVar.f = g4.getLong(e9);
                cVar.f309g = g4.getLong(e10);
                cVar.f310h = v.a(g4.getBlob(e11));
                pVar = new p(string, string2);
                pVar.f1451b = v.e(g4.getInt(e13));
                pVar.f1453d = g4.getString(e15);
                pVar.f1454e = androidx.work.b.a(g4.getBlob(e16));
                pVar.f = androidx.work.b.a(g4.getBlob(e17));
                pVar.f1455g = g4.getLong(e18);
                pVar.f1456h = g4.getLong(e19);
                pVar.f1457i = g4.getLong(e20);
                pVar.f1459k = g4.getInt(e21);
                pVar.f1460l = v.b(g4.getInt(e22));
                pVar.f1461m = g4.getLong(e23);
                pVar.f1462n = g4.getLong(e24);
                pVar.f1463o = g4.getLong(e25);
                pVar.f1464p = g4.getLong(e26);
                if (g4.getInt(e27) != 0) {
                    z8 = true;
                } else {
                    z8 = false;
                }
                pVar.f1465q = z8;
                pVar.f1466r = v.d(g4.getInt(e28));
                pVar.f1458j = cVar;
            } else {
                pVar = null;
            }
            g4.close();
            c0733i.k();
            return pVar;
        } catch (Throwable th2) {
            th = th2;
            g4.close();
            c0733i.k();
            throw th;
        }
    }

    public final ArrayList j() {
        C0733i b4 = C0733i.b("SELECT id, state FROM workspec WHERE id IN (SELECT work_spec_id FROM workname WHERE name=?)", 1);
        b4.g(1);
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            int e4 = B2.a.e(g4, "id");
            int e5 = B2.a.e(g4, "state");
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                p.a aVar = new p.a();
                aVar.f1467a = g4.getString(e4);
                aVar.f1468b = v.e(g4.getInt(e5));
                arrayList.add(aVar);
            }
            return arrayList;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final int k(String str) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        e eVar = this.f;
        r0.e a4 = eVar.a();
        if (str == null) {
            a4.f(1);
        } else {
            a4.g(str, 1);
        }
        abstractC0731g.c();
        try {
            int executeUpdateDelete = a4.f5706k.executeUpdateDelete();
            abstractC0731g.h();
            return executeUpdateDelete;
        } finally {
            abstractC0731g.f();
            eVar.c(a4);
        }
    }

    public final int l(String str, long j4) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        g gVar = this.f1475h;
        r0.e a4 = gVar.a();
        a4.d(1, j4);
        if (str == null) {
            a4.f(2);
        } else {
            a4.g(str, 2);
        }
        abstractC0731g.c();
        try {
            int executeUpdateDelete = a4.f5706k.executeUpdateDelete();
            abstractC0731g.h();
            return executeUpdateDelete;
        } finally {
            abstractC0731g.f();
            gVar.c(a4);
        }
    }

    public final int m(String str) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        f fVar = this.f1474g;
        r0.e a4 = fVar.a();
        if (str == null) {
            a4.f(1);
        } else {
            a4.g(str, 1);
        }
        abstractC0731g.c();
        try {
            int executeUpdateDelete = a4.f5706k.executeUpdateDelete();
            abstractC0731g.h();
            return executeUpdateDelete;
        } finally {
            abstractC0731g.f();
            fVar.c(a4);
        }
    }

    public final void n(String str, androidx.work.b bVar) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        c cVar = this.f1472d;
        r0.e a4 = cVar.a();
        byte[] c4 = androidx.work.b.c(bVar);
        if (c4 == null) {
            a4.f(1);
        } else {
            a4.a(1, c4);
        }
        if (str == null) {
            a4.f(2);
        } else {
            a4.g(str, 2);
        }
        abstractC0731g.c();
        try {
            a4.i();
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
            cVar.c(a4);
        }
    }

    public final void o(String str, long j4) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        d dVar = this.f1473e;
        r0.e a4 = dVar.a();
        a4.d(1, j4);
        if (str == null) {
            a4.f(2);
        } else {
            a4.g(str, 2);
        }
        abstractC0731g.c();
        try {
            a4.i();
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
            dVar.c(a4);
        }
    }

    public final int p(C0.o oVar, String... strArr) {
        AbstractC0731g abstractC0731g = this.f1469a;
        abstractC0731g.b();
        StringBuilder sb = new StringBuilder("UPDATE workspec SET state=? WHERE id IN (");
        int length = strArr.length;
        for (int i4 = 0; i4 < length; i4++) {
            sb.append("?");
            if (i4 < length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        String sb2 = sb.toString();
        abstractC0731g.a();
        abstractC0731g.b();
        SQLiteStatement compileStatement = ((C0779a) abstractC0731g.f5305c.D()).f5691j.compileStatement(sb2);
        compileStatement.bindLong(1, v.f(oVar));
        int i5 = 2;
        for (String str : strArr) {
            if (str == null) {
                compileStatement.bindNull(i5);
            } else {
                compileStatement.bindString(i5, str);
            }
            i5++;
        }
        abstractC0731g.c();
        try {
            int executeUpdateDelete = compileStatement.executeUpdateDelete();
            abstractC0731g.h();
            return executeUpdateDelete;
        } finally {
            abstractC0731g.f();
        }
    }
}
