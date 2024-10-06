import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
	
	public static int K;
	public static int M;
	public static int[][] map;
	public static Queue<Integer> pieces;
	public static int[][] maxMap;
	public static int maxGet;
	public static int[][] move = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	
	public static int[] core;
	public static int dir = 3;
	
	
	public static void rotate(int[][] cube) {
		int[][] after = new int[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				after[j][2-i] = cube[i][j];
			}
		}
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				cube[i][j] = after[i][j];
			}
		}
	}
	
	public static int getGold(int[][] copy) {
		int sum = 0;
		boolean[][] check = new boolean[5][5];
//		System.out.println("1차획득함수진입=====================");
//		for (int p=0; p<5; p++) {
//			System.out.println(Arrays.toString(copy[p]));
//		}
		for (int p=0; p<5; p++) {
			for (int q=0; q<5; q++) {
				if (!check[p][q]) {
//					System.out.println(p + ", " + q + " 의 인접 리스트  ");
//					for (int a=0; a<5; a++) {
//						System.out.println(Arrays.toString(copy[a]));
//					}
					check[p][q] = true;
					Queue<int[]> queue = new LinkedList<>();
					queue.offer(new int[] {p, q});
					
					boolean[][] visited = new boolean[5][5];
					visited[p][q] = true;
					
					ArrayList<int[]> list = bfs(queue, visited, copy, copy[p][q]);
				
					for (int i=0; i<list.size(); i++) {
						int[] arr = list.get(i);
//						System.out.print(Arrays.toString(arr));
//						System.out.print(" | ");
						check[arr[0]][arr[1]] = true;
						
						if (list.size() >= 2) {
							copy[arr[0]][arr[1]] = 0;
						}
						
					}
//					System.out.println();
					if (list.size() >= 2) {
						copy[p][q] = 0;
						sum += list.size()+1;
					}
				}
			}
		}
		return sum;
	}
	
	public static ArrayList<int[]> bfs(Queue<int[]> queue, boolean[][] visited, int[][] copy, int num) {
		ArrayList<int[]> list = new ArrayList<>();
		
		while(!queue.isEmpty()) {
			int[] cur = queue.poll();
			int x = cur[0];
			int y = cur[1];
			
			for (int[] mv: move) {
				if (x+mv[0] >= 0 && x+mv[0] < 5 && y+mv[1] >= 0 && y+mv[1] < 5) {
					if (!visited[x+mv[0]][y+mv[1]] && copy[x+mv[0]][y+mv[1]] == num) {
						queue.offer(new int[] {x+mv[0], y+mv[1]});
						visited[x+mv[0]][y+mv[1]] = true;
//						System.out.println((x+mv[0]) + " " + (y+mv[1]) + " 들어간다 ");
						list.add(new int[] {x+mv[0], y+mv[1]});
					}
				}
			}
		}
		return list;
	}
	
	public static void putPieces() {
		for (int j=0; j<5; j++) {
			for (int i=4; i>=0; i--) {
				if (map[i][j] == 0) {
					map[i][j] = pieces.poll();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		//System.setIn(new java.io.FileInputStream("src/sw_240102/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[5][5];
		maxMap = new int[5][5];
		
		pieces = new LinkedList<>();
		for (int i=0; i<5; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j=0; j<5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		st = new StringTokenizer(br.readLine());
		for (int i=0; i<M; i++) {
			pieces.offer(Integer.parseInt(st.nextToken()));
		}
		
		for (int t=0; t<K; t++) {
			maxGet = 0;
			maxMap = new int[5][5];
			
			for (int j=3; j>0; j--) {
				for (int i=3; i>0; i--) {
					for (int k=3; k>0; k--) { // 회전각 270 180 90
						//5X5 복사본 
						int[][] copy = new int[5][5];
						for (int p=0; p<5; p++) {
							for (int q=0; q<5; q++) {
								copy[p][q] = map[p][q];
							}
						}
						//3X3 회전 값
						int[][] cube = new int[3][3];
						int dx = i-1;
						int dy = j-1;
						for (int p=0; p<3; p++) {
							for (int q=0; q<3; q++) {
								cube[p][q] = map[p+dx][q+dy];
							}
						}
//						System.out.println(i + " " + j);
//						for (int p=0; p<3; p++) {
//							System.out.println(Arrays.toString(cube[p]));
//						}
//						System.out.println("======================");
						
						for (int r=0; r<k; r++) { 
							rotate(cube);
						}
						for (int p=0; p<3; p++) {
							for (int q=0; q<3; q++) {
								copy[p+dx][q+dy] = cube[p][q];
							}
						}
//						System.out.println(i + " " + j);
//						for (int p=0; p<5; p++) {
//							System.out.println(Arrays.toString(copy[p]));
//						}
//						System.out.println("..............................");
//						
						int get = getGold(copy);
//						
//						System.out.println("1차획득후================");
//						for (int p=0; p<5; p++) {
//							System.out.println(Arrays.toString(copy[p]));
//						}
						
						if (get >= maxGet) {
							if (k <= dir) {
								maxGet = get;
								dir = k;
								//copy -> maxMap으로 복사
//								System.out.println(i + " " + j);
//								for (int p=0; p<5; p++) {
//									System.out.println(Arrays.toString(copy[p]));
//								}
//								System.out.println("..............................");
								
								for (int p=0; p<5; p++) {
									for (int q=0; q<5; q++) {
										maxMap[p][q] = copy[p][q];
									}
								}
							}
						}
						
					}
				}
			}
			if (maxGet == 0) {
				break;
			}
			//maxMap -> map
			for (int i=0; i<5; i++) {
				for (int j=0; j<5; j++) {
					map[i][j] = maxMap[i][j];
				}
			}
//			for (int p=0; p<5; p++) {
//				System.out.println(Arrays.toString(map[p]));
//			}
//			System.out.println("..............................");
			//유물조각 채우기
			putPieces();
//			for (int p=0; p<5; p++) {
//				System.out.println(Arrays.toString(map[p]));
//			}
//			System.out.println("..............................");
			while (true) {
				int get = getGold(map);
//				for (int p=0; p<5; p++) {
//					System.out.println(Arrays.toString(map[p]));
//				}
//				System.out.println("..............................");
				if (get == 0) {
					break;
				}
				maxGet += get;
				putPieces();
//				for (int p=0; p<5; p++) {
//					System.out.println(Arrays.toString(map[p]));
//				}
//				System.out.println("..............................");
			}
			System.out.print(maxGet + " ");

			
		}
	}

}