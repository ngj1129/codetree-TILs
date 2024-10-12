import java.io.*;

import java.util.*;

class com implements Comparator<Integer> {
    @Override
    public int compare(Integer n1, Integer n2) {
        return Integer.compare(n2, n1);
    }
}

public class Main {
	
	public static int N;
	public static int M;
	public static int K;
	public static int[][] map;
	public static int[][] player;
	public static HashMap<String, PriorityQueue<Integer>> gunMap;
	public static int[] score;
	public static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	
	public static int[] pickDir(int who) {
		int curX = player[who][0];
		int curY = player[who][1];
		int d = player[who][2];
		int afterX = curX + dir[d][0];
		int afterY = curY + dir[d][1];
		if (afterX < 0 | afterX >= N | afterY < 0 | afterY >= N) {
//			System.out.println("이전 방향: " + d);
			d += 2;
			d %= 4;
//			System.out.println("이후 방향: " + d);
			player[who][2] = d;
			afterX = curX + dir[d][0];
			afterY = curY + dir[d][1];
		}
		return new int[] {afterX, afterY};
	}
	
	public static int[] pickDirLoser(int who, int other, int[] cur) {
		int curX = cur[0];
		int curY = cur[1];
		int d = player[who][2];
		int afterX = curX + dir[d][0];
		int afterY = curY + dir[d][1];
		while (true) {
			if (afterX >= 0 && afterX < N && afterY >= 0 && afterY < N) {
				if (map[afterX][afterY] == 0 | map[afterX][afterY] == who | map[afterX][afterY] == other) {
//					System.out.println("패배한 " + who + "는 " + curX + " " + curY + "에서 " + afterX + " " + afterY +" 로 이동 ");
					return new int[] {afterX, afterY};
				}
			}
			d ++;
			d %= 4;
			player[who][2] = d;
			afterX = curX + dir[d][0];
			afterY = curY + dir[d][1];
		}
	}
	
	public static void moveOne(int who, int[] next) {
		int curX = player[who][0];
		int curY = player[who][1];
		int afterX = next[0]; //next[0]
		int afterY = next[1];
		
		if (map[curX][curY] == who) {
			map[curX][curY] = 0;
		}
		player[who][0] = afterX;
		player[who][1] = afterY;
		map[afterX][afterY] = who;
	}
	
	public static boolean existGun(int[] next) {
		String key = next[0] + " " + next[1];
		if (gunMap.containsKey(key)) {
			PriorityQueue<Integer> guns = gunMap.get(key);
			if (!guns.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public static void getGun(int who, int[] next) {
		String key = next[0] + " " + next[1];
		PriorityQueue<Integer> guns = gunMap.get(key);
		int maxGun = guns.peek();
		if (maxGun > player[who][4]) {
			if (player[who][4] > 0) {
				guns.offer(player[who][4]);
			}
			player[who][4] = guns.poll();
		}
	}
	
	public static void fight(int who, int[] next) {
		int other = map[next[0]][next[1]];
		int curPower = player[who][3];
		int curGun = player[who][4];
		int nextPower = player[other][3];
		int nextGun = player[other][4];
		int win;
		int lose;
		
		if (curPower+curGun == nextPower+nextGun) {
			if (curPower > nextPower) {
				win = who;
				lose = other;
			}
			else {
				win = other;
				lose = who;
			}
		}
		else {
			if (curPower+curGun > nextPower+nextGun) {
				win = who;
				lose = other;
			}
			else {
				win = other;
				lose = who;
			}
		}
		
		int point = Math.abs((curPower+curGun) - (nextPower+nextGun));
		score[win] += point;
		
//		System.out.println(lose + "가 패배   =================");
		moveLoser(lose, win, next); //next 에서 이동 
		
		
		if (existGun(next)) {
			getGun(win, next);
			if (win == who) { //이동한 애가 이겼으면 
				moveOne(win, next);
			}
		}
	}
	
	public static void putGun(int who, int[] cur) {
		String key = cur[0] + " " + cur[1];
		PriorityQueue<Integer> guns = gunMap.get(key);
		
		if (player[who][4] > 0) {
			guns.offer(player[who][4]);
			player[who][4] = 0;
		}
	}
	
	public static void moveLoser(int who, int win, int[] cur) {
		putGun(who, cur);
		int[] next = pickDirLoser(who, win, cur);
		
		if (existGun(next)) {
			getGun(who, next);
		}
		moveOne(who, next);
	}

	public static void movePlayer() {
		for (int i=1; i<=M; i++) {
			int[] next = pickDir(i);
			
			if (map[next[0]][next[1]] > 0) {
//				System.out.println("싸움  =================");
				fight(i, next);
			}
			
			else {
				if (existGun(next)) {
//					System.out.println("총 있음 =================");
					getGun(i, next);
//					System.out.println("플레이어의 총 정보  =================");
//					printPlayer();
				}
				moveOne(i, next);
			}
//			System.out.println(i + "플레이어 이동 후 맵 정보  ,,,,,,,,,,,,,,,,,,,,,,,");
//			printMap();
//			System.out.println(i + "플레이어 이동 후 정보  ,,,,,,,,,,,,,,,,,,,,,,,");
//			printPlayer();
		}
	}
	
	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_220201/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		player = new int[M+1][5];
		score = new int[M+1];
		gunMap = new HashMap<>();
		
		for (int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<N; j++) {
				int gun = Integer.parseInt(st.nextToken());
				String key = i + " " + j;
				PriorityQueue<Integer> guns = new PriorityQueue<Integer>(new com());
				if (gun > 0) {
					guns.offer(gun);
				}
				gunMap.put(key, guns);
			}
		}
//		System.out.println(gunMap);
		
		for (int i=1; i<=M; i++) {
			st = new StringTokenizer(br.readLine());
			player[i][0] = Integer.parseInt(st.nextToken()) - 1;
			player[i][1] = Integer.parseInt(st.nextToken()) - 1;
			player[i][2] = Integer.parseInt(st.nextToken());
			player[i][3] = Integer.parseInt(st.nextToken());
			map[player[i][0]][player[i][1]] = i;
		}
		
//		printMap();
		
		for (int k=0; k<K; k++) {
			movePlayer();
//			System.out.println(k + "라운드 끝 !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			printMap();
//			System.out.println("-----------------------------");
//			printPlayer();
//			System.out.println("-----------------------------");
//			printScore();
//			System.out.println(gunMap);
		}
		for (int i=1; i<=M; i++) {
			System.out.print(score[i] + " ");
		}
	}
	
	public static void printMap() {
		for (int i=0; i<N; i++) {
			System.out.println(Arrays.toString(map[i]));
		}
	}
	
	public static void printPlayer() {
		for (int i=1; i<=M; i++) {
			System.out.println(Arrays.toString(player[i]));
		}
	}
	
	public static void printScore() {
		System.out.println(Arrays.toString(score));
	}
	

}