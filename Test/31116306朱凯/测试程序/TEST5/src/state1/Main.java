package state1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


public class Main {//��������
	public static void main (String [] args) throws Exception//������
	{		
		CPU cpu = new CPU();//��ʼ��CPU�����cpu
		cpu.InitialSystem();//��ʼ����
	}
}



//32KB�ڴ棬ÿ��������С512B����64������飬�����ַ16λ��˫�ֽڴ洢�������ڴ�128KB
//һ������32���ŵ���һ���ŵ�64��������һ������Ϊһ������飬��湲1MB,