/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.os.Bundle
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.View$OnLongClickListener
 *  android.view.ViewGroup
 *  android.widget.CompoundButton
 *  android.widget.CompoundButton$OnCheckedChangeListener
 *  android.widget.TextView
 *  android.widget.Toast
 *  androidx.appcompat.app.AlertDialog$Builder
 *  androidx.appcompat.app.AppCompatActivity
 *  androidx.recyclerview.widget.LinearLayoutManager
 *  androidx.recyclerview.widget.RecyclerView
 *  androidx.recyclerview.widget.RecyclerView$Adapter
 *  androidx.recyclerview.widget.RecyclerView$LayoutManager
 *  androidx.recyclerview.widget.RecyclerView$ViewHolder
 *  java.io.BufferedWriter
 *  java.io.Closeable
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.Writer
 *  java.lang.Appendable
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.nio.charset.Charset
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Unit
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Charsets
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 */
package Jose.jose;

import Jose.jose.MainActivity$$ExternalSyntheticLambda0;
import Jose.jose.MainActivity$$ExternalSyntheticLambda1;
import Jose.jose.MainActivity$$ExternalSyntheticLambda2;
import Jose.jose.MainActivity$$ExternalSyntheticLambda3;
import Jose.jose.MainActivity$$ExternalSyntheticLambda4;
import Jose.jose.MainActivity$$ExternalSyntheticLambda5;
import Jose.jose.MainActivity$$ExternalSyntheticLambda6;
import Jose.jose.MainActivity$$ExternalSyntheticLambda7;
import Jose.jose.MainActivity$ArchiveAdapter$$ExternalSyntheticLambda0;
import Jose.jose.R;
import Jose.jose.databinding.ActivityMainBinding;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001:\u000267B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0003J\b\u0010\u0019\u001a\u00020\u0016H\u0002J\u0017\u0010\u001a\u001a\u00020\u00162\b\u0010\u001b\u001a\u0004\u0018\u00010\rH\u0002\u00a2\u0006\u0002\u0010\u001cJ\u0010\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u0006H\u0002J\b\u0010\u001f\u001a\u00020\u0018H\u0002J\b\u0010 \u001a\u00020\u0016H\u0002J\b\u0010!\u001a\u00020\rH\u0002J\b\u0010\"\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\u0016H\u0002J\b\u0010#\u001a\u00020\u0016H\u0002J\u000f\u0010$\u001a\u0004\u0018\u00010\rH\u0002\u00a2\u0006\u0002\u0010%J\u000f\u0010&\u001a\u0004\u0018\u00010\rH\u0002\u00a2\u0006\u0002\u0010%J\u0010\u0010'\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u0006H\u0002J\u0012\u0010(\u001a\u00020\u00182\b\u0010)\u001a\u0004\u0018\u00010*H\u0014J\b\u0010+\u001a\u00020\u0016H\u0002J\b\u0010\u0010\u001a\u00020\u0016H\u0002J\b\u0010,\u001a\u00020\u0016H\u0002J\b\u0010-\u001a\u00020\rH\u0002J\b\u0010.\u001a\u00020\rH\u0002J\b\u0010/\u001a\u00020\u0016H\u0002J\b\u00100\u001a\u00020\u0018H\u0002J\b\u00101\u001a\u00020\u0016H\u0002J\b\u0010\u0011\u001a\u00020\u0016H\u0002J\b\u0010\u0012\u001a\u00020\u0016H\u0002J\b\u0010\u0013\u001a\u00020\u0016H\u0002J\b\u00102\u001a\u00020\u0016H\u0002J\b\u00103\u001a\u00020\u0016H\u0002J\b\u00104\u001a\u00020\u0016H\u0002J\b\u00105\u001a\u00020\u0016H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2={"LJose/jose/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "archiveAdapter", "LJose/jose/MainActivity$ArchiveAdapter;", "archiveCounter", "", "archiveList", "", "LJose/jose/MainActivity$ArchiveItem;", "binding", "LJose/jose/databinding/ActivityMainBinding;", "hoja", "", "marco", "marcoV", "paflon", "tope", "tres", "tresDos", "tubo", "bandejas", "", "configurarBotones", "", "contraMarco", "df1", "defo", "(Ljava/lang/Float;)Ljava/lang/String;", "eliminarElemento", "position", "exportarDatos", "generarTexto", "hojaH", "hojaV", "marcoM", "med1", "()Ljava/lang/Float;", "med2", "mostrarDialogoEliminar", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "pBandejas", "panel", "panelRefHorizontal", "panelRefVertical", "platina", "radio", "riel", "tresOcho", "tuboPuente", "uno", "vidrio", "ArchiveAdapter", "ArchiveItem", "app_debug"}, k=1, mv={1, 9, 0}, xi=48)
public final class MainActivity
extends AppCompatActivity {
    private ArchiveAdapter archiveAdapter;
    private int archiveCounter = 1;
    private final List<ArchiveItem> archiveList = (List)new ArrayList();
    private ActivityMainBinding binding;
    private float hoja = 199.0f;
    private float marco = 2.2f;
    private float marcoV = 2.0f;
    private float paflon = 8.25f;
    private float tope = 1.5f;
    private float tres = 3.0f;
    private float tresDos = 3.0f;
    private float tubo = 2.5f;

    public static /* synthetic */ void $r8$lambda$B2dz-fhMy_2vL6_wCDJMFq6H-nQ(MainActivity mainActivity, View view) {
        MainActivity.configurarBotones$lambda$1(mainActivity, view);
    }

    public static /* synthetic */ void $r8$lambda$LLLwPNfd-2I9R78lXOevoqr2rg4(MainActivity mainActivity, CompoundButton compoundButton, boolean bl) {
        MainActivity.radio$lambda$6(mainActivity, compoundButton, bl);
    }

    public static /* synthetic */ void $r8$lambda$R-8l-IK0lGfUSnbXBzYpCy2EIdw(DialogInterface dialogInterface, int n) {
        MainActivity.mostrarDialogoEliminar$lambda$4(dialogInterface, n);
    }

    public static /* synthetic */ void $r8$lambda$TJW3Nh4Cx_DJJC-H7sp2swMf2LU(MainActivity mainActivity, int n, DialogInterface dialogInterface, int n2) {
        MainActivity.mostrarDialogoEliminar$lambda$3(mainActivity, n, dialogInterface, n2);
    }

    public static /* synthetic */ void $r8$lambda$a8XQbHhIAycjYfCtHOEIpgOYHGc(MainActivity mainActivity, View view) {
        MainActivity.configurarBotones$lambda$2(mainActivity, view);
    }

    public static /* synthetic */ void $r8$lambda$eeW5jFJIDjM8_Rp9L-Zd7co-0aE(MainActivity mainActivity, CompoundButton compoundButton, boolean bl) {
        MainActivity.radio$lambda$7(mainActivity, compoundButton, bl);
    }

    public static /* synthetic */ boolean $r8$lambda$gBspQxdlxTqPG8hJ3Esaq8dTqgo(MainActivity mainActivity, View view) {
        return MainActivity.onCreate$lambda$0(mainActivity, view);
    }

    public static /* synthetic */ void $r8$lambda$l1cn5S0zVRgv1ldzBfSGDwujHvg(MainActivity mainActivity, CompoundButton compoundButton, boolean bl) {
        MainActivity.radio$lambda$5(mainActivity, compoundButton, bl);
    }

    public static final /* synthetic */ void access$mostrarDialogoEliminar(MainActivity mainActivity, int n) {
        mainActivity.mostrarDialogoEliminar(n);
    }

    private final String bandejas() {
        Object object = this.binding;
        Object var7_2 = null;
        Object object2 = object;
        if (object == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object2 = null;
        }
        float f = Float.parseFloat((String)((ActivityMainBinding)object2).etMarco.getText().toString());
        float f2 = this.hojaH() - 1.2f;
        float f3 = 2;
        float f4 = this.tresDos;
        float f5 = this.hojaV();
        object2 = "25.8 x " + this.df1(Float.valueOf((float)(f2 - this.tresDos * f3)));
        object = this.df1(Float.valueOf((float)((f2 - f4 * f3 - this.tresDos * (float)4) / (float)5))) + " x " + this.df1(Float.valueOf((float)(f5 - (this.tresDos * f3 + 28.8f))));
        object = new StringBuilder().append((String)object2).append(" =1\n").append((String)object).append(" = 5\n");
        object2 = this.med1();
        object2 = object2 != null ? Float.valueOf((float)(object2.floatValue() - f3 * f)) : null;
        object = object.append(this.df1((Float)object2)).append(" x ");
        Float f6 = this.med2();
        object2 = var7_2;
        if (f6 != null) {
            object2 = Float.valueOf((float)(f6.floatValue() - (f2 + f + this.tresDos + 1.5f)));
        }
        object2 = object.append(this.df1((Float)object2));
        return object2.append(" =1").toString();
    }

    private final void configurarBotones() {
        ActivityMainBinding activityMainBinding = this.binding;
        Object var2_2 = null;
        ActivityMainBinding activityMainBinding2 = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        activityMainBinding2.btcalcular.setOnClickListener((View.OnClickListener)new MainActivity$$ExternalSyntheticLambda0(this));
        activityMainBinding2 = this.binding;
        if (activityMainBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = var2_2;
        }
        activityMainBinding2.btArchivar.setOnClickListener((View.OnClickListener)new MainActivity$$ExternalSyntheticLambda1(this));
    }

    private static final void configurarBotones$lambda$1(MainActivity mainActivity, View object) {
        block19: {
            Object var2_4;
            block18: {
                ActivityMainBinding activityMainBinding;
                block17: {
                    block16: {
                        block15: {
                            block14: {
                                Intrinsics.checkNotNullParameter((Object)((Object)mainActivity), (String)"this$0");
                                activityMainBinding = mainActivity.binding;
                                var2_4 = null;
                                object = activityMainBinding;
                                if (activityMainBinding != null) break block14;
                                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                                object = null;
                            }
                            object.txPru.setText((CharSequence)mainActivity.generarTexto());
                            activityMainBinding = mainActivity.binding;
                            object = activityMainBinding;
                            if (activityMainBinding != null) break block15;
                            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                            object = null;
                        }
                        object.etMed1.setHint((CharSequence)String.valueOf((Object)mainActivity.med1()));
                        activityMainBinding = mainActivity.binding;
                        object = activityMainBinding;
                        if (activityMainBinding != null) break block16;
                        Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                        object = null;
                    }
                    object.etMed1.setText((CharSequence)"");
                    activityMainBinding = mainActivity.binding;
                    object = activityMainBinding;
                    if (activityMainBinding != null) break block17;
                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                    object = null;
                }
                object.etMed2.setHint((CharSequence)String.valueOf((Object)mainActivity.med2()));
                activityMainBinding = mainActivity.binding;
                object = activityMainBinding;
                if (activityMainBinding != null) break block18;
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = null;
            }
            object.etMed2.setText((CharSequence)"");
            object = mainActivity.binding;
            if (object != null) break block19;
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = var2_4;
        }
        try {
            object.etMed1.requestFocus();
        }
        catch (Exception exception) {
            Toast.makeText((Context)((Context)mainActivity), (CharSequence)"Ingrese n\u00fameros v\u00e1lidos", (int)0).show();
        }
    }

    private static final void configurarBotones$lambda$2(MainActivity mainActivity, View object) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)((Object)mainActivity), (String)"this$0");
        Object object2 = mainActivity.binding;
        Object var3_3 = null;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        if (bl = ((CharSequence)(object = StringsKt.trim((CharSequence)((ActivityMainBinding)object).txPru.getText().toString()).toString())).length() > 0) {
            ArchiveItem archiveItem = new ArchiveItem(mainActivity.archiveCounter, (String)object);
            object2 = mainActivity.archiveAdapter;
            object = object2;
            if (object2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"archiveAdapter");
                object = null;
            }
            ((ArchiveAdapter)((Object)object)).addArchiveItem(archiveItem);
            ++mainActivity.archiveCounter;
            object = mainActivity.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = var3_3;
            }
            ((ActivityMainBinding)object).txPru.setText((CharSequence)"");
            Toast.makeText((Context)((Context)mainActivity), (CharSequence)"Archivo archivado exitosamente", (int)0).show();
        } else {
            Toast.makeText((Context)((Context)mainActivity), (CharSequence)"No hay texto para archivar", (int)0).show();
        }
    }

    private final String contraMarco() {
        Object object = this.med2();
        object = object != null ? Float.valueOf((float)(object.floatValue() - this.marco)) : null;
        Float f = this.med1();
        return this.df1(f) + "= 1\n" + this.df1((Float)object) + " = 2";
    }

    private final String df1(Float object) {
        if (StringsKt.endsWith$default((String)String.valueOf((Object)object), (String)".0", (boolean)false, (int)2, null)) {
            object = StringsKt.replace$default((String)String.valueOf((Object)object), (String)".0", (String)"", (boolean)false, (int)4, null);
        } else {
            object = String.format((String)"%.1f", (Object[])Arrays.copyOf((Object[])new Object[]{object}, (int)1));
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"format(this, *args)");
        }
        return StringsKt.replace$default((String)object, (String)",", (String)".", (boolean)false, (int)4, null);
    }

    private final void eliminarElemento(int n) {
        ArchiveAdapter archiveAdapter;
        ArchiveAdapter archiveAdapter2 = archiveAdapter = this.archiveAdapter;
        if (archiveAdapter == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"archiveAdapter");
            archiveAdapter2 = null;
        }
        archiveAdapter2.removeArchiveItem(n);
        Toast.makeText((Context)((Context)this), (CharSequence)"Archivo eliminado", (int)0).show();
    }

    /*
     * Unable to fully structure code
     */
    private final void exportarDatos() {
        var9_1 = new File(this.getExternalFilesDir(null), "ExportedData");
        if (!var9_1.exists() && !var9_1.mkdirs()) {
            Toast.makeText((Context)((Context)this), (CharSequence)"No se pudo crear el directorio de exportaci\u00f3n", (int)0).show();
            return;
        }
        var6_2 /* !! */  = (Map)new LinkedHashMap();
        for (ArchiveItem var5_7 : this.archiveList) {
            var8_15 = "puerta" + var5_7.getNumber();
            var7_14 /* !! */  = StringsKt.split$default((CharSequence)var5_7.getContent(), (String[])new String[]{"\n"}, (boolean)false, (int)0, (int)6, null);
            var3_6 = null;
            var10_17 = var7_14 /* !! */ .iterator();
            while (var10_17.hasNext()) {
                var7_14 /* !! */  = StringsKt.trim((CharSequence)((String)var10_17.next())).toString();
                var1_5 = ((CharSequence)var7_14 /* !! */ ).length() == 0;
                if (var1_5) continue;
                var11_18 /* !! */  = (CharSequence)var7_14 /* !! */ ;
                if (new Regex("^[A-Za-z].*").matches((CharSequence)var11_18 /* !! */ )) {
                    var3_6 = var7_14 /* !! */ ;
                    if (var6_2 /* !! */ .containsKey((Object)var3_6)) continue;
                    var6_2 /* !! */ .put((Object)var3_6, (Object)((List)new ArrayList()));
                    continue;
                }
                if (var3_6 == null) continue;
                var11_18 /* !! */  = (CharSequence)var7_14 /* !! */ ;
                if (new Regex("^\\d+(\\.\\d+)?\\s*=\\s*\\d+$").matches((CharSequence)var11_18 /* !! */ )) {
                    if ((var7_14 /* !! */  = Regex.find$default((Regex)new Regex("^(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+)$"), (CharSequence)((CharSequence)var7_14 /* !! */ ), (int)0, (int)2, null)) == null) continue;
                    var11_18 /* !! */  = var7_14 /* !! */ .getDestructured();
                    var7_14 /* !! */  = (String)var11_18 /* !! */ .getMatch().getGroupValues().get(1);
                    var12_19 /* !! */  = (String)var11_18 /* !! */ .getMatch().getGroupValues().get(3);
                    var11_18 /* !! */  = (List)var6_2 /* !! */ .get((Object)var3_6);
                    if (var11_18 /* !! */  == null) continue;
                    var11_18 /* !! */ .add((Object)new Pair((Object)((String)var7_14 /* !! */  + ':' + (String)var12_19 /* !! */  + ':' + (String)var8_15), var8_15));
                    continue;
                }
                var11_18 /* !! */  = (CharSequence)var7_14 /* !! */ ;
                if (!new Regex("^\\d+(\\.\\d+)?\\s*x\\s*\\d+(\\.\\d+)?\\s*=\\s*\\d+$").matches((CharSequence)var11_18 /* !! */ ) || (var7_14 /* !! */  = Regex.find$default((Regex)new Regex("^(\\d+(\\.\\d+)?)\\s*x\\s*(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+)$"), (CharSequence)((CharSequence)var7_14 /* !! */ ), (int)0, (int)2, null)) == null) continue;
                var12_19 /* !! */  = var7_14 /* !! */ .getDestructured();
                var11_18 /* !! */  = (String)var12_19 /* !! */ .getMatch().getGroupValues().get(1);
                var7_14 /* !! */  = (String)var12_19 /* !! */ .getMatch().getGroupValues().get(3);
                var12_19 /* !! */  = (String)var12_19 /* !! */ .getMatch().getGroupValues().get(5);
                var7_14 /* !! */  = (String)var11_18 /* !! */  + ',' + (String)var7_14 /* !! */  + ',' + (String)var12_19 /* !! */  + ",TRUE," + (String)var8_15;
                var11_18 /* !! */  = (List)var6_2 /* !! */ .get((Object)var3_6);
                if (var11_18 /* !! */  == null) continue;
                var11_18 /* !! */ .add((Object)new Pair((Object)var7_14 /* !! */ , var8_15));
            }
        }
        var3_6 = var6_2 /* !! */ .entrySet().iterator();
        var4_4 = var6_2 /* !! */ ;
        while (var3_6.hasNext()) {
            block32: {
                block31: {
                    var5_7 = (Map.Entry)var3_6.next();
                    var10_17 = (String)var5_7.getKey();
                    var8_15 = (List)var5_7.getValue();
                    var6_2 /* !! */  = (Iterable)var8_15;
                    var2_20 = false;
                    var11_18 /* !! */  = (Collection)new ArrayList();
                    var5_7 = var6_2 /* !! */ ;
                    var7_14 /* !! */  = var5_7.iterator();
                    while (var7_14 /* !! */ .hasNext()) {
                        var13_21 = var7_14 /* !! */ .next();
                        var12_19 /* !! */  = (Pair)var13_21;
                        var1_5 = StringsKt.contains$default((CharSequence)((CharSequence)var12_19 /* !! */ .getFirst()), (CharSequence)":", (boolean)false, (int)2, null) != false && StringsKt.contains$default((CharSequence)((CharSequence)var12_19 /* !! */ .getFirst()), (CharSequence)",", (boolean)false, (int)2, null) == false;
                        if (!var1_5) continue;
                        var11_18 /* !! */ .add(var13_21);
                    }
                    var11_18 /* !! */  = (List)var11_18 /* !! */ ;
                    var7_14 /* !! */  = (Iterable)var8_15;
                    var2_20 = false;
                    var12_19 /* !! */  = (Collection)new ArrayList();
                    var5_7 = var7_14 /* !! */ ;
                    var13_21 = var5_7.iterator();
                    var6_2 /* !! */  = var8_15;
                    while (var13_21.hasNext()) {
                        var8_15 = var13_21.next();
                        if (!StringsKt.contains$default((CharSequence)((CharSequence)((Pair)var8_15).getFirst()), (CharSequence)",", (boolean)false, (int)2, null)) continue;
                        var12_19 /* !! */ .add(var8_15);
                    }
                    var6_2 /* !! */  = (List)var12_19 /* !! */ ;
                    if (!var11_18 /* !! */ .isEmpty()) {
                        var5_7 = new File(var9_1, var10_17 + ".txt");
                        var7_14 /* !! */  = new FileOutputStream((File)var5_7, true);
                        var8_15 = (OutputStream)var7_14 /* !! */ ;
                        var5_7 = Charsets.UTF_8;
                        var7_14 /* !! */  = new OutputStreamWriter((OutputStream)var8_15, (Charset)var5_7);
                        var5_7 = (Writer)var7_14 /* !! */ ;
                        var5_7 = var5_7 instanceof BufferedWriter != false ? (BufferedWriter)var5_7 : new BufferedWriter((Writer)var5_7, 8192);
                        var7_14 /* !! */  = (Closeable)var5_7;
                        var5_7 = (BufferedWriter)var7_14 /* !! */ ;
                        var8_15 = ((Appendable)var5_7).append((CharSequence)var10_17);
                        Intrinsics.checkNotNullExpressionValue((Object)var8_15, (String)"append(value)");
                        Intrinsics.checkNotNullExpressionValue((Object)var8_15.append('\n'), (String)"append('\\n')");
                        var8_15 = var11_18 /* !! */ .iterator();
                        while (var8_15.hasNext()) {
                            var11_18 /* !! */  = (String)((Pair)var8_15.next()).component1();
                            var11_18 /* !! */  = ((Appendable)var5_7).append((CharSequence)var11_18 /* !! */ );
                            Intrinsics.checkNotNullExpressionValue((Object)var11_18 /* !! */ , (String)"append(value)");
                            Intrinsics.checkNotNullExpressionValue((Object)var11_18 /* !! */ .append('\n'), (String)"append('\\n')");
                        }
                        var5_7 = Unit.INSTANCE;
                        CloseableKt.closeFinally((Closeable)var7_14 /* !! */ , null);
                        catch (Throwable var5_8) {
                            try {
                                throw var5_8;
                            }
                            catch (Throwable var8_16) {
                                try {
                                    CloseableKt.closeFinally((Closeable)var7_14 /* !! */ , (Throwable)var5_8);
                                    throw var8_16;
                                }
                                catch (Exception var5_9) {
                                    var5_9.printStackTrace();
                                    Toast.makeText((Context)((Context)this), (CharSequence)("Error al exportar " + var10_17 + ".txt"), (int)0).show();
                                }
                            }
                        }
                    }
                    if (((Collection)var6_2 /* !! */ ).isEmpty()) continue;
                    var5_7 = new File(var9_1, var10_17 + ".csv");
                    var7_14 /* !! */  = new FileOutputStream((File)var5_7, true);
                    var8_15 = (OutputStream)var7_14 /* !! */ ;
                    var5_7 = Charsets.UTF_8;
                    var7_14 /* !! */  = new OutputStreamWriter((OutputStream)var8_15, (Charset)var5_7);
                    var5_7 = (Writer)var7_14 /* !! */ ;
                    var5_7 = var5_7 instanceof BufferedWriter != false ? (BufferedWriter)var5_7 : new BufferedWriter((Writer)var5_7, 8192);
                    var7_14 /* !! */  = (Closeable)var5_7;
                    var5_7 = (BufferedWriter)var7_14 /* !! */ ;
                    var8_15 = ((Appendable)var5_7).append((CharSequence)var10_17);
                    Intrinsics.checkNotNullExpressionValue((Object)var8_15, (String)"append(value)");
                    Intrinsics.checkNotNullExpressionValue((Object)var8_15.append('\n'), (String)"append('\\n')");
                    var6_2 /* !! */  = var6_2 /* !! */ .iterator();
                    ** while (var2_20 = var6_2 /* !! */ .hasNext())
lbl-1000:
                    // 1 sources

                    {
                        try {
                            var8_15 = (String)((Pair)var6_2 /* !! */ .next()).component1();
                            var8_15 = ((Appendable)var5_7).append((CharSequence)var8_15);
                            Intrinsics.checkNotNullExpressionValue((Object)var8_15, (String)"append(value)");
                            Intrinsics.checkNotNullExpressionValue((Object)var8_15.append('\n'), (String)"append('\\n')");
                            continue;
                        }
                        catch (Throwable var5_10) {
                            break block31;
                        }
                    }
lbl142:
                    // 2 sources

                    var5_7 = Unit.INSTANCE;
                    CloseableKt.closeFinally((Closeable)var7_14 /* !! */ , null);
                    continue;
                    {
                        catch (Exception var5_12) {
                            break block32;
                        }
                    }
                    catch (Throwable var5_11) {
                        // empty catch block
                    }
                }
                try {
                    throw var5_7;
                }
                catch (Throwable var6_3) {
                    CloseableKt.closeFinally((Closeable)var7_14 /* !! */ , (Throwable)var5_7);
                    throw var6_3;
                }
                catch (Exception var5_13) {
                    // empty catch block
                }
            }
            var5_7.printStackTrace();
            Toast.makeText((Context)((Context)this), (CharSequence)("Error al exportar " + var10_17 + ".csv"), (int)0).show();
        }
        Toast.makeText((Context)((Context)this), (CharSequence)("Datos exportados exitosamente en " + var9_1.getAbsolutePath()), (int)1).show();
    }

    private final String generarTexto() {
        ActivityMainBinding activityMainBinding = this.binding;
        Object var2_2 = null;
        Object object = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        if (((ActivityMainBinding)object).rbPh.isChecked()) {
            object = new StringBuilder().append("Canal\n").append(this.marco()).append("\nTubo 2\u215c x 1\n").append(this.tuboPuente());
            object = object.append("\nTubo \u25a1 3 cm\n").append(this.tres());
            object = object.append("\nTubo \u25a1 1\u00bd\n").append(this.tresOcho());
            object = object.append("\nTope\n").append(this.tope());
            object = object.append("\nPanel\n").append(this.panel());
            object = object.append("\nVidrios\n").append(this.vidrio()).toString();
        } else {
            activityMainBinding = this.binding;
            object = activityMainBinding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = null;
            }
            if (((ActivityMainBinding)object).rbPb.isChecked()) {
                object = new StringBuilder().append("Canal\n").append(this.marco()).append("\nTubo 2\u215c x 1\n").append(this.tuboPuente());
                object = object.append("\nPaflon\n").append(this.paflon());
                object = object.append("\nTubo \u25a1 1\n").append(this.uno());
                object = object.append("\nRiel\n").append(this.riel());
                object = object.append("\nTope\n").append(this.tope());
                object = object.append("\nVidrios\n").append(this.vidrio()).toString();
            } else {
                object = this.binding;
                if (object == null) {
                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                    object = var2_2;
                }
                if (((ActivityMainBinding)object).rbPp.isChecked()) {
                    object = new StringBuilder().append("Marco Plegado\n").append(this.marcoM()).append("\nContramarco\n").append(this.contraMarco()).append("\nBandejas\n").append(this.bandejas());
                    object = object.append("\nFierro \u25a1 3\u00bc\n").append(this.tresDos());
                    object = object.append("\nPanel\n").append(this.panel());
                    object = object.append("\nPlatina\n").append(this.platina());
                    object = object.append("\nPlancha\n").append(this.pBandejas()).toString();
                } else {
                    object = "";
                }
            }
        }
        return object;
    }

    private final float hojaH() {
        ActivityMainBinding activityMainBinding;
        block6: {
            block11: {
                float f;
                block13: {
                    block12: {
                        Object var3_2;
                        ActivityMainBinding activityMainBinding2;
                        block8: {
                            block10: {
                                block9: {
                                    block4: {
                                        block7: {
                                            block5: {
                                                activityMainBinding2 = this.binding;
                                                var3_2 = null;
                                                activityMainBinding = activityMainBinding2;
                                                if (activityMainBinding2 == null) {
                                                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                                                    activityMainBinding = null;
                                                }
                                                f = Float.parseFloat((String)activityMainBinding.etHoja.getText().toString());
                                                activityMainBinding = activityMainBinding2 = this.binding;
                                                if (activityMainBinding2 == null) {
                                                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                                                    activityMainBinding = null;
                                                }
                                                if (!activityMainBinding.rbPh.isChecked()) break block4;
                                                activityMainBinding = this.med2();
                                                Intrinsics.checkNotNull((Object)activityMainBinding);
                                                if (!(f >= activityMainBinding.floatValue())) break block5;
                                                activityMainBinding2 = this.med2();
                                                activityMainBinding = var3_2;
                                                if (activityMainBinding2 == null) break block6;
                                                f = activityMainBinding2.floatValue() - this.marco;
                                                break block7;
                                            }
                                            f = this.hoja;
                                        }
                                        activityMainBinding = Float.valueOf((float)f);
                                        break block6;
                                    }
                                    activityMainBinding = activityMainBinding2 = this.binding;
                                    if (activityMainBinding2 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                                        activityMainBinding = null;
                                    }
                                    if (!activityMainBinding.rbPb.isChecked()) break block8;
                                    activityMainBinding = this.med2();
                                    Intrinsics.checkNotNull((Object)activityMainBinding);
                                    if (!(f >= activityMainBinding.floatValue())) break block9;
                                    activityMainBinding2 = this.med2();
                                    activityMainBinding = var3_2;
                                    if (activityMainBinding2 == null) break block6;
                                    f = activityMainBinding2.floatValue() - this.marco;
                                    break block10;
                                }
                                f = this.hoja;
                            }
                            activityMainBinding = Float.valueOf((float)f);
                            break block6;
                        }
                        activityMainBinding = activityMainBinding2 = this.binding;
                        if (activityMainBinding2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                            activityMainBinding = null;
                        }
                        if (!activityMainBinding.rbPp.isChecked()) break block11;
                        activityMainBinding = this.med2();
                        Intrinsics.checkNotNull((Object)activityMainBinding);
                        if (!(f >= activityMainBinding.floatValue())) break block12;
                        activityMainBinding2 = this.med2();
                        activityMainBinding = var3_2;
                        if (activityMainBinding2 == null) break block6;
                        f = activityMainBinding2.floatValue() - (float)3;
                        break block13;
                    }
                    f = this.hoja + 0.2f;
                }
                activityMainBinding = Float.valueOf((float)f);
                break block6;
            }
            activityMainBinding = Float.valueOf((float)0.0f);
        }
        Intrinsics.checkNotNull((Object)activityMainBinding);
        return activityMainBinding.floatValue();
    }

    private final float hojaV() {
        ActivityMainBinding activityMainBinding = this.binding;
        Object var3_2 = null;
        ActivityMainBinding activityMainBinding2 = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        float f = Float.parseFloat((String)activityMainBinding2.etMarco.getText().toString());
        activityMainBinding = this.med1();
        activityMainBinding2 = var3_2;
        if (activityMainBinding != null) {
            activityMainBinding2 = Float.valueOf((float)(activityMainBinding.floatValue() - ((float)2 * f + 1.0f + 0.3f)));
        }
        Intrinsics.checkNotNull((Object)activityMainBinding2);
        return activityMainBinding2.floatValue();
    }

    private final String marco() {
        float f;
        Float f2 = this.med2();
        Object object = this.med1();
        Object var3_3 = null;
        object = object != null ? Float.valueOf((float)(object.floatValue() - (float)2 * this.marco)) : null;
        StringBuilder stringBuilder = new StringBuilder();
        if (object != null) {
            f = ((Number)object).floatValue();
            object = this.df1(Float.valueOf((float)f));
        } else {
            object = null;
        }
        stringBuilder = stringBuilder.append((String)object).append("= 1\n");
        object = var3_3;
        if (f2 != null) {
            f = ((Number)f2).floatValue();
            object = this.df1(Float.valueOf((float)f));
        }
        return stringBuilder.append((String)object).append(" = 2").toString();
    }

    private final String marcoM() {
        Object object = this.med2();
        object = object != null ? Float.valueOf((float)(object.floatValue() - this.marcoV)) : null;
        Float f = this.med1();
        return this.df1(f) + "= 1\n" + this.df1((Float)object) + " = 2";
    }

    private final Float med1() {
        ActivityMainBinding activityMainBinding;
        ActivityMainBinding activityMainBinding2 = activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        return StringsKt.toFloatOrNull((String)activityMainBinding2.etMed1.getText().toString());
    }

    private final Float med2() {
        ActivityMainBinding activityMainBinding;
        ActivityMainBinding activityMainBinding2 = activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        return StringsKt.toFloatOrNull((String)activityMainBinding2.etMed2.getText().toString());
    }

    private final void mostrarDialogoEliminar(int n) {
        ArchiveItem archiveItem = (ArchiveItem)this.archiveList.get(n);
        new AlertDialog.Builder((Context)this).setTitle((CharSequence)"Eliminar Archivo").setMessage((CharSequence)("\u00bfEst\u00e1 seguro de que desea eliminar el archivo: puerta" + archiveItem.getNumber() + '?')).setPositiveButton((CharSequence)"S\u00ed", (DialogInterface.OnClickListener)new MainActivity$$ExternalSyntheticLambda2(this, n)).setNegativeButton((CharSequence)"No", (DialogInterface.OnClickListener)new MainActivity$$ExternalSyntheticLambda3()).create().show();
    }

    private static final void mostrarDialogoEliminar$lambda$3(MainActivity mainActivity, int n, DialogInterface dialogInterface, int n2) {
        Intrinsics.checkNotNullParameter((Object)((Object)mainActivity), (String)"this$0");
        mainActivity.eliminarElemento(n);
        dialogInterface.dismiss();
    }

    private static final void mostrarDialogoEliminar$lambda$4(DialogInterface dialogInterface, int n) {
        dialogInterface.dismiss();
    }

    private static final boolean onCreate$lambda$0(MainActivity mainActivity, View view) {
        Intrinsics.checkNotNullParameter((Object)((Object)mainActivity), (String)"this$0");
        mainActivity.exportarDatos();
        return true;
    }

    private final String pBandejas() {
        Object object = this.binding;
        Object var7_2 = null;
        Object object2 = object;
        if (object == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object2 = null;
        }
        float f = Float.parseFloat((String)((ActivityMainBinding)object2).etMarco.getText().toString());
        float f2 = this.hojaH() - 1.2f;
        float f3 = 2;
        float f4 = this.tresDos;
        float f5 = this.hojaV();
        object = "27.8 x " + this.df1(Float.valueOf((float)(f2 - this.tresDos * f3 + f3)));
        object2 = this.df1(Float.valueOf((float)((f2 - f4 * f3 - this.tresDos * (float)4) / (float)5 + f3))) + " x " + this.df1(Float.valueOf((float)(f5 - (this.tresDos * f3 + 28.8f) + f3)));
        object = new StringBuilder().append((String)object).append(" =1\n").append((String)object2).append(" = 5\n");
        object2 = this.med1();
        object2 = object2 != null ? Float.valueOf((float)(object2.floatValue() - f * f3 + f3)) : null;
        object = object.append(this.df1((Float)object2)).append(" x ");
        Float f6 = this.med2();
        object2 = var7_2;
        if (f6 != null) {
            object2 = Float.valueOf((float)(f6.floatValue() - (f2 + f + this.tresDos + 1.5f) + f3));
        }
        object2 = object.append(this.df1((Float)object2));
        return object2.append(" =1").toString();
    }

    private final String paflon() {
        float f = this.hojaH();
        float f2 = 1.0f;
        Float f3 = this.med1();
        if (f3 != null) {
            float f4 = f3.floatValue();
            float f5 = 2;
            f3 = Float.valueOf((float)(f4 - (this.marco * f5 + f2) - f5 * this.paflon));
        } else {
            f3 = null;
        }
        return this.df1(Float.valueOf((float)(f - f2))) + " = 2\n" + this.df1(f3) + " = 2";
    }

    private final String panel() {
        float f = this.hojaH() - 1.2f;
        float f2 = this.panelRefVertical();
        float f3 = this.panelRefHorizontal();
        ActivityMainBinding activityMainBinding = this.binding;
        StringBuilder stringBuilder = null;
        Object var5_6 = null;
        Object object = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        if (((ActivityMainBinding)object).rbPp.isChecked()) {
            object = new StringBuilder().append(this.df1(Float.valueOf((float)f))).append(" x ").append(this.df1(Float.valueOf((float)30.0f))).append(" = 2\n").append(this.df1(Float.valueOf((float)f3)));
            object = object.append(" x ").append(this.df1(Float.valueOf((float)(f2 - 0.1f))));
            stringBuilder = object.append(" = 8\n");
            object = this.med2();
            object = object != null ? Float.valueOf((float)(object.floatValue() - (this.marcoV + 1.5f + f))) : null;
            object = stringBuilder.append(this.df1((Float)object));
            stringBuilder = object.append(" x ");
            activityMainBinding = this.med1();
            object = var5_6;
            if (activityMainBinding != null) {
                object = Float.valueOf((float)(activityMainBinding.floatValue() - this.marcoV * (float)2));
            }
            object = stringBuilder.append(this.df1((Float)object));
            object = object.append(" =2").toString();
        } else {
            object = this.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = stringBuilder;
            }
            if (((ActivityMainBinding)object).rbPh.isChecked()) {
                object = new StringBuilder().append(this.df1(Float.valueOf((float)f))).append(" x ").append(16.2f).append(" = 2\n").append(this.df1(Float.valueOf((float)f3))).append(" x ").append(this.df1(Float.valueOf((float)f2))).append(" = 4\n ").append(this.df1(Float.valueOf((float)((f3 - (float)21) / (float)2))));
                object = object.append(" x ").append(this.df1(Float.valueOf((float)f2)));
                object = object.append(" = 12").toString();
            } else {
                object = "";
            }
        }
        return object;
    }

    private final float panelRefHorizontal() {
        Object object = this.med1();
        Object var5_2 = null;
        Object var4_3 = null;
        Float f = object != null ? Float.valueOf((float)(object.floatValue() - ((float)2 * this.marco + 1.0f))) : null;
        float f2 = this.hojaV();
        ActivityMainBinding activityMainBinding = this.binding;
        object = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        if (object.rbPh.isChecked()) {
            object = var5_2;
            if (f != null) {
                object = Float.valueOf((float)(f.floatValue() - (16.2f + 0.6f)));
            }
        } else {
            object = this.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = var4_3;
            }
            object = object.rbPp.isChecked() ? Float.valueOf((float)(f2 - (30.0f + 0.6f))) : Unit.INSTANCE;
        }
        Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.Float");
        return object.floatValue();
    }

    private final float panelRefVertical() {
        return (this.hojaH() - 1.0f - (float)4 * 0.6f) / (float)5;
    }

    private final String platina() {
        float f = this.hojaH();
        return this.df1(Float.valueOf((float)(f - 1.2f))) + " = 2";
    }

    private final void radio() {
        ActivityMainBinding activityMainBinding = this.binding;
        Object var2_2 = null;
        ActivityMainBinding activityMainBinding2 = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        if (!activityMainBinding2.rbPp.isChecked()) {
            activityMainBinding2 = activityMainBinding = this.binding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                activityMainBinding2 = null;
            }
            if (!activityMainBinding2.rbPb.isChecked()) {
                activityMainBinding2 = activityMainBinding = this.binding;
                if (activityMainBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                    activityMainBinding2 = null;
                }
                if (!activityMainBinding2.rbPh.isChecked()) {
                    activityMainBinding2 = activityMainBinding = this.binding;
                    if (activityMainBinding == null) {
                        Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                        activityMainBinding2 = null;
                    }
                    activityMainBinding2.rbPp.setChecked(true);
                }
            }
        }
        activityMainBinding2 = activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        activityMainBinding2.rbPp.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)new MainActivity$$ExternalSyntheticLambda4(this));
        activityMainBinding2 = activityMainBinding = this.binding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        activityMainBinding2.rbPb.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)new MainActivity$$ExternalSyntheticLambda5(this));
        activityMainBinding2 = this.binding;
        if (activityMainBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = var2_2;
        }
        activityMainBinding2.rbPh.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)new MainActivity$$ExternalSyntheticLambda6(this));
    }

    private static final void radio$lambda$5(MainActivity object, CompoundButton object2, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"this$0");
        if (bl) {
            ActivityMainBinding activityMainBinding = object.binding;
            Object var3_4 = null;
            object2 = activityMainBinding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object2 = null;
            }
            object2.rbPb.setChecked(false);
            object = object.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = var3_4;
            }
            ((ActivityMainBinding)object).rbPh.setChecked(false);
        }
    }

    private static final void radio$lambda$6(MainActivity object, CompoundButton object2, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"this$0");
        if (bl) {
            ActivityMainBinding activityMainBinding = object.binding;
            Object var3_4 = null;
            object2 = activityMainBinding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object2 = null;
            }
            object2.rbPp.setChecked(false);
            object = object.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = var3_4;
            }
            ((ActivityMainBinding)object).rbPh.setChecked(false);
        }
    }

    private static final void radio$lambda$7(MainActivity object, CompoundButton object2, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"this$0");
        if (bl) {
            ActivityMainBinding activityMainBinding = object.binding;
            Object var3_4 = null;
            object2 = activityMainBinding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object2 = null;
            }
            object2.rbPp.setChecked(false);
            object = object.binding;
            if (object == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object = var3_4;
            }
            ((ActivityMainBinding)object).rbPb.setChecked(false);
        }
    }

    private final String riel() {
        float f = this.hojaH();
        float f2 = 1.0f;
        float f3 = 2;
        float f4 = this.paflon;
        Object object = this.med1();
        object = object != null ? Float.valueOf((float)(object.floatValue() - (this.marco * f3 + f2) - f3 * this.paflon)) : null;
        return this.df1(Float.valueOf((float)(f - f2 - f4 * f3))) + " = 2\n" + this.df1((Float)object) + " = 2";
    }

    private final String tope() {
        float f = this.hojaH();
        Object object = this.med1();
        Float f2 = null;
        object = object != null ? Float.valueOf((float)(object.floatValue() - (float)2 * this.marco)) : null;
        Float f3 = this.med2();
        if (f3 != null) {
            f2 = Float.valueOf((float)(f3.floatValue() - (this.hoja + this.tubo + this.marco + (float)2 * this.tope)));
        }
        f3 = new StringBuilder().append(this.df1(Float.valueOf((float)f))).append(" = 2\n");
        Intrinsics.checkNotNull((Object)object);
        object = f3.append(this.df1((Float)object)).append(" = 3\n");
        Intrinsics.checkNotNull((Object)f2);
        return object.append(this.df1(f2)).append(" =2").toString();
    }

    private final String tres() {
        float f;
        float f2 = this.hojaH();
        float f3 = 1.0f;
        f2 -= f3;
        Float f4 = this.med1();
        Float f5 = null;
        if (f4 != null) {
            float f6 = f4.floatValue();
            f = 2;
            f4 = Float.valueOf((float)(f6 - (this.marco * f + f3) - f * this.tres));
        } else {
            f4 = null;
        }
        f = 2;
        f3 = this.tres;
        if (f4 != null) {
            f5 = Float.valueOf((float)(f4.floatValue() - (float)15));
        }
        f4 = new StringBuilder().append(this.df1(Float.valueOf((float)f2))).append(" = 2\n").append(this.df1(f4)).append(" = 2\n").append(this.df1(Float.valueOf((float)(f2 - f3 * f)))).append(" = 1\n").append(this.df1(f5)).append(" = 2\n").append(this.df1(Float.valueOf((float)((this.panelRefHorizontal() - (float)21) / f - this.tres))));
        f4 = f4.append(" = 4\n").append(this.df1(Float.valueOf((float)((this.panelRefHorizontal() - (float)21) / f - 1.2f))));
        return f4.append(" = 2").toString();
    }

    private final String tresDos() {
        ActivityMainBinding activityMainBinding = this.binding;
        Object var7_2 = null;
        ActivityMainBinding activityMainBinding2 = activityMainBinding;
        if (activityMainBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            activityMainBinding2 = null;
        }
        float f = Float.parseFloat((String)activityMainBinding2.etMarco.getText().toString());
        float f2 = this.hojaH() - 1.2f;
        float f3 = this.hojaV();
        float f4 = 2;
        float f5 = this.tresDos;
        activityMainBinding = new StringBuilder().append(this.df1(Float.valueOf((float)f2))).append(" = 2\n").append(this.df1(Float.valueOf((float)(f2 - f5 * f4)))).append(" = 1\n").append(this.df1(Float.valueOf((float)f3))).append(" = 2\n").append(this.df1(Float.valueOf((float)(f3 - (this.tresDos * f4 + 28.8f))))).append(" = 4\n");
        Float f6 = this.med1();
        activityMainBinding2 = var7_2;
        if (f6 != null) {
            activityMainBinding2 = Float.valueOf((float)(f6.floatValue() - f4 * f));
        }
        activityMainBinding2 = activityMainBinding.append(this.df1((Float)activityMainBinding2));
        return activityMainBinding2.append(" = 1\n9 = 2").toString();
    }

    private final String tresOcho() {
        float f = this.panelRefVertical();
        float f2 = 2;
        float f3 = this.hojaH();
        return this.df1(Float.valueOf((float)(f3 - 1.07f - (f * f2 + 1.2f)))) + " = 2\n21 = 2";
    }

    private final String tuboPuente() {
        Object object = this.med1();
        object = object != null ? Float.valueOf((float)(object.floatValue() - (float)2 * this.marco)) : null;
        StringBuilder stringBuilder = new StringBuilder();
        Intrinsics.checkNotNull((Object)object);
        return stringBuilder.append(this.df1((Float)object)).append(" = 1").toString();
    }

    private final String uno() {
        float f;
        float f2;
        float f3 = this.hojaH();
        float f4 = 1.0f;
        Float f5 = this.med1();
        Float f6 = null;
        if (f5 != null) {
            f2 = f5.floatValue();
            f = 2;
            f5 = Float.valueOf((float)(f2 - (this.marco * f + f4) - f * this.paflon));
        } else {
            f5 = null;
        }
        f = 2;
        f2 = this.paflon;
        if (f5 != null) {
            f6 = Float.valueOf((float)(f5.floatValue() - ((float)12 + this.tubo)));
        }
        return this.df1(Float.valueOf((float)(f3 - f4 - f * f2))) + " = 1\n" + this.df1(f6) + "=4";
    }

    private final String vidrio() {
        Object object;
        Float f = this.med2();
        Object object2 = this.med1();
        Object var10_3 = null;
        Object var11_4 = null;
        object2 = object2 != null ? Float.valueOf((float)(object2.floatValue() - ((float)2 * this.marco + 0.4f))) : null;
        Object object3 = this.med2();
        Float f2 = object3 != null ? Float.valueOf((float)(object3.floatValue() - (this.hoja + this.tubo + 1.0f + this.marco + 0.4f))) : null;
        float f3 = this.hojaH();
        float f4 = 1.0f;
        float f5 = 2;
        float f6 = (f3 -= f4) - this.paflon * f5;
        object3 = this.med1();
        object3 = object3 != null ? Float.valueOf((float)(object3.floatValue() - (this.marco * f5 + f4) - f5 * this.paflon)) : null;
        Object object4 = object = this.binding;
        if (object == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object4 = null;
        }
        boolean bl = ((ActivityMainBinding)object4).rbPh.isChecked();
        object = "";
        if (bl) {
            Intrinsics.checkNotNull((Object)f);
            object2 = f3 >= f.floatValue() ? object : this.df1((Float)object2) + " x " + this.df1(f2) + " = 1";
        } else {
            ActivityMainBinding activityMainBinding = this.binding;
            object4 = activityMainBinding;
            if (activityMainBinding == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                object4 = null;
            }
            if (((ActivityMainBinding)object4).rbPp.isChecked()) {
                object2 = object;
            } else {
                object4 = object = this.binding;
                if (object == null) {
                    Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
                    object4 = null;
                }
                if (((ActivityMainBinding)object4).rbPb.isChecked()) {
                    Intrinsics.checkNotNull((Object)f);
                    if (f3 < f.floatValue()) {
                        f2 = new StringBuilder().append(this.df1((Float)object2)).append(" x ").append(this.df1(f2)).append(" = 1\n");
                        object2 = var11_4;
                        if (object3 != null) {
                            object2 = Float.valueOf((float)(object3.floatValue() - 0.5f));
                        }
                        object2 = f2.append(this.df1((Float)object2));
                        object2 = object2.append(" x ").append(this.df1(Float.valueOf((float)(f6 - 0.4f))));
                        object2 = object2.append(" = 1").toString();
                    } else {
                        f2 = new StringBuilder();
                        object2 = var10_3;
                        if (object3 != null) {
                            object2 = Float.valueOf((float)(object3.floatValue() - 0.5f));
                        }
                        object2 = f2.append(this.df1((Float)object2)).append(" x ").append(this.df1(Float.valueOf((float)(f6 - 0.4f)))).append(" = 1").toString();
                    }
                } else {
                    object2 = Unit.INSTANCE;
                }
            }
        }
        return object2.toString();
    }

    protected void onCreate(Bundle object) {
        super.onCreate((Bundle)object);
        object = ActivityMainBinding.inflate(this.getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"inflate(...)");
        Object object2 = this.binding = object;
        Object var2_3 = null;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        this.setContentView((View)((ActivityMainBinding)object).getRoot());
        object2 = this.binding;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        ((ActivityMainBinding)object).etMed1.requestFocus();
        this.archiveAdapter = new ArchiveAdapter(this.archiveList, (Function1<? super Integer, Unit>)((Function1)new Function1<Integer, Unit>(this){
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
                super(1);
            }

            public final void invoke(int n) {
                MainActivity.access$mostrarDialogoEliminar(this.this$0, n);
            }
        }));
        object2 = this.binding;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        RecyclerView recyclerView = ((ActivityMainBinding)object).rvMateriales;
        object2 = this.archiveAdapter;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"archiveAdapter");
            object = null;
        }
        recyclerView.setAdapter((RecyclerView.Adapter)object);
        object2 = this.binding;
        object = object2;
        if (object2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = null;
        }
        ((ActivityMainBinding)object).rvMateriales.setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager((Context)this));
        object = this.binding;
        if (object == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"binding");
            object = var2_3;
        }
        ((ActivityMainBinding)object).btArchivar.setOnLongClickListener((View.OnLongClickListener)new MainActivity$$ExternalSyntheticLambda7(this));
        this.configurarBotones();
        this.radio();
    }

    @Metadata(d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0018B6\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012!\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0005J\b\u0010\u0010\u001a\u00020\bH\u0016J\u0018\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\bH\u0016J\u0018\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\bH\u0016J\u000e\u0010\u0017\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\bR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R)\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2={"LJose/jose/MainActivity$ArchiveAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "LJose/jose/MainActivity$ArchiveAdapter$ArchiveViewHolder;", "archiveList", "", "LJose/jose/MainActivity$ArchiveItem;", "onItemClick", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "position", "", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;)V", "addArchiveItem", "archiveItem", "getItemCount", "onBindViewHolder", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "removeArchiveItem", "ArchiveViewHolder", "app_debug"}, k=1, mv={1, 9, 0}, xi=48)
    public static final class ArchiveAdapter
    extends RecyclerView.Adapter<ArchiveViewHolder> {
        private final List<ArchiveItem> archiveList;
        private final Function1<Integer, Unit> onItemClick;

        public static /* synthetic */ void $r8$lambda$ujRFbE4ZkdO_ZQ_stBEAdG3ewA4(ArchiveAdapter archiveAdapter, int n, View view) {
            ArchiveAdapter.onBindViewHolder$lambda$0(archiveAdapter, n, view);
        }

        public ArchiveAdapter(List<ArchiveItem> list, Function1<? super Integer, Unit> function1) {
            Intrinsics.checkNotNullParameter(list, (String)"archiveList");
            Intrinsics.checkNotNullParameter(function1, (String)"onItemClick");
            this.archiveList = list;
            this.onItemClick = function1;
        }

        private static final void onBindViewHolder$lambda$0(ArchiveAdapter archiveAdapter, int n, View view) {
            Intrinsics.checkNotNullParameter((Object)((Object)archiveAdapter), (String)"this$0");
            archiveAdapter.onItemClick.invoke((Object)n);
        }

        public final void addArchiveItem(ArchiveItem archiveItem) {
            Intrinsics.checkNotNullParameter((Object)archiveItem, (String)"archiveItem");
            this.archiveList.add(0, (Object)archiveItem);
            this.notifyItemInserted(0);
        }

        public int getItemCount() {
            return this.archiveList.size();
        }

        public void onBindViewHolder(ArchiveViewHolder archiveViewHolder, int n) {
            Intrinsics.checkNotNullParameter((Object)((Object)archiveViewHolder), (String)"holder");
            Object object = (ArchiveItem)this.archiveList.get(n);
            object = "puerta" + ((ArchiveItem)object).getNumber() + '\n' + ((ArchiveItem)object).getContent();
            archiveViewHolder.getTextViewContent().setText((CharSequence)object);
            archiveViewHolder.itemView.setOnClickListener((View.OnClickListener)new MainActivity$ArchiveAdapter$$ExternalSyntheticLambda0(this, n));
        }

        public ArchiveViewHolder onCreateViewHolder(ViewGroup viewGroup, int n) {
            Intrinsics.checkNotNullParameter((Object)viewGroup, (String)"parent");
            viewGroup = LayoutInflater.from((Context)viewGroup.getContext()).inflate(R.layout.item_archive, viewGroup, false);
            Intrinsics.checkNotNull((Object)viewGroup);
            return new ArchiveViewHolder((View)viewGroup);
        }

        public final void removeArchiveItem(int n) {
            if (n >= 0 && n < this.archiveList.size()) {
                this.archiveList.remove(n);
                this.notifyItemRemoved(n);
            }
        }

        @Metadata(d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"LJose/jose/MainActivity$ArchiveAdapter$ArchiveViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "textViewContent", "Landroid/widget/TextView;", "getTextViewContent", "()Landroid/widget/TextView;", "app_debug"}, k=1, mv={1, 9, 0}, xi=48)
        public static final class ArchiveViewHolder
        extends RecyclerView.ViewHolder {
            private final TextView textViewContent;

            public ArchiveViewHolder(View view) {
                Intrinsics.checkNotNullParameter((Object)view, (String)"itemView");
                super(view);
                view = view.findViewById(R.id.textViewContent);
                Intrinsics.checkNotNullExpressionValue((Object)view, (String)"findViewById(...)");
                this.textViewContent = (TextView)view;
            }

            public final TextView getTextViewContent() {
                return this.textViewContent;
            }
        }
    }

    @Metadata(d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0013"}, d2={"LJose/jose/MainActivity$ArchiveItem;", "", "number", "", "content", "", "(ILjava/lang/String;)V", "getContent", "()Ljava/lang/String;", "getNumber", "()I", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"}, k=1, mv={1, 9, 0}, xi=48)
    public static final class ArchiveItem {
        private final String content;
        private final int number;

        public ArchiveItem(int n, String string2) {
            Intrinsics.checkNotNullParameter((Object)string2, (String)"content");
            this.number = n;
            this.content = string2;
        }

        public static /* synthetic */ ArchiveItem copy$default(ArchiveItem archiveItem, int n, String string2, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = archiveItem.number;
            }
            if ((n2 & 2) != 0) {
                string2 = archiveItem.content;
            }
            return archiveItem.copy(n, string2);
        }

        public final int component1() {
            return this.number;
        }

        public final String component2() {
            return this.content;
        }

        public final ArchiveItem copy(int n, String string2) {
            Intrinsics.checkNotNullParameter((Object)string2, (String)"content");
            return new ArchiveItem(n, string2);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof ArchiveItem)) {
                return false;
            }
            object = (ArchiveItem)object;
            if (this.number != ((ArchiveItem)object).number) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.content, (Object)((ArchiveItem)object).content);
        }

        public final String getContent() {
            return this.content;
        }

        public final int getNumber() {
            return this.number;
        }

        public int hashCode() {
            return Integer.hashCode((int)this.number) * 31 + this.content.hashCode();
        }

        public String toString() {
            return "ArchiveItem(number=" + this.number + ", content=" + this.content + ')';
        }
    }
}

