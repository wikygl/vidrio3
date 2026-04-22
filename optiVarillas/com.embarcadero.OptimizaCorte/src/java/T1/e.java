package T1;

import V1.InterfaceC0300f;
import W1.AbstractDialogInterface$OnClickListenerC0333v;
import W1.C0324l;
import W1.C0330s;
import W1.C0331t;
import W1.C0332u;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.C;
import androidx.fragment.app.J;
import com.google.errorprone.annotations.RestrictedInheritance;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;
import e0.C0405a;
import g2.InterfaceC0427c;

@RestrictedInheritance(allowedOnPath = ".*java.*/com/google/android/gms.*", allowlistAnnotations = {InterfaceC0427c.class, g2.d.class}, explanation = "Sub classing of GMS Core's APIs are restricted to GMS Core client libs and testing fakes.", link = "go/gmscore-restrictedinheritance")
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class e extends f {

    /* renamed from: c  reason: collision with root package name */
    public static final Object f2351c = new Object();

    /* renamed from: d  reason: collision with root package name */
    public static final e f2352d = new Object();

    public static AlertDialog e(Context context, int i4, AbstractDialogInterface$OnClickListenerC0333v abstractDialogInterface$OnClickListenerC0333v, DialogInterface.OnCancelListener onCancelListener) {
        String string;
        AlertDialog.Builder builder = null;
        if (i4 == 0) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843529, typedValue, true);
        if ("Theme.Dialog.Alert".equals(context.getResources().getResourceEntryName(typedValue.resourceId))) {
            builder = new AlertDialog.Builder(context, 5);
        }
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(C0330s.b(context, i4));
        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }
        Resources resources = context.getResources();
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    string = resources.getString(17039370);
                } else {
                    string = resources.getString(2131820620);
                }
            } else {
                string = resources.getString(2131820630);
            }
        } else {
            string = resources.getString(2131820623);
        }
        if (string != null) {
            builder.setPositiveButton(string, abstractDialogInterface$OnClickListenerC0333v);
        }
        String c4 = C0330s.c(context, i4);
        if (c4 != null) {
            builder.setTitle(c4);
        }
        Log.w("GoogleApiAvailability", C0405a.c("Creating dialog for Google Play services availability issue. ConnectionResult=", i4), new IllegalArgumentException());
        return builder.create();
    }

    /* JADX WARN: Type inference failed for: r2v0, types: [T1.c, android.app.DialogFragment] */
    public static void f(Activity activity, AlertDialog alertDialog, String str, DialogInterface.OnCancelListener onCancelListener) {
        try {
            if (activity instanceof androidx.fragment.app.p) {
                C c4 = ((androidx.fragment.app.p) activity).B.a.n;
                k kVar = new k();
                C0324l.e(alertDialog, "Cannot display null dialog");
                alertDialog.setOnCancelListener(null);
                alertDialog.setOnDismissListener(null);
                kVar.f2359s0 = alertDialog;
                if (onCancelListener != null) {
                    kVar.f2360t0 = onCancelListener;
                }
                ((androidx.fragment.app.j) kVar).p0 = false;
                ((androidx.fragment.app.j) kVar).q0 = true;
                c4.getClass();
                androidx.fragment.app.a aVar = new androidx.fragment.app.a(c4);
                ((J) aVar).o = true;
                aVar.f(0, kVar, str, 1);
                aVar.e(false);
                return;
            }
        } catch (NoClassDefFoundError unused) {
        }
        FragmentManager fragmentManager = activity.getFragmentManager();
        ?? dialogFragment = new DialogFragment();
        C0324l.e(alertDialog, "Cannot display null dialog");
        alertDialog.setOnCancelListener(null);
        alertDialog.setOnDismissListener(null);
        dialogFragment.f2345j = alertDialog;
        if (onCancelListener != null) {
            dialogFragment.f2346k = onCancelListener;
        }
        dialogFragment.show(fragmentManager, str);
    }

    @ResultIgnorabilityUnspecified
    public final void d(Activity activity, int i4, DialogInterface.OnCancelListener onCancelListener) {
        AlertDialog e4 = e(activity, i4, new C0331t(super.b(i4, activity, "d"), activity), onCancelListener);
        if (e4 == null) {
            return;
        }
        f(activity, e4, "GooglePlayServicesErrorDialog", onCancelListener);
    }

    /* JADX WARN: Type inference failed for: r3v6, types: [B.r, B.u, java.lang.Object] */
    @TargetApi(20)
    public final void g(Context context, int i4, PendingIntent pendingIntent) {
        String c4;
        String d4;
        int i5;
        NotificationChannel notificationChannel;
        CharSequence name;
        Log.w("GoogleApiAvailability", I.h.b(i4, "GMS core API Availability. ConnectionResult=", ", tag=null"), new IllegalArgumentException());
        if (i4 == 18) {
            new l(this, context).sendEmptyMessageDelayed(1, 120000L);
        } else if (pendingIntent == null) {
            if (i4 == 6) {
                Log.w("GoogleApiAvailability", "Missing resolution for ConnectionResult.RESOLUTION_REQUIRED. Call GoogleApiAvailability#showErrorNotification(Context, ConnectionResult) instead.");
            }
        } else {
            if (i4 == 6) {
                c4 = C0330s.e(context, "common_google_play_services_resolution_required_title");
            } else {
                c4 = C0330s.c(context, i4);
            }
            if (c4 == null) {
                c4 = context.getResources().getString(2131820627);
            }
            if (i4 != 6 && i4 != 19) {
                d4 = C0330s.b(context, i4);
            } else {
                d4 = C0330s.d(context, "common_google_play_services_resolution_required_text", C0330s.a(context));
            }
            Resources resources = context.getResources();
            Object systemService = context.getSystemService("notification");
            C0324l.d(systemService);
            NotificationManager notificationManager = (NotificationManager) systemService;
            B.s sVar = new B.s(context, null);
            sVar.f265l = true;
            sVar.f269p.flags |= 16;
            sVar.f259e = B.s.b(c4);
            ?? obj = new Object();
            obj.f254b = B.s.b(d4);
            if (sVar.f264k != obj) {
                sVar.f264k = obj;
                obj.d(sVar);
            }
            PackageManager packageManager = context.getPackageManager();
            if (a2.d.f2862a == null) {
                a2.d.f2862a = Boolean.valueOf(packageManager.hasSystemFeature("android.hardware.type.watch"));
            }
            if (a2.d.f2862a.booleanValue()) {
                sVar.f269p.icon = context.getApplicationInfo().icon;
                sVar.f262i = 2;
                if (a2.d.b(context)) {
                    sVar.f256b.add(new B.p(IconCompat.b((Resources) null, "", 2131165323), resources.getString(2131820635), pendingIntent, new Bundle(), null, null));
                } else {
                    sVar.f260g = pendingIntent;
                }
            } else {
                sVar.f269p.icon = 17301642;
                sVar.f269p.tickerText = B.s.b(resources.getString(2131820627));
                sVar.f269p.when = System.currentTimeMillis();
                sVar.f260g = pendingIntent;
                sVar.f = B.s.b(d4);
            }
            if (a2.g.a()) {
                if (a2.g.a()) {
                    synchronized (f2351c) {
                    }
                    notificationChannel = notificationManager.getNotificationChannel("com.google.android.gms.availability");
                    String string = context.getResources().getString(2131820626);
                    if (notificationChannel == null) {
                        notificationManager.createNotificationChannel(B0.f.c(string));
                    } else {
                        name = notificationChannel.getName();
                        if (!string.contentEquals(name)) {
                            notificationChannel.setName(string);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                    }
                    sVar.f267n = "com.google.android.gms.availability";
                } else {
                    throw new IllegalStateException();
                }
            }
            Notification a4 = sVar.a();
            if (i4 != 1 && i4 != 2 && i4 != 3) {
                i5 = 39789;
            } else {
                i.f2356a.set(false);
                i5 = 10436;
            }
            notificationManager.notify(i5, a4);
        }
    }

    @ResultIgnorabilityUnspecified
    public final void h(Activity activity, InterfaceC0300f interfaceC0300f, int i4, DialogInterface.OnCancelListener onCancelListener) {
        AlertDialog e4 = e(activity, i4, new C0332u(super.b(i4, activity, "d"), interfaceC0300f), onCancelListener);
        if (e4 == null) {
            return;
        }
        f(activity, e4, "GooglePlayServicesErrorDialog", onCancelListener);
    }
}
