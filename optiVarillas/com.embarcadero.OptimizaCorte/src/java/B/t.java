package B;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class t extends u {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static Notification.Builder a(Notification.Builder builder, String str) {
            return builder.addPerson(str);
        }

        public static Notification.Builder b(Notification.Builder builder, String str) {
            return builder.setCategory(str);
        }
    }

    @Override // B.u
    public final void a(Bundle bundle) {
        super.a(bundle);
        bundle.putInt("android.callType", 0);
        bundle.putBoolean("android.callIsVideo", false);
        bundle.putCharSequence("android.verificationText", null);
        bundle.putParcelable("android.answerIntent", null);
        bundle.putParcelable("android.declineIntent", null);
        bundle.putParcelable("android.hangUpIntent", null);
    }

    @Override // B.u
    public final void b(v vVar) {
        CharSequence charSequence;
        if (Build.VERSION.SDK_INT >= 31) {
            if (Log.isLoggable("NotifCompat", 3)) {
                Log.d("NotifCompat", "Unrecognized call type in CallStyle: " + String.valueOf(0));
                return;
            }
            return;
        }
        Notification.Builder builder = vVar.f273b;
        CharSequence charSequence2 = null;
        builder.setContentTitle(null);
        Bundle bundle = this.f271a.f266m;
        if (bundle != null && bundle.containsKey("android.text")) {
            charSequence = this.f271a.f266m.getCharSequence("android.text");
        } else {
            charSequence = null;
        }
        if (charSequence != null) {
            charSequence2 = charSequence;
        }
        builder.setContentText(charSequence2);
        a.b(builder, "call");
    }

    @Override // B.u
    public final String c() {
        return "androidx.core.app.NotificationCompat$CallStyle";
    }
}
