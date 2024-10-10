import java.io.*;
import java.util.*;

public class Main {
	
	public static int N;
	public static int M;
	public static int K;
	public static int[][][] map;
	public static int[] giver;
	public static int[] receiver;
	public static Stack<int[]> RList;
	public static HashMap<String, String> path;
	public static int[][] laserDir = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
	public static int[][] bombDir = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {-1, 1}, {1, 1}, {-1, -1}, {1, -1}};
	public static HashMap<String, String> dir;
	public static boolean[][] damaged;
	
	public static void pickGiver() {
		int minPower = 5001;
		int maxTime = 0;
		int maxSum = 0;
		int maxC = 0;
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				int power = map[i][j][0];
				int time = map[i][j][1];
				
				if (power == 0) {
					continue;
				}
				
				if (power < minPower) {
					minPower = power;
					maxTime = time;
					maxSum = i+j;
					maxC = j;
				}
				else if (power == minPower) {
					if(time > maxTime) {
						minPower = power;
						maxTime = time;
						maxSum = i+j;
						maxC = j;
					}
					else if (time == maxTime) {
						if (i+j > maxSum) {
							minPower = power;
							maxTime = time;
							maxSum = i+j;
							maxC = j;
						}
						else if (i+j == maxSum) {
							if (j > maxC) {
								minPower = power;
								maxTime = time;
								maxSum = i+j;
								maxC = j;
							}
						}
					}
				}
			}
		}
		giver[0] = maxSum - maxC;
		giver[1] = maxC;
	}
	
	public static void pickReceiver() {
		int maxPower = 0;
		int minTime = 1001;
		int minSum = 21;
		int minC = 11;
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				int power = map[i][j][0];
				int time = map[i][j][1];
				
				if (power == 0) {
					continue;
				}
				
				if (i == giver[0] && j == giver[1]) {
					continue;
				}
				
				if (power > maxPower) {
					maxPower = power;
					minTime = time;
					minSum = i+j;
					minC = j;
				}
				else if (power == maxPower) {
					if(time < minTime) {
						maxPower = power;
						minTime = time;
						minSum = i+j;
						minC = j;
					}
					else if (time == minTime) {
						if (i+j < minSum) {
							maxPower = power;
							minTime = time;
							minSum = i+j;
							minC = j;
						}
						else if (i+j == minSum) {
							if (j < minC) {
								maxPower = power;
								minTime = time;
								minSum = i+j;
								minC = j;
							}
						}
					}
				}
			}
		}
		receiver[0] = minSum - minC;
		receiver[1] = minC;
	}
	
	public static boolean laserAttack() {
		Queue<int[]> q = new LinkedList<>();
		boolean[][] visited = new boolean[N][M];
		q.offer(new int[] {giver[0], giver[1]});
		visited[giver[0]][giver[1]] = true;
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			int curX = cur[0];
			int curY = cur[1];
			
			if (curX == receiver[0] && curY == receiver[1]) {
				return true;
			}
			
			for (int i=0; i<4; i++) {
				int afterX = curX + laserDir[i][0];
				int afterY = curY + laserDir[i][1];
				
				if (afterX < 0 | afterX >= N | afterY < 0 | afterY >= M) {
					int[] arr = getDir(afterX, afterY);
					afterX = arr[0];
					afterY = arr[1];
				}
				
				if (map[afterX][afterY][0] == 0) {
					continue;
				}
				if (!visited[afterX][afterY]) {
					q.offer(new int[] {afterX, afterY});
					visited[afterX][afterY] = true;
					path.put(afterX + " " + afterY, curX + " " + curY);
				}
			}
			
		}
		return false;
	}
	
	public static void updateList() {
		String str = receiver[0] + " " + receiver[1];
		String end = giver[0] + " " + giver[1];
		
		while (true) {
			String[] arr = str.split(" ");
			int[] get = new int[2];
			get[0] = Integer.parseInt(arr[0]);
			get[1] = Integer.parseInt(arr[1]);
			
			RList.add(new int[] {get[0], get[1]});
			
			if (path.get(str).equals(end)) {
				break;
			}
			str = path.get(str);
		}
	}
	
	public static void bombAttack() {
		int curX = receiver[0];
		int curY = receiver[1];
		
		RList.add(new int[] {curX, curY});
		
		for (int i=0; i<8; i++) {
			int afterX = curX + bombDir[i][0];
			int afterY = curY + bombDir[i][1];
			
			if (afterX < 0 | afterX >= N | afterY < 0 | afterY >= M) {
				int[] arr = getDir(afterX, afterY);
				afterX = arr[0];
				afterY = arr[1];
			}
			
			if (map[afterX][afterY][0] == 0) {
				continue;
			}
			if (afterX == giver[0] && afterY == giver[1]) {
				continue;
			}
			
			RList.add(new int[] {afterX, afterY});
		}
	}
	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_230101/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][M][2];

		for (int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<M; j++) {
				map[i][j][0] = Integer.parseInt(st.nextToken());
				map[i][j][1] = 0;
			}
		}
