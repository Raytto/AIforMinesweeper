package ai;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import tools.Dot;

public class Executor {

	private Recognizer recognizer;
	private static final int executedTimeLimit = 300;

	public Executor() {
		// TODO Auto-generated constructor stub
		recognizer = null;
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	public void playIt() throws Exception {
		recognizer = new Recognizer();
		recognizer.selectAimArea();
		recognizer.judgeSelectedArea();
		recognizer.makeResult();
		int t = 0;
		int failTime = 0;
		Thread.sleep(2000);
		while (makeAStep() == 1) {
			t++;
			if (t == executedTimeLimit) {
				System.out.println("get executed time limit:"
						+ executedTimeLimit);
				break;
			}
			if (recognizer.makeResult() == 2) {
				failTime++;
				System.out.println("executed fail:" + failTime);
				if (failTime == 5)
					break;
				continue;
			}
			failTime = 0;
		}
		// makeAStep();
		System.out.println("end play");
	}

	private int makeAStep() throws AWTException {
		int t=simpleStrategy();
		if(t==1)
			return 1;

		// collection strategy
		HashMap<String, HashMap<String, Object>> cOfdotCollection = new HashMap<String, HashMap<String, Object>>();
		while (true) {
			t = collectionStrategy(cOfdotCollection);
			if (t == 2) {
				System.out.println("again in collection strategy");
				continue;
			}
			if(t==1)
				return 1;
			break;
		}
		return 0;
	}
	
	private int simpleStrategy() throws AWTException{
		if (recognizer == null || recognizer.getResult() == null)
			return -1;
		// simple strategy
		for (int i = 0; i < recognizer.getA(); i++)
			for (int j = 0; j < recognizer.getB(); j++) {
				if (recognizer.getResult()[i][j] == 0
						|| recognizer.getResult()[i][j] > 8)
					continue;
				ArrayList<Integer[]> alb = getAroundList(i, j,
						Recognizer.ResultBlank);
				ArrayList<Integer[]> alf = getAroundList(i, j,
						Recognizer.ResultFlag);
				if (alb.size() > 0
						&& alb.size() + alf.size() == recognizer.getResult()[i][j]) {
					System.out.println("find1:" + i + " " + j + " "
							+ alb.size() + " " + alf.size());
					execute(alb.get(0)[0], alb.get(0)[1], 2);
					return 1;// ////
				}
				if (alb.size() != 0
						&& alf.size() == recognizer.getResult()[i][j]) {
					execute(i, j, 3);
					return 1;
				}
			}
		return 0;
	}

	private int collectionStrategy(
			HashMap<String, HashMap<String, Object>> cOfdotCollection)
			throws AWTException {
		if (recognizer == null || recognizer.getResult() == null)
			return -1;
		for (int i = 0; i < recognizer.getA(); i++)
			for (int j = 0; j < recognizer.getB(); j++) {
				ArrayList<Integer[]> alb = getAroundList(i, j,
						Recognizer.ResultBlank);
				ArrayList<Integer[]> alf = getAroundList(i, j,
						Recognizer.ResultFlag);
				if (alb == null || alf == null)
					return -1;
				if (alb.size() > 0
						&& alb.size() > recognizer.getResult()[i][j]
								- alf.size()) {
					HashMap<String, Dot> dotCollection = new HashMap<String, Dot>();
					for (int k = 0; k < alb.size(); k++) {
						Dot dot = new Dot(alb.get(k)[0], alb.get(k)[1]);
						dotCollection.put(dot.toString(), dot);
					}
					HashMap<String, Object> infoOfdotCollection = new HashMap<String, Object>();
					infoOfdotCollection
							.put("NUMOFMINE",
									(Integer) recognizer.getResult()[i][j]
											- alf.size());
					infoOfdotCollection.put("DOTCOLLECTION", dotCollection);
					cOfdotCollection.put(dotCollection.toString(),
							infoOfdotCollection);
				}
			}

		for (Entry<String, HashMap<String, Object>> entry : cOfdotCollection
				.entrySet()) {
			System.out.println("entry:" + entry.getValue());
			HashMap<String, Object> infoOfdotCollection = entry.getValue();
			HashMap<String, Dot> dotCollection = (HashMap<String, Dot>) infoOfdotCollection
					.get("DOTCOLLECTION");
			for (Entry<String, HashMap<String, Object>> entry2 : cOfdotCollection
					.entrySet()) {
				if (entry == entry2)
					continue;
				HashMap<String, Object> infoOfdotCollection2 = entry2
						.getValue();
				HashMap<String, Dot> dotCollection2 = (HashMap<String, Dot>) infoOfdotCollection2
						.get("DOTCOLLECTION");
				HashMap<String, Dot> differentDots = differentSet(
						dotCollection, dotCollection2);
				if (differentDots.size() == 0)
					continue;
				if (differentDots.size() == (int) infoOfdotCollection
						.get("NUMOFMINE")
						- (int) infoOfdotCollection2.get("NUMOFMINE")) {
					for (Entry<String, Dot> entry3 : differentDots.entrySet()) {
						execute(entry3.getValue().x, entry3.getValue().y, 2);
						return 1;
					}
				}
				if (differentDots.size() + dotCollection2.size() == dotCollection
						.size()) {
					if ((int) infoOfdotCollection.get("NUMOFMINE") == (int) infoOfdotCollection2
							.get("NUMOFMINE")) {
						for (Entry<String, Dot> entry3 : differentDots
								.entrySet()) {
							execute(entry3.getValue().x, entry3.getValue().y, 1);
							return 1;
						}
					}
					if ((int) infoOfdotCollection.get("NUMOFMINE")
							- (int) infoOfdotCollection2.get("NUMOFMINE") > 0) {
						if (cOfdotCollection.get(differentDots.toString()) == null) {
							HashMap<String, Object> newInfoOfdotCollection = new HashMap<String, Object>();
							newInfoOfdotCollection.put(
									"NUMOFMINE",
									(Integer) (int) infoOfdotCollection
											.get("NUMOFMINE")
											- (int) infoOfdotCollection2
													.get("NUMOFMINE"));
							newInfoOfdotCollection.put("DOTCOLLECTION",
									differentDots);
							cOfdotCollection.put(differentDots.toString(),
									newInfoOfdotCollection);
							return 2;
						}
					}
				}
			}
		}
		return 0;
	}

	private HashMap<String, Dot> differentSet(HashMap<String, Dot> hma,
			HashMap<String, Dot> hmb) {
		HashMap<String, Dot> result = new HashMap<String, Dot>();
		for (Entry<String, Dot> entry : hma.entrySet()) {
			if (hmb.get(entry.getKey()) == null) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private ArrayList<Integer[]> getAroundList(int i, int j, int kind) {
		if (recognizer == null || recognizer.getResult() == null)
			return null;
		if (i >= recognizer.getA() || j >= recognizer.getB() || i < 0 || j < 0)
			return null;
		ArrayList<Integer[]> resultList = new ArrayList<Integer[]>();
		if (i != 0 && recognizer.getResult()[i - 1][j] == kind) {
			Integer[] location = { i - 1, j };
			resultList.add(location);
		}
		if (i != 0 && j != 0 && recognizer.getResult()[i - 1][j - 1] == kind) {
			Integer[] location = { i - 1, j - 1 };
			resultList.add(location);
		}
		if (j != 0 && recognizer.getResult()[i][j - 1] == kind) {
			Integer[] location = { i, j - 1 };
			resultList.add(location);
		}
		if (i != recognizer.getA() - 1 && j != 0
				&& recognizer.getResult()[i + 1][j - 1] == kind) {
			Integer[] location = { i + 1, j - 1 };
			resultList.add(location);
		}
		if (i != recognizer.getA() - 1
				&& recognizer.getResult()[i + 1][j] == kind) {
			Integer[] location = { i + 1, j };
			resultList.add(location);
		}
		if (i != recognizer.getA() - 1 && j != recognizer.getB() - 1
				&& recognizer.getResult()[i + 1][j + 1] == kind) {
			Integer[] location = { i + 1, j + 1 };
			resultList.add(location);
		}
		if (j != recognizer.getB() - 1
				&& recognizer.getResult()[i][j + 1] == kind) {
			Integer[] location = { i, j + 1 };
			resultList.add(location);
		}
		if (i != 0 && j != recognizer.getB() - 1
				&& recognizer.getResult()[i - 1][j + 1] == kind) {
			Integer[] location = { i - 1, j + 1 };
			resultList.add(location);
		}
		return resultList;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param howToPress
	 *            0 for nothing but move; 1 for click left; 2 for click right; 3
	 *            for click both
	 * @throws AWTException
	 */
	private int execute(int i, int j, int howToPress) throws AWTException {
		// System.out.println("in execute1"+recognizer.getA()+" "+recognizer.getB());
		System.out.println("exe:" + i + " " + j + " " + howToPress);
		if (recognizer == null || recognizer.getResult() == null)
			return -1;
		if (i >= recognizer.getA() || j >= recognizer.getB() || i < 0 || j < 0)
			return -1;
		Robot rb = new Robot();
		rb.mouseMove(recognizer.getSelectedArea().x
				+ (recognizer.getDivSNLine().get(j) + recognizer.getDivSNLine()
						.get(j + 1)) / 2, recognizer.getSelectedArea().y
				+ (recognizer.getDivWELine().get(i) + recognizer.getDivWELine()
						.get(i + 1)) / 2);
		rb.delay(100);
		if (howToPress == 1) {
			rb.mousePress(InputEvent.BUTTON1_MASK);
			rb.mousePress(InputEvent.BUTTON1_MASK);
			rb.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		if (howToPress == 2) {
			rb.mousePress(InputEvent.BUTTON3_MASK);
			rb.mousePress(InputEvent.BUTTON3_MASK);
			rb.mouseRelease(InputEvent.BUTTON3_MASK);
		}
		if (howToPress == 3) {
			rb.mousePress(InputEvent.BUTTON1_MASK);
			rb.mousePress(InputEvent.BUTTON1_MASK);
			rb.mousePress(InputEvent.BUTTON3_MASK);
			rb.mousePress(InputEvent.BUTTON3_MASK);
			rb.delay(100);
			rb.mouseRelease(InputEvent.BUTTON1_MASK);
			rb.mouseRelease(InputEvent.BUTTON3_MASK);
		}
		rb.delay(100);
		rb.mouseMove(recognizer.getSelectedArea().x - 10,
				recognizer.getSelectedArea().y - 10);
		recognizer.clearResult();
		return 1;
	}
}
