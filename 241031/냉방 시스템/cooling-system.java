import java.util.*;
import java.io.*;

public class Main {
	
	public static int N;
	public static int M;
	public static int K;
	public static int[][] Map;
	public static int[][][] airMap;
	public static int[][] coldMap;
	public static ArrayList<int[]> airCon;
	public static int[][] Dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; //상하좌우 
	public static int Time;
	
	public static void getCold(int curX, int curY, int d, int power, boolean[][] visited) {
		if (power == 0) {
			return;
		}
		if (visited[curX][curY]) {
			return;
		}
		visited[curX][curY] = true;
		coldMap[curX][curY] += power;
		
		int other;
		if (d == 0 | d == 1) {
			other = 2;
		}
		else {
			other = 0;
		}
		
		if (airMap[curX][curY][d] == 0) {
			int afterX = curX + Dir[d][0];
			int afterY = curY + Dir[d][1];
			if (check(afterX, afterY)) {
				getCold(afterX, afterY, d, power-1, visited);
			}
		}

		if (airMap[curX][curY][other] == 0) {
			int midX = curX + Dir[other][0];
			int midY = curY + Dir[other][1];
			if (check(midX, midY)) {
				if (airMap[midX][midY][d] == 0) {
					int afterX = midX + Dir[d][0];
					int afterY = midY + Dir[d][1];
					if (check(afterX, afterY)) {
						getCold(afterX, afterY, d, power-1, visited);
					}
				}
			}
		}
		
		other++;
		
		if (airMap[curX][curY][other] == 0) {
			int midX = curX + Dir[other][0];
			int midY = curY + Dir[other][1];
			if (check(midX, midY)) {
				if (airMap[midX][midY][d] == 0) {
					int afterX = midX + Dir[d][0];
					int afterY = midY + Dir[d][1];
					if (check(afterX, afterY)) {
						getCold(afterX, afterY, d, power-1, visited);
					}
				}
			}
		}
		
	}
	
	public static int[][] spreadCold() {
		int[][] diffMap = new int[N][N];
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<N; j++) {
				int curPower = coldMap[i][j];
//				System.out.println("현재좌표: " + i + " " + j + "냉기: " + curPower);
				for (int d=0; d<4; d++) {
					if (airMap[i][j][d] == 0) {
						int adjX = i + Dir[d][0];
						int adjY = j + Dir[d][1];
						if (check(adjX, adjY)) {
							int adjPower = coldMap[adjX][adjY];
//							System.out.println("이웃 좌표: " + adjX + " " + adjY + "냉기: " + adjPower);
							if (curPower > adjPower) {
								diffMap[i][j] -= (curPower-adjPower)/4;
								diffMap[adjX][adjY] += (curPower-adjPower)/4;
							}
//							System.out.println(diffMap[i][j] +", "+ diffMap[adjX][adjY]);
						}
					}
				}
			}
		}
		return diffMap;
	}
	
	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_210202/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		Map = new int[N][N];
		coldMap = new int[N][N];
		airMap = new int[N][N][4];
		airCon = new ArrayList<int[]>();
		Time = 1;
		boolean flag = false;
		
		for (int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<N; j++) {
				Map[i][j] = Integer.parseInt(st.nextToken());
				if (Map[i][j] == 2) {
					airCon.add(new int[] {i, j, 2});
				}
				if (Map[i][j] == 3) {
					airCon.add(new int[] {i, j, 0});
				}
				if (Map[i][j] == 4) {
					airCon.add(new int[] {i, j, 3});
				}
				if (Map[i][j] == 5) {
					airCon.add(new int[] {i, j, 1});
				}
			}
		}
		
		for (int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			int d = Integer.parseInt(st.nextToken());
			if (d == 0) {
				airMap[x][y][0] = 1;
				airMap[x-1][y][1] = 1;
			}
			else {
				airMap[x][y][2] = 1;
				airMap[x][y-1][3] = 1;
			}
		}
		
//		printMap();
//		printAirMap();
		
		while (Time <= 100) {
			for (int i=0; i<airCon.size(); i++) {
				int[] aircon = airCon.get(i);
				int x = aircon[0];
				int y = aircon[1];
				int d = aircon[2];
				int[] dir = Dir[d];
				getCold(x+dir[0], y+dir[1], d, 5, new boolean[N][N]);
//				System.out.println(i + "번째 에어컨 작동 후 ====================");
//				printColdMap();
			}
			
			int[][] diffMap = spreadCold();
//			System.out.println("차이  ====================");
//			for (int i=0; i<N; i++) {
//				System.out.println(Arrays.toString(diffMap[i]));
//			}
			
			for (int i=0; i<N; i++) {
				for (int j=0; j<N; j++) {
					coldMap[i][j] += diffMap[i][j];
					coldMap[i][j] = Math.max(coldMap[i][j], 0);
				}
			}
			
//			System.out.println("전파 후  ====================");
//			printColdMap();
			
			downCold();
			
//			System.out.println(Time+ "끝난  후 ====================");
			
//			printColdMap();
			if (checkColdEqualK()) {
				flag = true;
				System.out.println(Time);
				break;
			}
			Time++;
		}
		if (!flag) {
			System.out.println("-1");
		}
	}
	
	public static boolean checkColdEqualK() {
		for (int i=0; i<N; i++) {
			for (int j=0; j<N; j++) {
				if (Map[i][j] == 1) {
					if (coldMap[i][j] < K) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static void downCold() {
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<N; j++) {
				if (i == 0 | i == N-1 | j == 0 | j == N-1) {
					coldMap[i][j] = Math.max(coldMap[i][j]-1, 0);
				}
			}
		}
	}
	
	public static boolean check(int x, int y) {
		if (x >= 0 && x < N && y >= 0 && y < N) {
			return true;
		}
		return false;
	}
	
	public static void printMap() {
		for (int i=0; i<N; i++) {
			System.out.println(Arrays.toString(Map[i]));
		}
	}
	
	public static void printAirMap() {
		for (int i=0; i<N; i++) {
			for (int j=0; j<N; j++) {
				System.out.print(Arrays.toString(airMap[i][j]));
			}
			System.out.println();
		}
	}
	
	public static void printColdMap() {
		for (int i=0; i<N; i++) {
			System.out.println(Arrays.toString(coldMap[i]));
		}
	}

}