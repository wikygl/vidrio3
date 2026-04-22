package o1;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.Adler32;
import m1.C0736a;
import o1.f;
import s1.C0795a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class d implements t {

    /* renamed from: a  reason: collision with root package name */
    public final Context f5432a;

    /* renamed from: b  reason: collision with root package name */
    public final p1.d f5433b;

    /* renamed from: c  reason: collision with root package name */
    public final f f5434c;

    public d(Context context, p1.d dVar, f fVar) {
        this.f5432a = context;
        this.f5433b = dVar;
        this.f5434c = fVar;
    }

    @Override // o1.t
    public final void a(i1.s sVar, int i4, boolean z4) {
        Context context = this.f5432a;
        ComponentName componentName = new ComponentName(context, JobInfoSchedulerService.class);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        Adler32 adler32 = new Adler32();
        adler32.update(context.getPackageName().getBytes(Charset.forName("UTF-8")));
        adler32.update(sVar.a().getBytes(Charset.forName("UTF-8")));
        adler32.update(ByteBuffer.allocate(4).putInt(C0795a.a(sVar.c())).array());
        if (sVar.b() != null) {
            adler32.update(sVar.b());
        }
        int value = (int) adler32.getValue();
        if (!z4) {
            Iterator<JobInfo> it = jobScheduler.getAllPendingJobs().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                JobInfo next = it.next();
                int i5 = next.getExtras().getInt("attemptNumber");
                if (next.getId() == value) {
                    if (i5 >= i4) {
                        C0736a.a(sVar, "JobInfoScheduler", "Upload for context %s is already scheduled. Returning...");
                        return;
                    }
                }
            }
        }
        long x4 = this.f5433b.x(sVar);
        JobInfo.Builder builder = new JobInfo.Builder(value, componentName);
        f1.d c4 = sVar.c();
        f fVar = this.f5434c;
        builder.setMinimumLatency(fVar.b(c4, x4, i4));
        Set<f.b> b4 = fVar.c().get(c4).b();
        if (b4.contains(f.b.f5437j)) {
            builder.setRequiredNetworkType(2);
        } else {
            builder.setRequiredNetworkType(1);
        }
        if (b4.contains(f.b.f5439l)) {
            builder.setRequiresCharging(true);
        }
        if (b4.contains(f.b.f5438k)) {
            builder.setRequiresDeviceIdle(true);
        }
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putInt("attemptNumber", i4);
        persistableBundle.putString("backendName", sVar.a());
        persistableBundle.putInt("priority", C0795a.a(sVar.c()));
        if (sVar.b() != null) {
            persistableBundle.putString("extras", Base64.encodeToString(sVar.b(), 0));
        }
        builder.setExtras(persistableBundle);
        Object[] objArr = {sVar, Integer.valueOf(value), Long.valueOf(fVar.b(sVar.c(), x4, i4)), Long.valueOf(x4), Integer.valueOf(i4)};
        String c5 = C0736a.c("JobInfoScheduler");
        if (Log.isLoggable(c5, 3)) {
            Log.d(c5, String.format("Scheduling upload for context %s with jobId=%d in %dms(Backend next call timestamp %d). Attempt %d", objArr));
        }
        jobScheduler.schedule(builder.build());
    }

    @Override // o1.t
    public final void b(i1.s sVar, int i4) {
        a(sVar, i4, false);
    }
}
