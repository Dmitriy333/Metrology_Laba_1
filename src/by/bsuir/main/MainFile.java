package by.bsuir.main;

import by.bsuir.pascal.PascalAnalyzer;

public class MainFile {
	public static void main(String[] args) {
		PascalAnalyzer pascalAnalyzer = new PascalAnalyzer();
		System.out.println(pascalAnalyzer.spenMetricsInfo("resources/Pascal.txt"));
	}
}
