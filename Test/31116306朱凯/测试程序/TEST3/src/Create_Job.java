import java.io.FileNotFoundException;

public class Create_Job {
	protected int Job_Number;
	protected int time = 0;
	protected JobQueue jobqueue;
	

	public Create_Job() throws FileNotFoundException//���캯��
	{
		this.jobqueue = new JobQueue();
		this.Job_Number = 3 ;// (int)(2 + 3 * Math.random());//2-4����ҵ
		//JCB.Job_Number = this.Job_Number;
		for(int i=0;i<this.Job_Number;i++)
		{
			JCB jcb = new JCB(i+1,time);
			jcb.Create();
			
			jcb.WriteOutPageList();
			
			jobqueue.join(jcb);
			time += (int)(100 * (2 + 4 * Math.random()));//ÿ��200-600�������һ����ҵ����
		}
	}
	public static void main (String [] args) throws Exception
	{	
		Create_Job CJ = new Create_Job();
		System.out.println("��ҵ�������");
	}
}
