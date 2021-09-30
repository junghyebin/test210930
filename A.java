import java.util.Calendar;

public class A{
	public static void main(String[] args) {
		//Calendar객체생성 (현재 날짜와 시간정보)
		Calendar now = Calendar.getInstance();
		//현재 시간만 hour에 저장
		int hour = now.get(Calendar.HOUR_OF_DAY);
		//현재 분만 min에 저장
		int min = now.get(Calendar.MINUTE);
		
		//현재 시간 출력
		System.out.println("현재 시간은 "+hour+"시"+min+"분"+"입니다.");
		
		//새벽 4시에서 낮 12시 이전이면 "Good Morning"출력
		if(4<=hour && hour <12) {
			System.out.println("Good Morning");
		}
		//오후 6시 이전이면 "Good Afternoon"출력
		else if(hour<18) {
			System.out.println("Good Afternoon");
		}
		//밤 10시 이전이면 "Good Evening"출력
		else if(hour<22) {
			System.out.println("Good Evening");
		}
		//그 이후 "Good Night"출력
		else {
			System.out.println("Good Night");
		}
	}
		
}
	
