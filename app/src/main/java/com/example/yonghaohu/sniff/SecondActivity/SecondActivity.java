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

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        this.context = getApplicationContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.second_layout);
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

       // ClassicPcap test = new ClassicPcap();
       // String pcptmp = test.echo();
       // AlertDialog.Builder builder1 = new AlertDialog.Builder(SecondActivity.this);
       // builder1.setMessage("pcptmp is "+pcptmp);
       // builder1.show();

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
        for (Program aa2 : transfer_list_program) {
            StringBuilder sb = new StringBuilder();
            sb.append(aa2.getName() + "in act2, pid is " + aa2.getPid() + "\n");
            AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
            builder2.setMessage(sb);
            builder2.show();
        }

        Button start_button = (Button)findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartSniff(transfer_list_program, is_Checked);

            }
        });

        Button stop_button = (Button)findViewById(R.id.stop_button);
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SniffPackets sniffpackets = new SniffPackets(context);//catch origin
                sniffpackets.stopTCPdump(context);
//                if(isWifi(context))

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
        if(is_Checked == true) {
            SniffPackets sniffpackets = new SniffPackets(this.context);//catch origin
            sniffpackets.startTCPdump(this.context);
            Log.d("SniffPackets end", "HELLO");
        } else {
            Log.d("SocketSummary", "HELLO");
            SocketSummary(transfer_list_program);
        }
    }

    protected void SocketSummary(List<Program> transfer_list_program) {
        PollData polldata = new PollData();
        String apkRoot= getPackageCodePath();
        AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
        String returnString = "";
       if(RootCmd(apkRoot) == "false") {
           builder2.setMessage("can not root");
           builder2.show();
       }
       if(RootCmd("/proc/") == "false") {
           builder2.setMessage("can not root all");
           builder2.show();
       }

        for (int i = 0; i < transfer_list_program.size(); i++) {
            String path = "/proc/" + transfer_list_program.get(i).getPid() + "/fd";
            String cmd =  "mkdir /sdcard/Android/data/com.example.yonghaohu.sniff";//  netstat -alp tcp      chmod -R 777
            if(RootCmd(cmd) == "false") {
                builder2.setMessage("rootcmd mkdir false\n");
                builder2.show();
            }else {
                cmd =  "chmod 777 /sdcard/Android/data/com.example.yonghaohu.sniff";//  netstat -alp tcp      chmod -R 777
                if(RootCmd(cmd) == "false") {
                    builder2.setMessage("chmod false\n");
                    builder2.show();
                }else{
                    builder2.setMessage("mkdir and chomod success\n");
                    builder2.show();
                }
            }
            cmd =  "ls -l "+path+" >  /sdcard/Android/data/com.example.yonghaohu.sniff/fdres";//  netstat -alp tcp      chmod -R 777 ./myresult/fdres
            if(RootCmd(cmd) == "false") {
                builder2.setMessage("rootcmd false\n");
                builder2.show();
            }else {
                builder2.show();
                cmd = "cat  /sdcard/Android/data/com.example.yonghaohu.sniff/fdres";
                returnString = RootCmd(cmd);
                if(returnString == "false") {
                    builder2.setMessage("cat false\n");
                    builder2.show();
                }else {

                    builder2.setMessage(cmd+" then "+returnString+" end\n");
                    builder2.show();
                    String res_content = new String();
                    ArrayList<Integer> res_of_socket = new ArrayList<Integer>();
                    res_of_socket = polldata.ParseOutput(returnString);
                    if(res_of_socket.size() == 0)
                        res_content = "aaaaaaaaaaaa";
                    else {
                        res_content += "\ntcp start: ";
                        res_content += polldata.testNoListeningTcpPorts(res_of_socket);
                        res_content += "\ntcp6 start:";
                        res_content += polldata.testNoListeningTcp6Ports(res_of_socket);
                    }
                   // writeFileSdcardFile("/sdcard/Android/data/com.example.yonghaohu.sniff/socketres", res_content);
                    cmd = "echo \"" + res_content + "\" " + "/sdcard/Android/data/com.example.yonghaohu.sniff/socketres";
                    returnString = RootCmd(cmd);
                    builder2.setMessage(returnString);
                    builder2.show();
                }
            }

//            NdkJniUtils jni = new NdkJniUtils();
//            builder2.setMessage("Is file readable :" + scriptfile.canRead()+" finishcall");
//            builder2.show();
         //   String res = jni.getCLanguageString(path);
         //   builder2.setMessage(res);
         //   builder2.show();

//            try {
//                AccessController.checkPermission(new FilePermission(path, "read"));
//                StringBuilder sb = new StringBuilder();
//                sb.append("You have  permition: "+path+"\n"+transfer_list_program.get(i).getName()+"\n");
//                File dir = new File(path);
//                File file[] = dir.listFiles();
//                AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
//                if(file == null || file.length == 0) {
//                   String cmd =  "netstat -apeen ";// lsof -i -w tcp
//
//                    String returnString = resultExeCmd(cmd);
//                   builder2.setMessage(cmd+"then "+returnString+" end\n");
//                   builder2.show();
//                    String apkRoot= getPackageCodePath();
//                    builder2.setMessage("目前路径 ： "+ apkRoot);
//                    builder2.show();
//                    if(RootCmd(apkRoot) != true) {
//                        builder2.setMessage("can not root");
//                        builder2.show();
//                    }
//                     cmd = "ls -l " + path;
//                     returnString = resultExeCmd(cmd);
//                    if(returnString.length() != 0 || returnString != null) {
//                        builder2.setMessage(cmd+"then "+returnString);
//                        builder2.show();
//                        char first = 'l';//returnString.charAt(0);
//                        if(first != 'l')
//                            returnString = "普通文件";
//                    }else
//                        returnString = "NULL";
//                    sb.append(returnString +"\n");
//                }
//                else {
//                    String res_content = new String();
//                    ArrayList<Integer> res_of_socket = new ArrayList<Integer>();
//                    res_of_socket = polldata.ParsePid(transfer_list_program.get(i).getPid());
//                    if(res_of_socket == null)
//                        res_content = "aaaaaaaaaaaa";
//                    else {
//                        res_content = polldata.testNoListeningTcpPorts(res_of_socket);
//                        res_content += "finish";
//                    }
//                    sb.append(res_content);
//                }
//                builder2.setMessage(sb);
//                builder2.show();
//            } catch (SecurityException e) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("You have no  permition to use : "+path);
//                AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
//                builder2.setMessage(sb);
//                builder2.show();
//
//            }


        }
    }

    public static String resultExeCmd(String cmd) {
        String returnString = "";
        Process pro = null;
        Runtime runTime = Runtime.getRuntime();
        if (runTime == null) {
            System.err.println("Create runtime false!");
        }
        try {
            pro = runTime.exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                returnString = returnString + line + "\n";
            }
            input.close();
            output.close();
            pro.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnString;
    }

    public String RootCmd(String cmd){
        Process process = null;
        DataOutputStream os = null;
        String returnString = cmd+"\noutput is : ";
        try{
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd+ "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return "false";
        } finally {
            try {
                if (os != null)   {
                    os.close();
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SecondActivity.this);
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                PrintWriter output = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    returnString = returnString + line + "\n";
                }
                //builder2.setMessage(returnString);
                //builder2.show();
                input.close();
                output.close();

                process.destroy();
            } catch (Exception e) {
            }
        }
        return returnString;
    }

    public void writeFileSdcardFile(String fileName,String write_str){
        try{
            FileOutputStream fout = new FileOutputStream(fileName);
            byte [] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        }
        catch(Exception e){
            e.printStackTrace();
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

    private static boolean upload_file(Context mContext) {
        String filePath = "", url="";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        File file = new File(filePath);
        if(file == null){
        }
        EntityBuilder multipartEntity = EntityBuilder.create();
        multipartEntity.setFile(file);
        BasicHttpEntityHC4 entity = (BasicHttpEntityHC4)multipartEntity.build();

        HttpPostHC4 httpPostHC4 = new HttpPostHC4(url);
        httpPostHC4.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPostHC4);

    }
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

