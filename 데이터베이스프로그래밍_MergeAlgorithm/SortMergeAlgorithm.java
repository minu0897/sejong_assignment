package org.dfpl.lecture.database.assignment4.assignment_21013215;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class SortMergeAlgorithm {
	// 추가적인 멤버변수를 선언하지 않는다.
	private String baseDirectory;
	private int[] buffer;
	private int m;
	private int n;
	private int b;

	/**
	 * 정렬되지 않은 데이터는 생성당시에 baseDirectory의 unsorted.txt를 생성하여 저장되며 초기화 된다. 
	 * 
	 * @param baseDirectory data가 저장될 위치 (예: d:\\)
	 * @param data 정렬되지 않은 데이터
	 * @param m 각 페이지의 메모리 사이즈: 교과서의 m 정의
	 * @param n 각 정수의 파일시스템에서 차지하는 바이트 수, 정수이므로 적어도 4 이상일 것
	 * @param b 교과서의 $b_b$ 정의
	 * 
	 */
	public SortMergeAlgorithm(String baseDirectory, ArrayList<Integer> data, int m, int n, int b) {
		this.baseDirectory = baseDirectory;
		this.buffer = new int[m*b];
		this.m = m;
		this.n = n;
		this.b = b;

		//기존 unsorted파일 지우기
		try{
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(baseDirectory+"\\unsorted.txt"));
			bos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		for (Integer item : data) {
			MySortMerge.append(baseDirectory, "unsorted.txt", item, n);
		}
	}
	
	/**
	 * 구현을 할 때 (생성자) 활용 가능한 메소드 
	 * 
	 * @param data ArrayList<Integer> 형태의 데이터 
	 * * @param fileName baseDirectory안에 들어있는 fileName (예: unsorted.txt, r0-1.txt, r1-1.txt, r1-2.txt, r2-1.txt, sorted.txt 등)
	 */
	public void write(ArrayList<Integer> data, String fileName) {};
	
	/**
	 * 파일의 내용을 ArrayList<Integer> 로 반환한다. 
	 * 
	 * @param fileName baseDirectory안에 들어있는 fileName (예: unsorted.txt, r0-1.txt,  r1-1.txt, r1-2.txt, r2-1.txt, sorted.txt 등)
	 * @return 파일의 내용을 ArrayList<Integer> 로 반환한다. 
	 */
	public ArrayList<Integer> read(String fileName) { return null; };
	
	/**
	 * unsorted.txt 를 r0-0.txt r0-1.txt r0-2.txt... 등으로 만든다. 각 integer는 n bytes를 사용하여 파일에 연속적으로 저장된다.
	 * 
	 * @return run을 생성할 때의 block transfer 와 disk seek 갯수를 반환한다. 
	 */
	public Cost createRuns() { return null; };
	
	/**
	 * r0-0.txt r0-1.txt r0-2.txt... 를 sorted.txt 로 만든다. 각 integer는 n bytes를 사용하여 파일에 연속적으로 저장한다.
	 * p번째 pass의 q번째 run은 rp-q.txt로 저장된다.
	 * 예: 1번째 pass의 2번째 run은 r1-2.txt로 저장된다.  
	 * 
	 * @return run을 병합하여 sorted.txt 생성할 때까지의 block transfer 와 disk seek 갯수를 반환한다. 
	 */
	public Cost mergeRuns() { return null; };
	
	/**
	 * 각 run의 파일 이름을 반환한다. 
	 * p번째 pass의 q번째 run은 rp-q.txt로 저장된다. order by p ASC, order by q ASC의 순서로 반환한다.
	 * 예: [r0-1.txt, r0-2.txt, r0-3.txt, r1-1.txt, r1-2.txt, r1-3.txt, r2-1.txt]
	 * 
	 * @return
	 */
	public ArrayList<String> getRunNames() { return null; };
	
	/**
	 * unsorted.txt 를 sorted.txt 로 만드는 전체 메소드 (변경하지 말것)
	 * 
	 * @return unsorted.txt 를 sorted.txt 로 만드는 전체 메소드
	 */
	public Cost run() {
		//여기서 Run을 쪼갠다.
		Cost cost1 = createRuns();
		System.out.println(cost1);

		//쪼갠Runs를 합친다.
		Cost cost2 = mergeRuns();
		System.out.println(cost2);
		return new Cost(cost1.getBlockTransfer() + cost2.getBlockTransfer(), cost1.getDiskSeek() + cost2.getDiskSeek());
	};

	@Override
	public String toString() {
		return "SortMergeAlgorithm [baseDirectory=" + baseDirectory + ", buffer=" + Arrays.toString(buffer) + ", m=" + m
				+ ", n=" + n + ", b=" + b + "]";
	};
}
