package org.dfpl.lecture.database.assignment4.assignment_21013215;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MySortMerge extends SortMergeAlgorithm {
	private String baseDirectory;
	private int[] buffer;
	private int m;
	private int n;
	private int b;

	public MySortMerge(String baseDirectory, ArrayList<Integer> data, int m, int n, int b) {
		super(baseDirectory, data, m, n, b);
		this.baseDirectory = baseDirectory;
		this.buffer = new int[m*b];//m개의 메모리 갯수 * b칸의 값
		this.m = m;
		this.n = n;
		this.b = b;
	}

	@Override
	public void write(ArrayList<Integer> data, String fileName) {
		try {
			File file = new File(baseDirectory + File.separator + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
				for (Integer item : data) {
					bos.write(ByteBuffer.allocate(n).putInt(item).array());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file: " + fileName, e);
		}
	}

	@Override
	public ArrayList<Integer> read(String fileName) {
		ArrayList<Integer> ret = new ArrayList<>();
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(baseDirectory + File.separator + fileName))) {
			while (true) {
				byte[] read = new byte[n];
				int result = bis.read(read);
				if (result == -1) break;
				int value = ByteBuffer.wrap(read).getInt();
				ret.add(value);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + fileName, e);
		}
		return ret;
	}

	@Override
	public Cost createRuns() {
		Cost retCost = new Cost(0, 0);
		ArrayList<Integer> unsortList = read("unsorted.txt");
		int fileIndex = 0;
		int size = unsortList.size();
		retCost.setBlockTransfer(retCost.getBlockTransfer()+size);

		File directory = new File(baseDirectory);


		// r로 시작하는 파일 필터링
		File[] filesToDelete = directory.listFiles(file ->
				file.isFile() && (file.getName().startsWith("r") || file.getName().startsWith("sorted"))
		);

		// 그전에 만든 파일 삭제
		if (filesToDelete != null)
			for (File file : filesToDelete)
				file.delete();

		for (int i = 0; i < size;) {
			retCost.setDiskSeek(retCost.getDiskSeek()+1);
			int endIdx = Math.min(i + m, size);
			ArrayList<Integer> rList = new ArrayList<>(unsortList.subList(i, endIdx));
			rList.sort(Integer::compareTo);
			retCost.setBlockTransfer(retCost.getBlockTransfer()+rList.size());
			retCost.setDiskSeek(retCost.getDiskSeek()+1);
			write(rList, "r0_" + (fileIndex++) + ".txt");
			i += m;
		}
		return retCost;
	}

	@Override
	public Cost mergeRuns() {
		//

		Cost retCost = new Cost(0, 0);
		//파일에서 현재까지 읽은 index
		int[] fsIndex = new int[m];
		//fsIndex 메모리에 각 file buffer page count , buffer page for writing
		int[] fsCount = new int[m];
		//buffer Index
		int[] bfIndex = new int[m];
		//버퍼의 마지막 인덱스 or 버퍼의 page갯수
		int bufLast = m-1;
		int fileIndex = 0;
		String targetName = "";
		while (true) {
			ArrayList<String> fileArrList = getRunNames();
			String findFile = "r" + fileIndex;
			List<String> fileList = fileArrList.stream()
					.filter(s -> s.startsWith(findFile))
					.collect(Collectors.toList());

			if (fileList.size() == 1) {
				targetName = "r" + fileIndex+"_0.txt";
				break;
			}
			for (int i = 0 ; i<m;i++){
				fsIndex[i]=0;
				fsCount[i]=0;
				bfIndex[i]=0;
			}
			int newFileIndex = 0;
			for (int t = 0; t < Math.ceil((double) fileList.size() / (m - 1)); t++) {
				//buffer와 fsIndex 맨처음 초기화
				int fileCount = initializeBuffer(buffer, fsIndex, fsCount, bfIndex, fileList, fileIndex, t);
				//읽은 파일 갯수만큼 더한다.
				retCost.setDiskSeek(retCost.getDiskSeek()+fileCount);

				//BlockTransfer 갯수
				for (int i = 0 ; i<m;i++)
					retCost.setBlockTransfer(retCost.getBlockTransfer()+fsCount[i]);

				while (true) {
					//minIdx 찾기
					int minIdx = -1;//minIdx는 몇번째 파일인지를 나타낸다
					for (int i = 0; i < fileCount; i++) {
						if( fsCount[i] == 0)	continue;
						if ( minIdx == -1 || buffer[minIdx*b+bfIndex[minIdx]] > buffer[i*b+bfIndex[i]]) {
							minIdx = i;
						}
					}

					if (minIdx == -1) {						
						//해당 while문은 모든 파일의 값을 다 writing page에 옮겼지만 꽉차지는 않은 상태에서 실행되는 코드
						if(fsCount[bufLast] > 0) {
							retCost.setDiskSeek(retCost.getDiskSeek() + 1);
						}
						while(fsCount[bufLast] > 0){
							int bufferValue = buffer[b*bufLast+fsIndex[bufLast]];

							retCost.setBlockTransfer(retCost.getBlockTransfer()+1);
							append(baseDirectory, "r" + (fileIndex + 1) + "_" + newFileIndex + ".txt", bufferValue, n);

							fsCount[bufLast]-=1;
							fsIndex[bufLast]+=1;
						}
						for (int i = 0 ; i<m;i++){
							bfIndex[i]=0;
						}
						break;
					}

					//buffer page에서 한개 가져오기
					int value = buffer[minIdx*b+bfIndex[minIdx]];
					fsCount[minIdx]-=1;
					bfIndex[minIdx]+=1;

					//가져온 값 writing page에 넣기
					buffer[b*bufLast+fsCount[bufLast]] = value;
					fsCount[bufLast]+=1;

					//buffer Page에 값을 다 writing page로 옮긴경우
					//2024-11-28 수정
					//문제점 : filePage에서 가져온 값을 다 buffer page로 옮겼을 경우 1개만 옮기고 있음
					//수정 : 1개가 아닌 최소1개에서 b개의 item을 넣어줘야한다.
					//2024-11-29 수정
					//문제점 : 다음 item에 값이 -1인경우에도 DS값이 올라간다
					int oneT = 0;
					if(fsCount[minIdx] == 0 && fsIndex[minIdx] != -1){
						for (int z = 0 ; z < b ;z++){
							int readVal = read(baseDirectory, generateFileName(fileIndex, t, minIdx), fsIndex[minIdx], n);
							if(readVal != -1){
								buffer[minIdx*b+fsCount[minIdx]] = readVal;
								fsIndex[minIdx]++;
								fsCount[minIdx]++;
								bfIndex[minIdx] = 0;
								retCost.setBlockTransfer(retCost.getBlockTransfer()+1);
								if(oneT==0){
									retCost.setDiskSeek(retCost.getDiskSeek()+1);
									oneT++;
								}
							}else{
								//fsIndex의 값이 -1인경우 해당 파일의 값을 모두 읽은 것 이다.
								fsIndex[minIdx]= -1;
								break;
							}
						}
					}
					
					//writing page에 값이 꽉참
					if(fsCount[bufLast] == b){
						retCost.setDiskSeek(retCost.getDiskSeek()+1);
						while(fsCount[bufLast] > 0){
							int bufferValue = buffer[b*bufLast+fsIndex[bufLast]];
							append(baseDirectory, "r" + (fileIndex + 1) + "_" + newFileIndex + ".txt", bufferValue, n);
							retCost.setBlockTransfer(retCost.getBlockTransfer()+1);
							fsCount[bufLast]-=1;
							fsIndex[bufLast]+=1;
						}
						fsIndex[bufLast]=0;
					}
				}
				newFileIndex++;
			}
			fileIndex++;
		}
		
		//한개로 Merge된 파일을 sorted파일로 생성
		//File tarFile = new File(baseDirectory + File.separator +targetName);
		//File newFile = new File(baseDirectory + File.separator +"sorted.txt");

		int index = 0;
		//이미 r(last)_0에서 retCost.setBlockTransfer(retCost.getBlockTransfer()+1)을 다 해줬으므로
		//아래 sorted.txt파일은 setBlockTransfer를 따로 해주지않는다.
		while (true) {
			int value = read(baseDirectory, targetName, index, n);
			if (value == -1) break;
			//retCost.setBlockTransfer(retCost.getBlockTransfer()+1);

			//읽은 값을 newFile에 추가
			append(baseDirectory, "sorted.txt", value, n);
			//retCost.setBlockTransfer(retCost.getBlockTransfer()+1);
			index++;
		}

		return retCost;
	}

	@Override
	public ArrayList<String> getRunNames() {
		ArrayList<String> retList = new ArrayList<>();
		File directory = new File(baseDirectory);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().startsWith("r")) {
					retList.add(file.getName());
				}
			}
		}
		retList.sort(String::compareTo);
		return retList;
	}

	public static int read(String baseDirectory, String fileName, int index, int n) {
		try (RandomAccessFile raf = new RandomAccessFile(baseDirectory + File.separator + fileName, "r")) {
			raf.seek((long) index * n);
			byte[] buf = new byte[n];
			int bytesRead = raf.read(buf);
			if (bytesRead == -1) return -1;
			return ByteBuffer.wrap(buf).getInt();
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + fileName, e);
		}
	}

	public static void append(String baseDirectory, String fileName, int item, int n) {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(baseDirectory + File.separator + fileName, true))) {
			bos.write(ByteBuffer.allocate(n).putInt(item).array());
		} catch (IOException e) {
			throw new RuntimeException("Error appending to file: " + fileName, e);
		}
	}

	private int initializeBuffer(int[] buffer, int[] fsIndex, int[] fsCount, int[] bfIndex, List<String> fileList, int fileIndex, int group) {
		int fileCount = 0;
		for (int i = 0; i < m; i++) {
			bfIndex[i] = 0;
			if (i == m - 1) {
				fsIndex[i] = 0;
				fsCount[i] = 0;
			} else {
				String fileName = generateFileName(fileIndex, group, i);
				if (fileList.contains(fileName)) {//파일이 있을경우
					for(int k =0;k<b;k++){
						int insertVal = read(baseDirectory, fileName, k, n);
						if(insertVal != -1){
							buffer[i*b+k] = insertVal;
							fsIndex[i] = k+1;
							fsCount[i] = k+1;
						}else{
							fsIndex[i] = -1;
							break;
						}
					}
					fileCount++;
				}
			}
		}
		return fileCount;
	}

	private String generateFileName(int fileIndex, int group, int bufferIndex) {
		return "r" + fileIndex + "_" + (group * (m - 1) + bufferIndex) + ".txt";
	}
}
