package T0;

import android.content.Context;
import android.widget.ArrayAdapter;
import b1.C0354b;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b extends ArrayAdapter<C0354b> {

    /* renamed from: j  reason: collision with root package name */
    public final Context f2313j;

    public b(Context context, List<C0354b> list) {
        super(context, 2131427415, list);
        this.f2313j = context;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0079, code lost:
        if (r5.equals("typeBoth") == false) goto L12;
     */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.view.View getView(int r5, android.view.View r6, android.view.ViewGroup r7) {
        /*
            r4 = this;
            java.lang.Object r5 = r4.getItem(r5)
            b1.b r5 = (b1.C0354b) r5
            r0 = 0
            if (r6 != 0) goto L18
            android.content.Context r6 = r4.getContext()
            android.view.LayoutInflater r6 = android.view.LayoutInflater.from(r6)
            r1 = 2131427415(0x7f0b0057, float:1.8476446E38)
            android.view.View r6 = r6.inflate(r1, r7, r0)
        L18:
            r7 = 2131231311(0x7f08024f, float:1.80787E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r1 = 2131231317(0x7f080255, float:1.8078712E38)
            android.view.View r1 = r6.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r2 = 2131231002(0x7f08011a, float:1.8078073E38)
            android.view.View r2 = r6.findViewById(r2)
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            android.content.Context r3 = r4.f2313j
            if (r3 == 0) goto L44
            boolean r3 = r3 instanceof com.embarcadero.OptimizaCorte.Activities.ActivityRetales
            if (r3 == 0) goto L41
            r3 = 8
            r2.setVisibility(r3)
            goto L44
        L41:
            r2.setVisibility(r0)
        L44:
            java.lang.String r3 = r5.f2899b
            r7.setText(r3)
            java.lang.String r7 = r5.f2900c
            r1.setText(r7)
            java.lang.String r5 = r5.f
            r5.getClass()
            r7 = -1
            int r1 = r5.hashCode()
            switch(r1) {
                case -676851237: goto L73;
                case -676563359: goto L68;
                case 507033346: goto L5d;
                default: goto L5b;
            }
        L5b:
            r0 = -1
            goto L7c
        L5d:
            java.lang.String r0 = "typeRight"
            boolean r5 = r5.equals(r0)
            if (r5 != 0) goto L66
            goto L5b
        L66:
            r0 = 2
            goto L7c
        L68:
            java.lang.String r0 = "typeLeft"
            boolean r5 = r5.equals(r0)
            if (r5 != 0) goto L71
            goto L5b
        L71:
            r0 = 1
            goto L7c
        L73:
            java.lang.String r1 = "typeBoth"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L7c
            goto L5b
        L7c:
            switch(r0) {
                case 0: goto L94;
                case 1: goto L8d;
                case 2: goto L86;
                default: goto L7f;
            }
        L7f:
            r5 = 2131165455(0x7f07010f, float:1.7945128E38)
            r2.setImageResource(r5)
            goto L9a
        L86:
            r5 = 2131165458(0x7f070112, float:1.7945134E38)
            r2.setImageResource(r5)
            goto L9a
        L8d:
            r5 = 2131165375(0x7f0700bf, float:1.7944965E38)
            r2.setImageResource(r5)
            goto L9a
        L94:
            r5 = 2131165313(0x7f070081, float:1.794484E38)
            r2.setImageResource(r5)
        L9a:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: T0.b.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }
}
