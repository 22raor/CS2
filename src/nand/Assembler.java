package nand;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import nand.Parser.CommandType;

public class Assembler {

	public static void main(String[] args) throws IOException {
		String input = args[0];
		String output = input.replace(".asm", ".hack");

		Parser p = new Parser(input);

		Symbols s = new Symbols(p);

		ArrayList<String> assembled = new ArrayList<>();

		for (int a = 0; a < p.getNumCommands(); a++) {
			p.setCurrent(a);
			if (p.commandType() == CommandType.A_COMMAND) {
				assembled.add("0" + Code.binary(p.symbol()));
			} else if (p.commandType() == CommandType.C_COMMAND) {
				assembled.add("111" + Code.comp(p.comp()) + Code.dest(p.dest()) + Code.jump(p.jump()));
			}
		}

		Files.write(Paths.get(output), assembled, Charset.defaultCharset());

	}

}
