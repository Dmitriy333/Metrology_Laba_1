package by.bsuir.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.bsuir.substance.Procedure;

public class MainFile {
	private static List<String> reservedWords = getReservedWords("resources/ReservedWords.txt");
	private static List<Procedure> procedures = new ArrayList<Procedure>();

	private static List<String> getReservedWords(String fileName) {
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

	private static String getPascalCode(String fileName) {
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

	private static List<String> getProceduresHeaders(String text) {
		List<String> headers = new ArrayList<String>();
		Pattern pHeader = Pattern.compile("(?<!\\w)procedure\\s+[\\w\\s.]+;");
		Matcher mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(mWord.group());
		}
		pHeader = Pattern
				.compile("(?<!\\w)procedure\\s+[\\w\\s.]+\\([\\w\\s,.=':;$/*()]*?\\)\\s*;");
		mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(mWord.group());
		}
		return headers;
	}

	private static List<String> getVarSections(String text) {
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

	private static List<String> getVariablesFromVarSection(String varSection) {
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

	private static String getProcedureCode(String fileName, String procedureName) {
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

	private static void countVariables(Procedure procedure) {
		System.out.println("____________________________________");
		System.out.println(procedure.getName());
		if (procedure.getVariables() != null) {
			for (String variable : procedure.getVariables()) {
				int count = 0;
				for (int i = 0; i < procedure.getBody().length()
						- variable.length(); i++) {
					if (procedure.getBody()
							.subSequence(i, i + variable.length())
							.equals(variable)) {
						count++;
					}
				}
				System.out.println(variable + ": " + count);
			}
		}
	}
	
	private static Procedure getMainProcedure(String pascalCode, List<Procedure> procedures){
		String simpleCode = pascalCode;
		for (Procedure procedure : procedures) {
			simpleCode = simpleCode.replace(procedure.getBody(), "");
		}
//		System.out.println(simpleCode);
		Procedure simpleCodeProc = new Procedure();
		simpleCodeProc.setName("MainProgram");
		simpleCodeProc.setBody(simpleCode);
		for (String varSection : getVarSections(pascalCode)) {
			if (simpleCodeProc.getBody().contains(varSection)) {
				simpleCodeProc.setVarSection(varSection);
				simpleCodeProc
						.setVariables(getVariablesFromVarSection(varSection));
				break;
			}
		}
//		for (String header : getProceduresHeaders(simpleCode)) {
//			simpleCodeProc.setName(header);
//			simpleCodeProc.setBody(getProcedureCode(fileName, header));
//			
//			procedures.add(simpleCodeProc);
//		}
		return simpleCodeProc;
	}

	// split("procedure|function|begin");
	public static void main(String[] args) {
		String fileName = "resources/Pascal.txt";
		String pascalCode = getPascalCode(fileName);
		// for (String header : getProceduresHeaders(pascalCode)) {
		// System.out.println(header);
		// }
		for (String varSection : getVarSections(pascalCode)) {
			// System.out.println(varSection.trim());
			for (String string : getVariablesFromVarSection(varSection)) {
//				System.out.println(string);
			}
		}
		for (String header : getProceduresHeaders(pascalCode)) {
			Procedure procedure = new Procedure();
			procedure.setName(header);
			procedure.setBody(getProcedureCode(fileName, header));
			for (String varSection : getVarSections(pascalCode)) {
				if (procedure.getBody().contains(varSection)) {
					procedure.setVarSection(varSection);
					procedure
							.setVariables(getVariablesFromVarSection(varSection));
					break;
				}
			}
			procedures.add(procedure);
		}
		procedures.add(getMainProcedure(pascalCode,procedures));
		for (Procedure procedure : procedures) {
			countVariables(procedure);
		}

		// for (Procedure procedure : procedures) {
		// System.out.println(procedure.getBody());
		// }
	}

}
