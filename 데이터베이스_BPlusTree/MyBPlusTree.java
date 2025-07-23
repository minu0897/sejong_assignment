package org.dfpl.lecture.database.assignment2.assignment1_21013215;
import org.dfpl.lecture.database.assignment2.assignment1_21013215.MyBPlusTreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;

@SuppressWarnings("unused")
public class MyBPlusTree implements NavigableSet<Integer> {

	// Data Abstraction 은 예시일 뿐 자유롭게 B+ Tree 의 범주 안에서 어느정도 수정가능
	private MyBPlusTreeNode root;
	private LinkedList<MyBPlusTreeNode> leafList;
	private int size;
	
	//생성자 
	public MyBPlusTree(int m) {
		size=m;
		this.leafList = null;//아무것도 넣지 않으면 null
		root = new MyBPlusTreeNode();//빈 노드 대입
	}
	
	//root 변수 반환
	public MyBPlusTreeNode getRoot() {
		return root;
	}
		
	//leafList 변수 반환
	public LinkedList<MyBPlusTreeNode> getLeaf() {
		return leafList;
	}
	
	
	//leafList에서 T를 삭제한다.
	public void removeLeafNode(MyBPlusTreeNode T) {
		if(leafList == null)	return;
		int delidx =leafList.indexOf(T);
		leafList.remove(delidx);
		if(leafList.size()==0) {
			System.out.println(1);
		}
	}
	
	//재귀로 찾으려는 key 값을 가진 node 를 찾는다.
	public MyBPlusTreeNode searchNode(MyBPlusTreeNode targetnode,int target) {
		List<Integer> keylist = targetnode.getKeylist();
		for (int i = 0; i < keylist.size(); i++) {
			if(target < keylist.get(i) ) {//targetnode의 key 값이 값(target)보다 작으면 node 다시 세팅한 후 함수 실행 
				MyBPlusTreeNode next = targetnode.getChildren().get(i);//child node 세팅
				
				if(next.getChildren().size() == 0) {//만약 child node 가 leafnode 이면 반환
					return next;
				}
				return searchNode(next,target);//children node에서 다시 값 찾아서 반환
			}
		}
		
		//keylist에 값 중 작은 값이 없으면 맨끝에 값을 기준으로 다시 찾기
		MyBPlusTreeNode next = targetnode.getChildren().get(keylist.size());
		if(next.getChildren().size() == 0) {// leafnode 이면 반환
			return next;
		}
		//leafnode 아니면 맨끝children node로 다시 찾기
		return searchNode(targetnode.getChildren().get(keylist.size()),target);
	}
	
	//재귀로 삭제할 node 를 찾는다.
	public MyBPlusTreeNode deleteNode(MyBPlusTreeNode targetnode,int target) {
		List<Integer> keylist = targetnode.getKeylist();
		for (int i = 0; i < keylist.size(); i++) {
			if(target == keylist.get(i)  ) {
				if(targetnode.getChildren().size()==0)
					return targetnode;//targetnode에 key에 찾으려는 값이 있으면 targetnode를 return한다.
				else {
					return deleteNode(targetnode.getChildren().get(i+1),target);
				}
			}	
			if(target < keylist.get(i) ) {
				if(targetnode.getChildren().size() == 0)	return null;
				return deleteNode(targetnode.getChildren().get(i),target);//해당 키 리스트에 
			}
		}
		if(targetnode.getChildren().size() ==0 )return null;//leaf node 까지 다 확인을 하였으나 값이 없을 경우
		return deleteNode(targetnode.getChildren().get(targetnode.getChildren().size()-1),target);
	}
	
	
	// 특정 값을 찾는 함수
	public MyBPlusTreeNode getNode(Integer key) {
		MyBPlusTreeNode start = root;//root에서 시작
		if(start == null )
			System.out.println(key+" not found ");
		
		while(true) {//leaf node에 도달할 때까지 값을 기준으로 찾아간다.
			if(start.getChildren().size()==0) {
				if(start.getKeylist().contains(key)) {//리프 노드안에 찾으려는 값 있을경우
					System.out.println(key+" found ");//
					return start;
				}else {
					System.out.println(key+" not found ");
					return null;
				}
			}
			List<Integer> keylist = start.getKeylist();
			Boolean last = true;
			for(int i=0;i<keylist.size();i++) {
				if(key < keylist.get(i)) {//key list에 값이 작으면 해당하는 child node로 다시 while문이 시작
					System.out.println("less than "+keylist.get(i));
					start = start.getChildren().get(i);
					last=false;
					break;
				}
			}
			if(last) {//keylist의 값 중 찾으려는 값보다 작은게 없으면 start를 child node의 끝 node로 재설정 한 후 다시 while문 실행
				System.out.println("larger than or equal to "+keylist.get(keylist.size()-1));
				start = start.getChildren().get(start.getKeylist().size());
			}
		}
	}

