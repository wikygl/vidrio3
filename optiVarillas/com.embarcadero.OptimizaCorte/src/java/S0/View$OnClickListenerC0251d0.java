package S0;

import android.view.View;

/* renamed from: S0.d0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0251d0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2228j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2229k;

    public /* synthetic */ View$OnClickListenerC0251d0(int i4, Object obj) {
        this.f2228j = i4;
        this.f2229k = obj;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00a6, code lost:
        if (r4.equals("typeLeft") == false) goto L21;
     */
    @Override // android.view.View.OnClickListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onClick(android.view.View r8) {
        /*
            r7 = this;
            java.lang.String r8 = "typeRight"
            java.lang.String r0 = "typeLeft"
            java.lang.String r1 = "typeBoth"
            java.lang.Object r2 = r7.f2229k
            r3 = 1
            int r4 = r7.f2228j
            switch(r4) {
                case 0: goto L80;
                case 1: goto L40;
                default: goto Le;
            }
        Le:
            U2.u r2 = (U2.u) r2
            android.widget.EditText r8 = r2.f
            if (r8 != 0) goto L15
            goto L3f
        L15:
            int r8 = r8.getSelectionEnd()
            android.widget.EditText r0 = r2.f
            if (r0 == 0) goto L2c
            android.text.method.TransformationMethod r0 = r0.getTransformationMethod()
            boolean r0 = r0 instanceof android.text.method.PasswordTransformationMethod
            if (r0 == 0) goto L2c
            android.widget.EditText r0 = r2.f
            r1 = 0
            r0.setTransformationMethod(r1)
            goto L35
        L2c:
            android.widget.EditText r0 = r2.f
            android.text.method.PasswordTransformationMethod r1 = android.text.method.PasswordTransformationMethod.getInstance()
            r0.setTransformationMethod(r1)
        L35:
            if (r8 < 0) goto L3c
            android.widget.EditText r0 = r2.f
            r0.setSelection(r8)
        L3c:
            r2.q()
        L3f:
            return
        L40:
            androidx.appcompat.app.b$a r8 = new androidx.appcompat.app.b$a
            com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte$f r2 = (com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte.f) r2
            com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte r0 = com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte.this
            r8.<init>(r0)
            java.lang.String r1 = "Premium"
            androidx.appcompat.app.AlertController$b r4 = r8.a
            r4.d = r1
            r1 = 2131820879(0x7f11014f, float:1.9274485E38)
            java.lang.String r1 = r0.getString(r1)
            r4.f = r1
            r4.m = r3
            r1 = 2131820598(0x7f110036, float:1.9273915E38)
            java.lang.String r1 = r0.getString(r1)
            S0.D r4 = new S0.D
            r4.<init>(r3, r2)
            r8.c(r1, r4)
            r1 = 2131820593(0x7f110031, float:1.9273905E38)
            java.lang.String r0 = r0.getString(r1)
            S0.l0 r1 = new S0.l0
            r1.<init>(r3)
            r8.b(r0, r1)
            androidx.appcompat.app.b r8 = r8.a()
            r8.show()
            return
        L80:
            android.app.ProgressDialog r4 = com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte.l0
            android.widget.ImageButton r2 = (android.widget.ImageButton) r2
            java.lang.Object r4 = r2.getTag()
            java.lang.String r4 = r4.toString()
            r4.getClass()
            r5 = -1
            int r6 = r4.hashCode()
            switch(r6) {
                case -676851237: goto La9;
                case -676563359: goto La2;
                case 507033346: goto L99;
                default: goto L97;
            }
        L97:
            r3 = -1
            goto Lb1
        L99:
            boolean r3 = r4.equals(r8)
            if (r3 != 0) goto La0
            goto L97
        La0:
            r3 = 2
            goto Lb1
        La2:
            boolean r4 = r4.equals(r0)
            if (r4 != 0) goto Lb1
            goto L97
        La9:
            boolean r3 = r4.equals(r1)
            if (r3 != 0) goto Lb0
            goto L97
        Lb0:
            r3 = 0
        Lb1:
            switch(r3) {
                case 0: goto Ld2;
                case 1: goto Lc8;
                case 2: goto Lbe;
                default: goto Lb4;
            }
        Lb4:
            r8 = 2131165374(0x7f0700be, float:1.7944963E38)
            r2.setImageResource(r8)
            r2.setTag(r0)
            goto Ldd
        Lbe:
            r8 = 2131165312(0x7f070080, float:1.7944838E38)
            r2.setImageResource(r8)
            r2.setTag(r1)
            goto Ldd
        Lc8:
            r0 = 2131165457(0x7f070111, float:1.7945132E38)
            r2.setImageResource(r0)
            r2.setTag(r8)
            goto Ldd
        Ld2:
            r8 = 2131165454(0x7f07010e, float:1.7945126E38)
            r2.setImageResource(r8)
            java.lang.String r8 = "typeRect"
            r2.setTag(r8)
        Ldd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: S0.View$OnClickListenerC0251d0.onClick(android.view.View):void");
    }
}
