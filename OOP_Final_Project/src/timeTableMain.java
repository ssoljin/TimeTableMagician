import java.util.Collections;
import java.util.Vector;

class timeTableMain{
	static Vector<timeTable> timeTables = new Vector<timeTable>();
	static Vector<Vector<lecture>> lectures = new Vector<Vector<lecture>>();
	static Vector<nonLecture> nonLectures = new Vector<nonLecture>();
	static Vector<Vector<lecture>> essential = new Vector<Vector<lecture>>();
	static Vector<Vector<lecture>> onlyMajor=new Vector<Vector<lecture>>();
	static Vector<Integer> creditList = new Vector<Integer>();
	static int mCredit, MCredit, essentialFlag = 0;
	static float startTime, endTime;
	
	public static void pushMajor(Vector<lecture> tempLectures, int credit, int pivot) { //Push essential major
		if(tempLectures.size()==essentialFlag) {
			onlyMajor.add(tempLectures);
			creditList.add(credit);
			return;
		}
		for(int i=pivot;i<essentialFlag;i++) {
			for(int j=0;j<essential.elementAt(i).size();j++) {
				boolean canPush = true;
				for(int k=0;k<nonLectures.size();k++) {
					if(nonLectures.elementAt(k).isIntersected(essential.elementAt(i).elementAt(j))) {
						canPush=false;
						break;
					}
				}
				for(int k=0;k<tempLectures.size();k++) {
					if(tempLectures.elementAt(k).isIntersected(essential.elementAt(i).elementAt(j).getTime())) {
						canPush=false;
						break;
					}
				}
				if(canPush) {
					if(essential.elementAt(i).elementAt(j).isInStartToEnd(startTime, endTime)) {
						@SuppressWarnings("unchecked")
						Vector<lecture> t = (Vector<lecture>)tempLectures.clone();
						t.add(essential.elementAt(i).elementAt(j));
						pushMajor(t, credit+essential.elementAt(i).elementAt(j).getCredit(), i+1);
					}
				}
			}
		}
	}
	
	public static void pushTimetable(Vector<lecture> tempLectures, int credit, int pivot) { //Push lectures
		if(credit>=mCredit&&credit<=MCredit) {
			timeTables.add(new timeTable(credit, tempLectures, nonLectures));
			timeTables.lastElement().computeEndtime();
			timeTables.lastElement().computeStarttime();
		}
		if(credit>=MCredit) {
			return;
		}
		for(int i=pivot;i<lectures.size();i++) {
			for(int j=0;j<lectures.elementAt(i).size();j++) {
				boolean canPush = true;
				for(int k=0;k<nonLectures.size();k++) {
					if(nonLectures.elementAt(k).isIntersected(lectures.elementAt(i).elementAt(j))) {
						canPush=false;
						break;
					}
					
				}
				for(int k=0;k<tempLectures.size();k++) {
					if(tempLectures.elementAt(k).isIntersected(lectures.elementAt(i).elementAt(j).getTime())) {
						canPush=false;
						break;
					}
				}
				if(canPush) {
					if(lectures.elementAt(i).elementAt(j).isInStartToEnd(startTime, endTime)) {
						@SuppressWarnings("unchecked")
						Vector<lecture> t = (Vector<lecture>)tempLectures.clone();
						t.add(lectures.elementAt(i).elementAt(j));
						pushTimetable(t, credit+lectures.elementAt(i).elementAt(j).getCredit(), i+1);
					}
				}
			}
		}
		return;
	}
	
	public static void main(String[] args) {
		parsingLecture tool = new parsingLecture();
		lectures = tool.getLecture("C:\\Users\\solji\\eclipse-workspace\\OOP_Final_Project\\src\\TEST.xls");//.xls ���� ���
		System.out.println(lectures.size());
		nonLectureWindow w = new nonLectureWindow();
		w.callWindow(new Vector<String>()); //Push nonLecture
		boolean isBreak = false;
		while(!isBreak) {
			isBreak = w.isClosed();
			System.out.print(".");
		}
		
		nonLectures = w.getNonLecture();
		etcSettingWindow etc = new etcSettingWindow(); //Setting time, credit, etc
		etc.callWindow();
		isBreak = false;
		while(!isBreak) {
			isBreak=etc.isClosed();
			System.out.print(".");
		}
		mCredit = etc.getmC();
		MCredit = etc.getMC();
		startTime = (float)etc.getsM()/6+etc.getsH();
		endTime = etc.geteH()+(float)etc.geteM()/6;
		int grade = etc.getGrade();
		
		sortLectures sorter = new sortLectures(grade); //Sort...
		Collections.sort(lectures, sorter);
		
		for(int i=0;i<lectures.size();i++) { //Check how many essential lecture vector has
			if(lectures.elementAt(i).elementAt(0).isMajor()&&lectures.elementAt(i).elementAt(0).getLecture_type()==0) {
				essentialFlag++;
				essential.add(lectures.elementAt(i));
			}
			else {
				break;
			}
		}
		

		pushMajor(new Vector<lecture>(), 0, 0); //Push essential lectures
		
		for(int i=0;i<onlyMajor.size();i++) {
			pushTimetable(onlyMajor.elementAt(i), creditList.elementAt(i), essentialFlag); //Make timetable by backtracking
		}
		
		int index = 0;		
		while(true) {
			isBreak = false;
			timeTables.elementAt(index).callWindow();
			while(!isBreak) {
				if(timeTables.elementAt(index).isClosed()) {
					isBreak = true;
					index = index + timeTables.elementAt(index).selectWay();
					if(index<=0) {
						index=0;
					}
					if(index>=timeTables.size()) {
						index--;
					}
				}
			}
			
		}
	}
}
