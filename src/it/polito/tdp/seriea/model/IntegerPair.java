package it.polito.tdp.seriea.model;
public class IntegerPair {
	private int n1 ;
	private int n2 ;
	private int num;
	public IntegerPair(int n1, int n2) {
			this.n1 = n1;	this.n2 = n2;	}

	public IntegerPair(int n1, int n2, int num) {
		super();
		this.n1 = n1;
		this.n2 = n2;
		this.num = num;
	}

	public int getN1() {		return n1;	}
	public int getN2() {		return n2;	}
	public void setN1(int n1) {		this.n1 = n1;	}
	public void setN2(int n2) {		this.n2 = n2;	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "IntegerPair [n1=" + n1 + ", n2=" + n2 + ", num=" + num + "]"+"\n";
	}

	

}
