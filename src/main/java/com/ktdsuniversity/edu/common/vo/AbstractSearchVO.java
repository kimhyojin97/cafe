package com.ktdsuniversity.edu.common.vo;

public abstract class AbstractSearchVO {
	/**
	 * 검색할 페이지의 번호(0-Zerobase)
	 */
	private int pageNo;
	/**
	 * 한 목록에 노출시킬 게시글의 개수
	 */
	private int listSize;
	/**
	 * 생성할 최대 페이지의 수
	 */
	private int pageCount;
	
	/**
	 * 한 페이지 그룹에 보여줄 페이지 번호의 개수
	 */
	private int pageCountInGroup;
	
	/**
	 * 총 페이지 그룹의 개수
	 */
	private int groupCount;
	
	/**
	 * 현재 페이지의 그룹 번호
	 */
	private int groupNo;

	/**
	 * 페이지 그룹 번호의 시작 페이지 번호
	 */
	private int groupStartPageNo;

	/**
	 * 페이지 그룹 번호의 끝 페이지 번호
	 */
	private int groupEndPageNo;
	
	/**
	 * 다음 그룹이 존재하는지 확인
	 */
	private boolean hasNextGroup;
	
	/**
	 * 이전그룹이 존재하는지 확인
	 */
	private boolean hasPrevGroup;

	/**
	 * 다음 그룹의 시작페이지 번호
	 */
	private int nextGroupStartPageNo;

	/**
	 * 이전 그룹의 시작 페이지 번호
	 */
	private int prevGroupStartPageNo;
	
	public AbstractSearchVO() {
		listSize = 10;
		pageCountInGroup = 10;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public int getPageCount() {
		return pageCount;
	}
	
	/**
	 * 게시글의 개수를 할당하면 생성되어야 하는 페이지의 최대 크기가 pageCount에 할당.
	 * @param listSize 검색된 게시글의 총 개수
	 */
	public void setPageCount(int listCount) {
		this.pageCount = (int) Math.ceil((double) listCount / this.listSize);
		this.groupCount = (int) Math.ceil((double) this.pageCount /	this.pageCountInGroup);
		this.groupNo = this.pageNo / this.pageCountInGroup;
		this.groupStartPageNo = this.groupNo * this.pageCountInGroup;
		this.groupEndPageNo = (this.groupNo + 1) * this.pageCountInGroup - 1;
		if (this.groupEndPageNo > this.pageCount) {
		this.groupEndPageNo = this.pageCount - 1;
		}
		this.hasNextGroup = this.groupNo + 1 < this.groupCount;
		this.hasPrevGroup = this.groupNo > 0;
		this.nextGroupStartPageNo = this.groupEndPageNo + 1;
		this.prevGroupStartPageNo = this.groupStartPageNo - this.pageCountInGroup;
	}

	public int getPageCountInGroup() {
		return pageCountInGroup;
	}

	public void setPageCountInGroup(int pageCountInGroup) {
		this.pageCountInGroup = pageCountInGroup;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public int getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}

	public int getGroupStartPageNo() {
		return groupStartPageNo;
	}

	public void setGroupStartPageNo(int groupStartPageNo) {
		this.groupStartPageNo = groupStartPageNo;
	}

	public int getGroupEndPageNo() {
		return groupEndPageNo;
	}

	public void setGroupEndPageNo(int groupEndPageNo) {
		this.groupEndPageNo = groupEndPageNo;
	}

	public boolean isHasNextGroup() {
		return hasNextGroup;
	}

	public void setHasNextGroup(boolean hasNextGroup) {
		this.hasNextGroup = hasNextGroup;
	}

	public boolean isHasPrevGroup() {
		return hasPrevGroup;
	}

	public void setHasPrevGroup(boolean hasPrevGroup) {
		this.hasPrevGroup = hasPrevGroup;
	}

	public int getNextGroupStartPageNo() {
		return nextGroupStartPageNo;
	}

	public void setNextGroupStartPageNo(int nextGroupStartPageNo) {
		this.nextGroupStartPageNo = nextGroupStartPageNo;
	}

	public int getPrevGroupStartPageNo() {
		return prevGroupStartPageNo;
	}

	public void setPrevGroupStartPageNo(int prevGroupStartPageNo) {
		this.prevGroupStartPageNo = prevGroupStartPageNo;
	}
}