	//과제랑 관련x
	public void treeprint(MyBPlusTreeNode node,int level) {
		System.out.println("=====start=====");
		treeprintfun(node, level);
		System.out.println("=====end=====");
	}
	public void treeprintfun(MyBPlusTreeNode node,int level) {
		System.out.print("level:"+level);
		
		System.out.println("");
		for(int i = 0 ; i<node.getKeylist().size() ; i++) {
			System.out.print(node.getKeylist().get(i)+" ");
		}

		System.out.println("");
		
		for(int i = 0 ; i<node.getChildren().size() ; i++){
			treeprintfun(node.getChildren().get(i),level+1);
		}
		System.out.println("");
	}


	//과제랑 관련x
	public void leafprint() {
		if(leafList==null)	return;
		for(int i = 0 ; i<leafList.size();i++) {
			for(int j=0;j<leafList.get(i).getKeylist().size();j++) {
				System.out.print(leafList.get(i).getKeylist().get(j)+" ");	
			}
			
		}
	}
	
	//순회
	public void inorderTraverse() {
		//재귀를 통하여 순회
		inorderRecursion(root);
		System.out.println();
	}

	//재귀를 통하여 순회
	public void inorderRecursion(MyBPlusTreeNode node) {
		//leafnode이면 ketlist에 있는 값들 출력
		if(node.getChildren().size()==0)
			for(int j=0;j<node.getKeylist().size();j++)
				System.out.println(node.getKeylist().get(j));
		
		//child node가 있으면 childnode를 가지고 다시 함수 실행
		for(int i= 0 ; i<node.getChildren().size();i++) {
			inorderRecursion(node.getChildren().get(i));
		}
	}
	
	//keylist에 값이 맨 앞값이 바뀔 경우 부모노드의 값을 바꾸는 함수
	public void updateParentKey(MyBPlusTreeNode node,int oldVal,int newVal) {
		if( leafList != null && leafList.get(0)==node)	return;//leafnode의 맨앞은 해당x
		if( node.getParent() == null	)return;
		if(oldVal == newVal)	return;
		
		MyBPlusTreeNode parNode = node.getParent() ;
		int idx = parNode.getChildren().indexOf(node);
		if (idx == -1)	return ;
		if (idx == 0)	{
			updateParentKey(node.getParent(),oldVal,newVal);
			return;
		}
		
		if(parNode.getKeylist().get(idx-1) == oldVal ) {
			parNode.getKeylist().remove(idx-1);
			parNode.getKeylist().add(idx-1, newVal);
		}

		
		// MyBPlusTreeNode parNode = node ;
		// if(node == root)	return;//node가 root일 경우 해당x
		//
		// do{
		// 	for(int i=0;i<parNode.getKeylist().size();i++) {
		// 		if(oldVal == parNode.getKeylist().get(i)) {//기존의 값이 있을 경우
		// 			//기존 값을 삭제한 후 그 위치에 새로운 값을 넣는다.
		// 			parNode.getKeylist().remove(i);
		// 			parNode.getKeylist().add(i, newVal);
		// 			return;
		// 		}
		// 	}
		// 	//만약 값을 찾지 못했으면 다시 부모 node에서 찾는다.
		// 	parNode= parNode.getParent();
		// }while(parNode != null);
	}
	
	//bpTree에 값을 넣는 함수
	@Override
	public boolean add(Integer e) {
		MyBPlusTreeNode tnode;
		if(root.getChildren().size()==0){//아직 leafnode가 생성이 안된 경우
			tnode = root;
		}else {//leafnode가 생성된 경우 값을 넣어야하는 node를 찾아서 tnode로 초기화
			tnode = searchNode(root,e);
		}
		
		//tnode에 값을 넣는다.
		insert(tnode, e);
		
		//maxsize 를 위배하면 node를 분할한다.
		if(violateMaxSize(tnode.getKeylist().size())) {
			splitNode(tnode);
			return true;
		}
		
		return true;
	}

