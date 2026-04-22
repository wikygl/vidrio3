package X0;

import S0.View$OnClickListenerC0274p;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.fragment.app.k;
import b1.C0353a;
import b1.C0354b;
import com.google.android.material.textfield.TextInputEditText;
import e1.C0407a;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class b extends k {

    /* renamed from: c0  reason: collision with root package name */
    public Context f2805c0;

    /* renamed from: d0  reason: collision with root package name */
    public Button f2806d0;

    /* renamed from: e0  reason: collision with root package name */
    public TextInputEditText f2807e0;

    /* renamed from: f0  reason: collision with root package name */
    public TextInputEditText f2808f0;

    /* renamed from: g0  reason: collision with root package name */
    public TextInputEditText f2809g0;

    /* renamed from: h0  reason: collision with root package name */
    public ListView f2810h0;

    /* renamed from: i0  reason: collision with root package name */
    public Button f2811i0;

    /* renamed from: j0  reason: collision with root package name */
    public Button f2812j0;

    /* renamed from: k0  reason: collision with root package name */
    public long f2813k0;
    public final ArrayList<C0354b> l0 = new ArrayList<>();

    /* renamed from: m0  reason: collision with root package name */
    public T0.b f2814m0;

    /* renamed from: n0  reason: collision with root package name */
    public C0353a f2815n0;

    /* renamed from: o0  reason: collision with root package name */
    public C0407a f2816o0;

    public b() {
        new ArrayList();
    }

    public final void v(Context context) {
        super.v(context);
        this.f2805c0 = context;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v20, types: [android.view.View$OnClickListener, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r5v21, types: [android.view.View$OnClickListener, java.lang.Object] */
    public final View x(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(2131427388, viewGroup, false);
        this.f2807e0 = inflate.findViewById(2131230926);
        this.f2808f0 = inflate.findViewById(2131230933);
        this.f2809g0 = inflate.findViewById(2131230929);
        this.f2810h0 = (ListView) inflate.findViewById(2131231034);
        this.f2806d0 = (Button) inflate.findViewById(2131230824);
        this.f2811i0 = (Button) inflate.findViewById(2131230826);
        this.f2812j0 = (Button) inflate.findViewById(2131230831);
        if (this.f2815n0 == null) {
            this.f2815n0 = new C0353a();
        }
        C0407a c0407a = new C0407a(this.f2805c0);
        this.f2816o0 = c0407a;
        C0353a a4 = c0407a.a();
        this.f2815n0 = a4;
        this.f2813k0 = a4.f2889p;
        this.f2814m0 = new T0.b(this.f2805c0, this.l0);
        ListView listView = (ListView) inflate.findViewById(2131231034);
        listView.setAdapter((ListAdapter) this.f2814m0);
        listView.setLongClickable(true);
        this.f2806d0.setOnClickListener(new View$OnClickListenerC0274p(3, this));
        this.f2811i0.setOnClickListener(new Object());
        this.f2812j0.setOnClickListener(new Object());
        return inflate;
    }
}
