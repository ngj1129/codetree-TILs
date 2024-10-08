import java.io.*;
import java.util.*;

public class Main {
	
	public static int N; //게임판의 크기 
	public static int M; //게임 턴 수 
	public static int P; //산타의 수 
	public static int C; //루돌프의 힘 
	public static int D; //산타의 힘 
	
	public static int[][] map; //-1이면 루돌프, 숫자는 산타의 인덱스
	public static int[][] santa; //산타의 위치 
	public static int[] ru; //루돌프의 위치 
	public static int[][] ru_dir = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
	public static int[][] san_dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	
	public static int[] score; //산타의 점수
	public static int[] status; //산타 탈락/기절 여부 -1이면 탈락, 0보다 크면 기절
	
	public static int pickSanta() {
		int minD = N * N;
		int maxX = 0;
		int maxY = 0;
		int san = 0;
		
		int curX = ru[0];
		int curY = ru[1];
		
		for (int i=0; i<=P; i++) {
			if (status[i] < 0) {
				continue;
			}
			
			int dist = (curX - santa[i][0]) * (curX - santa[i][0]) + (curY - santa[i][1]) * (curY - santa[i][1]);
			if (dist < minD) {
				san = i;
				maxX = santa[i][0];
				maxY = santa[i][1];
				minD = dist;
			}
			else if (dist == minD) {
				if (santa[i][0] > maxX) {
					san = i;
					maxX = santa[i][0];
					maxY = santa[i][1];
					minD = dist;
				}
				else if (santa[i][0] == maxX) {
					if (santa[i][1] > maxY) {
						san = i;
						maxX = santa[i][0];
						maxY = santa[i][1];
						minD = dist;
					}
				}
			}
		}
		return san;
	}
	
	public static int[] pickDir(int san) {
		int[] d = {0, 0};
		
		int curX = ru[0];
		int curY = ru[1];
		
		int sx = santa[san][0];
		int sy = santa[san][1];
		
		int minD = (curX - sx) * (curX - sx) + (curY - sy) * (curY - sy);
		
		for (int[] dir: ru_dir) {
			int nextX = curX + dir[0];
			int nextY = curY + dir[1];
			
			int dist = (nextX - sx) * (nextX - sx) + (nextY - sy) * (nextY - sy);
			if (dist < minD) {
				minD = dist;
				d[0] = dir[0];
				d[1] = dir[1];
			}
		}
		return d;
	}
	
	public static boolean checkFail(int x, int y) {
		if (x >= 0 && x < N && y >= 0 && y < N) {
			return false;
		}
		return true;
	}
	
	public static HashMap<Integer, int[]> slideSan(int san, int[] san_d, int[] d, int type) {
		HashMap<Integer, int[]> dic = new HashMap<>();
		int x = santa[san][0] + san_d[0];
		int y = santa[san][1] + san_d[1];
//		System.out.println(d[0] + " " + d[1]);
		
		if (type == 0) {
			x = santa[san][0] + san_d[0];
			y = santa[san][1] + san_d[1];
		}
		else {
			x = santa[san][0] + d[0] + san_d[0];
			y = santa[san][1] + d[1] + san_d[1];
			d[0] *= -1;
			d[1] *= -1;
		}
		
		dic.put(san, new int[] {x, y});
		

		while (x >=0 && x < N && y >= 0 && y < N) {
			if (map[x][y] > 0) {
//				System.out.println("영향 받는 산타: " + map[x][y]);
//				System.out.println(x + " " + y);
//				System.out.println(d[0] + " " + d[1]);
				dic.put(map[x][y], new int[] {x+d[0], y+d[1]});
				x += d[0];
				y += d[1];
			}
			else {
				break;
			}
		}
		return dic;
	}
	
	public static void updateSan(HashMap<Integer, int[]> dic) {
		//지도 갱신 
		for (int i=1; i<=P; i++) {
			if (!dic.containsKey(i)) {
				continue;
			}
//			System.out.println("상호작용 산타: " + i);
			
			int x = santa[i][0];
			int y = santa[i][1];
			map[x][y] = 0;
		}
		
		//위치 변경 
		for (int i=1; i<=P; i++) {
			if (!dic.containsKey(i)) {
				continue;
			}	
//			System.out.println("상호작용 산타: " + i);
			
			int newX = dic.get(i)[0];
			int newY = dic.get(i)[1];
			
			if (checkFail(newX, newY)) {
//				System.out.println("탈락 ");
				status[i] = -1;
			}
			else {
//				System.out.println("위치 갱신  ");
//				System.out.println(newX + " " + newY);
				santa[i][0] = newX;
				santa[i][1] = newY;
				map[newX][newY] = i;
			}
		}
	}
	
	public static void boomRu(int san, int[] d) {
		HashMap<Integer, int[]> dic = new HashMap<>();
		int[] san_d = {d[0], d[1]};
		
//		System.out.println("충돌한 산타: " + san);
		score[san] += C;
		status[san] = 2;
		
		san_d[0] *= C;
		san_d[1] *= C;
		
		int sanX = santa[san][0] + san_d[0];
		int sanY = santa[san][1] + san_d[1];
		
//		System.out.println("밀려난 위치: " + sanX + " " + sanY);
		
		//탈락확인 
		if (checkFail(sanX, sanY)) {
//			System.out.println("탈락 ");
			status[san] = -1;
			map[santa[san][0]][santa[san][1]] = 0;
		}
		else {
			//상호작용 발생  
			if (map[sanX][sanY] > 0) {
//				System.out.println("상호작용 발생 ");
				dic = slideSan(san, san_d, d, 0);
			}
			else {
				//상호작용 발생 안하면 
//				System.out.println("상호작용 발생 안함 ");
				dic.put(san, new int[] {sanX, sanY});
			}
			updateSan(dic);
		}
	}
	