	//list 안에 키의 갯수가 m-1초과일 경우 True
	public boolean violateMaxSize(int Tsize) {
		return (size-1 < Tsize ?  true:false);
	}
	
	//list 안에 키의 갯수가 최소 키 갯수를 어긴 경우 True
	public boolean violateMinKeySize(int Tsize) {
		return (Tsize < (Math.ceilDiv((size), 2)-1) ?  true:false);
	}

	//node를 분할하는 함수
	public boolean splitNode(MyBPlusTreeNode Tnode) {
		if(Tnode.getChildren().size()==0 && Tnode == root) {//맨 처음 root 에서 분할 - > 분할과함께 leafnode 생성
			int mididx = Math.ceilDiv((size-1), 2);
			int lefidx = mididx-1;
			
			//left,right node 생성
			MyBPlusTreeNode left = new MyBPlusTreeNode();
			MyBPlusTreeNode right = new MyBPlusTreeNode();
			List<Integer> keylist = Tnode.getKeylist();
			
			//leftnode 세팅
			for (int i = 0; i <= lefidx; i++) {
				insert(left, keylist.get(0));
				keylist.remove(0);
			}
			int tmpsize =keylist.size();
			

			//rightnode 세팅
			insert(right, keylist.get(0));
			for (int i = 1; i < tmpsize; i++) {
				insert(right, keylist.get(1));
				keylist.remove(1);
			}
			
			//left,right node  parent,children 세팅
			left.setParent(root);
			right.setParent(root);
			root.getChildren().add(left);
			root.getChildren().add(right);
			
			//leafList 생성 및 설정
			leafList = new LinkedList<MyBPlusTreeNode>();
			leafList.add(left);
			leafList.add(right);
		}else if(Tnode.getChildren().size()==0) {//리프 노드가 분할
			//System.out.println("split start:leaf");
			int mididx = Math.ceilDiv((size-1), 2);
			int lefidx = mididx-1;
			MyBPlusTreeNode parNode = Tnode.getParent();
			
			//middle 값이 위(parent)로 올라감
			int insertidx = insert(parNode, Tnode.getKeylist().get(mididx));//중간 키(값)가 부모 노드의 keylist로 삽입하고 그 위치를 변수로 저장

			//right node 생성
			MyBPlusTreeNode right = new MyBPlusTreeNode();//분할되어 새로운 노드 생성

			//rightnode 세팅
			List<Integer> keylist = Tnode.getKeylist();
			int tmpsize =keylist.size();
			for (int i = mididx; i < tmpsize; i++) {
				insert(right, keylist.get(mididx));
				keylist.remove(mididx);
			}
			//right node  parent,children 세팅
			right.setParent(parNode);
			parNode.getChildren().add(insertidx+1,right);
			leafList.add(leafList.indexOf(Tnode)+1,right);
			
			//parNode는 새로운 값이 추가되었으므로 max값을 넘었는지 확인, 넘었을 경우 부모node를 split한다.
			if(violateMaxSize(parNode.getKeylist().size())) {
				splitNode(parNode);
			}
		}else if(root == Tnode) {//root node 가 분할
			int mididx = Math.floorDiv((size-1), 2);
			
			//기존root -> left
			//newroot  -> root
			//right    -> right
			MyBPlusTreeNode right = new MyBPlusTreeNode();//분할되어 새로운 노드 생성 (오른쪽 노드)
			MyBPlusTreeNode newRoot = new MyBPlusTreeNode();//분할되어 새로운 노드 생성 (새로운 root 노드)
			
			//부모 설정
			Tnode.setParent(newRoot);
			right.setParent(newRoot);
			
			//child 설정
			newRoot.getChildren().add(Tnode);
			newRoot.getChildren().add(right);
			
			//right의 key 설정
			int tmpsize =Tnode.getKeylist().size();
			for(int i=mididx+1 ; i<tmpsize;i++) {
				insert(right, Tnode.getKeylist().get(mididx+1));
				Tnode.getKeylist().remove(mididx+1);
			}
			//newroot key설정
			insert(newRoot, Tnode.getKeylist().get(mididx));
			
			//기존 Tnode key삭제
			Tnode.getKeylist().remove(mididx);
			
			//right node의 child설정
			tmpsize = Tnode.getChildren().size();
			for(int i=mididx+1 ; i< tmpsize;i++) {
				MyBPlusTreeNode moveNode = Tnode.getChildren().remove(mididx+1);
				right.getChildren().add(moveNode);
				moveNode.setParent(right);
			}
			
			//newRoot를 root로 초기화
			root = newRoot;
		}else {//internal node 분할
			int mididx = Math.floorDiv((size-1), 2);
			int lefidx = mididx-1;
			MyBPlusTreeNode parNode = Tnode.getParent();
			int insertidx = insert(parNode, Tnode.getKeylist().get(mididx));//중간 키가 부모 노드의 keylist로 삽입하고 그 위치를 변수로 저장
			
			//right node 생성
			MyBPlusTreeNode right = new MyBPlusTreeNode();//분할되어 새로운 노드 생성
			
			List<Integer> keylist = Tnode.getKeylist();
			
			//right node 설정
			int tmpsize =keylist.size();
			for (int i = mididx+1; i < tmpsize; i++) {
				insert(right, keylist.get(mididx+1));
				keylist.remove(mididx+1);
				right.getChildren().add(Tnode.getChildren().get(mididx+1));
				Tnode.getChildren().get(mididx+1).setParent(right);
				Tnode.getChildren().remove(mididx+1);
			}
			//child는 한번 더 설정
			right.getChildren().add(Tnode.getChildren().get(mididx+1));
			Tnode.getChildren().get(mididx+1).setParent(right);
			Tnode.getChildren().remove(mididx+1);
			
			//부모로 옮긴 값 삭제
			keylist.remove(mididx);
			
			//부모노드의 right 추가 및 설정
			right.setParent(parNode);
			parNode.getChildren().add(insertidx+1,right);

			//parNode는 새로운 값이 추가되었으므로 max값을 넘었는지 확인, 넘었을 경우 부모node를 split한다.
			if(violateMaxSize(parNode.getKeylist().size())) {
				splitNode(parNode);
			}
		}
		return true;
	}

