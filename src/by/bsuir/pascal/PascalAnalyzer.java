package by.bsuir.pascal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.bsuir.substance.Procedure;

public class PascalAnalyzer {

	private List<String> reservedWords = getReservedWords("resources/ReservedWords.txt");
	private List<Procedure> procedures = new ArrayList<Procedure>();

	private List<String> getReservedWords(String fileName) {
		File f = new File(fileName);
		List<String> reservedWords = null;
		try {
			reservedWords = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				reservedWords.add(line.toLowerCase().trim());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reservedWords;
	}

	private String getPascalCode(String fileName) {
		StringBuilder sb = new StringBuilder();
		File f = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private List<Header> getProceduresHeaders(String text) {
		List<Header> headers = new ArrayList<Header>();
		Pattern pHeader = Pattern.compile("(?<!\\w)procedure\\s+[\\w\\s.]+;");
		Matcher mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), false));
		}
		pHeader = Pattern
				.compile("(?<!\\w)procedure\\s+[\\w\\s.]+\\([\\w\\s,.=':;$/*()]*?\\)\\s*;");
		mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), true));
		}
		return headers;
	}

	private List<String> getVarSections(String text) {
		List<String> varSections = new ArrayList<String>();//
		Pattern pSection = Pattern
				.compile("(var)([\\r\\n|\\r|\\n]*.*[\\r\\n|\\r|\\n])*");
		String bufferText = text;
		Matcher mSection = pSection.matcher(bufferText);
		String varSection = null;
		String[] sections = null;
		while (mSection.find()) {
			varSection = mSection.group();
			sections = varSection.split("procedure|function|begin");
			varSection = sections[0];
			bufferText = bufferText.replace(varSection, "");
			mSection = pSection.matcher(bufferText);
			varSections.add(varSection);
		}
		return varSections;
	}

	private List<String> getVariablesFromVarSection(String varSection) {
		List<String> variables = new ArrayList<String>();
		Pattern pWord = Pattern.compile("([A-Za-z]+)|([\\W&&[^\\s\\n=<>\\&]])");
		Matcher mWord = pWord.matcher(varSection);
		while (mWord.find()) {
			if (!reservedWords.contains(mWord.group().toLowerCase())) {
				variables.add(mWord.group());
			}
		}
		return variables;
	}

	private String getProcedureCode(String fileName, String procedureName) {
		StringBuilder sb = new StringBuilder();
		File f = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			boolean writeFlag = false;
			while (!(line.equals("end;") && (writeFlag == true))) {
				if (line.trim().equals(procedureName)) {
					writeFlag = true;
					sb.append(System.lineSeparator());
				}
				if (writeFlag) {
					sb.append(line);
					sb.append(System.lineSeparator());
				}
				line = br.readLine();
			}
			sb.append("end;");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private List<String> getArguments(String procedureDeclaration){
		List<String> arguments = new ArrayList<String>();
		Pattern pBrackets = Pattern.compile("\\(.*\\)");
		Matcher mBrackets = pBrackets.matcher(procedureDeclaration);
		String argumentDeclaration = null;
		while (mBrackets.find()) {
			argumentDeclaration = mBrackets.group();
		}
		Pattern pWord = Pattern.compile("([A-Za-z]+)|([\\W&&[^\\s\\n=<>\\&]])");
		Matcher mWord = pWord.matcher(argumentDeclaration);
		while (mWord.find()) {
			if (!reservedWords.contains(mWord.group().toLowerCase())) {
				arguments.add(mWord.group());
			}
		}
		return arguments;
		
	}
	
	private List<String> parseTextIntoWords(String text){
		List<String> words = new ArrayList<String>();
		Pattern pWord = Pattern.compile("([A-Za-z]+)|([\\W&&[^\\s\\n=<>\\&]])");
		Matcher mWord = pWord.matcher(text);
		while (mWord.find()) {
			words.add(mWord.group());
		}
		return words;
	}

	private String countVariables(Procedure procedure) {
		StringBuilder sb = new StringBuilder();
		List<String> words = parseTextIntoWords(procedure.getBody());
		sb.append(procedure.getHeader().getName());
		if(procedure.getHeader().isComplexHeader()){
			sb.append(" " + "(comlex header)\n");
			List<String> arguments = getArguments(procedure.getHeader().getName());
			for (String argument : arguments) {
				int count = 0;
				for (String word : words) {
					if(argument.toLowerCase().equals(word.toLowerCase())){
						count++;
					}
				}
				sb.append(argument + ": " + count + ";\n");
			}
		}
		sb.append("\n");
		if (procedure.getVariables() != null) {
			for (String variable : procedure.getVariables()) {
				int count = 0;
				for (String word : words) {
					if(variable.toLowerCase().equals(word.toLowerCase())){
						count++;
					}
				}
				sb.append(variable + ": " + count + ";\n");
			}
		}else{
			sb.append("No local variables.\n");
		}
		return sb.toString();
	}

	private Procedure getMainProcedure(String pascalCode,
			List<Procedure> procedures) {
		String simpleCode = pascalCode;
		for (Procedure procedure : procedures) {
			simpleCode = simpleCode.replace(procedure.getBody(), "");
		}
		Procedure simpleCodeProc = new Procedure();
		simpleCodeProc.setHeader(new Header("MainProgram", false));
		simpleCodeProc.setBody(simpleCode);
		for (String varSection : getVarSections(pascalCode)) {
			if (simpleCodeProc.getBody().contains(varSection)) {
				simpleCodeProc.setVarSection(varSection);
//				simpleCodeProc.setVariables(getVariablesFromVarSection(varSection));
				break;
			}
		}
		simpleCodeProc.setBody(pascalCode);
		simpleCodeProc.setVariables(getVariablesFromVarSection(simpleCodeProc.getVarSection()));
		return simpleCodeProc;
	}

	public String spenMetricsInfo(String fileName) {
		StringBuilder sb = new StringBuilder();
		String pascalCode = getPascalCode(fileName);
		for (Header header : getProceduresHeaders(pascalCode)) {
			Procedure procedure = new Procedure();
			procedure.setHeader(header);
			procedure.setBody(getProcedureCode(fileName, header.getName()));
			for (String varSection : getVarSections(pascalCode)) {
				if (procedure.getBody().contains(varSection)) {
					procedure.setVarSection(varSection);
					procedure.setVariables(getVariablesFromVarSection(varSection));
					break;
				}
			}
			procedures.add(procedure);
		}
		procedures.add(getMainProcedure(pascalCode, procedures));
		for (Procedure procedure : procedures) {
			sb.append(countVariables(procedure)).append("\n");
		}
		return sb.toString();
	}

}
