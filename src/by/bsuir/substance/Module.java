package by.bsuir.substance;

import java.util.List;

import by.bsuir.pascal.Header;

public class Procedure {
	private Header header;
	private String varSection;
	private String body;
	private List<String> variables;
	private List<String> arguments;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header name) {
		this.header = name;
	}
	public String getVarSection() {
		return varSection;
	}
	public void setVarSection(String varSection) {
		this.varSection = varSection;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}