	//해당 node의 keyList에 삽입
	//return : insert가 일어난 위치
	public int insert(MyBPlusTreeNode Tnode,int keyval) {
		int insidx = Tnode.insertIdx(keyval);
		if( insidx == 0 && Tnode.getKeylist().size()>=2 && Tnode.getChildren().size()==0)//맨앞이 추가된 경우 && 기존의 값이 있던 상태일 경우만 업데이트 실행
			updateParentKey(Tnode, Tnode.getKeylist().get(insidx+1), keyval);
		return insidx;
	}
	
	//insert(parNode, Tnode.getKeylist().get(mididx));
	
	//node에 해당 값 삭제
	@Override
	public boolean remove(Object o) {
		int delVal = o.hashCode();
		MyBPlusTreeNode T = deleteNode(root, delVal);//삭제할 값이 있는 node
		if(T == null)	return false;
		
		List<Integer> key = T.getKeylist();
		int delidx = key.indexOf(delVal);
		
		//key에서 삭제
		T.getKeylist().remove(delidx);
		
		//root에서 삭제한 후 keylist에 값이 없을 경우 함수를 끝낸다.
		if(T==root && T.getKeylist().size() == 0 )	return true;
		
		if(delidx == 0 ) {//삭제한 key가 맨 앞에 위치한 경우 부모의 key값을 같이 변경해준다.
			updateParentKey(T, delVal, key.get(0));
		}
		
		//삭제한 후 removeFun함수 실행
		return removeFun(T);
	}
	
