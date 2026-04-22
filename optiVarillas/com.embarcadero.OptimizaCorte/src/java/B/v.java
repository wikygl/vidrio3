package B;

import B.B;
import B.z;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.content.Context;
import android.content.LocusId;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;
import androidx.core.graphics.drawable.IconCompat;
import java.util.ArrayList;
import java.util.Iterator;
import r.C0775d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class v {

    /* renamed from: a  reason: collision with root package name */
    public final Context f272a;

    /* renamed from: b  reason: collision with root package name */
    public final Notification.Builder f273b;

    /* renamed from: c  reason: collision with root package name */
    public final s f274c;

    /* renamed from: d  reason: collision with root package name */
    public final Bundle f275d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static Notification.Builder a(Notification.Builder builder, Notification.Action action) {
            return builder.addAction(action);
        }

        public static Notification.Action.Builder b(Notification.Action.Builder builder, Bundle bundle) {
            return builder.addExtras(bundle);
        }

        public static Notification.Action.Builder c(Notification.Action.Builder builder, RemoteInput remoteInput) {
            return builder.addRemoteInput(remoteInput);
        }

        public static Notification.Action d(Notification.Action.Builder builder) {
            return builder.build();
        }

        public static Notification.Action.Builder e(int i4, CharSequence charSequence, PendingIntent pendingIntent) {
            return new Notification.Action.Builder(i4, charSequence, pendingIntent);
        }

        public static String f(Notification notification) {
            return notification.getGroup();
        }

        public static Notification.Builder g(Notification.Builder builder, String str) {
            return builder.setGroup(str);
        }

        public static Notification.Builder h(Notification.Builder builder, boolean z4) {
            return builder.setGroupSummary(z4);
        }

        public static Notification.Builder i(Notification.Builder builder, boolean z4) {
            return builder.setLocalOnly(z4);
        }

        public static Notification.Builder j(Notification.Builder builder, String str) {
            return builder.setSortKey(str);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static Notification.Builder a(Notification.Builder builder, String str) {
            return builder.addPerson(str);
        }

        public static Notification.Builder b(Notification.Builder builder, String str) {
            return builder.setCategory(str);
        }

        public static Notification.Builder c(Notification.Builder builder, int i4) {
            return builder.setColor(i4);
        }

        public static Notification.Builder d(Notification.Builder builder, Notification notification) {
            return builder.setPublicVersion(notification);
        }

        public static Notification.Builder e(Notification.Builder builder, Uri uri, Object obj) {
            return builder.setSound(uri, (AudioAttributes) obj);
        }

        public static Notification.Builder f(Notification.Builder builder, int i4) {
            return builder.setVisibility(i4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class c {
        public static Notification.Action.Builder a(Icon icon, CharSequence charSequence, PendingIntent pendingIntent) {
            return new Notification.Action.Builder(icon, charSequence, pendingIntent);
        }

        public static Notification.Builder b(Notification.Builder builder, Icon icon) {
            return builder.setLargeIcon(icon);
        }

        public static Notification.Builder c(Notification.Builder builder, Object obj) {
            return builder.setSmallIcon((Icon) obj);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class d {
        public static Notification.Action.Builder a(Notification.Action.Builder builder, boolean z4) {
            return builder.setAllowGeneratedReplies(z4);
        }

        public static Notification.Builder b(Notification.Builder builder, RemoteViews remoteViews) {
            return builder.setCustomBigContentView(remoteViews);
        }

        public static Notification.Builder c(Notification.Builder builder, RemoteViews remoteViews) {
            return builder.setCustomContentView(remoteViews);
        }

        public static Notification.Builder d(Notification.Builder builder, RemoteViews remoteViews) {
            return builder.setCustomHeadsUpContentView(remoteViews);
        }

        public static Notification.Builder e(Notification.Builder builder, CharSequence[] charSequenceArr) {
            return builder.setRemoteInputHistory(charSequenceArr);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class e {
        public static Notification.Builder a(Context context, String str) {
            return new Notification.Builder(context, str);
        }

        public static Notification.Builder b(Notification.Builder builder, int i4) {
            return builder.setBadgeIconType(i4);
        }

        public static Notification.Builder c(Notification.Builder builder, boolean z4) {
            return builder.setColorized(z4);
        }

        public static Notification.Builder d(Notification.Builder builder, int i4) {
            return builder.setGroupAlertBehavior(i4);
        }

        public static Notification.Builder e(Notification.Builder builder, CharSequence charSequence) {
            return builder.setSettingsText(charSequence);
        }

        public static Notification.Builder f(Notification.Builder builder, String str) {
            return builder.setShortcutId(str);
        }

        public static Notification.Builder g(Notification.Builder builder, long j4) {
            return builder.setTimeoutAfter(j4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class f {
        public static Notification.Builder a(Notification.Builder builder, Person person) {
            return builder.addPerson(person);
        }

        public static Notification.Action.Builder b(Notification.Action.Builder builder, int i4) {
            return builder.setSemanticAction(i4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class g {
        public static Notification.Builder a(Notification.Builder builder, boolean z4) {
            return builder.setAllowSystemGeneratedContextualActions(z4);
        }

        public static Notification.Builder b(Notification.Builder builder, Notification.BubbleMetadata bubbleMetadata) {
            return builder.setBubbleMetadata(bubbleMetadata);
        }

        public static Notification.Action.Builder c(Notification.Action.Builder builder, boolean z4) {
            return builder.setContextual(z4);
        }

        public static Notification.Builder d(Notification.Builder builder, Object obj) {
            return builder.setLocusId((LocusId) obj);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class h {
        public static Notification.Action.Builder a(Notification.Action.Builder builder, boolean z4) {
            return builder.setAuthenticationRequired(z4);
        }

        public static Notification.Builder b(Notification.Builder builder, int i4) {
            return builder.setForegroundServiceBehavior(i4);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v21, types: [java.lang.CharSequence, android.net.Uri, long[], java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v24 */
    public v(s sVar) {
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        Icon g4;
        ArrayList<z> arrayList;
        ?? r4;
        int i4;
        Bundle bundle;
        Bundle[] bundleArr;
        ArrayList<p> arrayList2;
        String str;
        ArrayList<z> arrayList3;
        int i5;
        ArrayList<String> arrayList4;
        CharSequence charSequence;
        B[] bArr;
        B[] bArr2;
        Bitmap c4;
        v vVar = this;
        new ArrayList();
        vVar.f275d = new Bundle();
        vVar.f274c = sVar;
        Context context = sVar.f255a;
        vVar.f272a = context;
        int i6 = Build.VERSION.SDK_INT;
        if (i6 >= 26) {
            vVar.f273b = e.a(context, sVar.f267n);
        } else {
            vVar.f273b = new Notification.Builder(sVar.f255a);
        }
        Notification notification = sVar.f269p;
        Resources resources = null;
        Notification.Builder lights = vVar.f273b.setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, null).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
        int i7 = 2;
        if ((notification.flags & 2) != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        Notification.Builder ongoing = lights.setOngoing(z4);
        if ((notification.flags & 8) != 0) {
            z5 = true;
        } else {
            z5 = false;
        }
        Notification.Builder onlyAlertOnce = ongoing.setOnlyAlertOnce(z5);
        if ((notification.flags & 16) != 0) {
            z6 = true;
        } else {
            z6 = false;
        }
        Notification.Builder deleteIntent = onlyAlertOnce.setAutoCancel(z6).setDefaults(notification.defaults).setContentTitle(sVar.f259e).setContentText(sVar.f).setContentInfo(null).setContentIntent(sVar.f260g).setDeleteIntent(notification.deleteIntent);
        if ((notification.flags & 128) != 0) {
            z7 = true;
        } else {
            z7 = false;
        }
        deleteIntent.setFullScreenIntent(null, z7).setNumber(0).setProgress(0, 0, false);
        if (i6 < 23) {
            Notification.Builder builder = vVar.f273b;
            IconCompat iconCompat = sVar.f261h;
            if (iconCompat == null) {
                c4 = null;
            } else {
                c4 = iconCompat.c();
            }
            builder.setLargeIcon(c4);
        } else {
            Notification.Builder builder2 = vVar.f273b;
            IconCompat iconCompat2 = sVar.f261h;
            if (iconCompat2 == null) {
                g4 = null;
            } else {
                g4 = iconCompat2.g(context);
            }
            c.b(builder2, g4);
        }
        vVar.f273b.setSubText(null).setUsesChronometer(false).setPriority(sVar.f262i);
        u uVar = sVar.f264k;
        if (uVar instanceof t) {
            t tVar = (t) uVar;
            int b4 = C.a.b(tVar.f271a.f255a, 2131034162);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) tVar.f271a.f255a.getResources().getString(2131820602));
            spannableStringBuilder.setSpan(new ForegroundColorSpan(b4), 0, spannableStringBuilder.length(), 18);
            Context context2 = tVar.f271a.f255a;
            PorterDuff.Mode mode = IconCompat.k;
            context2.getClass();
            IconCompat b5 = IconCompat.b(context2.getResources(), context2.getPackageName(), 2131165357);
            Bundle bundle2 = new Bundle();
            CharSequence b6 = s.b(spannableStringBuilder);
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            if (arrayList5.isEmpty()) {
                bArr = null;
            } else {
                bArr = (B[]) arrayList5.toArray(new B[arrayList5.size()]);
            }
            if (arrayList6.isEmpty()) {
                bArr2 = null;
            } else {
                bArr2 = (B[]) arrayList6.toArray(new B[arrayList6.size()]);
            }
            p pVar = new p(b5, b6, null, bundle2, bArr2, bArr);
            pVar.f241a.putBoolean("key_action_priority", true);
            ArrayList arrayList7 = new ArrayList(3);
            arrayList7.add(pVar);
            ArrayList<p> arrayList8 = tVar.f271a.f256b;
            if (arrayList8 != null) {
                Iterator<p> it = arrayList8.iterator();
                while (it.hasNext()) {
                    p next = it.next();
                    if (next.f246g) {
                        arrayList7.add(next);
                    } else if (!next.f241a.getBoolean("key_action_priority") && i7 > 1) {
                        arrayList7.add(next);
                        i7--;
                    }
                }
            }
            Iterator it2 = arrayList7.iterator();
            while (it2.hasNext()) {
                vVar.a((p) it2.next());
            }
        } else {
            Iterator<p> it3 = sVar.f256b.iterator();
            while (it3.hasNext()) {
                vVar.a(it3.next());
            }
        }
        Bundle bundle3 = sVar.f266m;
        if (bundle3 != null) {
            vVar.f275d.putAll(bundle3);
        }
        int i8 = Build.VERSION.SDK_INT;
        vVar.f273b.setShowWhen(sVar.f263j);
        a.i(vVar.f273b, sVar.f265l);
        a.g(vVar.f273b, null);
        a.j(vVar.f273b, null);
        a.h(vVar.f273b, false);
        b.b(vVar.f273b, null);
        b.c(vVar.f273b, 0);
        b.f(vVar.f273b, 0);
        b.d(vVar.f273b, null);
        b.e(vVar.f273b, notification.sound, notification.audioAttributes);
        ArrayList<String> arrayList9 = sVar.f270q;
        ArrayList<z> arrayList10 = sVar.f257c;
        String str2 = "";
        if (i8 < 28) {
            if (arrayList10 == null) {
                arrayList4 = null;
            } else {
                arrayList4 = new ArrayList<>(arrayList10.size());
                Iterator<z> it4 = arrayList10.iterator();
                while (it4.hasNext()) {
                    z next2 = it4.next();
                    String str3 = next2.f280c;
                    if (str3 == null) {
                        if (next2.f278a == null) {
                            str3 = "";
                        } else {
                            str3 = "name:" + ((Object) charSequence);
                        }
                    }
                    arrayList4.add(str3);
                }
            }
            if (arrayList4 != null) {
                if (arrayList9 == null) {
                    arrayList9 = arrayList4;
                } else {
                    C0775d c0775d = new C0775d(arrayList9.size() + arrayList4.size());
                    c0775d.addAll(arrayList4);
                    c0775d.addAll(arrayList9);
                    arrayList9 = new ArrayList<>(c0775d);
                }
            }
        }
        if (arrayList9 != null && !arrayList9.isEmpty()) {
            for (String str4 : arrayList9) {
                b.a(vVar.f273b, str4);
            }
        }
        ArrayList<p> arrayList11 = sVar.f258d;
        if (arrayList11.size() > 0) {
            if (sVar.f266m == null) {
                sVar.f266m = new Bundle();
            }
            Bundle bundle4 = sVar.f266m.getBundle("android.car.EXTENSIONS");
            bundle4 = bundle4 == null ? new Bundle() : bundle4;
            Bundle bundle5 = new Bundle(bundle4);
            Bundle bundle6 = new Bundle();
            int i9 = 0;
            while (i9 < arrayList11.size()) {
                String num = Integer.toString(i9);
                p pVar2 = arrayList11.get(i9);
                Bundle bundle7 = new Bundle();
                if (pVar2.f242b == null && (i5 = pVar2.f247h) != 0) {
                    pVar2.f242b = IconCompat.b(resources, str2, i5);
                }
                IconCompat iconCompat3 = pVar2.f242b;
                if (iconCompat3 != null) {
                    i4 = iconCompat3.d();
                } else {
                    i4 = 0;
                }
                bundle7.putInt("icon", i4);
                bundle7.putCharSequence("title", pVar2.f248i);
                bundle7.putParcelable("actionIntent", pVar2.f249j);
                Bundle bundle8 = pVar2.f241a;
                if (bundle8 != null) {
                    bundle = new Bundle(bundle8);
                } else {
                    bundle = new Bundle();
                }
                bundle.putBoolean("android.support.allowGeneratedReplies", pVar2.f244d);
                bundle7.putBundle("extras", bundle);
                B[] bArr3 = pVar2.f243c;
                if (bArr3 == null) {
                    arrayList2 = arrayList11;
                    arrayList3 = arrayList10;
                    str = str2;
                    bundleArr = null;
                } else {
                    bundleArr = new Bundle[bArr3.length];
                    arrayList2 = arrayList11;
                    str = str2;
                    int i10 = 0;
                    while (i10 < bArr3.length) {
                        B b7 = bArr3[i10];
                        B[] bArr4 = bArr3;
                        Bundle bundle9 = new Bundle();
                        b7.getClass();
                        bundle9.putString("resultKey", null);
                        bundle9.putCharSequence("label", null);
                        bundle9.putCharSequenceArray("choices", null);
                        bundle9.putBoolean("allowFreeFormInput", false);
                        bundle9.putBundle("extras", null);
                        bundleArr[i10] = bundle9;
                        i10++;
                        bArr3 = bArr4;
                        arrayList10 = arrayList10;
                    }
                    arrayList3 = arrayList10;
                }
                bundle7.putParcelableArray("remoteInputs", bundleArr);
                bundle7.putBoolean("showsUserInterface", pVar2.f245e);
                bundle7.putInt("semanticAction", pVar2.f);
                bundle6.putBundle(num, bundle7);
                i9++;
                arrayList11 = arrayList2;
                str2 = str;
                arrayList10 = arrayList3;
                resources = null;
            }
            arrayList = arrayList10;
            bundle4.putBundle("invisible_actions", bundle6);
            bundle5.putBundle("invisible_actions", bundle6);
            if (sVar.f266m == null) {
                sVar.f266m = new Bundle();
            }
            sVar.f266m.putBundle("android.car.EXTENSIONS", bundle4);
            vVar = this;
            vVar.f275d.putBundle("android.car.EXTENSIONS", bundle5);
        } else {
            arrayList = arrayList10;
        }
        int i11 = Build.VERSION.SDK_INT;
        if (i11 >= 24) {
            vVar.f273b.setExtras(sVar.f266m);
            r4 = 0;
            d.e(vVar.f273b, null);
        } else {
            r4 = 0;
        }
        if (i11 >= 26) {
            e.b(vVar.f273b, 0);
            e.e(vVar.f273b, r4);
            e.f(vVar.f273b, r4);
            e.g(vVar.f273b, 0L);
            e.d(vVar.f273b, 0);
            if (!TextUtils.isEmpty(sVar.f267n)) {
                vVar.f273b.setSound(r4).setDefaults(0).setLights(0, 0, 0).setVibrate(r4);
            }
        }
        if (i11 >= 28) {
            Iterator<z> it5 = arrayList.iterator();
            while (it5.hasNext()) {
                z next3 = it5.next();
                Notification.Builder builder3 = vVar.f273b;
                next3.getClass();
                f.a(builder3, z.a.b(next3));
            }
        }
        if (Build.VERSION.SDK_INT >= 29) {
            g.a(vVar.f273b, sVar.f268o);
            g.b(vVar.f273b, null);
        }
    }

    public final void a(p pVar) {
        int i4;
        Notification.Action.Builder e4;
        Bundle bundle;
        int i5;
        int i6 = Build.VERSION.SDK_INT;
        Icon icon = null;
        if (pVar.f242b == null && (i5 = pVar.f247h) != 0) {
            pVar.f242b = IconCompat.b((Resources) null, "", i5);
        }
        IconCompat iconCompat = pVar.f242b;
        PendingIntent pendingIntent = pVar.f249j;
        CharSequence charSequence = pVar.f248i;
        if (i6 >= 23) {
            if (iconCompat != null) {
                icon = iconCompat.g((Context) null);
            }
            e4 = c.a(icon, charSequence, pendingIntent);
        } else {
            if (iconCompat != null) {
                i4 = iconCompat.d();
            } else {
                i4 = 0;
            }
            e4 = a.e(i4, charSequence, pendingIntent);
        }
        B[] bArr = pVar.f243c;
        if (bArr != null) {
            RemoteInput[] remoteInputArr = new RemoteInput[bArr.length];
            for (int i7 = 0; i7 < bArr.length; i7++) {
                bArr[i7].getClass();
                RemoteInput.Builder addExtras = new RemoteInput.Builder(null).setLabel(null).setChoices(null).setAllowFreeFormInput(false).addExtras(null);
                if (Build.VERSION.SDK_INT >= 29) {
                    B.a.b(addExtras, 0);
                }
                remoteInputArr[i7] = addExtras.build();
            }
            for (RemoteInput remoteInput : remoteInputArr) {
                a.c(e4, remoteInput);
            }
        }
        Bundle bundle2 = pVar.f241a;
        if (bundle2 != null) {
            bundle = new Bundle(bundle2);
        } else {
            bundle = new Bundle();
        }
        boolean z4 = pVar.f244d;
        bundle.putBoolean("android.support.allowGeneratedReplies", z4);
        int i8 = Build.VERSION.SDK_INT;
        if (i8 >= 24) {
            d.a(e4, z4);
        }
        int i9 = pVar.f;
        bundle.putInt("android.support.action.semanticAction", i9);
        if (i8 >= 28) {
            f.b(e4, i9);
        }
        if (i8 >= 29) {
            g.c(e4, pVar.f246g);
        }
        if (i8 >= 31) {
            h.a(e4, pVar.f250k);
        }
        bundle.putBoolean("android.support.action.showsUserInterface", pVar.f245e);
        a.b(e4, bundle);
        a.a(this.f273b, a.d(e4));
    }
}
