import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;

public class Main {
	
	public static int R;
	public static int C;
	public static int K;
	public static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; //북동남
	public static int[][] left = {{-1, 0}, {0, -1}, {1, 0}};
	public static int[][] right = {{-1, 0}, {0, 1}, {1, 0}};
	public static int[][] map; //골렘번호가 십자모양으로 채워짐, 비어있으면 0
	public static int[][] core; //i번째 배열이 i번째 골렘에 있는 중심 좌표 
	public static int[][] exit; //i번째 배열이 i번째 골렘의 출구 좌표 
	public static int answer = 0;
	public static int cx;
	public static int cy;
	public static int d;
	public static int ex;
	public static int ey;
	public static boolean[][] visited;
	
	public static boolean checkDown() {
		int cur_x = cx + 1;
		for (int[] mv: dir) {
			int x = cur_x + mv[0];
			int y = cy + mv[1];
			if (x >= R | y < 0 | y >= C) {
				return false;	
			}
			if (x >= 0) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void moveDown() {
		cx++;
	}
	
	public static boolean checkLeft() {
		int cur_x = cx;
		int cur_y = cy-1; //왼쪽으로 한칸 이동
		for (int[] mv: left) {
			int x = cur_x + mv[0];
			int y = cur_y + mv[1];
			if (x >= R | y < 0 | y >= C) {
				return false;
			}
			if (x >= 0) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		cur_x++; //아래로 한칸 이동 
		for (int[] mv: dir) {
			int x = cur_x + mv[0];
			int y = cur_y + mv[1];
			if (x >= R | y < 0 | y >= C) {
				return false;
			}
			if (x >= 0) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void moveLeft() {
		cx++;
		cy--;
		d--;
		if (d < 0) {
			d = 3;
		}
	}
	
	public static boolean checkRight() {
		int cur_x = cx;
		int cur_y = cy+1; //오른쪽으로 한칸 이동
		for (int[] mv: right) {
			int x = cur_x + mv[0];
			int y = cur_y + mv[1];
			if (x >= R | y < 0 | y >= C) {
				return false;
			}
			if (x >= 0) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		cur_x++; //아래로 한칸 이동 
		for (int[] mv: dir) {
			int x = cur_x + mv[0];
			int y = cur_y + mv[1];
			if (x >= R | y < 0 | y >= C) {
				return false;
			}
			if (x >= 0) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void moveRight() {
		cx++;
		cy++;
		d++;
		if (d > 3) {
			d = 0;
		}
	}
	
	public static int bfs(Queue<int[]> q) {
		int max_x = 0;
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			int cur_x = cur[0];
			int cur_y = cur[1];
			int num = cur[2];
			if (cur_x > max_x) {
				max_x = cur_x;
			}
			for (int[] mv: dir) {
				int x = cur_x + mv[0];
				int y = cur_y + mv[1];
				
				//경계 안에 있을 조건 
				if (x >= 0 && x < R && y >= 0 && y < C) {
					if (!visited[x][y]) {
						//골렘 내부일 경우 
						if (map[x][y] == num) {
							q.offer(new int[] {x, y, map[x][y]});
							visited[x][y] = true;
						}
						//다른 골렘일 경우 
						else if (map[x][y] > 0) {
							//현재 위치가 출구일 경우 
							if (exit[num][0] == cur_x && exit[num][1] == cur_y) {
								q.offer(new int[] {x, y, map[x][y]});
								visited[x][y] = true;
							}
						}
					}
				}
			}
			
		}
		return max_x + 1;
	}
	
	public static void main(String[] args) throws Exception {
		//System.setIn(new java.io.FileInputStream("src/sw_240101/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[R][C];
		core = new int[K+1][2];
		exit = new int[K+1][2];
		visited = new boolean[R][C];
		
		for (int i=1; i<=K; i++) {
			st = new StringTokenizer(br.readLine());
			int c = Integer.parseInt(st.nextToken()) - 1;
			d = Integer.parseInt(st.nextToken());
			
			cx = -2;
			cy = c;
			visited = new boolean[R][C];
//			ex = cx + dir[d][0];
//			ey = cy + dir[d][1];
			
//			System.out.println(cx + " " + cy);
//			System.out.println(ex + " " + ey);
			
			while (true) {
				if (checkDown()) {
					moveDown();
//					System.out.println(i + "가 아래로 움직임 ");
//					for (int j=0; j<R; j++) {
//						System.out.println(Arrays.toString(map[j]));
//					}
					continue;
				}
				if (checkLeft()) {
					moveLeft();
//					System.out.println(i + "가 왼쪽으로 움직임 ");
//					for (int j=0; j<R; j++) {
//						System.out.println(Arrays.toString(map[j]));
//					}
					continue;
				}
				if (checkRight()) {
					moveRight();
//					System.out.println(i + "가 오른쪽으로 움직임 ");
//					for (int j=0; j<R; j++) {
//						System.out.println(Arrays.toString(map[j]));
//					}
					continue;
				}
				break;
			}
			boolean out = false;
			//몸이 바깥으로 빠져나가있는지 확인 
			for (int[] mv: dir) {
				int x = cx + mv[0];
				int y = cy + mv[1];
				if (x < 0 | x >= R | y < 0 | y >=C) {
					map = new int[R][C];
					out = true;
					break;
				}
			}
			if (out) {
				continue;
			}
			map[cx][cy] = i;
			ex = cx + dir[d][0];
			ey = cy + dir[d][1];
			exit[i] = new int[] {ex, ey};
			for (int[] mv: dir) {
				int x = cx + mv[0];
				int y = cy + mv[1];
				map[x][y] = i;
			}
//			for (int j=0; j<R; j++) {
//				System.out.println(Arrays.toString(map[j]));
//			}
//			System.out.println("======================");
//			System.out.println(exit[i][0] + " " + exit[i][1]);
//			System.out.println("........................");
			
			Queue<int[]> q = new LinkedList<>();
			q.add(new int[] {cx, cy, i});
			visited[cx][cy] = true;
			
			int result = bfs(q);
			answer += result;
//			System.out.println(result);
		}
		System.out.println(answer);
	}

}