import java.io.*;
import java.util.*;

public class Main {
	
	public static int L;
	public static int N;
	public static int Q;
	public static int[][] map; //0이면 빈칸, -1이면 벽
	public static boolean[][] trap; //true면 함정
	public static int[][] player;
	public static int[] damage;
	public static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	public static boolean check;
	public static ArrayList<Integer> list;
	
	public static void setFail(int who) {
		int x = player[who][0];
		int y = player[who][1];
		int h = player[who][2];
		int w = player[who][3];
		
		for (int i=x; i<x+h; i++) {
			for (int j=y; j<y+w; j++) {
				map[i][j] = 0;
			}
		}
	}
	
	public static void getDamage(int attack) {
		for (int ix=0; ix<list.size(); ix++) {
			int who = list.get(ix);
			if (who == attack) {
				continue;
			}
			
			int x = player[who][0];
			int y = player[who][1];
			int h = player[who][2];
			int w = player[who][3];
			
			for (int i=x; i<x+h; i++) {
				for (int j=y; j<y+w; j++) {
					if (trap[i][j]) {
						player[who][4]--;
						damage[who]++;
						if (player[who][4] == 0) {
							setFail(who);
						}
					}
				}
			}
		}
	}
	
	public static void makeMap() {
		for (int ix=0; ix<list.size(); ix++) {
			int who = list.get(ix);
			int x = player[who][0];
			int y = player[who][1];
			int h = player[who][2];
			int w = player[who][3];
			
			for (int i=x; i<x+h; i++) {
				for (int j=y; j<y+w; j++) {
					map[i][j] = who;
				}
			}
		}
	}
	
	public static void setMap() {
		for (int ix=0; ix<list.size(); ix++) {
			int who = list.get(ix);
			int x = player[who][0];
			int y = player[who][1];
			int h = player[who][2];
			int w = player[who][3];
			
			//원래 있던 곳 비우기 
			for (int i=x; i<x+h; i++) {
				for (int j=y; j<y+w; j++) {
					map[i][j] = 0;
				}
			}
		}
	}
	
	public static void setPlayer(int d) {
		for (int ix=0; ix<list.size(); ix++) {
			int who = list.get(ix);
			int x = player[who][0];
			int y = player[who][1];
			
			int dx = dir[d][0];
			int dy = dir[d][1];
			
			player[who][0] = x + dx;
			player[who][1] = y + dy;
		}
	}
 	public static void checkMove(int who, int d, boolean[] visited) {
		if (!check) {
			return;
		}
		
		int x = player[who][0];
		int y = player[who][1];
		int h = player[who][2];
		int w = player[who][3];
		
		int dx = dir[d][0];
		int dy = dir[d][1];
		
		for (int i=x; i<x+h; i++) {
			for (int j=y; j<y+w; j++) {
				if (i+dx >= 0 && i+dx < L && j+dy >= 0 && j+dy < L) {
					if (map[i+dx][j+dy] >= 0) {
						if (map[i+dx][j+dy] > 0 && !visited[map[i+dx][j+dy]]) {
							list.add(map[i+dx][j+dy]);
							visited[map[i+dx][j+dy]] = true;
							checkMove(map[i+dx][j+dy], d, visited);
						}
					}
					else { //벽이면 
						check = false;
						return;
					}
				}
				else { //경계 넘어가면
					check = false;
					return;
				}
			}
		}
		return;
	}

	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_230201/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		
		map = new int[L][L];
		trap = new boolean[L][L];
		player = new int[N+1][5];
		damage = new int[N+1];
		
		for (int i=0; i<L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<L; j++) {
				int type = Integer.parseInt(st.nextToken());
				if (type == 1) {
					map[i][j] = 0;
					trap[i][j] = true;
				}
				else if (type == 2) {
					map[i][j] = -1;
				}
				else {
					map[i][j] = type;
				}
			}
		}
		
		for (int n=1; n<=N; n++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			
			for (int i=x; i<x+h; i++) {
				for (int j=y; j<y+w; j++) {
					map[i][j] = n;
				}
			}
			player[n][0] = x; //좌상단 x좌표 
			player[n][1] = y; //좌상단 y좌표 
			player[n][2] = h; //높이 
			player[n][3] = w; //너비 
			player[n][4] = k; //체력 
		}
		
		
		
		for (int q=0; q<Q; q++) {
			st = new StringTokenizer(br.readLine());
			int attack = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			if (player[attack][4] <= 0) {
				continue;
			}
			
			check = true;
			list = new ArrayList<>();
			list.add(attack);
			boolean[] visited = new boolean[N+1];
			visited[attack] = true;
			
			checkMove(attack, d, visited);
//			System.out.println(check);
			
			if (check) {
				setMap();
				setPlayer(d);
				makeMap();
//				System.out.println("이동함========================");
				getDamage(attack);
			}
//			else {
//				System.out.println("이동안함!!!!!!!========================");
//			}
//			for (int i=0; i<L; i++) {
//				System.out.println(Arrays.toString(map[i]));
//			}
		}
		
		int answer = 0;
		for (int i=1; i<=N; i++) {
			if (player[i][4] > 0) {
				answer += damage[i];
			}
		}
		
		System.out.println(answer);
		
//		for (int i=0; i<L; i++) {
//			System.out.println(Arrays.toString(map[i]));
//		}
//		for (int i=0; i<L; i++) {
//			System.out.println(Arrays.toString(trap[i]));
//		}
	}
}