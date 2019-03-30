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


public class Main {//主函数类
	public static void main (String [] args) throws Exception//主函数
	{		
		CPU cpu = new CPU();//初始化CPU类对象cpu
		cpu.InitialSystem();//开始运行
	}
}



//32KB内存，每个物理块大小512B，共64个物理块，物理地址16位，双字节存储，虚拟内存128KB
//一个柱面32个磁道，一个磁道64个扇区，一个扇区为一个物理块，外存共1MB,