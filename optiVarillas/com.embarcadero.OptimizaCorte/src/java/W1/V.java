package W1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class V {

    /* renamed from: d  reason: collision with root package name */
    public static final Uri f2662d = new Uri.Builder().scheme("content").authority("com.google.android.gms.chimera").build();

    /* renamed from: a  reason: collision with root package name */
    public final String f2663a;

    /* renamed from: b  reason: collision with root package name */
    public final String f2664b;

    /* renamed from: c  reason: collision with root package name */
    public final boolean f2665c;

    public V(String str, boolean z4) {
        if (!TextUtils.isEmpty(str)) {
            this.f2663a = str;
            if (!TextUtils.isEmpty("com.google.android.gms")) {
                this.f2664b = "com.google.android.gms";
                this.f2665c = z4;
                return;
            }
            throw new IllegalArgumentException("Given String is empty or null");
        }
        throw new IllegalArgumentException("Given String is empty or null");
    }

    public final Intent a(Context context) {
        Bundle bundle;
        Intent intent = null;
        String str = this.f2663a;
        if (str != null) {
            if (this.f2665c) {
                Bundle bundle2 = new Bundle();
                bundle2.putString("serviceActionBundleKey", str);
                try {
                    bundle = context.getContentResolver().call(f2662d, "serviceIntentCall", (String) null, bundle2);
                } catch (IllegalArgumentException e4) {
                    Log.w("ConnectionStatusConfig", "Dynamic intent resolution failed: ".concat(e4.toString()));
                    bundle = null;
                }
                if (bundle != null) {
                    intent = (Intent) bundle.getParcelable("serviceResponseIntentKey");
                }
                if (intent == null) {
                    Log.w("ConnectionStatusConfig", "Dynamic lookup for intent failed for action: ".concat(String.valueOf(str)));
                }
            }
            if (intent == null) {
                return new Intent(str).setPackage(this.f2664b);
            }
            return intent;
        }
        return new Intent().setComponent(null);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof V)) {
            return false;
        }
        V v4 = (V) obj;
        if (C0323k.a(this.f2663a, v4.f2663a) && C0323k.a(this.f2664b, v4.f2664b) && C0323k.a(null, null) && this.f2665c == v4.f2665c) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f2663a, this.f2664b, null, 4225, Boolean.valueOf(this.f2665c)});
    }

    public final String toString() {
        String str = this.f2663a;
        if (str != null) {
            return str;
        }
        C0324l.d(null);
        throw null;
    }
}
