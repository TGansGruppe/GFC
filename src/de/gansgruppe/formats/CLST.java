/*
Copyright (c) 2021-22, GansGruppe & Associates

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.gansgruppe.formats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * A simple list format originally for the AIA4 STD-Compiler.
 * This list is divided into "columns" using semi-colons,
 * list entries are divided using double-colons.
 * All data inside a CLST is regarded as a String by this
 * parser.
 *
 * PART OF THE GFC.
 *
 * @author Henry Portsmith
 * */
public class CLST {
	private File file;
	private ArrayList<String[]> columns;

	/**
	 * Creates an empty CLST
	 * */
	public CLST() {
		this.columns = new ArrayList<>();
	}

	/**
	 * Creates a CLST from a file
	 * @param file The CLST file
	 * */
	public CLST(String file) {
		this.columns = new ArrayList<>();
		this.file = new File(file);
		loadCLST(this.file);
	}

	/**
	 * Creates a CLST from a file
	 * @param file The CLST file
	 * */
	public CLST(File file) {
		this.columns = new ArrayList<>();
		this.file = file;
		loadCLST(this.file);
	}

	/**
	 * Returns an entry from the loaded CLST
	 * @param column The column from which the entry is to be returned
	 * @param entry The index of the entry
	 * @return The requested entry
	 * */
	public String getEntry(int column, int entry) {
		if (columns.size() < 1) {
			throw new NullPointerException("No columns present!");
		}

		return columns.get(column)[entry];
	}

	/**
	 * Returns a column from the loaded CLST
	 * @param column The index of requested column
	 * @return The requested column
	 * */
	public String[] getColumn(int column) {
		if (columns.size() < 1) {
			throw new NullPointerException("No columns present!");
		}

		return columns.get(column);
	}

	/**
	 * Overwrites an entry in the loaded CLST
	 * @param column The column in which the entry is to be overwritten
	 * @param entry The entry which is to be overwritten
	 * @param value The value
	 * */
	public void overwriteEntry(int column, int entry, String value) {
		if (columns.size() < 1) {
			throw new NullPointerException("No columns present!");
		}

		columns.get(column)[entry] = value;
	}

	/**
	 * Overwrites a column within the loaded CLST
	 * @param column The index of the column
	 * @param values The data of the column
	 * */
	public void overwriteColumn(int column, String[] values) {
		if (columns.size() < 1) {
			throw new NullPointerException("No columns present!");
		}

		columns.set(column, values);
	}

	/**
	 * Appends a column
	 * @param values The new values
	 * */
	public void addColumn(String[] values) {
		columns.add(values);
	}

	/**
	 * Loads a CLST from a file
	 * @param file The file to be loaded
	 * */
	public void loadCLST(File file) {
		Path path = file.toPath();
		String cont = "";
		try {
			cont = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		String[] contSplit = cont.split(";");
		for (String s : contSplit) {
			columns.add(s.split(":"));
		}
	}

	/**
	 * Saves the changes
	 * @throws IOException
	 * */
	public void save() throws IOException {
		if (file == null) {
			throw new NullPointerException("No file loaded!");
		}
		write(file, false);
	}

	/**
	 * Writes a CLST file to disk.
	 * @param file The file
	 * @param changeFile Sets if the internal file object should be updated
	 * @throws IOException
	 * */
	public void write(File file, boolean changeFile) throws IOException {
		if (changeFile) this.file = file;

		/*
		 * An array has to be used for a single String since
		 * Java 8 only allows final or "effectivly-final" variables
		 * inside a lambda.
		 */
		String[] out = new String[] {""};

		// Create CLST
		columns.forEach((v) -> {
			for (String s : v) {
				out[0] += s + ":";
			}
			out[0] = out[0].substring(0, out[0].length() - 1);
			out[0] += ";";
		});

		// Write to file
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(out[0]);

		writer.close();
	}

	/**
	 * Sets the internal file object used for saving.
	 * @param file The file object
	 * */
	public void setFile(File file) { this.file = file; }

	/**
	 * @return the internal file object
	 * */
	public File getFile() { return this.file; }

	/**
	 * @return the colums of the CLST
	 * */
	public ArrayList<String[]> getColumns() { return this.columns; }
}