//		printMap();
		dir = new HashMap<>();
		setDir();
//		System.out.println(dir);
		
		for (int k=1; k<=K; k++) {
			
			if (checkStop()) {
				break;
			}
			
			RList = new Stack<>();
			path = new HashMap<>();
			giver = new int[2];
			receiver= new int[2];
			damaged = new boolean[N][M];
			
			pickGiver();
			
			//공격력 증가 , 공격 시점 업데이트
			map[giver[0]][giver[1]][0] += (N+M);
			map[giver[0]][giver[1]][1] = k;
			
//			System.out.println(Arrays.toString(giver));
			pickReceiver();
//			System.out.println(Arrays.toString(receiver));

			
//			bombAttack();
//			System.out.println("공격 경로 ==========================");
//			while (!RList.isEmpty()) {
//				System.out.println(Arrays.toString(RList.pop()));
//			}
			
			if (laserAttack()) {
				updateList();
//				System.out.println("공격 경로 ==========================");
//				while (!RList.isEmpty()) {
//					System.out.println(Arrays.toString(RList.pop()));
//				}
			}
			else {
				bombAttack();
//				System.out.println("공격 경로 ==========================");
//				while (!RList.isEmpty()) {
//					System.out.println(Arrays.toString(RList.pop()));
//				}
			}
			
			attack();
			afterAttack();
//			System.out.println(k+" 턴 종료 === ==========================");
//			printMap();
			
		}
		
		printMaxPower();

	}
	
	public static void printMaxPower() {
		int maxPower = 0;
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				if (map[i][j][0] > maxPower) {
					maxPower = map[i][j][0];
				}
			}
		}
		System.out.println(maxPower);
	}
	
	public static void afterAttack() {
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				if (map[i][j][0] > 0 && !damaged[i][j]) {
					map[i][j][0]++;
				}
			}
		}
	}
	
	public static void attack() {
		
		damaged[giver[0]][giver[1]] = true;
		
		while (!RList.isEmpty()) {
			int[] cur = RList.pop();
			int p = map[giver[0]][giver[1]][0];
			int x = cur[0];
			int y = cur[1];
			if (x == receiver[0] && y == receiver[1]) {
				map[x][y][0] -= p;
			}
			else {
				map[x][y][0] -= (p/2);
			}
			if (map[x][y][0] < 0) {
				map[x][y][0] = 0;
			}
			damaged[x][y] = true;
		}
	}
	
	public static int[] getDir(int x, int y) {
		String beforeStr = x + " " + y;
		String afterStr = dir.get(beforeStr);
		
		String[] beforeArr = afterStr.split(" ");
		int[] arr = new int[2];
		
		arr[0] = Integer.parseInt(beforeArr[0]);
		arr[1] = Integer.parseInt(beforeArr[1]);
		return arr;
	}
	
	public static void setDir() {
		//위 
		for (int j=0; j<M; j++) {
			String before = -1 + " " + j;
			String after = (N-1) + " " + j;
			dir.put(before, after);
		}
		//아래
		for (int j=0; j<M; j++) {
			String before = N + " " + j;
			String after = 0 + " " + j;
			dir.put(before, after);
		}
		//왼쪽 
		for (int i=0; i<N; i++) {
			String before = i + " " + -1;
			String after = i + " " + (M-1);
			dir.put(before, after);
		}
		//오른쪽 
		for (int i=0; i<N; i++) {
			String before = i + " " + M;
			String after = i + " " + 0;
			dir.put(before, after);
		}
		
		dir.put((-1 + " " + M), ((N-1) + " " + 0));
		dir.put((-1 + " " + -1), ((N-1) + " " + (M-1)));
		dir.put((N + " " + M), (0 + " " + 0));
		dir.put((N + " " + -1), (0 + " " + (M-1)));
	}
	
	public static boolean checkStop() {
		int save = 0;
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				if (map[i][j][0] > 0) {
					save++;
				}
			}
		}
		if (save == 1) {
			return true;
		}
		return false;
	}
	
	public static void printMap() {
		for (int i=0; i<N; i++) {
			for (int j=0; j<M; j++) {
				System.out.print(map[i][j][0] + ".");
				System.out.print(map[i][j][1] + " ");
			}
			System.out.println();
		}
	}

}