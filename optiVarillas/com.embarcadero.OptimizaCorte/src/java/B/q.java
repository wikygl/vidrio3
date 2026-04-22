package B;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.core.graphics.drawable.IconCompat;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class q extends u {

    /* renamed from: b  reason: collision with root package name */
    public IconCompat f251b;

    /* renamed from: c  reason: collision with root package name */
    public IconCompat f252c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f253d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static void a(Notification.BigPictureStyle bigPictureStyle, Icon icon) {
            bigPictureStyle.bigLargeIcon(icon);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static void a(Notification.BigPictureStyle bigPictureStyle, Icon icon) {
            bigPictureStyle.bigPicture(icon);
        }

        public static void b(Notification.BigPictureStyle bigPictureStyle, CharSequence charSequence) {
            bigPictureStyle.setContentDescription(charSequence);
        }

        public static void c(Notification.BigPictureStyle bigPictureStyle, boolean z4) {
            bigPictureStyle.showBigPictureWhenCollapsed(z4);
        }
    }

    @Override // B.u
    public final void b(v vVar) {
        Notification.BigPictureStyle bigContentTitle = new Notification.BigPictureStyle(vVar.f273b).setBigContentTitle(null);
        IconCompat iconCompat = this.f251b;
        Context context = vVar.f272a;
        if (iconCompat != null) {
            if (Build.VERSION.SDK_INT >= 31) {
                b.a(bigContentTitle, iconCompat.g(context));
            } else if (iconCompat.e() == 1) {
                bigContentTitle = bigContentTitle.bigPicture(this.f251b.c());
            }
        }
        if (this.f253d) {
            IconCompat iconCompat2 = this.f252c;
            if (iconCompat2 == null) {
                bigContentTitle.bigLargeIcon((Bitmap) null);
            } else if (Build.VERSION.SDK_INT >= 23) {
                a.a(bigContentTitle, iconCompat2.g(context));
            } else if (iconCompat2.e() == 1) {
                bigContentTitle.bigLargeIcon(this.f252c.c());
            } else {
                bigContentTitle.bigLargeIcon((Bitmap) null);
            }
        }
        if (Build.VERSION.SDK_INT >= 31) {
            b.c(bigContentTitle, false);
            b.b(bigContentTitle, null);
        }
    }

    @Override // B.u
    public final String c() {
        return "androidx.core.app.NotificationCompat$BigPictureStyle";
    }
}
