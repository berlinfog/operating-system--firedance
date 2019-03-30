package state1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Create_Job {
	protected int Job_Number;//������ɵ���ҵ��Ŀ
	protected int time = 0;//��һ����ҵ�����ʱ��
	protected JobQueue jobqueue;//������ҵʱ�õ�����ҵ����

	public Create_Job() throws IOException//���캯��
	{
		File diskfile = new File("Job");//����Ĵ����ļ���
		if(!diskfile.exists())//������ļ��в�����
		{
			diskfile.mkdirs();//���������ļ���
		}
		this.jobqueue = new JobQueue();//��ʼ����ҵ����
		this.Job_Number = (int)(4 + 4 * Math.random());//�������4-7����ҵ
		PrintWriter pw = new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File("Job\\JobNumber.txt"))//���ļ�
								)));
		pw.println(Integer.toString(this.Job_Number));//�����ҵ��Ŀ
		pw.close();//�ر�
		//JCB.Job_Number = this.Job_Number;
		for(int i=0;i<this.Job_Number;i++)//����ÿһ����ҵ����ϸ��Ϣ
		{
			JCB jcb = new JCB(i,time);//��ʼ��ÿһ��jcb�ڵ�
			jcb.Create();//����ÿһ����ҵ����ϸ��Ϣ
			jcb.WriteOutPageList();//д����ҵÿһ�����̵���ҳ��
			jobqueue.join(jcb);//�������õ���ҵ�ڵ������ҵ����
			time += (int)(100 * (2 + 4 * Math.random()));//ÿ��200-600�������һ����ҵ����
		}
	}
}
