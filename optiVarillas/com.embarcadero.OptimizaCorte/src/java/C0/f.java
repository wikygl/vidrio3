package C0;

import android.app.Notification;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public final int f318a;

    /* renamed from: b  reason: collision with root package name */
    public final int f319b;

    /* renamed from: c  reason: collision with root package name */
    public final Notification f320c;

    public f(int i4, Notification notification, int i5) {
        this.f318a = i4;
        this.f320c = notification;
        this.f319b = i5;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || f.class != obj.getClass()) {
            return false;
        }
        f fVar = (f) obj;
        if (this.f318a != fVar.f318a || this.f319b != fVar.f319b) {
            return false;
        }
        return this.f320c.equals(fVar.f320c);
    }

    public final int hashCode() {
        return this.f320c.hashCode() + (((this.f318a * 31) + this.f319b) * 31);
    }

    public final String toString() {
        return "ForegroundInfo{mNotificationId=" + this.f318a + ", mForegroundServiceType=" + this.f319b + ", mNotification=" + this.f320c + '}';
    }
}