	//삭제한 후 해당node가 문제없는지 확인 및 수정하는 함수
	public boolean removeFun(MyBPlusTreeNode T) {
		List<Integer> key = T.getKeylist();
		
		//해당node가 root이고 값이 한개이상 있다면 함수 끝
		//+root는 key가 한개만 있어도 된다.
		if( T == root && T.getKeylist().size() >= 1 )	return true;
		
		// minkey를 위반할 경우 
		if(violateMinKeySize(key.size())) {
			MyBPlusTreeNode P = T.getParent();//T기준 부모 node
			int tmpidx = P.getChildren().indexOf(T);//몇번째 자식인지 변수에 저장
			MyBPlusTreeNode Ls = null;
			MyBPlusTreeNode Rs = null;
		
			//Ls : 부모의 T기준 왼쪽 node
			if(tmpidx != 0)
				Ls = P.getChildren().get(tmpidx-1);//T기준 왼쪽 node

			//Rs : 부모의 T기준 오른쪽 node
			if(tmpidx < P.getChildren().size()-1)
				Rs = P.getChildren().get(tmpidx+1);//T기준 오른쪽 node
			
			if(Ls != null && !violateMinKeySize(Ls.getKeylist().size()-1)) {//왼쪽node가 있고 왼쪽node의 key가 한개 없어도 minkey를 위배하지않으면
				//왼쪽node에서 하나의 값을 빌려온다.
				int newVal = Ls.getKeylist().get(Ls.getKeylist().size()-1);
				Ls.getKeylist().remove(Ls.getKeylist().size()-1);
				

				if(Ls.getChildren().size() > 0) {
					int oldVal = Ls.getChildren().get(Ls.getChildren().size()-1).getKeylist().get(Ls.getChildren().get(Ls.getChildren().size()-1).getKeylist().size()-1);
					updateParentKey(T,oldVal,newVal);
					insert(T,newVal);
					
					MyBPlusTreeNode moveT = Ls.getChildren().get(Ls.getChildren().size()-1);
					
					T.getChildren().add(0,moveT);
					moveT.setParent(T);
					Ls.getChildren().remove(Ls.getChildren().size()-1);
					//updateParentKey(Rs,oldVal,Rs.getChildren().get(0).getKeylist().get(0));
				}else {//internalnode가 아니면 Ls에 값을 빌려왔으니 Ls의 부모node를 수정한 후 빌려온값을 insert한다.
					//updateParentKey(T.getParent(),T.getKeylist().get(0),newVal);//왼쪽에서 빌려온 값은 Tnode에 가장 작은 값이기에 update를한다.
					insert(T,newVal);
				}
				
			}else if(Rs != null && !violateMinKeySize(Rs.getKeylist().size()-1)){//오른쪽node가 있고 오른쪽node의 key가 한개 없어도 minkey를 위배하지않으면
				//오른쪽node에서 하나의 값을 빌려온다.
				int newVal = Rs.getKeylist().get(0);
				Rs.getKeylist().remove(0);
				
				//internal node일 경우 (key뿐아니라 childnode 도 가져와야한다)
				if(Rs.getChildren().size() > 0) {
					//key 세팅
					//오른쪽node의 첫번째 node의 첫번째 key를 가져와 update와 insert를한다.
					int oldVal = Rs.getChildren().get(0).getKeylist().get(0);
					updateParentKey(Rs,oldVal,newVal);
					insert(T,oldVal);
					
					//오른쪽node의 첫번째 childnode를 가져와 추가 및 설정
					MyBPlusTreeNode moveT = Rs.getChildren().get(0);
					T.getChildren().add(moveT);
					moveT.setParent(T);
					Rs.getChildren().remove(0);
					updateParentKey(Rs,oldVal,Rs.getChildren().get(0).getKeylist().get(0));
				}else {//internalnode가 아니면 Rs에 값을 빌려왔으니 Rs의 부모node를 수정한 후 빌려온값을 insert한다.
					updateParentKey(Rs,newVal,Rs.getKeylist().get(0));
					insert(T,newVal);
				}
			}else {//Ls와 Rs 모두 값을 빌려줄 수 없을 경우 merge한다.
				if(Ls !=null) {//Ls가 존재시
					if(Ls.getChildren().size()==0) {
						//Ls에 T의 key들을 넣는다.
						for(int i=0;i<T.getKeylist().size();i++) {
							insert(Ls,T.getKeylist().get(i));
						}
						//부모node에서 T를 삭제한다.
						P.getKeylist().remove(tmpidx-1);
						P.getChildren().remove(tmpidx);
						removeLeafNode(T);
						if( T.getParent() == root && root.getKeylist().size() == 0 ) {
							root = Ls;
							return true;
						}
					}else {
						int tmp = Ls.getKeylist().size();
						for(int i=tmp-1;i>=0;i--) {
							P.getKeylist().add(0,Ls.getKeylist().get(i));
							Ls.getKeylist().remove(i);
						}
						tmp = Ls.getChildren().size();
						P.getChildren().remove(0);
						for(int i=tmp-1;i>=0;i--) {
							MyBPlusTreeNode addNode = Ls.getChildren().get(i);
							P.getChildren().add(0,addNode);
							Ls.getChildren().remove(i);
							addNode.setParent(P);
						}
						

						tmp = T.getKeylist().size();
						for(int i=0 ; i < tmp ; i++) {
							P.getKeylist().add(T.getKeylist().get(0));
							T.getKeylist().remove(0);
						}
						tmp = T.getChildren().size();
						P.getChildren().remove(P.getChildren().size()-1);
						for(int i=0 ; i < tmp ; i++) {
							MyBPlusTreeNode addNode = T.getChildren().get(0);
							P.getChildren().add(addNode);
							T.getChildren().remove(0);
							addNode.setParent(P);
						}
					}
				}else if(Rs != null) {//Ls가 존재x , Rs 존재
					//leaf node일 경우
					if(T.getChildren().size() == 0) {
						//T에 Rs의 key들을 넣는다.
						for(int i=0;i<Rs.getKeylist().size();i++) {
							insert(T,Rs.getKeylist().get(i));
						}
						//부모node에서 Rs를 삭제한다.
						P.getKeylist().remove(tmpidx);
						P.getChildren().remove(tmpidx+1);
						removeLeafNode(Rs);
						//만약  T의 부모노드가 root이고, Rs에 값을 삭제하여 key가 없을 경우 
						//T가 root가 된다.
						if( T.getParent() == root && root.getKeylist().size() == 0 ) {
							root = T;
							return true;
						}
					}else {//leaf node가 아닐 경우
						
						//부모node의 key와 childnode들을 T로 이동 및 설정
						for(int i= 0 ; i<P.getKeylist().size();i++) {
							//key
							insert(T, P.getKeylist().get(0));
							P.getKeylist().remove(0);
							
							//child node
							MyBPlusTreeNode childT =  P.getChildren().get(i+1);
							int tmpsize = childT.getKeylist().size();
							for(int j = 0;j<tmpsize;j++) {
								insert(T, childT.getKeylist().get(0));
								childT.getKeylist().remove(0);
							}
							//child node들의 parent 변수 설정
							tmpsize = childT.getChildren().size();
							for(int j = 0;j<tmpsize;j++) {
								MyBPlusTreeNode tmpnode =  childT.getChildren().get(0);
								tmpnode.setParent(T);
								T.getChildren().add(tmpnode);
								childT.getChildren().remove(0);
							}
						}
						//만약 P가 root였으면 T를 root로 초기화
						if(P == root) {
							root = T;
							return true	;
						}
					}
				}
				//merge 를 한 node를 기준으로 다시 함수를 실행한다.
				removeFun(P);
			}
		}
		return false;
	}

	
	@Override
	public Iterator<Integer> iterator() {
		List<Integer> retList = new ArrayList<>();
		if(leafList == null) {
			for (int j = 0; j<root.getKeylist().size() ;j++)
				retList.add(root.getKeylist().get(j));
			Iterator<Integer> ret = retList.iterator();
			
			return ret;
		}

		// TODO Auto-generated method stub
		for (int i = 0; i<leafList.size() ;i++) {
			MyBPlusTreeNode tmp = leafList.get(i);
			for (int j = 0; j<tmp.getKeylist().size() ;j++)
				retList.add(tmp.getKeylist().get(j));
			
		}
		Iterator<Integer> ret = retList.iterator();
		
		return ret;
	}
	@Override
	public int size() {
		if(leafList == null)return 0;
		
		int ret = 0;
		for(int i=0 ; i<leafList.size();i++) {
			ret += leafList.get(i).getKeylist().size();
		}
		return ret;
		
	}
	
	@Override
	public Comparator<? super Integer> comparator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer first() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer last() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Integer lower(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer floor(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer ceiling(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer higher(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer pollFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer pollLast() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NavigableSet<Integer> descendingSet() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Iterator<Integer> descendingIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement,
			boolean toInclusive) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SortedSet<Integer> tailSet(Integer fromElement) {
		// TODO Auto-generated method stub
		return null;
	}
}
