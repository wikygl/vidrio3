package Y0;

import Q0.d;
import S0.View$OnClickListenerC0274p;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.b;
import com.google.android.material.snackbar.Snackbar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public final Activity f2827a;

    /* renamed from: b  reason: collision with root package name */
    public final a f2828b;

    public b(Activity activity, a aVar) {
        this.f2827a = activity;
        this.f2828b = aVar;
    }

    public final void a() {
        Activity activity = this.f2827a;
        try {
            View inflate = LayoutInflater.from(activity).inflate(2131427385, (ViewGroup) null);
            b.a aVar = new b.a(activity);
            aVar.a.q = inflate;
            final androidx.appcompat.app.b a4 = aVar.a();
            String string = activity.getString(2131820934);
            a aVar2 = this.f2828b;
            d dVar = a.f2823d;
            aVar2.getClass();
            String c4 = a.c(dVar);
            String string2 = activity.getString(2131820935);
            ((TextView) inflate.findViewById(2131231316)).setText(string + c4 + string2);
            ((Button) inflate.findViewById(2131230848)).setOnClickListener(new View.OnClickListener() { // from class: R2.h
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    switch (r2) {
                        case 0:
                            Snackbar snackbar = (Snackbar) this;
                            snackbar.getClass();
                            ((View.OnClickListener) a4).onClick(view);
                            snackbar.b(1);
                            return;
                        default:
                            Y0.b bVar = (Y0.b) this;
                            Y0.a.g(bVar.f2827a, bVar.f2828b);
                            ((androidx.appcompat.app.b) a4).dismiss();
                            return;
                    }
                }
            });
            ((Button) inflate.findViewById(2131230847)).setOnClickListener(new View$OnClickListenerC0274p(4, a4));
            a4.show();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}
