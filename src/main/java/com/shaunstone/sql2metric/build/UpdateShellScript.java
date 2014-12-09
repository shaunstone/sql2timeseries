package com.shaunstone.sql2metric.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * used by the build process to update the shell script with the base64 value.
 * didnt want to write a plugin for this
 *
 * @author srstone
 *
 */
public class UpdateShellScript {

	public static void main(String[] args) throws IOException {
		byte[] filebytes = Files.readAllBytes(Paths.get(args[0]));

		Path shellscript = Paths.get(args[1]);
		Path outputpath = Paths.get(args[2]);

		String base64File = javax.xml.bind.DatatypeConverter.printBase64Binary(filebytes);
		System.out.println("SCRIPT FILE: " + shellscript.toString());
		BufferedReader raf = new BufferedReader(new FileReader(shellscript.toFile()));
		BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(outputpath.toString(), shellscript.getFileName().toString())
				.toFile()));

		String line;
		while ((line = raf.readLine()) != null) {
			if (line.contains("${base64}")) {
				line = line.replace("${base64}", base64File);
			}
			bw.write(line + "\n");
		}
		raf.close();
		bw.close();

	}
}
