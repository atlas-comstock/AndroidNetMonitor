package com.example.yonghaohu.sniff.SecondActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.message.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.entity.BasicHttpEntityHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.example.yonghaohu.sniff.R;
import com.example.yonghaohu.sniff.shark.SniffPackets;
import com.example.yonghaohu.sniff.useless.MyFileManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonghaohu on 15/10/22.
 * http://www.tuicool.com/articles/2e6bEj
 * http://m.blog.csdn.net/blog/daye_daye/44752189
 */
public class SecondActivity extends Activity {
    public static final int FILE_RESULT_CODE = 1;

    private TextView textView;
    public String file_path = new String();
    private boolean is_Checked = false;
    private Context context;
    private SniffPackets sniffpackets;
    Button start_button;
    Socket_Sniff socket_sniff;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.second_layout);
        context = getApplicationContext();
        sniffpackets = new SniffPackets(this.context);
        Switch toggle = (Switch) findViewById(R.id.ParamToggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_Checked = true;
                    Log.d("toogle", "The toggle is enabled");
                } else {
                    is_Checked = false;
                    Log.d("toogle", "The toggle is disabled");
                }
            }
        });

        Button button = (Button)findViewById(R.id.choose_dir);
        textView = (TextView)findViewById(R.id.text_view);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MyFileManager.class);
                startActivityForResult(intent, FILE_RESULT_CODE);
            }
        });

        Intent intent = getIntent();
        final List<Program> transfer_list_program = intent.getParcelableArrayListExtra("extra_data");
        socket_sniff = new Socket_Sniff(this.context, transfer_list_program );
        for (Program aa2 : transfer_list_program) {
            StringBuilder sb = new StringBuilder();
            sb.append(aa2.getName() + "in act2, pid is " + aa2.getPid() + "\n");
            AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
            builder2.setMessage(sb);
            builder2.show();
        }

        start_button = (Button)findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartSniff(transfer_list_program, is_Checked);
            }
        });

        Button stop_button = (Button)findViewById(R.id.stop_button);
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_button.setEnabled(true);
                if(isWifi(context)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setMessage("You have Wifi connection now, would you love to upload?");
                    builder.show();
                }
                socket_sniff.stopprocess();
                socket_sniff.interrupt();
                sniffpackets.interrupt();
                Toast.makeText(context, ("Stop Success"),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }


    /* functions helper */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(FILE_RESULT_CODE == requestCode){
            Bundle bundle = null;
            if(data!=null&&(bundle=data.getExtras())!=null){
                textView.setText("存储到文件夹："+bundle.getString("file"));
                file_path = bundle.getString("file");
                file_path += "/socket_output.txt";
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
                builder2.setMessage("path is "+file_path);
                builder2.show();

            }
        }
    }

    protected void StartSniff(List<Program> transfer_list_program, boolean is_Checked) {
        start_button.setEnabled(false);
        Toast.makeText(context, ("Start"),
                Toast.LENGTH_SHORT).show();
        socket_sniff.start();
        if(is_Checked == true) {
            Log.d("SniffPackets end", "HELLO");
            sniffpackets.setcontext(context);
            sniffpackets.start();
        }
    }


    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

//    private static boolean upload_file(Context mContext) {
//        String filePath = "", url="";
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        File file = new File(filePath);
//        if(file == null){
//        }
//        EntityBuilder multipartEntity = EntityBuilder.create();
//        multipartEntity.setFile(file);
//        BasicHttpEntityHC4 entity = (BasicHttpEntityHC4)multipartEntity.build();
//
//        HttpPostHC4 httpPostHC4 = new HttpPostHC4(url);
//        httpPostHC4.setEntity(entity);
//        CloseableHttpResponse response = httpClient.execute(httpPostHC4);

//    }

//    public final static void process(String[] cmdarray) throws Throwable {
//        ProcessBuilder pb = new ProcessBuilder(cmdarray);
//        pb.redirectErrorStream(true);
//        Process p = null;
//        BufferedReader br = null;
//        try {
//            p = pb.start();
//            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line = null;
//            logger.info("Invoke shell: {}", StringUtils.join(cmdarray, " "));
//            while ((line = br.readLine()) != null) {
//                logger.info(line);
//            }
//            p.waitFor();
//        } finally {
//            if (br != null) {
//                br.close();
//            }
//            if (p != null) {
//                p.destroy();
//            }
//        }
//    }


}

