package com.example.yonghaohu.sniff.FirstActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.yonghaohu.sniff.SecondActivity.Program;
import com.example.yonghaohu.sniff.R;
import com.example.yonghaohu.sniff.SecondActivity.SecondActivity;

import java.util.ArrayList;
import java.util.List;

//http://blog.csdn.net/ghd2000/article/details/6128500
//http://www.oschina.net/question/54100_31816?sort=default&p=2#answers
public class MainActivity extends Activity {

    List<Integer> listItemID = new ArrayList<Integer>();
    Button button_sure, select_all_button;
    ListView lv;
    ListAdapter adapter;
    List<Program> list;
    List<Program> transfer_list_program=new ArrayList<Program>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        lv = (ListView)findViewById(R.id.lvprogram);


        list = getRunningProcess();
        adapter = new ListAdapter(list, this);
        lv.setAdapter(adapter);

        Log.d("debug", Integer.toString(adapter.mChecked.size()));

        button_sure = (Button)findViewById(R.id.button1);
        select_all_button = (Button)findViewById(R.id.select_all);
        select_all_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transfer_list_program.clear();
                for(int i=0;i<adapter.mChecked.size();i++){
                    adapter.mChecked.set(i, true);
//                    transfer_list_program.add(list.get(i));
                }
                adapter.notifyDataSetChanged();

            }
        });




        button_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer_list_program.clear();
                for(int i=0;i<list.size();i++){
                    if(adapter.mChecked.get(i))
                        transfer_list_program.add(list.get(i));
                }

                if(transfer_list_program.size() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("没有选中任何记录");
                    builder1.show();
                }else {
//                    PollData polldata = new PollData();
//                    StringBuilder sb = new StringBuilder();
//                        sb.append("ItemID="+listItemID.get(i)+" . PID="+list.get(listItemID.get(i)).getPid());
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
//                    for (int i = 0; i < listItemID.size(); i++) {
//                        list.get(listItemID.get(i)).ge
//                        Log.d("debug", "is " + list.get(listItemID.get(i)).getPid());
//                        builder2.setMessage("Pid Is "+list.get(listItemID.get(i)).getPid());
//                        builder2.show();

//                        ArrayList<Integer> res_of_socket = new ArrayList<Integer>();
//                        res_of_socket = polldata.ParsePid(list.get(listItemID.get(i)).getPid());
//                        polldata.testNoListeningTcpPorts(res_of_socket);
//                    Intent in = new Intent().setClass(MainActivity.this, MainActivity1.class);
                    Log.d("transfer_list_program.size()", "is " + transfer_list_program.size());
                    for (Program aa2 : transfer_list_program) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(aa2.getName()+" pid is "+aa2.getPid()+"\n");
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setMessage(sb);
                        builder2.show();
                    }

                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("extra_data",
                            (ArrayList<? extends Parcelable>) transfer_list_program);
                    intent.setClass(MainActivity.this, SecondActivity.class);
                    MainActivity.this.startActivity(intent);

//                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                    Bundle bu = new Bundle();
//                    bu.putParcelableArrayList("extra_data", (ArrayList<? extends Parcelable>) transfer_list_program);
//                    intent.putExtra("extra_data", bu);
///                    startActivity(intent);
                    Log.d("debug", "end poll data");
                }
            }
        });
    }

    public List<Program> getRunningProcess() {
        PackagesInfo pi = new PackagesInfo(this);

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses(); //通过包名获取程序的图标和程序名
        PackageManager pm = this.getPackageManager();
        List<Program> list = new ArrayList<Program>();

        for (ActivityManager.RunningAppProcessInfo ra : run) {
            //过滤系统的应用和电话应用
            //if (ra.processName.equals("system") || ra.processName.equals("com.android.phone"))
            //    continue;
            if(pi.getInfo(ra.processName)==null)
                continue;

            Program pr = new Program();
            pr.setPid(ra.pid);
            pr.setIcon(pi.getInfo(ra.processName).loadIcon(pm));
            pr.setName(pi.getInfo(ra.processName).loadLabel(pm).toString());
//            System.out.println(pi.getInfo(ra.processName).loadLabel(pm).toString());
            list.add(pr);
        }
        return list;
    }

    /*
    private String getAppName() {
        String processName = "";
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                Log.d("**** my process: ", "Id: " + info.pid + " ProcessName: " + info.processName + "  Label: " + c.toString());
                //processName = c.toString();
                processName = info.processName;
            } catch (Exception e) {
                Log.d("Process", "Error>> :" + e.toString());
            }
        }
        return processName;
    }

    private void getAppName_other() {
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();

        int i = 1;

        for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
            Log.i("Application", (i++) + " PID: " + amProcess.pid
                    + "(processName=" + amProcess.processName + "UID=" + amProcess.uid + ")");
        }
    }
    */



}


