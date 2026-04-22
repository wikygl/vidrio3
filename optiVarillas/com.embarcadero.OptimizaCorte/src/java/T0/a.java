package T0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import b1.C0354b;
import java.text.MessageFormat;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a extends ArrayAdapter<C0354b> {

    /* renamed from: j  reason: collision with root package name */
    public final Context f2312j;

    public a(Context context, List<C0354b> list) {
        super(context, 2131427414, list);
        this.f2312j = context;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public final View getView(int i4, View view, ViewGroup viewGroup) {
        String str;
        int i5;
        C0354b item = getItem(i4);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(2131427414, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(2131231312);
        TextView textView2 = (TextView) view.findViewById(2131231318);
        if (item != null) {
            str = item.f2899b;
        } else {
            str = "";
        }
        textView.setText(str);
        if (item != null) {
            i5 = item.f2901d;
        } else {
            i5 = 0;
        }
        textView2.setText(MessageFormat.format("{0} {1}", Integer.valueOf(i5), this.f2312j.getString(2131820739)));
        return view;
    }
}
