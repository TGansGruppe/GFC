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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Typed Version of the CLST format. Two Level Hierachy.
 * Supported types are the following:
 * - str STRING
 * - int INTEGER
 * - flt/dbl FLOAT
 * - lng LONG
 * - bol BOOLEAN
 *
 * PART OF THE GFC.
 *
 * @author 0x1905
 */
public class LST {
	private HashMap<String, PropClass> propertyClasses;

	public LST() {
		this.propertyClasses = new HashMap<>();
	}

	/**
	 * Loads and parses a LST file from disk
	 *
	 * @param path The path to the file
	 * @throws IOException
	 */
	public void load(String path) throws IOException {
		this.propertyClasses.clear();

		String raw = new String(Files.readAllBytes(Paths.get(path)));
		raw = raw.replace("\t", " ");
		parse(raw);
	}

	/**
	 * Parses raw LST text to a LST object.
	 *
	 * @param text The raw text
	 */
	public void parse(String text) {
		String[] lines = text.split("\r\n");
		ArrayList<String> words = new ArrayList<>();

		for (int i = 0; i < lines.length; i++) {
			String[] lsplit = lines[i].split(" ");

			for (int j = 0; j < lsplit.length; j++) {
				String word = lsplit[j].replaceAll(" ", "");

				if (word.startsWith("#")) {
					break;
				} else if (!word.matches("")) {
					words.add(word);
				}
			}
		}
		String[] tmp = new String[]{""};

		words.forEach((v) -> {
			tmp[0] += v + " ";
		});
		text = tmp[0];

		String[] split = text.split("end");

		for (int i = 0; i < split.length; i++) {
			try {
				int offset = 0; // Offset for Splitting

				// Check if we are at the correct starting point, else set the offset to 1
				if (!split[i].split(" ")[0].matches("class")) offset = 1;
				String className = split[i].split(" ")[1 + offset].replace(":", "");
				String classText = split[i].split(className + ":")[1];

				this.propertyClasses.put(className, new PropClass(classText));
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
	}

	/**
	 * Gets a Property Class
	 *
	 * @param name The Name of the Class
	 * @return The Property Class
	 * when Class not Found
	 */
	public PropClass getPropClass(String name) {
		return propertyClasses.get(name);
	}

	/**
	 * Gets an Integer from a Property Class
	 *
	 * @param clazz The Property Class the Field is part of
	 * @param field The Field Name
	 * @return The value of the Field
	 */
	public int getInteger(String clazz, String field) {
		return getPropClass(clazz).getInteger(field);
	}

	/**
	 * Gets a Float from a Property Class
	 *
	 * @param clazz The Property Class the Field is part of
	 * @param field The Field Name
	 * @return The value of the Field
	 */
	public float getFloat(String clazz, String field) {
		return getPropClass(clazz).getFloat(field);
	}

	/**
	 * Gets a Long from a Property Class
	 *
	 * @param clazz The Property Class the Field is part of
	 * @param field The Field Name
	 * @return The value of the Field
	 */
	public long getLong(String clazz, String field) {return getPropClass(clazz).getLong(field); }

	/**
	 * Gets a Boolean from a Property Class
	 *
	 * @param clazz The Property Class the Field is part of
	 * @param field The Field Name
	 * @return The value of the Field
	 */
	public boolean getBoolean(String clazz, String field) {
		return getPropClass(clazz).getBoolean(field);
	}

	/**
	 * Gets a String from a Property Class
	 *
	 * @param clazz The Property Class the Field is part of
	 * @param field The Field Name
	 * @return The value of the Field
	 */
	public String getString(String clazz, String field) {
		return getPropClass(clazz).getString(field);
	}

	@Override
	public String toString() {
		String[] out = new String[]{""};

		this.propertyClasses.forEach((k, v) -> {
			out[0] += String.format("class %s:\n", k);
			out[0] += v.toString();
			out[0] += "end\n";
		});

		return out[0];
	}

	private class PropClass {
		private HashMap<String, Boolean> boolProps;
		private HashMap<String, String>  stringProps;
		private HashMap<String, Integer> intProps;
		private HashMap<String, Float>   floatProps;
		private HashMap<String, Long>    longProps;

		/**
		 * Create a new Property Class from Raw .lst Text
		 *
		 * @param rawText The raw Property Class text
		 */
		public PropClass(String rawText) {
			this.boolProps   = new HashMap<>();
			this.stringProps = new HashMap<>();
			this.intProps    = new HashMap<>();
			this.floatProps  = new HashMap<>();
			this.longProps   = new HashMap<>();

			parseClass(rawText);
		}

		private void parseClass(String rawText) {
			String[] split = rawText.split(" ");
			for (int i = 0; i < split.length; i++) {
				String type = split[i];
				String name = split[i + 1];

				if (!type.matches("str")) {
					switch (type) {
						case "int":
							intProps.put(name, Integer.parseInt(split[i + 3]));
							i += 3;
							break;
						case "dbl":
						case "flt":
							floatProps.put(name, Float.parseFloat(split[i + 3]));
							i += 3;
							break;
						case "lng":
							longProps.put(name, Long.parseLong(split[i + 3]));
							i += 3;
							break;
						case "bol":
							boolProps.put(name, Boolean.valueOf(split[i + 3]));
							i += 3;
							break;
						case " ":
							break;
					}
				} else {
					String value = assembleStringFromArray(i + 3, split, "\"", true);
					i += value.split(" ").length + 2;
					stringProps.put(name, value);
				}
			}
		}

		/**
		 * @param name of the Integer Field
		 * @return Value of The Field
		 */
		public int getInteger(String name) {
			if (!intProps.containsKey(name)) throw new NullPointerException();
			return intProps.get(name);
		}

		/**
		 * @param name of the Float Field
		 * @return Value of The Field
		 */
		public float getFloat(String name) {
			if (!floatProps.containsKey(name)) throw new NullPointerException();
			return floatProps.get(name);
		}
		/**
		 * @param name of the Long Field
		 * @return Value of The Field
		 */
		public long getLong(String name) {
			if (!longProps.containsKey(name)) throw new NullPointerException();
			return longProps.get(name);
		}

		/**
		 * @param name of the Boolean Field
		 * @return Value of The Field
		 */
		public boolean getBoolean(String name) {
			if (!boolProps.containsKey(name)) throw new NullPointerException();
			return boolProps.get(name);
		}

		/**
		 * @param name of the String Field
		 * @return Value of The Field
		 */
		public String getString(String name) {
			if (!stringProps.containsKey(name)) throw new NullPointerException();
			return stringProps.get(name);
		}

		@Override
		public String toString() {
			String[] out = new String[]{""};

			boolProps.forEach((k, v) -> {
				out[0] += String.format("bol %s = %s\n", k, Boolean.toString(v));
			});

			floatProps.forEach((k, v) -> {
				out[0] += String.format("flt %s = %s\n", k, Float.toString(v));
			});

			intProps.forEach((k, v) -> {
				out[0] += String.format("int %s = %s\n", k, Integer.toString(v));
			});

			stringProps.forEach((k, v) -> {
				out[0] += String.format("str %s = \"%s\"\n", k, String.valueOf(v));
			});

			return out[0];
		}
	}

	private static String assembleStringFromArray(int index, String[] words, String terminator, boolean removeFirst) {
		String out = "";

		for (int i = index; i < words.length; i++) {
			out += words[i];
			if (words[i].endsWith(terminator)) break;
			out += " ";
		}
		return out.substring(removeFirst ? 1 : 0, out.length() - 1);
	}
}
