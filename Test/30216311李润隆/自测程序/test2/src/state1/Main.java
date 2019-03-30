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


public class Main {
	public static void main (String [] args) throws Exception
	{	
		PrintStream mytxt=new PrintStream("./test2.txt");
		PrintStream out=System.out;
		System.setOut(mytxt);
		//new Time().ShowTime();
		
		/*LinkList ls = new LinkList();
		ls.add(10);
		ls.showlist();*/
		
		/*MyQueue queue = new MyQueue();
		PCB p = new PCB(1);
		PCB q = new PCB(2);
		PCB r = new PCB(3);
		queue.join(p);
		queue.join(q);
		queue.join(r);
		queue.quit();
		queue.Show_PCB();*/
		
		CPU cpu = new CPU();
		cpu.Form_Job();
		cpu.Start();
		
		/*for(int i=0;i<20;i++)
		{
			System.out.println((int)(100 * (2 + 4 * Math.random())));
		}*/
		
	}
}



//32KB内存，每个物理块大小512B，共64个物理块，物理地址16位，双字节存储，虚拟内存128KB
//一个柱面32个磁道，一个磁道64个扇区，一个扇区为一个物理块，外存共1MB,