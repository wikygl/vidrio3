package o1;

import android.app.job.JobParameters;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class e implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ JobInfoSchedulerService f5435j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ JobParameters f5436k;

    public /* synthetic */ e(JobInfoSchedulerService jobInfoSchedulerService, JobParameters jobParameters) {
        this.f5435j = jobInfoSchedulerService;
        this.f5436k = jobParameters;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i4 = JobInfoSchedulerService.j;
        this.f5435j.jobFinished(this.f5436k, false);
    }
}
