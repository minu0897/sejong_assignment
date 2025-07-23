package org.dfpl.lecture.database.assignment2.assignment1_21013215;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class MyBPlusTreeNode {

	// Data Abstraction 은 예시일 뿐 자유롭게 B+ Tree 의 범주 안에서 어느정도 수정가능
	private MyBPlusTreeNode parent;
	private List<Integer> keyList;
	private List<MyBPlusTreeNode> children;

	public MyBPlusTreeNode() {
		this.parent = null;
		this.keyList = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	//get keyList
	//return : keyList 
	public List<Integer> getKeylist() {
		return keyList;
	}
	

	//get children
	//return : children 
	public List<MyBPlusTreeNode> getChildren() {
		return children;
	}
	
	//get parent
	//return : parent 
	public MyBPlusTreeNode getParent() {
		return parent;
	}


	//set parent
	public void setParent( MyBPlusTreeNode var ) {
		parent = var; 
	}
	
	
	
	
	//keyList에 삽입
	//return : 삽입된 위치
	public int insertIdx(Integer e) {
		int idx = Collections.binarySearch(keyList, e);
		if(idx<0) {
			idx=-idx-1;
		}
		 
		if( keyList.size()!=0 && idx < keyList.size() && keyList.get(idx) == e)
			idx+=1;
		keyList.add(idx,e);
		return idx;
	}
}
