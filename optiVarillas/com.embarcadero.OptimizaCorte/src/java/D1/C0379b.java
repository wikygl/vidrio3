package d1;

import S0.DialogInterface$OnClickListenerC0254f;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import androidx.core.content.FileProvider;
import b1.C0353a;
import com.google.gson.Gson;
import com.google.gson.i;
import com.google.gson.internal.Excluder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/* renamed from: d1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0379b {
    public static void a(Context context, C0353a c0353a, String str) {
        FileWriter fileWriter;
        Excluder excluder = Excluder.o;
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        LinkedList linkedList = new LinkedList();
        ArrayList arrayList3 = new ArrayList(arrayList2.size() + arrayList.size() + 3);
        arrayList3.addAll(arrayList);
        Collections.reverse(arrayList3);
        ArrayList arrayList4 = new ArrayList(arrayList2);
        Collections.reverse(arrayList4);
        arrayList3.addAll(arrayList4);
        boolean z4 = com.google.gson.internal.sql.a.a;
        Gson gson = new Gson(excluder, new HashMap(hashMap), true, true, new ArrayList(arrayList), new ArrayList(arrayList2), arrayList3, new ArrayList(linkedList));
        File file = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "cutter_report.txt");
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        if (c0353a != null) {
            try {
                gson.h(c0353a, C0353a.class, gson.e(fileWriter));
                fileWriter.flush();
                fileWriter.close();
                Context applicationContext = context.getApplicationContext();
                Uri b4 = FileProvider.c(0, applicationContext, context.getApplicationContext().getPackageName() + ".provider").b(file);
                Intent intent = new Intent("android.intent.action.SEND");
                intent.addFlags(1);
                intent.setType("vnd.android.cursor.dir/email");
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"soldierdeveloper@gmail.com"});
                intent.putExtra("android.intent.extra.STREAM", b4);
                intent.putExtra("android.intent.extra.SUBJECT", "Cutter bug report");
                intent.putExtra("android.intent.extra.TEXT", str);
                context.startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (IOException e5) {
                throw new RuntimeException(e5);
            }
        }
        try {
            gson.g(i.j, gson.e(fileWriter));
            fileWriter.flush();
            fileWriter.close();
            Context applicationContext2 = context.getApplicationContext();
            Uri b42 = FileProvider.c(0, applicationContext2, context.getApplicationContext().getPackageName() + ".provider").b(file);
            Intent intent2 = new Intent("android.intent.action.SEND");
            intent2.addFlags(1);
            intent2.setType("vnd.android.cursor.dir/email");
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{"soldierdeveloper@gmail.com"});
            intent2.putExtra("android.intent.extra.STREAM", b42);
            intent2.putExtra("android.intent.extra.SUBJECT", "Cutter bug report");
            intent2.putExtra("android.intent.extra.TEXT", str);
            context.startActivity(Intent.createChooser(intent2, "Send email..."));
        } catch (IOException e6) {
            throw new RuntimeException(e6);
        }
    }

    public static void b(final Context context, final C0353a c0353a) {
        View inflate = LayoutInflater.from(context).inflate(2131427458, (ViewGroup) null);
        final EditText editText = (EditText) inflate.findViewById(2131230953);
        b.a aVar = new b.a(context);
        AlertController.b bVar = aVar.a;
        bVar.d = "Send report";
        bVar.q = inflate;
        aVar.c("send", new DialogInterface.OnClickListener() { // from class: d1.a
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i4) {
                String obj = editText.getText().toString();
                boolean isEmpty = obj.isEmpty();
                Context context2 = context;
                C0353a c0353a2 = c0353a;
                if (isEmpty) {
                    C0379b.b(context2, c0353a2);
                    dialogInterface.dismiss();
                    return;
                }
                C0379b.a(context2, c0353a2, obj);
            }
        });
        aVar.b("cancel", new DialogInterface$OnClickListenerC0254f(1));
        aVar.a().show();
    }
}
