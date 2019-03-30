package state1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Create_Job {
	protected int Job_Number;//随机生成的作业数目
	protected int time = 0;//第一个作业到达的时间
	protected JobQueue jobqueue;//创建作业时用到的作业队列

	public Create_Job() throws IOException//构造函数
	{
		File diskfile = new File("Job");//仿真的磁盘文件夹
		if(!diskfile.exists())//如果该文件夹不存在
		{
			diskfile.mkdirs();//创建磁盘文件架
		}
		this.jobqueue = new JobQueue();//初始化作业队列
		this.Job_Number = (int)(4 + 4 * Math.random());//随机生成4-7个作业
		PrintWriter pw = new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File("Job\\JobNumber.txt"))//打开文件
								)));
		pw.println(Integer.toString(this.Job_Number));//输出作业数目
		pw.close();//关闭
		//JCB.Job_Number = this.Job_Number;
		for(int i=0;i<this.Job_Number;i++)//生成每一个作业的详细信息
		{
			JCB jcb = new JCB(i,time);//初始化每一个jcb节点
			jcb.Create();//生成每一个作业的详细信息
			jcb.WriteOutPageList();//写该作业每一个进程的外页表
			jobqueue.join(jcb);//将创建好的作业节点加入作业队列
			time += (int)(100 * (2 + 4 * Math.random()));//每隔200-600毫秒产生一个作业请求
		}
	}
}
