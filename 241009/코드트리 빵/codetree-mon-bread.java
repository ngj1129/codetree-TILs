import java.io.*;
import java.util.*;

public class Main {
	
	public static int N;
	public static int M;
	public static int H;
	public static int[][] map;
	public static int[][] home;
	public static int[][] goal;
	public static int[][] player;
	public static boolean[] in; //사람들이 격자 안에 있는지 
	public static int all = 0;
	public static int T = 0; //격자 안에 들어갈 사람 
	public static int[][] move = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
	public static Queue<Integer> endList;
	public static int time = 0;
	
	public static int[] getNext(HashMap<String, String> path, int[] start, int[] gl) {
		String str = gl[0]+" "+gl[1];
		String end = start[0]+" "+start[1];
//		System.out.println(path);
//		System.out.println(str);
		while (!path.get(str).equals(end)) {
			str = path.get(str);
		}
//		System.out.println(str);
		String[] arr = str.split(" ");
		
//		System.out.println(Arrays.toString(arr));
		//System.out.println(arr[1]);
		int[] next = new int[2];
		next[0] = Integer.parseInt(arr[0]);
		next[1] = Integer.parseInt(arr[1]);
		return next;
	}
	
	public static HashMap<String, String> getPathToGoal(Queue<int[]> q, boolean[][] visited, int[] gl) {
		HashMap<String, String> path = new HashMap<>();
		
		while (!q.isEmpty()) {
			int[] cur = q.poll();
			int curX = cur[0];
			int curY = cur[1];
			
			if (curX == gl[0] && curY == gl[1]) {
				return path;
			}
			
			for (int[] mv: move) {
				int afterX = curX + mv[0];
				int afterY = curY + mv[1];
				if (afterX >= 0 && afterX < N && afterY >= 0 && afterY < N) {
					if (map[afterX][afterY] == 0 && !visited[afterX][afterY]) {
						q.offer(new int[] {afterX, afterY});
						visited[afterX][afterY] = true;
						path.put(afterX+" "+afterY, curX+" "+curY);
					}
				}
			}
		}
		return path;
	}
	public static void toGoal() {
		for (int i=0; i<M; i++) {
			//격자 안에 없으면 
			if (!in[i]) { 
				continue;
			}
			//편의점에 도착했으면 
			if (player[i][0] == goal[i][0] && player[i][1] == goal[i][1]) {
				continue;
			}
			
			int curX = player[i][0];
			int curY = player[i][1];
			
			Queue<int[]> q = new LinkedList<>();
			boolean[][] visited = new boolean[N][N];
			q.add(new int[] {curX, curY});
			visited[curX][curY] = true;
			
			HashMap<String, String> path = getPathToGoal(q, visited, new int[] {goal[i][0], goal[i][1]});
			int[] next = getNext(path, new int[] {curX, curY}, new int[] {goal[i][0], goal[i][1]});
//			System.out.println(next[0] + " " + next[1] + "로 이동 ");
			
			player[i][0] = next[0];
			player[i][1] = next[1];
			
			if (player[i][0] == goal[i][0] && player[i][1] == goal[i][1]) {
				endList.add(i);
			}
		}
	}
	
	public static void setGoal() {
		while(!endList.isEmpty()) {
			int who = endList.poll();
			int X = goal[who][0];
			int Y = goal[who][1];
			map[X][Y] = -1;
			all++;
		}
	}
	
	public static int getDistToHome(Queue<int[]> q, boolean[][] visited, int[] hm) {
		while (!q.isEmpty()) {
			int[] cur = q.poll();
			int curX = cur[0];
			int curY = cur[1];
			int d = cur[2];
			
			if (curX == hm[0] && curY == hm[1]) {
				return d;
			}
			
			for (int[] mv: move) {
				int afterX = curX + mv[0];
				int afterY = curY + mv[1];
				if (afterX >= 0 && afterX < N && afterY >= 0 && afterY < N) {
					if (map[afterX][afterY] == 0 && !visited[afterX][afterY]) {
						q.offer(new int[] {afterX, afterY, d+1});
						visited[afterX][afterY] = true;
					}
				}
			}
		}
		return 0;
	}
	
	public static void toHome() {
		int minD = N * N;
		int minR = N;
		int minC = N;
		
		int curX = goal[T][0];
		int curY = goal[T][1];
		
		for (int[] hm: home) {
			int hx = hm[0];
			int hy = hm[1];
			if (map[hx][hy] == -1) {
				continue; //이미 사람이 들어간 집이면 넘어감 
			}
			Queue<int[]> q = new LinkedList<>();
			boolean[][] visited = new boolean[N][N];
			q.add(new int[] {curX, curY, 0});
			visited[curX][curY] = true;
			
			int dist = getDistToHome(q, visited, new int[] {hx, hy});
//			System.out.println(hx + " " + hy + " 까지의 최단거리 " + dist);
			if (dist < minD) {
				minD = dist;
				minR = hx;
				minC = hy;
			}
			else if (dist == minD) {
				if (hx < minR) {
					minD = dist;
					minR = hx;
					minC = hy;
				}
				else if (hx == minR) {
					if (hy < minC) {
						minD = dist;
						minR = hx;
						minC = hy;
					}
				}
			}
		}
		
		in[T] = true;
		map[minR][minC] = -1;
		player[T][0] = minR;
		player[T][1] = minC;
	}
	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_220202/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		goal = new int[M][2];
		player = new int[M][2];
		in = new boolean[M];
		
		for (int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<N; j++) {
				int num = Integer.parseInt(st.nextToken());
				if (num == 1) {
					H++;
				}
				map[i][j] = num;
			}
		}
		
		for (int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			goal[i][0] = Integer.parseInt(st.nextToken()) - 1;
			goal[i][1] = Integer.parseInt(st.nextToken()) - 1;
		}
		
		home = new int[H][2];
		int ix = 0;
		
		for (int i=0; i<N; i++) {
			for (int j=0; j<N; j++) {
				if (map[i][j] == 1) {
					map[i][j] = 0;
					home[ix][0] = i;
					home[ix][1] = j;
					ix++;
				}
			}
		}
		
//		for (int i=0; i<N; i++) {
//			System.out.println(Arrays.toString(map[i]));
//		}
//		for (int i=0; i<H; i++) {
//			System.out.println(Arrays.toString(home[i]));
//		}
		

//		for (int i=0; i<N; i++) {
//			System.out.println(Arrays.toString(map[i]));
//		}
		
		
		while (all < M) {
			endList = new LinkedList<>();
			toGoal();
			setGoal();
			if (T < M) {
				toHome();
				T++;
			}
			time++;
//			for (int i=0; i<N; i++) {
//				System.out.println(Arrays.toString(map[i]));
//			}
		}
		System.out.println(time);
		
	}

}