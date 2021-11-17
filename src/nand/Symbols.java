package nand;

import java.util.HashMap;

import nand.Parser.CommandType;

public class Symbols {

	public HashMap<String, Integer> vars;

	public Symbols(Parser p) {
		vars = new HashMap<String, Integer>() {
			{
				put("SP", 0);
				put("LCL", 1);
				put("ARG", 2);
				put("THIS", 3);
				put("THAT", 4);
				put("SCREEN", 16384);
				put("KBD", 24576);
				for (int i = 0; i < 16; i++) {
					put("R" + i, i);
				}
			}
		};

		int i = 0;

		for (int a = 0; a < p.getNumCommands(); a++) {
			p.setCurrent(a);
			if (p.commandType() == CommandType.L_COMMAND) {
				vars.put(p.symbol(), i);
			} else {
				i++;
			}
		}

		int ct = 16;
		for (int a = 0; a < p.getNumCommands(); a++) {
			p.setCurrent(a);
			if (p.commandType() == CommandType.A_COMMAND) {

				if (vars.containsKey(p.symbol())) {
					p.replaceSymbol(vars.get(p.symbol()) + "");

				} else if (!isNum(p.symbol())) {
					vars.put(p.symbol(), ct);
					p.replaceSymbol(ct + "");
					ct++;
				}

			}
		}

	}

	public static boolean isNum(String a) {
		try {
			Integer.parseInt(a);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