	public static void moveRu() {
		int san = pickSanta();
//		System.out.println("선택된 산타: " + san);
		int[] d = pickDir(san);
//		System.out.println("방향: " + d[0] + " "+ d[1]);
		
		int afterX = ru[0]+d[0];
		int afterY = ru[1]+d[1];
		
		//충돌 발생 
		if (map[afterX][afterY] > 0) {
			boomRu(map[afterX][afterY], d);
		}
		
		//루돌프 위치 갱신 
		map[ru[0]][ru[1]] = 0;
		ru[0] = afterX;
		ru[1] = afterY;
		map[afterX][afterY] = -1;
		
//		System.out.println("루돌프 이동 후: ===================");
//		for (int i=0; i<N; i++) {
//			System.out.println(Arrays.toString(map[i]));
//		}
	}
	
	public static int[] pickDirtoRu(int san) {
		int[] d = {0, 0};
		
		int curX = santa[san][0];
		int curY = santa[san][1];
		
		int rx = ru[0];
		int ry = ru[1];
		
		int minD = (curX - rx) * (curX - rx) + (curY - ry) * (curY - ry);
		
		for (int[] dir: san_dir) {
			int nextX = curX + dir[0];
			int nextY = curY + dir[1];
			
			if (nextX >= 0 && nextX < N && nextY >= 0 && nextY < N) {
				if (map[nextX][nextY] <= 0) {
					int dist = (nextX - rx) * (nextX - rx) + (nextY - ry) * (nextY - ry);
					if (dist < minD) {
						minD = dist;
						d[0] = dir[0];
						d[1] = dir[1];
					}
				}
			}
		}
		return d;
	}
	
	public static void boomSan(int san, int[] d) {
		HashMap<Integer, int[]> dic = new HashMap<>();
		int[] san_d = {d[0], d[1]};
		
//		System.out.println("충돌한 산타: " + san);
		score[san] += D;
		status[san] = 2;
		
		san_d[0] *= (-1 * D);
		san_d[1] *= (-1 * D);
		
		int sanX = santa[san][0] + d[0] + san_d[0];
		int sanY = santa[san][1] + d[1] + san_d[1];
		
//		System.out.println("밀려난 위치: " + sanX + " " + sanY);
		
		//탈락확인 
		if (checkFail(sanX, sanY)) {
//			System.out.println("탈락 ");
			status[san] = -1;
			map[santa[san][0]][santa[san][1]] = 0;
		}
		else {
			//상호작용 발생  
			if (map[sanX][sanY] > 0) {
//				System.out.println("상호작용 발생 ");
				dic = slideSan(san, san_d, d, 1);
			}
			else {
				//상호작용 발생 안하면 
//				System.out.println("상호작용 발생 안함 ");
				dic.put(san, new int[] {sanX, sanY});
			}
			updateSan(dic);
		}
	}
	
	public static void moveSanta() {
		for (int i=1; i<=P; i++) {
			if (status[i] != 0) {
				continue;
			}
//			System.out.println(i + "번째 산타 이동 시작 : ===================");
			int[] d = pickDirtoRu(i);
			
			if (d[0] == 0 && d[1] == 0) {
				continue;
			}
			
			int afterX = santa[i][0] + d[0];
			int afterY = santa[i][1] + d[1];
			
			if (map[afterX][afterY] == -1) {
				boomSan(i, d);
			}
			else {
				map[santa[i][0]][santa[i][1]] = 0;
				map[afterX][afterY] = i;
				santa[i][0] = afterX;
				santa[i][1] = afterY;
			}
//			System.out.println(i + "번째 산타 이동 후 : ===================");
//			for (int n=0; n<N; n++) {
//				System.out.println(Arrays.toString(map[n]));
//			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		// System.setIn(new java.io.FileInputStream("src/sw_230202/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		santa = new int[P+1][2];
		score = new int[P+1];
		status = new int[P+1];
		ru = new int[2];
		
		st = new StringTokenizer(br.readLine());
		
		ru[0] = Integer.parseInt(st.nextToken())-1;
		ru[1] = Integer.parseInt(st.nextToken())-1;
		map[ru[0]][ru[1]] = -1;
		
		for (int p=0; p<P; p++) {
			st = new StringTokenizer(br.readLine());
			int si = Integer.parseInt(st.nextToken());
			santa[si][0] = Integer.parseInt(st.nextToken())-1;
			santa[si][1] = Integer.parseInt(st.nextToken())-1;
			map[santa[si][0]][santa[si][1]] = si;
		}
//		System.out.println("초기 위치 ===================");
//		for (int i=0; i<N; i++) {
//			System.out.println(Arrays.toString(map[i]));
//		}
//		
//		for (int i=0; i<=P; i++) {
//			System.out.println(Arrays.toString(santa[i]));
//		}
		
		
		for (int m=0; m<M; m++) {
//			System.out.println(m + "번째 턴.........................");
			moveRu();
			moveSanta();
			boolean end = false;
			for (int i=1; i<=P; i++) {
				if (status[i] >= 0) {
					end = true;
					break;
				}
			}
			if (!end) {
				break;
			}
			for (int i=1; i<=P; i++) {
				if (status[i] < 0) {
					continue;
				}
				if (status[i] > 0) {
					status[i]--;
				}
				score[i]++;
//				System.out.println(i + "번째 산타의 점수: " + score[i]);
//				System.out.println(i + "번째 산타의 상태: " + status[i]);
			}
		}
		for (int i=1; i<=P; i++) {
			System.out.print(score[i] + " ");
		}	
	}

}