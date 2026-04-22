package R0;

import R0.a;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import java.util.Collection;
import java.util.Iterator;
import l3.g;
import u3.q;
import v3.h;
import v3.i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends i implements q<a, Float, Boolean, g> {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ a f2056k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public b(a aVar) {
        super(3);
        this.f2056k = aVar;
    }

    /* JADX WARN: Type inference failed for: r6v2, types: [R0.a, android.app.Dialog, e.t] */
    @Override // u3.q
    public final void c(Object obj, Float f, Boolean bool) {
        a aVar = (a) obj;
        ?? r6 = this.f2056k;
        Context context = r6.getContext();
        h.d(context, "context");
        a.C0021a c0021a = r6.f2039o;
        String str = c0021a.f2052b;
        if (str != null && str.length() != 0) {
            y3.a aVar2 = new y3.a(0, str.length() - 1, 1);
            if (!(aVar2 instanceof Collection) || !((Collection) aVar2).isEmpty()) {
                Iterator<Integer> it = aVar2.iterator();
                while (((y3.b) it).f6520l) {
                    char charAt = str.charAt(((m3.h) it).a());
                    if (!Character.isWhitespace(charAt) && !Character.isSpaceChar(charAt)) {
                        break;
                    }
                }
            }
        }
        String string = context.getString(2131820756);
        String packageName = context.getPackageName();
        c0021a.f2052b = string + ((Object) packageName);
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(c0021a.f2052b)));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context, context.getString(2131820674), 0).show();
        }
        r6.dismiss();
    }
}
