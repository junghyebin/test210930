import java.util.Calendar;

public class A{
	public static void main(String[] args) {
		//Calendar��ü���� (���� ��¥�� �ð�����)
		Calendar now = Calendar.getInstance();
		//���� �ð��� hour�� ����
		int hour = now.get(Calendar.HOUR_OF_DAY);
		//���� �и� min�� ����
		int min = now.get(Calendar.MINUTE);
		
		//���� �ð� ���
		System.out.println("���� �ð��� "+hour+"��"+min+"��"+"�Դϴ�.");
		
		//���� 4�ÿ��� �� 12�� �����̸� "Good Morning"���
		if(4<=hour && hour <12) {
			System.out.println("Good Morning");
		}
		//���� 6�� �����̸� "Good Afternoon"���
		else if(hour<18) {
			System.out.println("Good Afternoon");
		}
		//�� 10�� �����̸� "Good Evening"���
		else if(hour<22) {
			System.out.println("Good Evening");
		}
		//�� ���� "Good Night"���
		else {
			System.out.println("Good Night");
		}
	}
		
}
	
