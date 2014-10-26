package by.bsuir.substance;

import java.util.List;

public class Procedure {
	private String name;
	private String varSection;
	private String body;
	private List<String> variables;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
}
