package l;

import android.view.View;
import android.widget.AdapterView;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class K implements AdapterView.OnItemSelectedListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ L f5010j;

    public K(L l2) {
        this.f5010j = l2;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public final void onItemSelected(AdapterView<?> adapterView, View view, int i4, long j4) {
        H h4;
        if (i4 != -1 && (h4 = this.f5010j.f5025l) != null) {
            h4.setListSelectionHidden(false);
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public final void onNothingSelected(AdapterView<?> adapterView) {
    }
}
