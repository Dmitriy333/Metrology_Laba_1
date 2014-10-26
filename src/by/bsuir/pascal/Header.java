package by.bsuir.pascal;

public class Header{
	private String name;
	private boolean complexHeader = false;
	public Header(String name, boolean complexHeader){
		this.name = name;
		this.complexHeader = complexHeader;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isComplexHeader() {
		return complexHeader;
	}
	public void setComplexHeader(boolean complexHeader) {
		this.complexHeader = complexHeader;
	}
}
