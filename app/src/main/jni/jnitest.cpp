//
// Created by  YongHaoHu on 15/10/20.
//
#include "jnitest.h"
#include <unistd.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <fcntl.h>
#include <pwd.h>
#include <dirent.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/types.h>



//JNIEXPORT jstring JNICALL Java_com_example_yonghaohu_sniff_NdkJniUtils_getCLanguageString
//        (JNIEnv* env, jobject obj, jstring path)
//{
//        jboolean iscopy;
//        const char* result_path = (env)->GetStringUTFChars(path, &iscopy);
//        struct stat sb;
//        if (lstat(result_path, &sb) == -1) {
//                return env->NewStringUTF("error1");
//        }
//        int r = 0;
//        char *buf = new char[sb.st_size + 1];
//
//        r = readlink(result_path, buf, 1000);
//        if (r == -1)
//                return env->NewStringUTF("error2");
//        if (r > sb.st_size) {
//                return env->NewStringUTF("error3");
//        }
//
//        buf[r] = '\0';
//        return env->NewStringUTF( buf );
//}


//JNIEXPORT jstring JNICALL Java_com_example_yonghaohu_sniff_NdkJniUtils_getsocket
//        (JNIEnv* env, jobject obj, jstring path)
JNIEXPORT jstring JNICALL Java_com_example_yonghaohu_sniff_NdkJniUtils_getCLanguageString
        (JNIEnv* env, jobject obj, jstring path)
{
    DIR* pDir = NULL;
    struct dirent*ent;
    char *childpath = new char[1000];
    jboolean iscopy;
    const char* result_path = (env)->GetStringUTFChars(path, &iscopy);
    system("su");

    char *resbuf = new char[10000];
    pDir=opendir(result_path);
    if(pDir == NULL) {
        strcat(resbuf, result_path);
        strcat(resbuf, "fuck, null");
        return env->NewStringUTF(resbuf);
    }

    memset(childpath,0, 100);
    resbuf[0] = 'n';
    resbuf[1] = 'o';
    resbuf[2] = '\0';
    while((ent=readdir(pDir))!=NULL) {
        if(strcmp(ent->d_name,".")==0 || strcmp(ent->d_name,"..")==0)
            continue;

        if(strlen(resbuf) > 1000)
            break;

        memset(childpath, 0, 1000);
        sprintf(childpath,"%s/%s",result_path,ent->d_name);
        //return env->NewStringUTF(childpath);
//        char *newpath = new char[100];
//        char *buf = new char[100];
//        memset(newpath, 0 , 100);
//        memset(buf, 0 , 100);
        ssize_t r=0;

//            strcpy(newpath, result_path);//"/proc/8078/fd/"
//            strcat(newpath, ent->d_name);
        //r = readlink(childpath, buf, 100);
        //buf[strlen(buf)] = '\n';
        //strcat(resbuf, buf);
        strcat(resbuf, childpath);
        if (r == -1)
            return env->NewStringUTF("error: r=-1");
    }
    if (pDir != NULL) {
        closedir(pDir);
    }

    if(!*resbuf) {
        resbuf[0] = 'n';
        resbuf[1] = 'o';
        resbuf[2] = '\0';
    }
    return env->NewStringUTF( resbuf );
}



