package nand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
	public ArrayList<String> lines = new ArrayList<>();
	public int index = 0;
	public String current;

	public Parser(String filename) throws IOException {
		lines.addAll(Files.lines(Paths.get(filename)).filter(x -> (!x.startsWith("//") && !x.isEmpty()))
				.map(x -> process(x)).collect(Collectors.toList()));
		current = lines.get(0);
	}

	public Parser(List<String> lolz) {
		lines.addAll(lolz);
		current = lines.get(0);
	}

	public int getNumCommands() {
		return lines.size();
	}

	public String getCurrent() {
		return lines.get(index);
	}

	public void setCurrent(int i) {
		index = i;
		current = lines.get(index);
	}

	public String symbol() {
		return current.replaceAll("[()@]", "").trim();
	}

	public void replaceSymbol(String newSymbol) {
		lines.set(index, "@" + newSymbol);
	}

	public String dest() {
		return current.contains("=") ? current.substring(0, current.indexOf("=")) : null;
	}

	public String comp() {
		int[] a = { current.indexOf("=") + 1, current.indexOf(";") };
		return current.contains("=")
				? current.contains(";") ? current.substring(a[0], a[1]) : current.substring(a[0], current.length())
				: current.substring(0, a[1]);
	}

	public String jump() {
		return current.contains(";") ? current.substring(current.indexOf(";") + 1, current.length()) : null;
	}

	public CommandType commandType() {
		return current.contains("(") ? CommandType.L_COMMAND
				: current.contains("@") ? CommandType.A_COMMAND : CommandType.C_COMMAND;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getLines() {
		return (ArrayList<String>) lines.clone();
	}

	public void removeCurrentLine() {
		lines.remove(index);
	}

	public String process(String x) {
		if (x.contains("//")) {
			x = x.replace(x.substring(x.indexOf("//"), x.length()), "");
		}
		x = x.trim();
		return x;
	}

	public enum CommandType {
		A_COMMAND, C_COMMAND, L_COMMAND
	}
}
