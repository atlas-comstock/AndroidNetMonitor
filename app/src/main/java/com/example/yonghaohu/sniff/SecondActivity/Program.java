package com.example.yonghaohu.sniff.SecondActivity;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by yonghaohu on 15/10/18.
 */
public class Program implements Parcelable {
    private static final long serialVersionUID = 8749270711455960560L;
    private Drawable icon;
    private String name;
    private int pid;
//    private int uid;
//    private String label;


    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public int getPid() {
        return pid;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel dest, int flags) {
        //把数据写入Parcel
        dest.writeString(name);
        dest.writeInt(pid);
    }
    //3、自定义类型中必须含有一个名称为CREATOR的静态成员，该成员对象要求实现Parcelable.Creator接口及其方法
    public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel source) {
            //从Parcel中读取数据 //此处read顺序依据write顺序
            Program app = new Program();
            app.name = source.readString();
            app.pid = source.readInt();
            return app;
        }
        @Override
        public Program[] newArray(int size) {

            return new Program[size];
        }

    };

//    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//
//        out.writeUTF(icon);
//
//        out.writeUTF(productprice);
//
//        out.writeUTF(producturi);
//
//    }
//


//    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
//
//        productname = in.readUTF();
//
//        productprice = in.readUTF();
//        producturi = in.readUTF();
//
//    }
}
